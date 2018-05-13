package com.ela.eoswallet.elawalleta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import android.os.IBinder;
import android.os.Looper;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.content.ServiceConnection;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ela.eoswallet.elawalleta.R;
import android.content.Intent;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.exceptions.ElastosException;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import java.text.SimpleDateFormat;

import android.os.Message;
import android.os.Messenger;
import java.util.Date;
import android.content.ContentValues;
import com.ela.eoswallet.elawalleta.robot.RobotService;

public class AddFriendsActivity extends AppCompatActivity{
    private String TAG="AddFriendsActivity";
    private Carrier mycarrier;
    private EditText edt_addfriend;
    private Button btn_addfriend;
    private EditText edt_checkMessage;
    private ImageView btnsan;


    private RobotService myService;
    private Messenger messenger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
        edt_addfriend = (EditText) findViewById(R.id.edt_addfriend);
        btn_addfriend = (Button) findViewById(R.id.btn_addfriend);
        edt_checkMessage = (EditText) findViewById(R.id.edt_checkMessage);
        btnsan = (ImageView) findViewById(R.id.btn_sancode);

        Intent intent = new Intent(this, RobotService.class);
        bindService( intent , connection, Context.BIND_AUTO_CREATE);

        mycarrier = Carrier.getInstance();
        try{
            Log.i(TAG, "getAddress = " + mycarrier.getAddress());
            Log.i(TAG, "getNodeId = " + mycarrier.getNodeId());
            Log.i(TAG, "getUserId = " + mycarrier.getUserId());
            Log.i(TAG, "getFriends");
        }catch(ElastosException e){
            e.printStackTrace();
        }

        btn_addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_addfriend.getText().length() == 0){
                    Log.i(TAG,"addfriend id = "+edt_addfriend.getText().toString());
                    Toast.makeText(getApplicationContext(), "请填写好友地址", Toast.LENGTH_SHORT).show();
                }else{
                    addfriend();
                    Log.i(TAG,"addfriend id = "+edt_addfriend.getText().toString());
                }
            }
        });
        btnsan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(AddFriendsActivity.this);
                // 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setCaptureActivity(ScanActivity.class);
                integrator.setPrompt("请扫描二维码"); //底部的提示文字，设为""可以置空
                integrator.setCameraId(0); //前置或者后置摄像头
                integrator.setBeepEnabled(false); //扫描成功的「哔哔」声，默认开启
                integrator.setBarcodeImageEnabled(true);//是否保留扫码成功时候的截图
                integrator.initiateScan();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            String result = scanResult.getContents();
            edt_addfriend = (EditText) findViewById(R.id.edt_addfriend);
            edt_addfriend.setText(result);
           // Log.e("HYN", result);
            //Toast.makeText(AddFriendsActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }
    /**
     *
     *
     */
    private String userId;
    private void addfriend(){
        Log.i(TAG,"addfriend id = "+edt_addfriend.getText().toString());
        String address = edt_addfriend.getText().toString();
        String checkMessage = edt_checkMessage.getText().toString();
        userId = Carrier.getIdFromAddress(address);
        try{
            if( !Carrier.isValidAddress(address)){
                Toast.makeText(getApplicationContext(), "无效的地址", Toast.LENGTH_SHORT).show();
            }else if(!mycarrier.isFriend(userId)){
                mycarrier.addFriend(address,checkMessage);



                Toast.makeText(getApplicationContext(), "好友请求发送成功", Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(1000);
                    finish();
                } catch (InterruptedException e) {
                }
            }else{
                Toast.makeText(getApplicationContext(), "好友已添加", Toast.LENGTH_SHORT).show();
               // refresh();
            }
        }catch( ElastosException e ){
            e.printStackTrace();
        }
        //boolean isFriend(String userId);
        //void addFriend(String address, String hello)
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
