package com.ela.eoswallet.elawalleta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import com.ela.eoswallet.elawalleta.R;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.exceptions.ElastosException;

import java.util.Date;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;



import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import java.text.SimpleDateFormat;
import android.os.IBinder;
import android.os.Looper;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.content.ServiceConnection;

import android.content.ContentValues;

import android.os.Message;
import android.os.Messenger;
import org.elastos.carrier.exceptions.ElastosException;

import com.ela.eoswallet.elawalleta.robot.RobotService;


public class MyInfoActivity extends AppCompatActivity {
    private TextView myuserid;
    private TextView myaddress;
    private TextView username;
    private Button btn_msg;
    private ImageView ivTwoCode;
    private RobotService myService;
    private Messenger messenger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        Intent intent = new Intent(this, RobotService.class);
        bindService( intent , connection, Context.BIND_AUTO_CREATE);
        try{
            Carrier mycarrier = Carrier.getInstance();
            String myuserida = mycarrier.getUserId();
            String myaddressa = mycarrier.getAddress();
            String myusername = mycarrier.getSelfInfo().getName();
            System.out.println("我的昵称"+myusername);
            username = (TextView) findViewById(R.id.user_name);
            if(myusername.equals("")){
                username.setText("我的昵称");
            }else{
                username.setText(myusername);
            }

            myuserid = (TextView) findViewById(R.id.myuserid);
            myuserid.setText(myuserida);
            myaddress = (TextView) findViewById(R.id.myaddress);
            myaddress.setText(myaddressa);
            ivTwoCode = (ImageView) findViewById(R.id.iv_ercode);
            Bitmap bitmap = ZXingUtils.createQRImage(myaddressa, 100, 100);
            ivTwoCode.setImageBitmap(bitmap);
        }catch (ElastosException e){
            e.printStackTrace();;
        }
        TextView user_name = (TextView) findViewById(R.id.user_name);
        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyInfoActivity.this, EditinfoActivity.class));
            }
        });
    }


    class MyHandler extends Handler {
        MyHandler () {
            super(Looper.getMainLooper());
        }
        @Override
        public void handleMessage(Message msg) {
            Carrier mycarrier = Carrier.getInstance();
            try{
                String reveiverid = mycarrier.getUserId().toString();
                switch (msg.what) {
                    case 5:
                        String from = msg.getData().getString("from");
                        String msgBody = msg.getData().getString("message");
                        System.out.println("新的"+from);
                        SQLiteDatabase sqldb = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);
                        // sqldb =  dbhelper.getWritableDatabase();

                        Date date = new Date();
                        SimpleDateFormat curtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        ContentValues values = new ContentValues();
                        values.put("sender", from);
                        values.put("content", msgBody);
                        values.put("yn", 0);
                        values.put("curtime", curtime.format(date));
                        values.put("reciver",reveiverid);
                        sqldb.insert("messagelist", null, values);

                        break;
                    case 4:
                        String from1 = msg.getData().getString("from");
                        String hello = msg.getData().getString("hello");
                        System.out.println("来自："+from1);
                        System.out.println("加好友消息"+hello);
                        SQLiteDatabase sqldba = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);
                        ContentValues valuesa = new ContentValues();
                        valuesa.put("userid", from1);
                        valuesa.put("yn", 0);
                        valuesa.put("hello", hello);
                        sqldba.insert("newfirendlist", null, valuesa);
                    default:
                        super.handleMessage(msg);
                }
            }catch (ElastosException e){
                e.getErrorCode();
            }
        }
    }
    Messenger mMessenger = new Messenger(new MyHandler());
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            messenger = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);
            try {
                Message msg = Message.obtain(null, 2);
                msg.replyTo = mMessenger;
                messenger.send(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };




    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
    protected void refresh(){
        try{
            Carrier mycarrier = Carrier.getInstance();
            String myuserida = mycarrier.getUserId();
            String myaddressa = mycarrier.getAddress();
            String myusername = mycarrier.getSelfInfo().getName();
            System.out.println("我的昵称"+myusername);
            username = (TextView) findViewById(R.id.user_name);
            if(myusername.equals("")){
                username.setText("我的昵称");
            }else{
                username.setText(myusername);
            }
            ivTwoCode = (ImageView) findViewById(R.id.iv_ercode);
            Bitmap bitmap = ZXingUtils.createQRImage(myaddressa, 300, 300);
            ivTwoCode.setImageBitmap(bitmap);

            myuserid = (TextView) findViewById(R.id.myuserid);
            myuserid.setText(myuserida);
            myaddress = (TextView) findViewById(R.id.myaddress);
            myaddress.setText(myaddressa);
        }catch (ElastosException e){
            e.printStackTrace();;
        }
    }
    public void back(View view){
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
