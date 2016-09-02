package com.neu.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neu.adapter.MyContactAdapter;
import com.neu.entity.Contact;
import com.neu.utils.Constant;
import com.neu.utils.NetworkUtils;
import com.neu.utils.URLConnManager;
import com.vector.my12306.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhang on 2016/8/30.
 */
public class MyContactActivity extends AppCompatActivity {
    private static final String TAG = "MyContactActivity";
    private static final int INIT_CONTACT_MSG_SUCCESS = 1;
    private static final int INIT_CONTACT_MSG_ERROR = 2;
    private static final int LOGIN_MESSAGE =3;
    private ListView listView;

    private MyContactAdapter myContactAdapter;
    private List<Contact> contactList ;
    private ProgressDialog progressDialog;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case INIT_CONTACT_MSG_SUCCESS:
                    if(progressDialog!=null){
                        progressDialog.dismiss();
                    }
                    myContactAdapter.notifyDataSetChanged();
                    break;
                case INIT_CONTACT_MSG_ERROR:
                    if(progressDialog!=null){
                        progressDialog.dismiss();
                    }
                    Toast.makeText(MyContactActivity.this,"服务器出错,数据加载失败",Toast.LENGTH_LONG).show();
                    break;
                case LOGIN_MESSAGE:
                    Toast.makeText(MyContactActivity.this,"请重新登录",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(MyContactActivity.this,LoginActivity.class);
                    startActivity(i);
                    finish();
                    break;
                default:break;
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycontact);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.listview);
        contactList = new ArrayList<Contact>();
        myContactAdapter = new MyContactAdapter(this,contactList);
        listView.setAdapter(myContactAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = (Contact) listView.getItemAtPosition(position);
                Intent i = new Intent(MyContactActivity.this,UpdateContactActivity.class);
                i.putExtra("contact",contact);
                startActivity(i);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!NetworkUtils.check(this)){
            Toast.makeText(this,R.string.net_error,Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog = ProgressDialog.show(this,null,getResources().getString(R.string.contactadd),false,true);

        new Thread(){
            @Override
            public void run() {
                try {
                    HttpURLConnection conn = URLConnManager.getHttpURLConnection(Constant.HOST+"/otn/PassengerList");
                    SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
                    String cookValue = sp.getString("cookie","");
                    conn.setRequestProperty("Cookie",cookValue);
                    conn.connect();
                    int code = conn.getResponseCode();
                    if(code==200){
                        InputStream in = conn.getInputStream();
                        String response = URLConnManager.converStreamToString(in);
                        Log.d(TAG,response);
                        if(response.contains("请重新登陆")){
                            handler.sendEmptyMessage(LOGIN_MESSAGE);
                            return;
                        }
                        Gson gson = new Gson();
                        List<Contact> datas = gson.fromJson(response,new TypeToken<ArrayList<Contact>>(){}.getType());
                        contactList.clear();
                        contactList.addAll(datas);
                        for(Contact contact:contactList){
                            Log.d(TAG,contact.toString());
                        }
                        handler.sendEmptyMessage(INIT_CONTACT_MSG_SUCCESS);
                        Log.d(TAG,"======data");
                        in.close();
                    }else{
                        handler.sendEmptyMessage(INIT_CONTACT_MSG_ERROR);
                    }
                    conn.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mycontact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.addcontact:
                Intent i = new Intent(MyContactActivity.this,NewContactActivity.class);
                startActivity(i);
                break;
            case android.R.id.home:
                finish();
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }
}
