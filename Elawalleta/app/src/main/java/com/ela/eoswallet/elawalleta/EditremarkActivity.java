package com.ela.eoswallet.elawalleta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import com.ela.eoswallet.elawalleta.R;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.UserInfo;
import org.elastos.carrier.exceptions.ElastosException;
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
import java.util.Date;



public class EditremarkActivity extends AppCompatActivity{

    private TextView edt_userremark;
    private Button btn_saveuserremark;
    private EditText edt_checkMessage;
    private UserInfo userinfo;
    private RobotService myService;
    private Messenger messenger;
    private  String frienduserid;
    private  String oldremark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editremark);

        edt_userremark = (TextView) findViewById(R.id.edt_userremark);
         frienduserid = this.getIntent().getStringExtra("friendId");
        oldremark = this.getIntent().getStringExtra("curremark");
        System.out.println("值："+oldremark);
        edt_userremark.setText(String.valueOf(oldremark));
        Intent intent = new Intent(this, RobotService.class);
        bindService( intent , connection, Context.BIND_AUTO_CREATE);
        btn_saveuserremark = (Button) findViewById(R.id.btn_saveuserremark);

        btn_saveuserremark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_userremark.getText().length() == 0){
                    Toast.makeText(getApplicationContext(), "请填写备注", Toast.LENGTH_SHORT).show();
                }else{
                   // userinfo.setName(edt_userremark.getText().toString());
                    //更新数据库
                   // frienduserid
                    SQLiteDatabase sqldb = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);
                    ContentValues valb = new ContentValues();
                    valb.put("remark",edt_userremark.getText().toString());
                    updatedb(valb,frienduserid);

                    Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(1000);
                        finish();
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
    }

    public void updatedb(ContentValues vala,String frienduserid){
        SQLiteDatabase sqldba = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);
        sqldba.update("firendlist", vala, "userid=?", new String[] { frienduserid});
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
    public void back(View view){
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
