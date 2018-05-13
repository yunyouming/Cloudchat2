package com.ela.eoswallet.elawalleta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Button;
import java.util.List;
import  org.elastos.carrier.Carrier;
import  org.elastos.carrier.exceptions.ElastosException;
import org.w3c.dom.Text;


import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import java.text.SimpleDateFormat;


import android.content.ContentValues;

import com.ela.eoswallet.elawalleta.R;

import java.util.Map;

public class NewFriendsListAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, String>> datas;

    public NewFriendsListAdapter(Context context ,  List <Map<String, String>> datas){
        this.context = context;
        this.datas = datas;

    }
    public Map getItem(int position) {
        return datas.get(position);
    }
    public long getItemId(int position) {
        return position;
    }
    /**
     * get count of messages
     */
    public int getCount() {
        return datas.size();
    }
    @SuppressLint("NewApi")
    public View getView(int i, View view, ViewGroup viewGroup) {
        final  ViewHolder vh;
        if (view == null){
            vh = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.adapter_newfriend_item, null);
            //vh.id = (TextView)view.findViewById(R.id.name);
            vh.userid = (TextView)view.findViewById(R.id.userid);
            vh.hello = (TextView)view.findViewById(R.id.hello);
            vh.acceptbtn = (Button)view.findViewById(R.id.acceptbtn);
            vh.accepted = (TextView)view.findViewById(R.id.accepted);
            view.setTag(vh);

            vh.userid.setText((String)datas.get(i).get("userid"));
            vh.hello.setText((String)datas.get(i).get("hello"));
            System.out.println("当前状态："+datas.get(i).get("yn"));
            if(datas.get(i).get("yn").equals("0")){
                vh.acceptbtn.setVisibility(View.VISIBLE);
                vh.accepted.setVisibility(View.GONE);
            }else{
                vh.acceptbtn.setVisibility(View.GONE);
                vh.accepted.setVisibility(View.VISIBLE);
            }
           // vh.acceptbtn.setText((String)datas.get(i).get("acceptbtn"));
            //zujian.friendname.setText((String)data.get(position).get("friendname"));

        } else {
            vh = (ViewHolder)view.getTag();
        }
        vh.acceptbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               System.out.println("好友请求的信息："+ vh.userid.getText().toString());
                vh.acceptbtn.setVisibility(View.GONE);
                vh.accepted.setVisibility(View.VISIBLE);
                try{
                    Carrier mycarrier = Carrier.getInstance();
                    mycarrier.AcceptFriend(vh.userid.getText().toString());


                    SQLiteDatabase sqldb = SQLiteDatabase.openOrCreateDatabase("/data/data/com.ela.eoswallet.elawalleta/chat.db",null);
                    ContentValues values = new ContentValues();
                    System.out.println("UserId:"+vh.userid.getText().toString());
                    values.put("userid", vh.userid.getText().toString());
                    sqldb.insert("firendlist", null, values);


                }catch (ElastosException e){
                    e.printStackTrace();
                }
            }
        });
        return view;
    }
    public final class ViewHolder
    {
        public TextView id;
        public TextView userid;
        public TextView hello;
        public Button acceptbtn;
        public TextView accepted;
    }
}

