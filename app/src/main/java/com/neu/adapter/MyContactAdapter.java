package com.neu.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.neu.entity.Contact;
import com.vector.my12306.R;

import java.util.List;

/**
 * Created by zhang on 2016/8/30.
 */
public class MyContactAdapter extends BaseAdapter{

    private static final String TAG = "MyContactAdapter";
    private Context context;
    private List<Contact> contactList;

    public MyContactAdapter(Context context, List<Contact> contactList){
        this.context = context;
        this.contactList = contactList;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG,"====getView====");
        ViewHolder viewHolder;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_contact,null);
            viewHolder = new ViewHolder();
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_type);
            viewHolder.tv_IDnum = (TextView) convertView.findViewById(R.id.tv_IDnum);
            viewHolder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Contact contact = contactList.get(position);
        viewHolder.tv_name.setText(contact.getName());
        viewHolder.tv_IDnum.setText(contact.getId());
        viewHolder.tv_type.setText("("+contact.getType()+")");
        viewHolder.tv_phone.setText(contact.getTel());
        return convertView;
    }

    private static class ViewHolder{
        TextView tv_name;
        TextView tv_type;
        TextView tv_IDnum;
        TextView tv_phone;
    }

}
