package com.neu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.neu.entity.Train;
import com.vector.my12306.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by zhang on 2016/9/1.
 */
public class TrainAdapter extends BaseAdapter {
    private static final String SEAT1 = "高级软卧";
    private static final String SEAT2 ="软卧";
    private static final String SEAT3 = "一等座";
    private static final String SEAT4 = "硬卧";
    private Context context;
    private List<Train> trainList;
    public TrainAdapter(Context context, List<Train> trainList){
        this.context = context;
        this.trainList = trainList;
    }

    @Override
    public int getCount() {
        return trainList.size();
    }

    @Override
    public Object getItem(int position) {
        return trainList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ticket_result_step,null);
            viewHolder.tvTicketResultStepTrainNo = (TextView) convertView.findViewById(R.id.tvTicketResultStepTrainNo);
            viewHolder.tvTicketResultStepTimeFrom = (TextView) convertView.findViewById(R.id.tvTicketResultStepTimeFrom);
            viewHolder.tvTicketResultStepTimeTo = (TextView) convertView.findViewById(R.id.tvTicketResultStepTimeTo);
            viewHolder.tvTicketResultStepSeat1 = (TextView) convertView.findViewById(R.id.tvTicketResultStepSeat1);
            viewHolder.tvTicketResultStepSeat2 = (TextView) convertView.findViewById(R.id.tvTicketResultStepSeat2);
            viewHolder.tvTicketResultStepSeat3 = (TextView) convertView.findViewById(R.id.tvTicketResultStepSeat3);
            viewHolder.tvTicketResultStepSeat4 = (TextView) convertView.findViewById(R.id.tvTicketResultStepSeat4);
            viewHolder.imgTicketResultStepFlg1 = (ImageView) convertView.findViewById(R.id.imgTicketResultStepFlg1);
            viewHolder.imgTicketResultStepFlg2 = (ImageView)convertView.findViewById(R.id.imgTicketResultStepFlg2);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Train train = trainList.get(position);
        viewHolder.tvTicketResultStepTrainNo.setText(train.getTrainNo());
        viewHolder.tvTicketResultStepTimeFrom.setText(train.getStartTime());
        viewHolder.tvTicketResultStepTimeTo.setText(train.getArriveTime());
        if(train.getStartStationName().equals(train.getFromStationName())){
            viewHolder.imgTicketResultStepFlg1.setImageResource(R.mipmap.flg_shi);
        }else{
            viewHolder.imgTicketResultStepFlg1.setImageResource(R.mipmap.flg_guo);
        }

        if(train.getEndStationName().equals(train.getToStationName())){
            viewHolder.imgTicketResultStepFlg2.setImageResource(R.mipmap.flg_zhong);
        }else{
            viewHolder.imgTicketResultStepFlg2.setImageResource(R.mipmap.flg_guo);
        }
        if(train.getSeats().get(SEAT1)!=null){
            viewHolder.tvTicketResultStepSeat1.setText(train.getSeats().get(SEAT1).getSeatName()
                    +"："+train.getSeats().get(SEAT1).getSeatNum());
        }
        if(train.getSeats().get(SEAT2)!=null){

            viewHolder.tvTicketResultStepSeat2.setText(train.getSeats().get(SEAT2).getSeatName()
                    +"："+train.getSeats().get(SEAT2).getSeatNum());
        }
        if(train.getSeats().get(SEAT3)!=null){
            viewHolder.tvTicketResultStepSeat3.setText(train.getSeats().get(SEAT3).getSeatName()
                    +"："+train.getSeats().get(SEAT3).getSeatNum());
        }
        if(train.getSeats().get(SEAT4)!=null){

            viewHolder.tvTicketResultStepSeat4.setText(train.getSeats().get(SEAT4).getSeatName()
                    +"："+train.getSeats().get(SEAT4).getSeatNum());
        }
        return convertView;
    }

    private class ViewHolder{
        TextView tvTicketResultStepTrainNo;
        TextView tvTicketResultStepTimeFrom;
        TextView tvTicketResultStepTimeTo;
        TextView tvTicketResultStepSeat1;
        TextView tvTicketResultStepSeat2;
        TextView tvTicketResultStepSeat3;
        TextView tvTicketResultStepSeat4;
        ImageView imgTicketResultStepFlg1;
        ImageView imgTicketResultStepFlg2;
    }

}
