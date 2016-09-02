package com.neu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.neu.entity.Contact;
import com.vector.my12306.R;

import java.util.List;

/**
 * Created by zhang on 2016/9/1.
 */
public class CustomerCheckedAdapter extends BaseAdapter {
    private PriceListener priceListener;
    private Context context;
    private List<Contact> contactList;
    public CustomerCheckedAdapter(Context context,List<Contact> contactList){
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_customer_checked,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_cancel = (ImageView) convertView.findViewById(R.id.iv_Cancel);
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
        viewHolder.iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactList.remove(position);
                priceListener.delete(position,contactList.size());
                CustomerCheckedAdapter.this.notifyDataSetChanged();
            }
        });
        return convertView;
    }
    public class ViewHolder{
        TextView name;
        TextView IdCard;
        TextView tel;
        ImageView iv_cancel;
    }
    public interface  PriceListener{
        void delete(int position,int count);
    }

    public void setPriceListener(PriceListener priceListener){
        this.priceListener = priceListener;
    }

}
