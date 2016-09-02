package com.neu.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.neu.activity.LoginActivity;
import com.neu.activity.MyContactActivity;
import com.neu.activity.MyaccountActivity;
import com.neu.activity.UpdatePasswordActivity;
import com.neu.entity.User;
import com.neu.utils.Constant;
import com.neu.utils.DialogUtils;
import com.neu.utils.URLConnManager;
import com.vector.my12306.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhang on 2016/8/30.
 */
public class MyFragment extends Fragment{
    private static final int GET_OLDPASSWORD_SUCCESS = 1;
    private static final int GET_OLDPASSWORD_ERROR = 2;
    private static final String TAG = "myFragment";
    private LinearLayout ll_contact,ll_account,ll_password;
    private Button btn_exit;
    private String inputPassword;
    private ProgressDialog progressDialog;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(progressDialog!=null){
                progressDialog.dismiss();
            }
            switch (msg.what){
                case GET_OLDPASSWORD_SUCCESS:
                    String result = (String) msg.obj;
                    if(result.equals("1")){
                        Intent i = new Intent(getActivity(), UpdatePasswordActivity.class);
                        startActivity(i);
                    }else{
                        Toast.makeText(getActivity(),"密码错误",Toast.LENGTH_LONG).show();
                    }
                    break;
                case GET_OLDPASSWORD_ERROR:
                    break;
                default:break;
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ll_account = (LinearLayout) getView().findViewById(R.id.ll_account);
        ll_contact = (LinearLayout) getView().findViewById(R.id.ll_contact);
        ll_password = (LinearLayout) getView().findViewById(R.id.ll_password);
        btn_exit = (Button)getView().findViewById(R.id.btn_exit);
        ll_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MyContactActivity.class);
                startActivity(i);
            }
        });
        ll_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MyaccountActivity.class);
                startActivity(i);
            }
        });
        ll_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("请输入原密码");
                builder.setIcon(android.R.drawable.btn_star);
                final EditText edInput = new EditText(getContext());
                builder.setView(edInput);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputPassword = edInput.getText().toString();
                        progressDialog = ProgressDialog.show(getActivity(),null,"匹对中",false,true);
                        progressDialog.show();
                        new Thread(){
                            @Override
                            public void run() {
                                try {
                                    HttpURLConnection conn = URLConnManager.getHttpURLConnection(Constant.HOST+"/otn/AccountPassword");
                                    SharedPreferences sp = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                                    String cookie = sp.getString("cookie","");
                                    if (!TextUtils.isEmpty(cookie)){
                                        conn.setRequestProperty("Cookie",cookie);
                                        Map<String,String> params = new HashMap<String, String>();
                                        params.put("oldPassword",inputPassword);
                                        params.put("action","query");
                                        URLConnManager.postParams(conn.getOutputStream(),params);
                                        conn.connect();
                                        int code = conn.getResponseCode();
                                        if(code==200){
                                            InputStream in = conn.getInputStream();
                                            String response = URLConnManager.converStreamToString(in);
                                            Log.d(TAG,response);
                                            Gson gson = new Gson();
                                            String result = gson.fromJson(response,String.class);
                                            Message message = new Message();
                                            message.what = GET_OLDPASSWORD_SUCCESS;
                                            message.obj = result;
                                            handler.sendMessage(message);
                                            in.close();
                                        }else{
                                            handler.sendEmptyMessage(GET_OLDPASSWORD_ERROR);
                                        }
                                        conn.disconnect();
                                    }
                                }catch (IOException e) {
                                    handler.sendEmptyMessage(GET_OLDPASSWORD_ERROR);
                                    e.printStackTrace();
                                }
                            }
                        }.start();
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
        });
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getActivity().getSharedPreferences("user",getContext().MODE_PRIVATE);
                sp.edit().remove("username");
                sp.edit().remove("password");
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

    }

}
