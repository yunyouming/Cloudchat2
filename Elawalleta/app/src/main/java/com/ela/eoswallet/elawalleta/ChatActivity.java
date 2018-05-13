package com.ela.eoswallet.elawalleta;

import java.util.ArrayList;
import java.util.List;

import android.R.string;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ListAdapter;

import android.widget.Toast;
import org.elastos.carrier.Carrier;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.exceptions.ElastosException;

import java.util.Date;
import android.content.Context;
import android.content.Intent;
import java.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.os.Messenger;
import android.os.IBinder;
import android.os.Looper;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.text.TextUtils;
import com.ela.eoswallet.elawalleta.robot.RobotService;

public class ChatActivity extends Activity {

    private ListView msgListView;

    private EditText inputText;

    private Button send;
    private Messenger messenger;

    private MsgAdapter adapter;
    private SQLiteDatabase sqldb;

    private List<Msg> msgList = new ArrayList<Msg>();
    private String frienduserid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置窗口没有标题栏
        setContentView(R.layout.activity_chat);
        jiating();
         frienduserid = this.getIntent().getStringExtra("friendId");
        final Carrier mycarrier = Carrier.getInstance();
        sqldb = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);
        ContentValues valb = new ContentValues();
        valb.put("yn",1);
        updatedb(valb,frienduserid);
       //判断游标是否为空
        Cursor result = sqldb.query ("messagelist",new String[]{"sender,content"},"sender=? or reciver=?",new String[]{frienduserid,frienduserid},null,null,null);
        if(result.moveToFirst()){
            try{
                String myuserid = mycarrier.getUserId();
                while(!result.isAfterLast()){
                    String sender = result.getString(result.getColumnIndex("sender"));
                    String content = result.getString(result.getColumnIndex("content"));
                    if(sender.equals(myuserid)){
                        Msg msg1 = new Msg(content,Msg.SENT);
                        System.out.println("SEND");
                        msgList.add(msg1);
                    }else{
                        Msg msg1 = new Msg(content,Msg.RECEIVED);
                        System.out.println("RECEIVED");
                        msgList.add(msg1);
                    }
                    result.moveToNext();
                }
            }catch(ElastosException e){
                e.getErrorCode();
            }
        }
        adapter = new MsgAdapter(ChatActivity.this, R.layout.msg_item, msgList);
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        final Button btnmore = (Button) findViewById(R.id.btn_more);
        final LinearLayout bottommenu = (LinearLayout) findViewById(R.id.bottommenu);
        final ImageView zhuanzhang = (ImageView) findViewById(R.id.zhuanzhang);
        zhuanzhang.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(ChatActivity.this, ElawalletaActivity.class).putExtra("iswallet","1"));

                Toast.makeText(ChatActivity.this,"转账研发中",Toast.LENGTH_SHORT).show();
                //Toast.makeText(this,"转账研发中")
            }
        });
        btnmore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               Integer k =  bottommenu.getVisibility();
                if(k.equals(0)){
                    bottommenu.setVisibility(View.GONE);
                }else{
                    bottommenu.setVisibility(View.VISIBLE);
                }
            }
        });

        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence)) {
                    btnmore.setVisibility(View.GONE);
                    send.setVisibility(View.VISIBLE);
                } else {
                    send.setVisibility(View.GONE);
                    btnmore.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);
        send.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if(!"".equals(content)){
                    //保存数据到数据库

                    SQLiteDatabase sqldb = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);
                    ContentValues values = new ContentValues();
                    Carrier mycarriera = Carrier.getInstance();
                    try {
                        mycarrier.sendFriendMessage(frienduserid,content);
                        Date date = new Date();
                        SimpleDateFormat curtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                       // curtime.format(date);
                        values.put("curtime", curtime.format(date));
                        values.put("sender", mycarriera.getUserId());
                        values.put("content", content);
                        values.put("yn", 1);
                        values.put("curtime", curtime.format(date));
                        values.put("reciver", frienduserid);
                        sqldb.insert("messagelist", null, values);
                    }catch(ElastosException e){
                        e.printStackTrace();
                    }
                    Msg msg = new Msg(content, Msg.SENT);
                    msgList.add(msg);
                    adapter.notifyDataSetChanged();//有新消息时，刷新ListView中的显示
                    msgListView.setSelection(msgList.size());//将ListView定位到最后一行
                    inputText.setText("");//清空输入框的内容
                }
            }
        });
    }

    private void jiating(){
        Intent intent = new Intent(this, RobotService.class);
        bindService( intent , connection, Context.BIND_AUTO_CREATE);

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
                        SQLiteDatabase sqldb = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);
                        ContentValues values = new ContentValues();


                        Date date = new Date();
                        SimpleDateFormat curtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                        values.put("sender", from);
                        values.put("content", msgBody);
                        values.put("yn", 1);
                        values.put("curtime", curtime.format(date));
                        values.put("reciver",reveiverid);
                        sqldb.insert("messagelist", null, values);

                        Msg msga = new Msg(msgBody, Msg.RECEIVED);
                        msgList.add(msga);
                        adapter.notifyDataSetChanged();//有新消息时，刷新ListView中的显示
                        msgListView.setSelection(msgList.size());//将ListView定位到最后一行

                        break;
                    case 4:
                        String from1 = msg.getData().getString("from");
                        String hello = msg.getData().getString("hello");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }


    public class Msg{

        public static final int RECEIVED = 0;//收到一条消息

        public static final int SENT = 1;//发出一条消息

        private String  content;//消息的内容

        private int type;//消息的类型

        public  Msg(String content,int type){
            this.content = content;
            this.type = type;
        }

        public String getContent(){
            return content;
        }

        public int getType(){
            return type;
        }
    }

    public class MsgAdapter extends ArrayAdapter<Msg>{
        private int resourceId;

        public MsgAdapter(Context context, int textViewresourceId, List<Msg> objects) {
            super(context, textViewresourceId, objects);
            resourceId = textViewresourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Msg msg = getItem(position);
            View view;
            ViewHolder viewHolder;

            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.leftLayout = (LinearLayout)view.findViewById(R.id.left_layout);
                viewHolder.rightLayout = (LinearLayout)view.findViewById(R.id.right_Layout);
                viewHolder.leftMsg = (TextView)view.findViewById(R.id.left_msg);
                viewHolder.rightMsg = (TextView)view.findViewById(R.id.right_msg);
                view.setTag(viewHolder);
            }else{
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            if(msg.getType()==Msg.RECEIVED){
                //如果是收到的消息，则显示左边消息布局，将右边消息布局隐藏
                viewHolder.leftLayout.setVisibility(View.VISIBLE);
                viewHolder.rightLayout.setVisibility(View.GONE);
                viewHolder.leftMsg.setText(msg.getContent());
            }else if(msg.getType()==Msg.SENT){
                //如果是发出去的消息，显示右边布局的消息布局，将左边的消息布局隐藏
                viewHolder.rightLayout.setVisibility(View.VISIBLE);
                viewHolder.leftLayout.setVisibility(View.GONE);
                viewHolder.rightMsg.setText(msg.getContent());
            }
            return view;
        }
        class ViewHolder{
            LinearLayout leftLayout;
            LinearLayout rightLayout;
            TextView leftMsg;
            TextView rightMsg;
        }
    }
    public void updatedb(ContentValues vala,String frienduserid){
        SQLiteDatabase sqldba = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);
        sqldba.update("messagelist", vala, "sender=? or reciver=?", new String[] { frienduserid,frienduserid});
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
    public void back(View view){
        finish();
    }
}
