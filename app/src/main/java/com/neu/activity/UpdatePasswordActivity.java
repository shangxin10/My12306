package com.neu.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.neu.entity.User;
import com.neu.utils.Constant;
import com.neu.utils.URLConnManager;
import com.vector.my12306.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhang on 2016/8/31.
 */
public class UpdatePasswordActivity extends AppCompatActivity {
    private static final String TAG = "UpdatePasswordActivity";
    private static final int UPDATE_SUCCESS = 1;
    private static final int UPDATE_ERROR =- 2;
    private EditText firstPassword,secondPassword;
    private Button btn_save;
    private ProgressDialog progressDialog;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_SUCCESS:
                    String result = (String) msg.obj;
                    if(result.equals("1")){
                        Toast.makeText(UpdatePasswordActivity.this,"修改成功",Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(UpdatePasswordActivity.this,"修改失败",Toast.LENGTH_LONG).show();
                    }
                    break;
                case UPDATE_ERROR:
                    Toast.makeText(UpdatePasswordActivity.this,"服务器错误",Toast.LENGTH_LONG).show();
                    break;
                default:break;
            }
        }
    };
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatepassword);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firstPassword = (EditText) findViewById(R.id.firstPassword);
        secondPassword = (EditText) findViewById(R.id.secondPassword);
        btn_save = (Button) findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first = firstPassword.getText().toString();
                final String second = secondPassword.getText().toString();
                if(TextUtils.isEmpty(first)){
                    firstPassword.setError("请输入新密码");
                    firstPassword.requestFocus();
                    return;
                }else if(TextUtils.isEmpty(second)){
                    secondPassword.setError("请输入确认密码");
                    secondPassword.requestFocus();
                    return;
                }else if(!first.equals(second)){
                    Toast.makeText(UpdatePasswordActivity.this,"两次输入密码不一致",Toast.LENGTH_LONG).show();
                    return;
                }
                progressDialog = ProgressDialog.show(UpdatePasswordActivity.this,null,"修改中",false,true);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            HttpURLConnection conn = URLConnManager.getHttpURLConnection(Constant.HOST+"/otn/AccountPassword");
                            SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
                            String cookie = sp.getString("cookie","");
                            if (!TextUtils.isEmpty(cookie)){
                                conn.setRequestProperty("Cookie",cookie);
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("newPassword",second);
                                params.put("action","update");
                                URLConnManager.postParams(conn.getOutputStream(),params);
                                conn.connect();
                                int code = conn.getResponseCode();
                                if(code==200){
                                    InputStream in = conn.getInputStream();
                                    String response = URLConnManager.converStreamToString(in);
                                    Log.d(TAG,response);
                                    Gson gson = new Gson();
                                    String  result = gson.fromJson(response,String.class);
                                    Message message = new Message();
                                    message.what = UPDATE_SUCCESS;
                                    message.obj = result;
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
