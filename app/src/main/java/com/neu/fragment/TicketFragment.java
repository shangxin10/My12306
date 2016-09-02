package com.neu.fragment;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.neu.db.HistoryHelper;
import com.neu.ticket.StationListActivity;
import com.neu.ticket.TicketResultStepActivity;
import com.vector.my12306.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.OnClick;

/**
 * Created by zhang on 2016/8/30.
 */
public class TicketFragment extends Fragment {
    private TextView tvTicketStationFrom,tvTicketStationTo;
    private ImageView imgTicketExchange;
    private Button btnTicketQuery;
    private TextView tvTicketDateFrom;
    private TextView tvTicketHistory1, tvTicketHistory2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ticket,container,false);
    }


    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvTicketStationFrom = (TextView) getActivity().findViewById(R.id.tvTicketStationFrom);
        tvTicketStationTo = (TextView) getActivity().findViewById(R.id.tvTicketStationTo);
        imgTicketExchange = (ImageView) this.getActivity().findViewById(R.id.imgTicketExchange);
        btnTicketQuery = (Button) getActivity().findViewById(R.id.btnTicketQuery);
        tvTicketHistory1 = (TextView) getActivity().findViewById(R.id.tvTicketHistory1);
        tvTicketHistory2 = (TextView) getActivity().findViewById(R.id.tvTicketHistory2);
        tvTicketDateFrom = (TextView) getActivity().findViewById(R.id.tvTicketDateFrom);

        tvTicketDateFrom.setText(setCurrentDateToTvFromDate());

        tvTicketDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StationListActivity.class);
                startActivityForResult(intent,100);
            }
        });
        tvTicketStationTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),StationListActivity.class);
                startActivityForResult(intent,101);
            }
        });
        imgTicketExchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String stationFrom = tvTicketStationFrom.getText().toString();
                final String satationTo = tvTicketStationTo.getText().toString();

                TranslateAnimation animaFrom = new TranslateAnimation(0,150,0,0);
                animaFrom.setDuration(3000);
                animaFrom.setInterpolator(new AccelerateInterpolator());
                animaFrom.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tvTicketStationTo.setText(stationFrom);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                TranslateAnimation animaTo = new TranslateAnimation(0,-150,0,0);
                animaTo.setDuration(3000);
                animaTo.setInterpolator(new AccelerateInterpolator());
                animaTo.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tvTicketStationFrom.setText(satationTo);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                tvTicketStationFrom.startAnimation(animaFrom);
                tvTicketStationTo.startAnimation(animaTo);
            }
        });
        btnTicketQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryHelper historyHelper = new HistoryHelper(getActivity());
                SQLiteDatabase db = historyHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put("rec",tvTicketStationFrom.getText().toString()+"-"+tvTicketStationTo.getText().toString());
                db.insert("history",null,contentValues);
                db.close();
                historyHelper.close();
                Intent intent = new Intent(getActivity(), TicketResultStepActivity.class);
                intent.putExtra("ticketStationFrom",tvTicketStationFrom.getText().toString());
                intent.putExtra("ticketStationTo",tvTicketStationTo.getText().toString());
                intent.putExtra("ticketDateFrom",tvTicketDateFrom.getText().toString());
                startActivity(intent);
            }
        });
        tvTicketHistory1.setOnClickListener(new HistoryListener());
        tvTicketHistory2.setOnClickListener(new HistoryListener());

        tvTicketDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldDateFrom = tvTicketDateFrom.getText().toString();
                int oldYear = Integer.parseInt(oldDateFrom.split("-")[0]);
                int oldMonth = Integer.parseInt(oldDateFrom.split("-")[1]);
                int oldDay = Integer.parseInt(oldDateFrom.split("-")[2].split(" ")[0]);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year,monthOfYear,dayOfMonth);
                        String weekDay = DateUtils.formatDateTime(getActivity(),calendar.getTimeInMillis(),DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_WEEKDAY);
                        String ticketDate = year + "-"+monthOfYear+"-"+dayOfMonth+" "+weekDay;
                        tvTicketDateFrom.setText(ticketDate);
                    }
                },oldYear,oldMonth,oldDay);
                dialog.show();
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        HistoryHelper historyHelper = new HistoryHelper(getContext());
        SQLiteDatabase db = historyHelper.getReadableDatabase();
        Cursor cursor = db.query("history",null,null,null,null,null,"id desc","2");
        if(cursor.moveToNext()){
            tvTicketHistory1.setText(cursor.getString(cursor.getColumnIndex("rec")));
        }
        if(cursor.moveToNext()){
            tvTicketHistory2.setText(cursor.getString(cursor.getColumnIndex("rec")));
        }
        cursor.close();
        db.close();
        historyHelper.close();
    }

    private class HistoryListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String str ="";
            switch (v.getId()){
                case R.id.tvTicketHistory1:
                    str = tvTicketHistory1.getText().toString();
                    break;
                case R.id.tvTicketHistory2:
                    str = tvTicketHistory2.getText().toString();
                    break;
                default:break;
            }
            if(!TextUtils.isEmpty(str)){
                tvTicketStationFrom.setText(str.split("-")[0]);
                tvTicketStationTo.setText(str.split("-")[1]);
            }
        }
    }


    private String setCurrentDateToTvFromDate(){
        Calendar myCalendar = Calendar.getInstance(Locale.CHINA);
        Date myDate = new Date();
        myCalendar.setTime(myDate);

        int year = myCalendar.get(Calendar.YEAR);
        int month = myCalendar.get(Calendar.MONTH);
        int day = myCalendar.get(Calendar.DAY_OF_MONTH);

        String weekDay = DateUtils.formatDateTime(getActivity(),myCalendar.getTimeInMillis(),DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_WEEKDAY);
        String ticketDate = year +"-"+(month+1)+"-"+day+" "+weekDay;
        return ticketDate;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==200){
            if(data!=null){
                String stationName = data.getStringExtra("name");
                switch (requestCode){
                    case 100:
                        tvTicketStationFrom.setText(stationName);
                        break;
                    case 101:
                        tvTicketStationTo.setText(stationName);
                        break;
                    default:break;
                }
            }
        }else{
            Toast.makeText(getActivity(),"车站选择失败",Toast.LENGTH_LONG).show();
        }
    }
}
