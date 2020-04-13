package com.google.firebase.quickstart.fcm.java;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.quickstart.fcm.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class CompomentActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.compoment_activity);
    WebView webView = (WebView) findViewById(R.id.webView);
        webView.setPadding(0, 0, 0, 0);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

    String url = "http://192.168.0.2:8090/javascript_simple.html";
        webView.loadUrl(url);
    Button btnMove = (Button) findViewById(R.id.btnMove);
    //TextView recieveText = (TextView) findViewById(R.id.Text_car);
        btnMove.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
            String port = "8888";
            String ip = "192.168.0.2";
            String park = "parking";
            MyClientTask myClientTask = new MyClientTask(ip.toString(), Integer.parseInt(port), park.toString());
            myClientTask.execute();
        }
    });
}

public class MyClientTask extends AsyncTask<Void, Void, Void> {
    String dstAddress;
    int dstPort;
    String response = "";
    String myMessage = "parking";

    //constructor
    MyClientTask(String addr, int port, String message) {
        dstAddress = addr;
        dstPort = port;
        myMessage = message;

    }

    protected Void doInBackground(Void... arg0) {

        Socket socket = null;
        myMessage = myMessage.toString();
        try {
            socket = new Socket(dstAddress, dstPort);
            //송신
            OutputStream out = socket.getOutputStream();
            out.write(myMessage.getBytes());

            //수신
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int bytesRead;
            InputStream inputStream = socket.getInputStream();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
            }
            response = "response: " + response;

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    protected void onPostExecute(Void result) {
        //reciveText.setText(response);
        super.onPostExecute(result);
    }
}
}
