package com.neu.ticket;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.neu.activity.UpdateContactActivity;
import com.neu.adapter.CustomerAdapter;
import com.neu.entity.Contact;
import com.neu.entity.Seat;
import com.neu.entity.Train;
import com.neu.entity.User;
import com.neu.utils.Constant;
import com.neu.utils.URLConnManager;
import com.vector.my12306.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2016/9/1.
 */
public class CustomerActivity extends AppCompatActivity {
    private static final String TAG ="CustomerActivbity";
    private Button btn_addContact;
    private ListView lv_customer;
    private List<Contact> contactList;
    private Map<Integer,Contact> contactMap;
    private CustomerAdapter customerAdapter;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customadd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("添加乘车人");

        lv_customer = (ListView) findViewById(R.id.lv_customer);
        btn_addContact = (Button) findViewById(R.id.btn_addcustomer);
        contactList = new ArrayList<Contact>();
        contactMap = new HashMap<Integer,Contact>();

        customerAdapter = new CustomerAdapter(this,contactList);
        lv_customer.setAdapter(customerAdapter);

        btn_addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("contactMap", (Serializable) customerAdapter.getContactMap());
                setResult(101,i);
                finish();
            }
        });

        lv_customer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(CustomerActivity.this, UpdateContactActivity.class);
                i.putExtra("contact",contactList.get(position));
                startActivity(i);
            }
        });

        Intent i = getIntent();
        contactMap = (Map<Integer, Contact>) i.getSerializableExtra("contactMap");
        customerAdapter.setContactMap(contactMap);
        new StepTask().execute();

    }
    private class StepTask extends AsyncTask<String,Void,Object> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(CustomerActivity.this,null,"加载中",false,true);
        }

        @Override
        protected Object doInBackground(String... params) {
            String result = null;
            try {
                HttpURLConnection conn = URLConnManager.getHttpURLConnection(Constant.HOST + "/otn/TicketPassengerList");
                SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
                String cookieValue = sp.getString("cookie", "");
                conn.setRequestProperty("Cookie", cookieValue);//设置请求属性
                //请求数据
                URLConnManager.postParams(conn.getOutputStream(),null);
                conn.connect();
                int code = conn.getResponseCode();
                if (code == 200) {
                    InputStream inputStream = conn.getInputStream();
                    String response = URLConnManager.converStreamToString(inputStream);
                    Log.d(TAG,"=========="+response);
                    Gson gson = new Gson();
                    List<Contact> contacts = gson.fromJson(response,new TypeToken<List<Contact>>(){}.getType());
                    inputStream.close();
                    return contacts;
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
            contactList.clear();
            if(progressDialog!=null){
                progressDialog.dismiss();
            }
            if(result instanceof List){
                contactList.addAll((ArrayList<Contact>)result);
                if(contactList.size()==0) {
                    Toast.makeText(CustomerActivity.this, "没查询到对应的车次", Toast.LENGTH_LONG).show();
                }
                customerAdapter.notifyDataSetChanged();
            }else if(result instanceof String){
                if ("3".equals(result.toString())) {
                    Toast.makeText(CustomerActivity.this, "请重新登录", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CustomerActivity.this, "服务器错误，请重试", Toast.LENGTH_LONG).show();
                }
            }
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
