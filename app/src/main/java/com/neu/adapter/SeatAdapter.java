package com.neu.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.neu.entity.Seat;
import com.neu.entity.Train;
import com.neu.ticket.TicketResultStep3Activity;
import com.vector.my12306.R;

import java.util.List;

/**
 * Created by zhang on 2016/9/1.
 */
public class SeatAdapter extends BaseAdapter {

    private Context context;
    private List<Seat> seatList;
    private Train train;

    public SeatAdapter(Context context, List<Seat> seatList,Train train){
        this.context = context;
        this.seatList = seatList;
        this.train = train;
    }

    @Override
    public int getCount() {
        return seatList.size();
    }

    @Override
    public Object getItem(int position) {
        return seatList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_seat,null);
            viewHolder = new ViewHolder();
            viewHolder.tv_seatName = (TextView) convertView.findViewById(R.id.tv_seatName);
            viewHolder.tv_seatNum = (TextView) convertView.findViewById(R.id.seatcount);
            viewHolder.tv_seatPrice = (TextView) convertView.findViewById(R.id.tv_seatPrice);
            viewHolder.btn_yy = (Button) convertView.findViewById(R.id.btn_yy);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Seat seat = seatList.get(position);
        viewHolder.tv_seatName.setText(seat.getSeatName());
        viewHolder.tv_seatNum.setText(String.valueOf(seat.getSeatNum()));
        viewHolder.tv_seatPrice.setText("￥"+String.valueOf(seat.getSeatPrice()));
        viewHolder.btn_yy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, TicketResultStep3Activity.class);
                i.putExtra("seat",seatList.get(position));
                i.putExtra("train",train);
                context.startActivity(i);
            }
        });
        return convertView;
    }

    private class ViewHolder{
        TextView tv_seatName;
        TextView tv_seatNum;
        TextView tv_seatPrice;
        Button btn_yy;
    }
    public void setTrain(Train train){
        this.train = train;
    }
}
