package com.neu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.neu.entity.Contact;
import com.neu.entity.Train;
import com.vector.my12306.R;

import java.util.List;
import java.util.Random;

/**
 * Created by zhang on 2016/9/2.
 */
public class CustomerSeatAdapter extends BaseAdapter {
    private Context context;
    private List<Contact> contactList;
    private Train train;
    private int seat;
    public CustomerSeatAdapter(Context context,List<Contact> contactList,Train train){
        this.context = context;
        this.contactList = contactList;
        this.train = train;
        seat = 24;
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
        ViewHolder viewHolder = null;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_customerseat,null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.tvStep4Name);
            viewHolder.trainNo = (TextView) convertView.findViewById(R.id.tvStep4TrainNo);
            viewHolder.startDate = (TextView) convertView.findViewById(R.id.tvStep4StartDate);
            viewHolder.seat = (TextView) convertView.findViewById(R.id.tvStep4Seat);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Contact contact = contactList.get(position);
        viewHolder.name.setText(contact.getName());
        viewHolder.startDate.setText(train.getStartTime());
        viewHolder.seat.setText(String.valueOf(seat++)+"号");
        viewHolder.trainNo.setText(train.getTrainNo());
        return convertView;
    }

    public class ViewHolder{
        TextView name;
        TextView trainNo;
        TextView startDate;
        TextView seat;
    }
}
