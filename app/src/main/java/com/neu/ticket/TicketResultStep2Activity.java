package com.neu.ticket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.neu.adapter.SeatAdapter;
import com.neu.entity.Seat;
import com.neu.entity.Train;
import com.neu.utils.Constant;
import com.neu.utils.NetworkUtils;
import com.neu.utils.URLConnManager;
import com.vector.my12306.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2016/9/1.
 */
public class TicketResultStep2Activity extends AppCompatActivity {
    private static final String TAG = "TicketResultStep2Activity";
    private TextView starttime,endtime,trainNum,durationtime;
    private ListView lvseatDetail;
    private SeatAdapter seatAdapter;
    private List<Seat> seatList;
    private TextView tvTicketResultStepDateTitle,tvTicketResultStepStationTitle,tvTicketResultStepBefore,tvTicketResultStepAfter;
    private ProgressDialog progressDialog;
    private String fromStationName = null;
    private String toStationName = null;
    private String ticketDateFrom = null;
    private String trainNo = null;
    private LinearLayout ll_train;
    private Train currentTrain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketresult2);
        setTitle("车票预订2/5");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        starttime = (TextView) findViewById(R.id.starttime);
        endtime = (TextView) findViewById(R.id.endtime);
        trainNum = (TextView) findViewById(R.id.trainNum);
        durationtime = (TextView) findViewById(R.id.durationtime);
        ll_train = (LinearLayout) findViewById(R.id.ll_train);
        tvTicketResultStepDateTitle = (TextView) this.findViewById(R.id.tvTicketResultStepStationTitle);
        tvTicketResultStepStationTitle = (TextView) this.findViewById(R.id.tvTicketResultStepDateTitle);
        tvTicketResultStepBefore = (TextView) this.findViewById(R.id.tvTicketResultStepBefore);
        tvTicketResultStepAfter = (TextView) this.findViewById(R.id.tvTicketResultStepAfter);
        lvseatDetail = (ListView) findViewById(R.id.lvSeatDetail);
        seatList = new ArrayList<Seat>();
        seatAdapter = new SeatAdapter(this,seatList,currentTrain);
        lvseatDetail.setAdapter(seatAdapter);
        Intent i = getIntent();
        fromStationName = i.getStringExtra("fromStationName");
        toStationName = i.getStringExtra("toStationName");
        ticketDateFrom = i.getStringExtra("ticketDateFrom");
        trainNo = i.getStringExtra("trainNo");

        tvTicketResultStepDateTitle.setText(ticketDateFrom);
        tvTicketResultStepStationTitle.setText(fromStationName+"-"+toStationName);

        tvTicketResultStepBefore.setOnClickListener(new HandlerTicketResultStep());
        tvTicketResultStepAfter.setOnClickListener(new HandlerTicketResultStep());

        if(!NetworkUtils.check(this)){
            Toast.makeText(this,R.string.net_error,Toast.LENGTH_LONG).show();
            return;
        }
        String startTrainDate = ticketDateFrom.split(" ")[0];

        new StepTask(startTrainDate).execute();
    }

    private class StepTask extends AsyncTask<String,Void,Object> {

        String startTrainDate;
        public StepTask(String startTrainDate){
            this.startTrainDate = startTrainDate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG,"======onPreExecute");
            progressDialog = ProgressDialog.show(TicketResultStep2Activity.this,null,"加载中",false,true);
        }

        @Override
        protected Object doInBackground(String... params) {
            String result = null;
            try {
                HttpURLConnection conn = URLConnManager.getHttpURLConnection(Constant.HOST + "/otn/Train");
                SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
                String cookieValue = sp.getString("cookie", "");
                conn.setRequestProperty("Cookie", cookieValue);//设置请求属性
                //请求数据
                Map<String,String> param = new HashMap<String,String>();

                param.put("fromStationName", fromStationName);
                param.put("toStationName", toStationName);
                param.put("startTrainDate", startTrainDate);
                param.put("trainNo",trainNo);
                URLConnManager.postParams(conn.getOutputStream(),param);
                conn.connect();
                int code = conn.getResponseCode();
                if (code == 200) {
                    InputStream inputStream = conn.getInputStream();
                    String response = URLConnManager.converStreamToString(inputStream);
                    Log.d(TAG,"=========="+response);
                    Gson gson = new Gson();
                    Train train = gson.fromJson(response, Train.class);
                    inputStream.close();
                    return train;

                } else {
                    result = "2";
                }
                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                result = "2";
            } catch (JsonSyntaxException e) {
                result = "3";
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            seatList.clear();
            if(progressDialog!=null){
                progressDialog.dismiss();
            }
            if(result instanceof Train){
                currentTrain = (Train) result;
                seatAdapter.setTrain(currentTrain);
                if(((Train) result).getSeats()==null||((Train) result).getSeats().size()==0) {
                    Toast.makeText(TicketResultStep2Activity.this, "没查询到对应的车次", Toast.LENGTH_LONG).show();
                    ll_train.setVisibility(View.INVISIBLE);
                }else{
                    for(Map.Entry<String,Seat> entry:((Train) result).getSeats().entrySet()){
                        seatList.add(entry.getValue());
                    }
                    ll_train.setVisibility(View.VISIBLE);
                }
                seatAdapter.notifyDataSetChanged();
                starttime.setText(((Train) result).getStartTime());
                endtime.setText(((Train) result).getArriveTime());
                trainNum.setText(((Train) result).getTrainNo());
                durationtime.setText(((Train) result).getDurationTime());
            }else if(result instanceof String){
                if ("3".equals(result.toString())) {
                    Toast.makeText(TicketResultStep2Activity.this, "请重新登录", Toast.LENGTH_LONG).show();
                    ll_train.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(TicketResultStep2Activity.this, "服务器错误，请重试", Toast.LENGTH_LONG).show();
                    ll_train.setVisibility(View.INVISIBLE);
                }
            }else{
                ll_train.setVisibility(View.INVISIBLE);
                seatAdapter.notifyDataSetChanged();
            }
        }
    }
    private class HandlerTicketResultStep implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            String oldDateFrom=tvTicketResultStepDateTitle.getText().toString();
            Calendar calendar=Calendar.getInstance();

            int oldYear = Integer.parseInt(oldDateFrom.split("-")[0]);
            int oldMonth = Integer.parseInt(oldDateFrom.split("-")[1]) - 1;
            int oldDay = Integer.parseInt(oldDateFrom.split("-")[2].split(" ")[0]);

            calendar.set(oldYear,oldMonth,oldDay);

            switch (v.getId()){
                case R.id.tvTicketResultStepBefore:
                    calendar.add(Calendar.DAY_OF_MONTH,-1);
                    break;
                case R.id.tvTicketResultStepAfter:
                    calendar.add(Calendar.DAY_OF_MONTH,1);
                    break;
                default:break;
            }
            //更新日期
            String weekDay = DateUtils.formatDateTime(TicketResultStep2Activity.this, calendar.getTimeInMillis(), DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_WEEKDAY);
            String ticketDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + weekDay;
            tvTicketResultStepDateTitle.setText(ticketDate);
            //判斩当前网是否可用
            if (!NetworkUtils.check(TicketResultStep2Activity.this)) {
                Toast.makeText(TicketResultStep2Activity.this, R.string.net_error, Toast.LENGTH_LONG).show();
                return;//后续的代码不再执行
            }
            //调用异步任务
            String startTrainDate = tvTicketResultStepDateTitle.getText().toString().split(" ")[0];//获得载去星期的日期
            new StepTask(startTrainDate).execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }
}
