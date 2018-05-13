package com.ela.eoswallet.elawalleta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.elastos.carrier.FriendInfo;

import com.ela.eoswallet.elawalleta.R;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class ContactListAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, String>> datas;

    public ContactListAdapter(Context context ,  List <Map<String, String>> datas){
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
        ViewHolder vh;
        if (view == null){
            vh = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.adapter_contact_item, null);
            vh.id = (TextView)view.findViewById(R.id.name);
            vh.remarks = (TextView)view.findViewById(R.id.remark);
            view.setTag(vh);
        } else {
            vh = (ViewHolder)view.getTag();
        }
        vh.id.setText(datas.get(i).get("userid"));
        vh.remarks.setText(datas.get(i).get("remark"));
        return view;
    }
    public final class ViewHolder
    {
        public TextView id;
        public TextView remarks;
    }
}

