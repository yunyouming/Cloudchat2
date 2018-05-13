package com.ela.eoswallet.elawalleta;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import android.widget.AdapterView.OnItemClickListener;
import com.ela.eoswallet.elawalleta.R;

import org.elastos.carrier.Carrier;
import org.elastos.carrier.FriendInfo;
import org.elastos.carrier.exceptions.ElastosException;

import java.util.List;

import java.util.Date;


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


import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
public class ContactsFragment extends Fragment {
    private String TAG = "ContactsFragment";

    private LinearLayout contactsLists;
    private Carrier mycarrier;
    private List<FriendInfo> myFriends;
    private ListView listView;
    private RelativeLayout newFriends;
    private ImageView addFriend;
    private ImageView searchFriend;
    private RelativeLayout myinfo;
    private ContactListAdapter contactListAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mycarrier = Carrier.getInstance();
        initView();
        refresh();

    }
    protected void initView(){
        listView = (ListView) getView().findViewById(R.id.id_listview_list);
        newFriends = (RelativeLayout) getView().findViewById(R.id.newFriends);
        addFriend = (ImageView) getView().findViewById(R.id.addFriend);
        searchFriend = (ImageView) getView().findViewById(R.id.searchFriend);
        myinfo = (RelativeLayout) getView().findViewById(R.id.myinfo);

        myinfo.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getActivity(), MyInfoActivity.class));

            }
        });
        newFriends.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "新的好友", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), NewFriendsActivity.class));

            }
        });
        addFriend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(getActivity(), "添加好友", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), AddFriendsActivity.class));

            }
        });
        searchFriend.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "搜索好友", Toast.LENGTH_SHORT).show();

            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView<?> parent, View view, int position, long id) {
                try {
                        Map<String, String> friendInfo = ((Map<String, String>) listView.getItemAtPosition(position));
                        System.out.println("打印："+friendInfo.get("userid"));
                        startActivity(new Intent(getActivity(), UserInfoActivity.class).putExtra("friendId", friendInfo.get("userid")).putExtra("curremark",friendInfo.get("remark")));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
    protected void refresh(){
      /*  try{
            myFriends =  mycarrier.getFriends();
            Log.i(TAG,"myFriends  = " + myFriends);
            contactListAdapter = new ContactListAdapter(this.getActivity(), myFriends);
            listView.setAdapter(contactListAdapter);
        }catch( ElastosException e ){
            e.printStackTrace();
        }
        */
        dealfirend(getuserinfo());


        List<Map<String, String>> list=new ArrayList<Map<String,String>>();
        SQLiteDatabase sqldb = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);

        System.out.println("账号");
        System.out.println("账号sss");
        Cursor result = sqldb.query ("firendlist",null,null,null,null,null,null);
        if(result.moveToFirst()){
            while(!result.isAfterLast()){
                String fuserid = result.getString(result.getColumnIndex("userid"));
                String remark = result.getString(result.getColumnIndex("remark"));
                System.out.println("账号"+fuserid);
                System.out.println("账号sss"+remark);
                Map<String, String> maps = new HashMap<String, String>();
                maps.put("userid", fuserid);
                if(remark==null){
                    maps.put("remark",fuserid);
                }else{
                    maps.put("remark",remark);
                }
                list.add(maps);
                result.moveToNext();
            }
        }
        contactListAdapter = new ContactListAdapter(this.getActivity(), list);
        listView.setAdapter(contactListAdapter);
    }

    public void dealfirend(List<FriendInfo> infow){
        for ( FriendInfo infowa : infow) {
            String f = infowa.getUserId();
          ; if(!judge(f)){
                addfirend(f);
            }
        }
    }
    List<FriendInfo> infos;
    public List<FriendInfo> getuserinfo(){
        try{
           infos =  mycarrier.getFriends();
        }catch (ElastosException e){
            e.printStackTrace();
        }
        return  infos;
    }
     public void addfirend(String userid){
         SQLiteDatabase sqldb = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);
         ContentValues values = new ContentValues();
         values.put("userid", userid);
         sqldb.insert("firendlist", null, values);
     }
     public boolean judge(String userid){
         SQLiteDatabase sqldb = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);
         Cursor result = sqldb.query ("firendlist",null,"userid=?",new String[]{userid},null,null,null);
         boolean j = false;
         if(result.moveToFirst()){
             while(!result.isAfterLast()){
                 j = true;
                 result.moveToNext();
             }
         }
         return j;
     }
}