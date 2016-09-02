package com.neu.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.neu.utils.Constant;
import com.neu.utils.Md5Utils;
import com.neu.utils.NetworkUtils;
import com.neu.utils.URLConnManager;
import com.vector.my12306.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhang on 2016/8/10.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int GET_LOGINRESULT_SUCCESS = 1;
    private static final int GET_LOGINRESULT_ERROR =2;
    private static final int LOGINSUCCESS_CODE = 1;
    private static final int LOGINERROR_CODE = 2;
    private EditText et_username;
    private EditText et_password;
    private Button btn_login;
    private CheckBox cb_autologin;
    private TextView tv_forget;
    private ImageView iv_photo;
    private ProgressDialog progressDialog;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(progressDialog!=null){
                progressDialog.dismiss();
            }
            switch (msg.what){
                case GET_LOGINRESULT_SUCCESS:
                    int result = msg.arg1;
                    if(result==LOGINERROR_CODE){
                        et_username.setError("用户名或者密码错误");
                        et_username.requestFocus();
                    }else if(result==LOGINSUCCESS_CODE){
                        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        String jsessionid = (String) msg.obj;
                        editor.putString("cookie",jsessionid);
                        if(cb_autologin.isChecked()){
                            editor.putString("username",et_username.getText().toString());
                            editor.putString("password",et_password.getText().toString());
                        }else{
                            editor.remove("username");
                            editor.remove("password");
                        }
                        editor.commit();
                        Intent i  = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    break;
                case GET_LOGINRESULT_ERROR:
                    break;
                default:break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("登录");
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        cb_autologin = (CheckBox) findViewById(R.id.cb_autologin);
        tv_forget = (TextView) findViewById(R.id.tv_forget);
        iv_photo = (ImageView)findViewById(R.id.iv_photo);
        tv_forget.setText(Html.fromHtml("<a href=\"http://www.12306.cn\">忘记密码?</a>"));
        tv_forget.setMovementMethod(LinkMovementMethod.getInstance());

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username=et_username.getText().toString();
                final String password=et_password.getText().toString();
                if(TextUtils.isEmpty(username)){
                    et_username.setError("用户名不能为空");
                    et_username.requestFocus();
                }else if(TextUtils.isEmpty(password)){
                    et_password.setError("密码不能为空");
                    et_password.requestFocus();
                }else{
                    if(!NetworkUtils.check(LoginActivity.this)){
                        Toast.makeText(LoginActivity.this,R.string.net_error,Toast.LENGTH_LONG).show();
                        return;
                    }
                    progressDialog = ProgressDialog.show(LoginActivity.this,null,getResources().getString(R.string.logining),false,true);
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                HttpURLConnection conn = URLConnManager.getHttpURLConnection(Constant.HOST+"/Login");
                                Message msg = new Message();
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("username",username);
                                params.put("password", Md5Utils.MD5(password));
                                URLConnManager.postParams(conn.getOutputStream(),params);
                                conn.connect();
                                int code = conn.getResponseCode();
                                if(code==200){
                                    InputStream in = conn.getInputStream();
                                    XmlPullParser parser = Xml.newPullParser();
                                    parser.setInput(in,"UTF-8");
                                    int type = parser.getEventType();
                                    String xmlResult = null;
                                    while(type!=XmlPullParser.END_DOCUMENT){
                                        switch (type){
                                            case XmlPullParser.START_TAG:
                                                if("result".equals(parser.getName())){
                                                    xmlResult = parser.nextText();
                                                }
                                                break;
                                            default:break;
                                        }
                                        type = parser.next();
                                    }
                                    in.close();
                                    conn.disconnect();
                                    String cookieValue = null;
                                    String cookie = conn.getHeaderField("Set-Cookie");
                                    cookieValue = cookie.substring(0,cookie.indexOf(";"));
                                    msg.what = GET_LOGINRESULT_SUCCESS;
                                    msg.arg1 = Integer.parseInt(xmlResult);
                                    msg.obj = cookieValue;
                                    handler.sendMessage(msg);
                                }else{
                                    handler.sendEmptyMessage(GET_LOGINRESULT_ERROR);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (XmlPullParserException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
