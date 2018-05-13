package com.ela.eoswallet.elawalleta;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.os.Message;
import android.os.Messenger;
import android.os.Handler;

import android.os.IBinder;
import android.os.Looper;
import com.ela.eoswallet.elawalleta.robot.RobotService;
import com.ela.eoswallet.elawalleta.R;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.exceptions.ElastosException;

import org.elastos.carrier.exceptions.ElastosException;

public class SplashActivity extends AppCompatActivity {
    private static Context context;
    private static final int sleepTime = 1000;
    private RelativeLayout rootLayout;
    private Messenger messenger;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            startActivity(new Intent(SplashActivity.this, ElawalletaActivity.class));
            finish();

        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
           /* startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();*/
            messenger = new Messenger(service);
            try {
                Message msg = Message.obtain(null, 2);
                msg.replyTo = mMessenger;
                messenger.send(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(new Intent(SplashActivity.this, ElawalletaActivity.class));
            finish();
        }
    };
    class MyHandler extends Handler {
        MyHandler () {
            super(Looper.getMainLooper());
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 5:
                    String from = msg.getData().getString("from");
                    String msgBody = msg.getData().getString("message");
                  //  tx_from.setText(from);
                  //  tx_received.setText(msgBody);
                    break;
                case 4:
                    String from1 = msg.getData().getString("from");
                    String hello = msg.getData().getString("hello");
                    //Log.i(TAG, "from1 = " + from1);
                   // Log.i(TAG, "hello = " + hello);
                default:
                    super.handleMessage(msg);
            }

        }
    }
    Messenger mMessenger = new Messenger(new MyHandler());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(1500);
        rootLayout.startAnimation(animation);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                }
                if(!islogin()){
                    context = getApplicationContext();
                    Intent intent = new Intent(context, RobotService.class);
                    bindService( intent , connection, Context.BIND_AUTO_CREATE);
                }
            }
        }).start();
    }

    private boolean islogin(){
        boolean check = false;
        return check;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}