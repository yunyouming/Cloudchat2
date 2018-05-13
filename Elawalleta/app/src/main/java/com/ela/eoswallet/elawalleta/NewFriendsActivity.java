package com.ela.eoswallet.elawalleta;

import android.hardware.camera2.params.LensShadingMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.ela.eoswallet.elawalleta.R;
import com.ela.eoswallet.elawalleta.NewFriendsListAdapter;

import java.util.Date;

import java.util.HashMap;
import java.util.Map;
import org.elastos.carrier.Carrier;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;

import android.os.Bundle;
import android.os.Handler;
import java.text.SimpleDateFormat;
import android.os.IBinder;
import android.os.Looper;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.content.ServiceConnection;


import android.os.Message;
import android.os.Messenger;
import org.elastos.carrier.exceptions.ElastosException;

import com.ela.eoswallet.elawalleta.robot.RobotService;


public class NewFriendsActivity extends AppCompatActivity {
    private ListView listView;
    private NewFriendsListAdapter newFriendsListAdapter;
    private RobotService myService;
    private Messenger messenger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends);
        listView = (ListView) findViewById(R.id.listview_list);

        Intent intent = new Intent(this, RobotService.class);
        bindService( intent , connection, Context.BIND_AUTO_CREATE);

        //Map<String, String> maps = new HashMap<String, String>();
        List<Map<String, String>> list=new ArrayList<Map<String,String>>();
        SQLiteDatabase sqldb = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);

        Cursor result = sqldb.query ("newfirendlist",new String[]{"userid,yn,hello"},null,null,null,null,null);
        if(result.moveToFirst()){
                while(!result.isAfterLast()){
                    String fuserid = result.getString(result.getColumnIndex("userid"));
                    String hello = result.getString(result.getColumnIndex("hello"));
                    Integer yn = result.getInt(result.getColumnIndex("yn"));
                    Map<String, String> maps = new HashMap<String, String>();
                    maps.put("userid", fuserid);
                    maps.put("yn",yn.toString());
                    maps.put("message",hello);
                    list.add(maps);
                    result.moveToNext();
                }
        }
        newFriendsListAdapter = new NewFriendsListAdapter(this, list);
        listView.setAdapter(newFriendsListAdapter);
        //listView.getOnItemClickListener(new AdapterView.OnItemClickListener())
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
    public void back(View view) {
        finish();
    }
}
