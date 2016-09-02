package com.neu.ticket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.neu.adapter.TrainAdapter;
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
public class TicketResultStepActivity extends AppCompatActivity{
    private TextView tvTicketResultStepDateTitle;
    private TextView tvTicketResultStepStationTitle;
    private TextView tvTicketResultStepBefore;
    private TextView tvTicketResultStepAfter;
    private ListView lvTicketResultStep;


    private List<Train> trainList;
    private TrainAdapter trainAdapter;
    private String ticketStationFrom = null;
    private String ticketStationTo = null;
    private ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketresult);
        setTitle("车票预订1/5");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTicketResultStepDateTitle = (TextView) this.findViewById(R.id.tvTicketResultStepStationTitle);
        tvTicketResultStepStationTitle = (TextView) this.findViewById(R.id.tvTicketResultStepDateTitle);
        tvTicketResultStepBefore = (TextView) this.findViewById(R.id.tvTicketResultStepBefore);
        tvTicketResultStepAfter = (TextView) this.findViewById(R.id.tvTicketResultStepAfter);
        lvTicketResultStep = (ListView) this.findViewById(R.id.lvTicketResultStep);
        trainList = new ArrayList<Train>();

        trainAdapter = new TrainAdapter(this,trainList);
        lvTicketResultStep.setAdapter(trainAdapter);
        Intent i = getIntent();
        final String ticketDateFrom = i.getStringExtra("ticketDateFrom");
        ticketStationFrom = i.getStringExtra("ticketStationFrom");
        ticketStationTo = i.getStringExtra("ticketStationTo");
        tvTicketResultStepDateTitle.setText(ticketDateFrom);
        tvTicketResultStepStationTitle.setText(ticketStationFrom+"-"+ticketStationTo);

        tvTicketResultStepBefore.setOnClickListener(new HandlerTicketResultStep());
        tvTicketResultStepAfter.setOnClickListener(new HandlerTicketResultStep());

        if(!NetworkUtils.check(this)){
            Toast.makeText(this,R.string.net_error,Toast.LENGTH_LONG).show();
            return;
        }
        String startTrainDate = ticketDateFrom.split(" ")[0];

        new StepTask(startTrainDate).execute();

        lvTicketResultStep.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(TicketResultStepActivity.this,TicketResultStep2Activity.class);
                i.putExtra("fromStationName",ticketStationFrom);
                i.putExtra("toStationName",ticketStationTo);
                i.putExtra("ticketDateFrom",ticketDateFrom);
                i.putExtra("trainNo",trainList.get(position).getTrainNo());
                startActivity(i);
            }
        });
    }
    private class StepTask extends AsyncTask<String,Void,Object>{

        String startTrainDate;
        public StepTask(String startTrainDate){
            this.startTrainDate = startTrainDate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(TicketResultStepActivity.this,null,"加载中",false,true);
        }

        @Override
        protected Object doInBackground(String... params) {
            String result = null;
            try {
                HttpURLConnection conn = URLConnManager.getHttpURLConnection(Constant.HOST + "/otn/TrainList");
                SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
                String cookieValue = sp.getString("cookie", "");
                conn.setRequestProperty("Cookie", cookieValue);//设置请求属性
                //请求数据
                Map<String,String> param = new HashMap<String,String>();

                param.put("fromStationName", ticketStationFrom);
                param.put("toStationName", ticketStationTo);
                param.put("startTrainDate", startTrainDate);

                URLConnManager.postParams(conn.getOutputStream(), param);
                conn.connect();
                int code = conn.getResponseCode();
                if (code == 200) {
                    InputStream inputStream = conn.getInputStream();
                    String response = URLConnManager.converStreamToString(inputStream);
                    //    Log.i("My12306", "********TicketResultStep1Activity*********" + response);

                    Gson gson = new Gson();

                    List<Train> trains = gson.fromJson(response, new TypeToken<List<Train>>(){}.getType());
                    inputStream.close();
                    return trains;

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
            trainList.clear();
            if(progressDialog!=null){
                progressDialog.dismiss();
            }
            if(result instanceof List){
                trainList.addAll((ArrayList<Train>)result);
                if(trainList.size()==0) {
                    Toast.makeText(TicketResultStepActivity.this, "没查询到对应的车次", Toast.LENGTH_LONG).show();
                }
                trainAdapter.notifyDataSetChanged();

            }else if(result instanceof String){
                if ("3".equals(result.toString())) {
                    Toast.makeText(TicketResultStepActivity.this, "请重新登录", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TicketResultStepActivity.this, "服务器错误，请重试", Toast.LENGTH_LONG).show();

                }
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
            String weekDay = DateUtils.formatDateTime(TicketResultStepActivity.this, calendar.getTimeInMillis(), DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_WEEKDAY);
            String ticketDate = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + weekDay;
            tvTicketResultStepDateTitle.setText(ticketDate);
            //判斩当前网是否可用
            if (!NetworkUtils.check(TicketResultStepActivity.this)) {
                Toast.makeText(TicketResultStepActivity.this, R.string.net_error, Toast.LENGTH_LONG).show();
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
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }
}
