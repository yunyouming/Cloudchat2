package com.ela.eoswallet.elawalleta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Button;
import com.ela.eoswallet.elawalleta.R;


import android.os.Bundle;
import android.os.Handler;
import java.text.SimpleDateFormat;
import android.os.IBinder;
import android.os.Looper;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.content.ServiceConnection;

import java.util.Date;

import android.os.Message;
import android.os.Messenger;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.exceptions.ElastosException;

import com.ela.eoswallet.elawalleta.robot.RobotService;

import android.database.Cursor;

public class UserInfoActivity extends AppCompatActivity {
    private TextView user_name;
    private TextView user_remark;
    private Button btn_msg;
    private RobotService myService;
    private Messenger messenger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Intent intent = new Intent(this, RobotService.class);
        bindService( intent , connection, Context.BIND_AUTO_CREATE);
        user_name = (TextView) findViewById(R.id.user_name);
        user_name.setText(this.getIntent().getStringExtra("friendId"));
        btn_msg=(Button)findViewById(R.id.btn_msg);
        user_remark = (TextView) findViewById(R.id.tv_rmarke);
        user_remark.setText(this.getIntent().getStringExtra("curremark"));
/*
        SQLiteDatabase   sqldbc = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);
        Cursor result = sqldbc.query ("firendlist",new String[]{"remark"},"userid=?",new String[]{this.getIntent().getStringExtra("friendId")},null,null,"id desc","0,1");


            while(!result.isAfterLast()){
                String remarks = result.getString(result.getColumnIndex("remark"));
               // user_remark.setText(remarks);
            }

        result.close();
        sqldbc.close();
    */


        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_remark.getText().equals("")){
                    startActivity(new Intent(UserInfoActivity.this, EditremarkActivity.class).putExtra("friendId", user_name.getText()).putExtra("curremark",""));

                }else{
                    startActivity(new Intent(UserInfoActivity.this, EditremarkActivity.class).putExtra("friendId", user_name.getText()).putExtra("curremark",user_remark.getText()));

                }

            }
        });
        btn_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserInfoActivity.this, ChatActivity.class).putExtra("friendId", user_name.getText()));
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
                        //                            curtime.format(date);
                        ContentValues values = new ContentValues();
                        values.put("sender", from);
                        values.put("content", msgBody);
                        values.put("yn", 0);
                        values.put("curtime",curtime.format(date));
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
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
    public void back(View view){
        finish();
    }

}
