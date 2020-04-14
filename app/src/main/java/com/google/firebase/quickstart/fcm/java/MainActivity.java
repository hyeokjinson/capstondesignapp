/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.firebase.quickstart.fcm.java;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.quickstart.fcm.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Button btnMove;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        String url ="http://192.168.0.2:8080/stream/video.mjpeg";
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        // [END handle_data_extras]

        //Button subscribeButton = findViewById(R.id.subscribeButton);
        //subscribeButton.setOnClickListener(new View.OnClickListener() {
        //@Override
        //public void onClick(View v) {
        //Log.d(TAG, "Subscribing to weather topic");
        // [START subscribe_topics]
        //FirebaseMessaging.getInstance().subscribeToTopic("weather")
        //.addOnCompleteListener(new OnCompleteListener<Void>() {
        //@Override
        //public void onComplete(@NonNull Task<Void> task) {
        //String msg = getString(R.string.msg_subscribed);
        //if (!task.isSuccessful()) {
        //  msg = getString(R.string.msg_subscribe_failed);
        // }
        //Log.d(TAG, msg);
        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        //  }
        //});


        // [END subscribe_topics]
        // }
        // });

        //Button logTokenButton = findViewById(R.id.logTokenButton);
        //logTokenButton.setOnClickListener(new View.OnClickListener() {
        //@Override
        //public void onClick(View v) {
        // Get token
        // [START retrieve_current_token]
        //FirebaseInstanceId.getInstance().getInstanceId()
        //.addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
        //@Override
        //public void onComplete(@NonNull Task<InstanceIdResult> task) {
        //if (!task.isSuccessful()) {
        //Log.w(TAG, "getInstanceId failed", task.getException());
        //return;
        //}

        // Get new Instance ID token
        // String token = task.getResult().getToken();

        // Log and toast
        //String msg = getString(R.string.msg_token_fmt, token);
        //Log.d(TAG, msg);
        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        //}
        //});
        // [END retrieve_current_token]
        //}
        //});
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }


                        String token = task.getResult().getToken();

                        Log.d("MyFCM", "FCM token: " + token);
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
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

