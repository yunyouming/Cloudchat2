package com.ela.eoswallet.elawalleta;

import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.widget.ListView;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import android.widget.AdapterView;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.exceptions.ElastosException;

import android.os.IBinder;
import android.os.Looper;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.content.ServiceConnection;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteCursor;
import android.content.ContentValues;

import android.database.Cursor;

public class Fragmentmessage extends Fragment {
    private ListView listView;
    private List<FriendInfo> lista;
    private SQLiteDatabase sqldb;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_message, container,false);
        listView = (ListView)view.findViewById(R.id.lv);
        List<Map<String, Object>> list=getData();
        listView.setAdapter(new ListViewAdapter(getActivity(), list));
        return view;
    }
    public List<Map<String, Object>> getData(){
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
         sqldb = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);
         //List<Map<String,Object>> sendlista = getdblist();


       // for ( Map<String,Object> senderval : sendlista) {
            Cursor result = sqldb.query("messagelist", new String[]{"id,sender,content,reciver,curtime,yn"}, null, null, null, null, "id desc", "0,1");

            System.out.println("开始查询：");
            //判断游标是否为空
            if (result.moveToFirst()) {
                System.out.println("开始查询：");
                while (!result.isAfterLast()) {
                    String sender = result.getString(result.getColumnIndex("sender"));
                    String content = result.getString(result.getColumnIndex("content"));
                    String reciver = result.getString(result.getColumnIndex("reciver"));
                    String curtime = result.getString(result.getColumnIndex("curtime"));
                    String yn = result.getString(result.getColumnIndex("yn"));
                    System.out.println("账户：" + sender);
                    System.out.println("内容：" + content);
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("image", R.drawable.avatar);
                    Carrier mycarrier = Carrier.getInstance();
                    try {
                        if (mycarrier.getUserId().equals(sender)) {
                            map.put("friendname", reciver);
                        } else {
                            map.put("friendname", sender);
                        }
                    } catch (ElastosException e) {
                        e.printStackTrace();
                    }
                    map.put("curtime", curtime.substring(10));
                    map.put("info", content);
                    map.put("yn", yn);
                    list.add(map);
                    result.moveToNext();
                }
            }
        //}
        return list;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick( AdapterView<?> parent, View view, int position, long id) {
                    try {
                        HashMap friendInfo = ((HashMap) listView.getItemAtPosition(position));
                        System.out.println("数据库："+friendInfo.get("friendname"));
                        startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("friendId", friendInfo.get("friendname").toString()));
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });

    }
    @Override
    public void onResume() {
        System.out.println("dsadsa");
        super.onResume();
        refresh();
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            System.out.println("dsaaaaaaaaaaaaaaaaaaadsa");
            refresh();
        } else {
        }
    }
    protected void refresh(){
        System.out.println("dsadsa");
        List<Map<String, Object>> list=getData();
        System.out.println("dsadsaaaaa");
        listView.setAdapter(new ListViewAdapter(getActivity(), list));
        System.out.println("dsadsaooooo");
    }

    //private List<Map<String, Object>> sendlist ;
    Map<String, Object> sendlist = new HashMap<String, Object>();
    public List<Map<String, Object>>  getdblist(){
        sqldb = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);
        Cursor result = sqldb.query ("messagelist",new String[]{"id,sender"},null,null,"sender",null,"id desc");
        List<Map<String, Object>> lista=new ArrayList<Map<String,Object>>();
        if(result.moveToFirst()) {
            while (!result.isAfterLast()) {
                String sender = result.getString(result.getColumnIndex("sender"));
                System.out.println("发送者："+sender);
                sendlist.put("sender",sender);
                lista.add(sendlist);
            }
        }
        return lista;
    }
}