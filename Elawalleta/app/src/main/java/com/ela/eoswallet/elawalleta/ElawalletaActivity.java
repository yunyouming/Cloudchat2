package com.ela.eoswallet.elawalleta;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.Toast;
import java.util.List;

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
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import org.elastos.carrier.Carrier;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.exceptions.ElastosException;
import java.util.Date;
import com.ela.eoswallet.elawalleta.robot.RobotService;

public class ElawalletaActivity extends AppCompatActivity {
    private FragmentTransaction mFragmentTransaction;//fragment事务
    private FragmentManager mFragmentManager;//fragment管理者
    private ContactsFragment fragmentcontact;

    private Fragmentmessage fragmentmessage;
    private Fragmentquota fragmentquota;
    private Fragmentdapps fragmentdapps;
    private Fragmentwallet fragmentwallet;

    private List<FriendInfo> lista;
    private Mydb mydb;
    private RobotService myService;
    private Messenger messenger;
    private Mydb dbhelper;
    private SQLiteDatabase sqldb;
    private String iswallet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elawalleta);
        iswallet = this.getIntent().getStringExtra("iswallet");
        barinit();
        Intent intent = new Intent(this, RobotService.class);
        bindService( intent , connection, Context.BIND_AUTO_CREATE);
        //判断数据库
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);
        String sqla="create table if not exists messagelist ("
                + "id integer primary key autoincrement,"
                + "sender text, "
                + "content text, "
                + "yn integer,"
                + "curtime datetime,"
                + "reciver text)";
        String sqlb="create table if not exists firendlist("
                + "id integer primary key autoincrement, "
                + "userid text, "
                + "remark text,"
                + "nickname text)";
        String sqlc ="create table if not exists newfirendlist("
                + "id integer primary key autoincrement,"
                + "userid text,"
                + "yn integer,"
                + "hello text,"
                + "nickname text)";
        db.execSQL(sqla);
        db.execSQL(sqlb);
        db.execSQL(sqlc);
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
                            curtime.format(date);
                            System.out.println("单钱时间："+curtime.format(date));
                            System.out.println("当前时间2："+curtime.format(date));
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
    private void setdefaultfragment(){
        Fragmentmessage f1 = new Fragmentmessage();
        getFragmentManager().beginTransaction().replace(R.id.splash_root, f1).commit();
    }
    private void barinit(){
        BottomNavigationBar  bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_SHIFTING)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigationBar
                .setActiveColor("#2e2e2e")//选中颜色 图标和文字
                .setInActiveColor("#8e8e8e")//默认未选择颜色
                .setBarBackgroundColor("#ECECEC");//默认背景色
        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.bottom_bar_message_icon,"新消息").setInactiveIcon(ContextCompat.getDrawable(this,R.drawable.bottom_bar_unmessage_icon)))
                .addItem(new BottomNavigationItem(R.drawable.bottom_bar_contact_icon,"联系人").setInactiveIcon(ContextCompat.getDrawable(this,R.drawable.bottom_bar_uncontact_icon)))
                .addItem(new BottomNavigationItem(R.drawable.bottom_bar_quota_icon,"行情").setInactiveIcon(ContextCompat.getDrawable(this,R.drawable.bottom_bar_unquota_icon)))
                .addItem(new BottomNavigationItem(R.drawable.bottom_bar_dapps_icon,"应用").setInactiveIcon(ContextCompat.getDrawable(this,R.drawable.bottom_bar_undapps_icon)))
                .addItem(new BottomNavigationItem(R.drawable.bottom_bar_wallet_icon,"钱包").setInactiveIcon(ContextCompat.getDrawable(this,R.drawable.bottom_bar_unwallet_icon)))
                .setFirstSelectedPosition(0)
                .initialise();
        //setdefaultfragment();
        //
       // if(iswallet.equals(null)){

            mFragmentManager = getFragmentManager();
            FragmentTransaction mFragmentTransactiona = mFragmentManager.beginTransaction();
            fragmentmessage = new Fragmentmessage();
            mFragmentTransactiona.add(R.id.splash_root, fragmentmessage);
            mFragmentTransactiona.commit();
      //  }else{
       //     mFragmentManager = getFragmentManager();
       //     FragmentTransaction mFragmentTransactiona = mFragmentManager.beginTransaction();
       //     fragmentwallet = new Fragmentwallet();
      //      mFragmentTransactiona.add(R.id.splash_root, fragmentwallet);
      //      mFragmentTransactiona.commit();
      //  }




        bottomNavigationBar
                .setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(int position) {
                        mFragmentManager = getFragmentManager();
                        //开启事务
                        mFragmentTransaction = mFragmentManager.beginTransaction();
                        //显示之前将所有的fragment都隐藏起来,在去显示我们想要显示的fragment
                        hideFragment(mFragmentTransaction);
                        switch (position){
                            case 0:
                                //Toast.makeText(ElawalletaActivity.this,"这是新消息",Toast.LENGTH_SHORT).show();
                                if (fragmentmessage == null) {
                                    fragmentmessage = new Fragmentmessage();
                                    mFragmentTransaction.add(R.id.splash_root, fragmentmessage);

                                } else {
                                   mFragmentTransaction.show(fragmentmessage);
                                   // mFragmentTransaction.replace(R.id.splash_root,fragmentmessage).commit();

                                }
                                break;
                            case 1:
                               // Toast.makeText(ElawalletaActivity.this,"这是联系人",Toast.LENGTH_SHORT).show();
                                if (fragmentcontact == null) {
                                    //fragmentcontact = new Fragmentcontact();
                                   fragmentcontact = new ContactsFragment();
                                    mFragmentTransaction.add(R.id.splash_root, fragmentcontact);
                                } else {
                                    mFragmentTransaction.show(fragmentcontact);
                                   // mFragmentTransaction.replace(R.id.splash_root,fragmentcontact).commit();
                                }
                                break;
                            case 2:
                               Toast.makeText(ElawalletaActivity.this,"行情研发中",Toast.LENGTH_SHORT).show();
                                if (fragmentquota == null) {
                                    fragmentquota = new Fragmentquota();
                                    mFragmentTransaction.add(R.id.splash_root, fragmentquota);
                                } else {
                                    mFragmentTransaction.show(fragmentquota);
                                   // mFragmentTransaction.replace(R.id.splash_root,fragmentquota).commit();
                                }
                                break;
                            case 3:
                               Toast.makeText(ElawalletaActivity.this,"应用研发中",Toast.LENGTH_SHORT).show();
                                if (fragmentdapps == null) {
                                    fragmentdapps = new Fragmentdapps();
                                    mFragmentTransaction.add(R.id.splash_root, fragmentdapps);
                                } else {
                                    mFragmentTransaction.show(fragmentdapps);
                                    //mFragmentTransaction.replace(R.id.splash_root,fragmentdapps).commit();
                                }
                                break;
                            case 4:
                               Toast.makeText(ElawalletaActivity.this,"钱包研发中",Toast.LENGTH_SHORT).show();
                                if (fragmentwallet == null) {
                                    fragmentwallet = new Fragmentwallet();
                                    mFragmentTransaction.add(R.id.splash_root, fragmentwallet);
                                } else {
                                   mFragmentTransaction.show(fragmentwallet);
                                   // mFragmentTransaction.replace(R.id.splash_root,fragmentwallet).commit();
                                }
                                break;
                        }
                        mFragmentTransaction.commit();
                    }
                    @Override
                    public void onTabUnselected(int position) {

                    }

                    @Override
                    public void onTabReselected(int position) {

                    }
                });
    }
    private void hideFragment(FragmentTransaction fragmentTransaction) {
        //如果此fragment不为空的话就隐藏起来
        if (fragmentcontact != null) {
            fragmentTransaction.hide(fragmentcontact);
        }
        if (fragmentdapps != null) {
           fragmentTransaction.hide(fragmentdapps);
        }
        if (fragmentmessage != null) {
           fragmentTransaction.hide(fragmentmessage);
        }
        if (fragmentquota != null) {
            fragmentTransaction.hide(fragmentquota);
        }
        if (fragmentwallet != null) {
            fragmentTransaction.hide(fragmentwallet);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
    @Override
    public void onResume() {
        super.onResume();
    }
}
