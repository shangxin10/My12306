package com.neu.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.neu.entity.Contact;
import com.neu.entity.User;
import com.vector.my12306.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2016/9/1.
 */
public class CustomerAdapter extends BaseAdapter {

    private Context context;
    private List<Contact> contactList;
    private Map<Integer,Contact> contactMap = new HashMap<Integer,Contact>();
    public CustomerAdapter(Context context, List<Contact> contactList){
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_customer,null);
            viewHolder = new ViewHolder();
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.IdCard = (TextView) convertView.findViewById(R.id.IdCard);
            viewHolder.tel = (TextView) convertView.findViewById(R.id.tel);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Contact contact = contactList.get(position);
        viewHolder.name.setText(contact.getName()+"("+contact.getType()+")");
        viewHolder.tel.setText(contact.getTel());
        viewHolder.IdCard.setText(contact.getIdType()+":"+contact.getId());

        if(contactMap.get(position)!=null){
            viewHolder.checkBox.setChecked(true);
        }else{
            viewHolder.checkBox.setChecked(false);
        }
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contactMap.get(position)==null){
                    contactMap.put(position,contactList.get(position));
                }else{
                    contactMap.remove(position);
                }

            }
        });
        return convertView;
    }

    public class ViewHolder{
        CheckBox checkBox;
        TextView name;
        TextView IdCard;
        TextView tel;
    }

    public Map<Integer,Contact> getContactMap(){
        return contactMap;
    }

    public void setContactMap(Map<Integer,Contact> contactMap){
        this.contactMap = contactMap;
    }
}
