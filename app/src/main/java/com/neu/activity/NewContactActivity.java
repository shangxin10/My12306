package com.neu.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
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
 * Created by zhang on 2016/8/30.
 */
public class NewContactActivity extends AppCompatActivity {
    private static final String TAG = "newContactActivity";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static final int ADDCONTACT_SUCCESS = 1;
    private static final int ADDCONTACT_FAIL = 2;
    private RelativeLayout rl_name, rl_IDType, rl_IDnum, rl_customerType, rl_phone;
    private Button btn_add;
    private TextView tv_name, tv_IDType, tv_IDnum, tv_customerType, tv_phone;
    private ProgressDialog progressDialog;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (progressDialog!=null){
                progressDialog.dismiss();
            }
            switch(msg.what){
                case ADDCONTACT_SUCCESS:
                    String result = (String) msg.obj;
                    if(result.equals("1")){
                        Toast.makeText(NewContactActivity.this,"添加成功",Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(NewContactActivity.this,"添加失败",Toast.LENGTH_LONG).show();
                        break;
                    }
                    break;
                case ADDCONTACT_FAIL:
                    Toast.makeText(NewContactActivity.this,"服务器错误，请重试",Toast.LENGTH_LONG).show();
                    break;
                default:break;
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newcontact);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rl_name = (RelativeLayout) findViewById(R.id.rl_name);
        rl_IDType = (RelativeLayout) findViewById(R.id.rl_IDType);
        rl_IDnum = (RelativeLayout) findViewById(R.id.rl_IDnum);
        rl_customerType = (RelativeLayout) findViewById(R.id.rl_customerType);
        rl_phone = (RelativeLayout) findViewById(R.id.rl_phone);
        btn_add = (Button) findViewById(R.id.btn_add);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_IDType = (TextView) findViewById(R.id.tv_IDType);
        tv_IDnum = (TextView) findViewById(R.id.tv_IDnum);
        tv_customerType = (TextView) findViewById(R.id.tv_customerType);
        tv_phone = (TextView) findViewById(R.id.tv_phone);


        rl_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText(tv_name,"请输入姓名");
            }
        });
        rl_IDType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] types =  new String[]{"身份证","学生证","军人证"};
                setType(tv_IDType,"请选择证件类型",types);
            }
        });
        rl_IDnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText(tv_IDnum,"请输入身份证号码");
            }
        });
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
                setText(tv_phone,"请输入手机号码");
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetworkUtils.check(NewContactActivity.this)){
                    Toast.makeText(NewContactActivity.this,R.string.net_error,Toast.LENGTH_LONG).show();
                    return;
                }
                progressDialog = ProgressDialog.show(NewContactActivity.this,null,"添加中",false,true);
//                progressDialog.show();
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            HttpURLConnection conn = URLConnManager.getHttpURLConnection(Constant.HOST+"/otn/Passenger");
                            SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
                            String cookie = sp.getString("cookie","");
                            if (!TextUtils.isEmpty(cookie)){
                                conn.setRequestProperty("Cookie",cookie);
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("姓名",tv_name.getText().toString());
                                params.put("证件类型",tv_IDType.getText().toString());
                                params.put("证件号码",tv_IDnum.getText().toString());
                                params.put("乘客类型",tv_customerType.getText().toString());
                                params.put("电话",tv_phone.getText().toString());
                                params.put("action","new");
                                URLConnManager.postParams(conn.getOutputStream(),params);
                                conn.connect();
                                int code = conn.getResponseCode();
                                if(code==200){
                                    InputStream in = conn.getInputStream();
                                    String response = URLConnManager.converStreamToString(in);
                                    Gson gson = new Gson();
                                    String result = gson.fromJson(response,String.class);
                                    Message message = new Message();
                                    message.what = ADDCONTACT_SUCCESS;
                                    message.obj = result;
                                    handler.sendMessage(message);
                                    in.close();
                                }else{
                                    handler.sendEmptyMessage(ADDCONTACT_FAIL);
                                }
                                conn.disconnect();
                            }
                        }catch (IOException e) {
                            handler.sendEmptyMessage(ADDCONTACT_FAIL);
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
        getMenuInflater().inflate(R.menu.menu_contactadd, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchcontact:
                Log.d(TAG,"=====berfore");
                if(Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.M){
                    int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
                    if(hasWriteContactsPermission!= PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                                REQUEST_CODE_ASK_PERMISSIONS);
                        break;
                    }
                }
                Log.d(TAG,"====click");
                getContacts();
                break;
            case android.R.id.home:
                Log.d(TAG,"====finish");
                finish();
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getContacts(){
        ContentResolver cr = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = cr.query(uri,null,null,null,null);
        List<String> contacts = new ArrayList<String>();
        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contacts.add(name+"("+phone+")");
        }
        cursor.close();
        if(contacts.size()==0){
            new AlertDialog.Builder(this)
                    .setTitle("请选择")
                    .setMessage("通讯录为空")
                    .setNegativeButton("取消",null).show();
        }else{
            final String [] items = new String[contacts.size()];
            contacts.toArray(items);
            new AlertDialog.Builder(this).setTitle("请选择").setItems(items,new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String select = items[which];
                    String name = select.substring(0,select.indexOf("("));
                    String phone = select.substring(select.indexOf("(")+1,select.indexOf(")"));
                    tv_name.setText(name);
                    tv_phone.setText(phone);
                }
            }).setNegativeButton("取消",null).show();
        }
    }

}
