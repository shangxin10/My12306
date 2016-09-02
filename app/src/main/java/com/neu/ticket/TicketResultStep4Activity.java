package com.neu.ticket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.neu.activity.MainActivity;
import com.neu.adapter.CustomerSeatAdapter;
import com.neu.entity.Contact;
import com.neu.entity.Train;
import com.neu.utils.Constant;
import com.neu.utils.NetworkUtils;
import com.neu.utils.URLConnManager;
import com.vector.my12306.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2016/9/2.
 */
public class TicketResultStep4Activity extends AppCompatActivity {
    private TextView tvOrderId,tvPayAfter,tvPayNow;
    private ListView lvStep4;
    private CustomerSeatAdapter customerSeatAdapter;
    List<Map<String ,Object>> data;
    //code 用于生成唯一的订单号
    private static long code;
    //用于生成唯一的座位
    private static int seatNum;
    // 订单号
    private long orderId;
    //进度对话框
    private ProgressDialog pDialog;
    private Train train;
    private List<Contact> contactList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketresult4);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("车票预订4/5");
        initView();
    }


    private void initView(){
        tvOrderId = (TextView)findViewById(R.id.tvOrderId);
        tvPayAfter = (TextView)findViewById(R.id.tvPayAfter);
        tvPayNow = (TextView)findViewById(R.id.tvPayNow);
        lvStep4 = (ListView)findViewById(R.id.lvStep4);
        contactList = new ArrayList<Contact>();
        Intent i = getIntent();
        contactList = (List<Contact>) i.getSerializableExtra("contactList");
        train = (Train) i.getSerializableExtra("train");
        orderId = nextCode();
        tvOrderId.setText("订单提交成功，您的订单编号为："+orderId);
        customerSeatAdapter = new CustomerSeatAdapter(this,contactList,train);
        lvStep4.setAdapter(customerSeatAdapter);

        tvPayAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TicketResultStep4Activity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        tvPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetworkUtils.check(TicketResultStep4Activity.this)){
                    Toast.makeText(TicketResultStep4Activity.this, "当前网络不可用", Toast.LENGTH_SHORT).show();
                    return;
                }
                new StepTask().execute();
            }

        });
    }

    private class StepTask extends AsyncTask<Void,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog= ProgressDialog.show(TicketResultStep4Activity.this,null,"支付中，请稍候",false,true);
        }
        @Override
        protected String doInBackground(Void... params) {
            String result=null;
            InputStream inputStream=null;
            HttpURLConnection conn=null;
            try {
                //获取连接
                conn = URLConnManager.getHttpURLConnection(Constant.HOST
                        + "/otn/Pay");
                //获取保存的cookie
                SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
                String cookieValue = sp.getString("cookie", "");
                //设置请求属性
                conn.setRequestProperty("cookie", cookieValue);
                //封装请求参数
                Map<String,String> param = new HashMap<String,String>();
                param.put("order",String.valueOf(orderId));
                URLConnManager.postParams(conn.getOutputStream(), param);
                //连接
                conn.connect();
                //获得响应码
                int code=conn.getResponseCode();
                if(code==200){//连接成功
                    //获取服务器返回的数据
                    inputStream=conn.getInputStream();
                    //将输入流转成字符串
                    String reponse=URLConnManager.converStreamToString(inputStream);
                    //用Gson解析数据
                    Gson gson=new Gson();
                    result=gson.fromJson(reponse,String.class);
                    //成功时返回的是Train对象
                    return result;
                }else{//连接失败
                    result="2";
                }

            } catch (IOException e) {
                e.printStackTrace();
                result = "2";
            }catch (JsonSyntaxException e){
                e.printStackTrace();
                result="3";
            }
            finally {
                //关闭输入流和连接等资源
                if(inputStream!=null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(conn!=null){
                    conn.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //关闭进度对话框
            if (pDialog != null) {
                pDialog.dismiss();
            }
            switch (s) {
                case "1":
                    //支付成功，将订单编号传给支付成功界面
                    Toast.makeText(TicketResultStep4Activity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TicketResultStep4Activity.this, TicketResultStep5Activity.class);
                    intent.putExtra("orderId",orderId);
                    startActivity(intent);
                    finish();
                    break;
                case "2":
                    Toast.makeText(TicketResultStep4Activity.this, "服务器错误2", Toast.LENGTH_SHORT).show();
                    break;
                case "3":
                    Toast.makeText(TicketResultStep4Activity.this, "请重新登录", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }






    /**
     * 生成唯一的订单编号
     * @return
     */
    public static synchronized long nextCode() {
        code++;
        String str = new SimpleDateFormat("yyyyMM").format(new Date());
        long m = Long.parseLong((str)) * 10000;
        m += code;
        return m;
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
