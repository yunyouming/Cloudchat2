package com.ela.eoswallet.elawalleta;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ela.eoswallet.elawalleta.R;
import com.ela.eoswallet.elawalleta.robot.RobotService;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.CarrierHandler;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.common.TestOptions;
import org.elastos.carrier.exceptions.ElastosException;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Carrier mycarrier;
    private CarrierHandler handler;
    private static Context context;
    private TestOptions options;
    private TextView tx_version;
    private TextView tx_address;
    private TextView tx_nodeid;
    private TextView tx_userid;
    private TextView tx_received;
    private TextView tx_from;
    private EditText edt_addfriend;
    private EditText edt_tofriend;
    private EditText edt_sendMessage;
    private Button btn_addfriend;
    private Button btn_sendMessage;
    private ProgressDialog pd;

    private LinearLayout rl_friends;
    private List<FriendInfo> myFriends;

    private RobotService myService;
    private Messenger messenger;
    /**
     *
     *
     */
    private String getAppPath() {
        return getFilesDir().getAbsolutePath() + "-robot";
    }

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
    class MyHandler extends Handler {
        MyHandler () {
            super(Looper.getMainLooper());
        }
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "handleMessage = " + msg);
            switch (msg.what) {
                case 5:
                    String from = msg.getData().getString("from");
                    String msgBody = msg.getData().getString("message");
                    tx_from.setText(from);
                    tx_received.setText(msgBody);
                    break;
                case 4:
                    String from1 = msg.getData().getString("from");
                    String hello = msg.getData().getString("hello");
                    Log.i(TAG, "from1 = " + from1);
                    Log.i(TAG, "hello = " + hello);
                default:
                    super.handleMessage(msg);
            }

        }
    }
    Messenger mMessenger = new Messenger(new MyHandler());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, RobotService.class);
        bindService( intent , connection, Context.BIND_AUTO_CREATE);



        rl_friends = (LinearLayout) findViewById(R.id.rl_friends);
        context = getApplicationContext();
        options = new TestOptions(getAppPath());
        mycarrier = Carrier.getInstance();


        Log.i(TAG, "getVersion = " + mycarrier.getVersion());
        try{
            Log.i(TAG, "getAddress = " + mycarrier.getAddress());
            Log.i(TAG, "getNodeId = " + mycarrier.getNodeId());
            Log.i(TAG, "getUserId = " + mycarrier.getUserId());
            Log.i(TAG, "getFriends");
            //tx_version = (TextView) findViewById(R.id.tx_version);
            tx_address = (TextView) findViewById(R.id.tx_address);
           // tx_nodeid = (TextView) findViewById(R.id.tx_nodeid);
            tx_userid = (TextView) findViewById(R.id.tx_userid);

            //tx_version.setText(mycarrier.getVersion());
            tx_address.setText(mycarrier.getAddress());
           // tx_nodeid.setText(mycarrier.getNodeId());
            tx_userid.setText(mycarrier.getUserId());

        }catch(ElastosException e){
            e.printStackTrace();
        }
        refresh();
        tx_received = (TextView) findViewById(R.id.tx_received);
        tx_from = (TextView) findViewById(R.id.tx_from);
        edt_addfriend = (EditText) findViewById(R.id.edt_addfriend);
        edt_tofriend = (EditText) findViewById(R.id.edt_tofriend);
        edt_sendMessage = (EditText) findViewById(R.id.edt_sendMessage);
        btn_addfriend = (Button) findViewById(R.id.btn_addfriend);
        btn_sendMessage = (Button) findViewById(R.id.btn_sendMessage);
        /*edt_addfriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });*/
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

        btn_sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_tofriend.getText().length() == 0){
                    Toast.makeText(getApplicationContext(), "请填写发送地址", Toast.LENGTH_SHORT).show();
                }else if(edt_sendMessage.getText().length() == 0){
                    Toast.makeText(getApplicationContext(), "请填写发送内容", Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage();
                }
            }
        });

    }

    /**
     *
     *
     */

    private void addfriend(){
        Log.i(TAG,"addfriend id = "+edt_addfriend.getText().toString());
        String address = edt_addfriend.getText().toString();
        String checkMessage = "Hello";
        String userId = Carrier.getIdFromAddress(address);
        try{
            if( !Carrier.isValidAddress(address)){
                Toast.makeText(getApplicationContext(), "无效的地址", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!mycarrier.isFriend(userId)){
                mycarrier.addFriend(address,checkMessage);
                refresh();
            }else{
                Toast.makeText(getApplicationContext(), "好友已添加", Toast.LENGTH_SHORT).show();
                refresh();
            }
        }catch( ElastosException e ){
            e.printStackTrace();
        }


        //boolean isFriend(String userId);
        //void addFriend(String address, String hello)
    }



    private void sendMessage(){
        try {
            Message msg = Message.obtain(null, 8);
            Bundle data = new Bundle();
            data.putString("to", edt_tofriend.getText().toString());
            data.putString("message", edt_sendMessage.getText().toString());
            msg.setData(data);
            msg.replyTo = mMessenger;
            messenger.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * Refresh friends lists.
     *
     */

    private void refresh(){
        try{
            myFriends =  mycarrier.getFriends();
            Log.i(TAG,"myFriends  = " + myFriends);
            for ( FriendInfo friend : myFriends) {
                Log.i(TAG,"myFriend  = " + friend.getUserId());
                RelativeLayout view = new RelativeLayout(this);
                TextView tv1 = new TextView(this);
                tv1.setText(friend.getUserId());
                view.addView(tv1);
                rl_friends.addView(view);
            }
        }catch( ElastosException e ){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}