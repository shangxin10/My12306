package com.neu.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neu.entity.Contact;
import com.neu.entity.User;
import com.neu.utils.Constant;
import com.neu.utils.DialogUtils;
import com.neu.utils.NetworkUtils;
import com.neu.utils.URLConnManager;
import com.vector.my12306.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2016/8/31.
 */
public class MyaccountActivity extends AppCompatActivity {
    private static final String TAG = "MyAccountActivity";
    private static final int QUERY_SUCCESS = 1;
    private static final int QUERY_ERROR =2;
    private static final int UPDATE_SUCCESS =3;
    private static final int UPDATE_ERROR =4;
    private RelativeLayout rl_customerType,rl_phone;
    private TextView tv_username,tv_name,tv_IDType,tv_IDnum,tv_customerType,tv_phone;
    private Button btn_save;
    private String action;
    private ProgressDialog progressDialog;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(progressDialog!=null){
                progressDialog.dismiss();
            }
            switch (msg.what){
                case QUERY_SUCCESS:
                    User user = (User) msg.obj;
                    tv_username.setText(user.getUsername());
                    tv_phone.setText(user.getTel());
                    tv_IDnum.setText(user.getId());
                    tv_customerType.setText(user.getType());
                    tv_name.setText(user.getName());
                    break;
                case QUERY_ERROR:
                    Toast.makeText(MyaccountActivity.this,"服务器异常",Toast.LENGTH_LONG).show();
                    break;
                case UPDATE_SUCCESS:

                    Toast.makeText(MyaccountActivity.this,"保存成功",Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case UPDATE_ERROR:
                    Toast.makeText(MyaccountActivity.this,"服务器异常",Toast.LENGTH_LONG).show();
                    break;
                default:break;
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rl_customerType = (RelativeLayout) findViewById(R.id.rl_customerType);
        rl_phone = (RelativeLayout) findViewById(R.id.rl_phone);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_IDType = (TextView) findViewById(R.id.tv_IDType);
        tv_IDnum = (TextView) findViewById(R.id.tv_IDnum);
        tv_customerType = (TextView) findViewById(R.id.tv_customerType);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        btn_save = (Button) findViewById(R.id.btn_save);
        action = "query";

        progressDialog = ProgressDialog.show(this,null,"加载中",false,true);
        new Thread(){
            @Override
            public void run() {
                super.run();
                if(!NetworkUtils.check(MyaccountActivity.this)){
                    Toast.makeText(MyaccountActivity.this,R.string.net_error,Toast.LENGTH_LONG).show();
                    return;
                }
                try{
                    HttpURLConnection conn = URLConnManager.getHttpURLConnection(Constant.HOST+ "/otn/Account");
                    SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
                    String cookie = sp.getString("cookie","");
                    if(!TextUtils.isEmpty(cookie)){
                        conn.setRequestProperty("Cookie",cookie);
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("action",action);
                        URLConnManager.postParams(conn.getOutputStream(),params);
                        conn.connect();
                        int code = conn.getResponseCode();
                        if(code==200){
                            InputStream in = conn.getInputStream();
                            String response = URLConnManager.converStreamToString(in);
                            Log.d(TAG,response);
                            Gson gson = new Gson();
                            User user = gson.fromJson(response,User.class);
                            Message msg = new Message();
                            msg.what = QUERY_SUCCESS;
                            msg.obj = user;
                            handler.sendMessage(msg);
                            in.close();
                        }else{
                            handler.sendEmptyMessage(QUERY_ERROR);
                        }
                        conn.disconnect();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    handler.sendEmptyMessage(QUERY_ERROR);
                }
            }

        }.start();

        rl_customerType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] types = new String[]{"成人","学生","儿童","其他"};
                setType(tv_customerType,"请选择乘客类型",types);
            }
        });

        rl_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText(tv_phone,"请输入电话号码");
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = ProgressDialog.show(MyaccountActivity.this,null,"修改中",false,true);

                new Thread(){
                    @Override
                    public void run() {
                        try {
                            HttpURLConnection conn = URLConnManager.getHttpURLConnection(Constant.HOST+"/otn/Account");
                            SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
                            String cookie = sp.getString("cookie","");
                            if (!TextUtils.isEmpty(cookie)){
                                conn.setRequestProperty("Cookie",cookie);
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("乘客类型",tv_customerType.getText().toString());
                                params.put("电话",tv_phone.getText().toString());
                                params.put("action","update");
                                URLConnManager.postParams(conn.getOutputStream(),params);
                                conn.connect();
                                int code = conn.getResponseCode();
                                if(code==200){
                                    InputStream in = conn.getInputStream();
                                    String response = URLConnManager.converStreamToString(in);
                                    Log.d(TAG,response);
                                    Gson gson = new Gson();
                                    User user = gson.fromJson(response,User.class);
                                    Message message = new Message();
                                    message.what = UPDATE_SUCCESS;
                                    message.obj = user;
                                    handler.sendMessage(message);
                                    in.close();
                                }else{
                                    handler.sendEmptyMessage(UPDATE_ERROR);
                                }
                                conn.disconnect();
                            }
                        }catch (IOException e) {
                            handler.sendEmptyMessage(UPDATE_ERROR);
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

    }

    private void setText(final TextView textView,final String titleName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleName);
        builder.setIcon(android.R.drawable.btn_star);
        final EditText edInput = new EditText(this);
        builder.setView(edInput);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String input = edInput.getText().toString();
                if(TextUtils.isEmpty(input)) {
                    DialogUtils.dialogClose(dialog, false);
                    edInput.setError(titleName);
                    edInput.requestFocus();
                }else{
                    DialogUtils.dialogClose(dialog, true);
                    textView.setText(input);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DialogUtils.dialogClose(dialog,true);
            }
        });
        builder.create().show();
    }


    private void setType(final TextView textView, String titleName, final String[] types){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titleName).setIcon(android.R.drawable.btn_star);
        builder.setSingleChoiceItems(types, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textView.setText(types[which]);
                textView.setVisibility(View.VISIBLE);
                DialogUtils.dialogClose(dialog,true);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DialogUtils.dialogClose(dialog,true);
            }
        });

        builder.create().show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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
