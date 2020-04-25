
package com.google.firebase.quickstart.fcm.java;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
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
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    public Button ConnButton;
    public Button btnMove;
    public Button btnParking;
    public Button btnStop;
    private Socket socket;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        String url = "http://192.168.0.2:8080/stream/video.mjpeg";
        webView.loadUrl(url);
        ConnButton = findViewById(R.id.button1);
        btnMove = findViewById(R.id.btnMove);
        btnParking = findViewById(R.id.btnParking);
        btnStop = findViewById(R.id.btnStop);
        final EditText ipNumber = findViewById(R.id.ipText);
        ConnButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Connect 시도", Toast.LENGTH_SHORT).show();
                String addr = ipNumber.getText().toString().trim();
                ConnectThread thread = new ConnectThread(addr);

                //키보드 자동 내리기
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ipNumber.getWindowToken(), 0);

                thread.start();


            }
        });
        //btnParking.setOnClickListener();
        //TextView recieveText = (TextView) findViewById(R.id.Text_car);
        btnMove.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoveThread sthread = new MoveThread();
                btnMove.setEnabled(false);
                btnParking.setEnabled(true);
                btnStop.setEnabled(true);
                sthread.start();

            }
        });
        btnParking.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParkThread sthread = new ParkThread();
                btnMove.setEnabled(true);
                btnParking.setEnabled(false);
                btnStop.setEnabled(true);
                sthread.start();

            }
        });
        btnStop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                StopThread sthread = new StopThread();
                btnMove.setEnabled(true);
                btnParking.setEnabled(true);
                btnStop.setEnabled(false);
                sthread.start();

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

    class MoveThread extends Thread {

        int bytes;
        String Dtmp;
        int dlen;

        public MoveThread() {


        }


        public String byteArrayToHex(byte[] a) {
            StringBuilder sb = new StringBuilder();
            for (final byte b : a)
                sb.append(String.format("%02x ", b & 0xff));
            return sb.toString();
        }

        public void run() {

            // 데이터 송신
            try {

                String OutData = "o\n";
                byte[] data = OutData.getBytes();
                OutputStream output = socket.getOutputStream();
                output.write(data);
                Log.d(TAG, "move\\n COMMAND 송신");

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "데이터 송신 오류");
            }

            // 데이터 수신
            try {
                Log.d(TAG, "데이터 수신 준비");

                //TODO:수신 데이터(프로토콜) 처리

                while (true) {
                    byte[] buffer = new byte[1024];

                    InputStream input = socket.getInputStream();

                    bytes = input.read(buffer);
                    Log.d(TAG, "byte = " + bytes);

                    //바이트 헥사(String)로 바꿔서 Dtmp String에 저장.
                    Dtmp = byteArrayToHex(buffer);
                    Dtmp = Dtmp.substring(0, bytes * 3);
                    Log.d(TAG, Dtmp);


                    //프로토콜 나누기
                    String[] DSplit = Dtmp.split("a5 5a"); // sync(2byte) 0xA5, 0x5A
                    Dtmp = "";
                    for (int i = 1; i < DSplit.length - 1; i++) { // 제일 처음과 끝은 잘림. 데이터 버린다.
                        Dtmp = Dtmp + DSplit[i] + "\n";
                    }
                    dlen = DSplit.length - 2;


                    runOnUiThread(new Runnable() {
                        public void run() {

                        }
                    });

                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "수신 에러");
            }


        }

    }

    // fixme: Stop 버튼 클릭 시 데이터 송신.
    class StopThread extends Thread {


        public StopThread() {
        }

        public void run() {

            // 데이터 송신
            try {

                String OutData = "p\n";
                byte[] data = OutData.getBytes();
                OutputStream output = socket.getOutputStream();
                output.write(data);
                Log.d(TAG, "Stop\\n COMMAND 송신");

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }
    class ParkThread extends Thread {


        public ParkThread() {
        }

        public void run() {

            // 데이터 송신
            try {

                String OutData = "i\n";
                byte[] data = OutData.getBytes();
                OutputStream output = socket.getOutputStream();
                output.write(data);
                Log.d(TAG, "parking\\n ");

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    // fixme: Socket Connect.
    class ConnectThread extends Thread {
        String hostname;

        public ConnectThread(String addr) {
            hostname = addr;
        }

        public void run() {
            try { //클라이언트 소켓 생성

                int port = 50000;
                socket = new Socket(hostname, port);
                Log.d(TAG, "Socket 생성, 연결.");


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        InetAddress addr = socket.getInetAddress();
                        String tmp = addr.getHostAddress();
                        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                        btnStop.setEnabled(true);
                        btnParking.setEnabled(true);
                        ConnButton.setEnabled(false);
                        btnMove.setEnabled(true);
                    }
                });


            } catch (UnknownHostException uhe) { // 소켓 생성 시 전달되는 호스트(www.unknown-host.com)의 IP를 식별할 수 없음.

                Log.e(TAG, " 생성 Error : 호스트의 IP 주소를 식별할 수 없음.(잘못된 주소 값 또는 호스트 이름 사용)");
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error : 호스트의 IP 주소를 식별할 수 없음.(잘못된 주소 값 또는 호스트 이름 사용)", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException ioe) { // 소켓 생성 과정에서 I/O 에러 발생.

                Log.e(TAG, " 생성 Error : 네트워크 응답 없음");
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error : 네트워크 응답 없음", Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (SecurityException se) { // security manager에서 허용되지 않은 기능 수행.

                Log.e(TAG, " 생성 Error : 보안(Security) 위반에 대해 보안 관리자(Security Manager)에 의해 발생. (프록시(proxy) 접속 거부, 허용되지 않은 함수 호출)");
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error : 보안(Security) 위반에 대해 보안 관리자(Security Manager)에 의해 발생. (프록시(proxy) 접속 거부, 허용되지 않은 함수 호출)", Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (IllegalArgumentException le) { // 소켓 생성 시 전달되는 포트 번호(65536)이 허용 범위(0~65535)를 벗어남.

                Log.e(TAG, " 생성 Error : 메서드에 잘못된 파라미터가 전달되는 경우 발생.(0~65535 범위 밖의 포트 번호 사용, null 프록시(proxy) 전달)");
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), " Error : 메서드에 잘못된 파라미터가 전달되는 경우 발생.(0~65535 범위 밖의 포트 번호 사용, null 프록시(proxy) 전달)", Toast.LENGTH_SHORT).show();
                    }
                });


            }


        }
    }

    @Override
    protected void onStop() {  //앱 종료시
        super.onStop();
        try {
            socket.close(); //소켓을 닫는다.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}






