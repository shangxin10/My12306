package com.neu.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.neu.entity.Contact;
import com.neu.utils.Constant;
import com.neu.utils.DialogUtils;
import com.neu.utils.NetworkUtils;
import com.neu.utils.URLConnManager;
import com.vector.my12306.R;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhang on 2016/8/30.
 */
public class UpdateContactActivity extends AppCompatActivity {
    private static final String TAG = "updateContactActivity";
    private static final int UPDATECONTACT_SUCCESS =1;
    private static final int UPDATECONTACT_ERROR = 2;
    private RelativeLayout rl_name,rl_IDType,rl_IDnum,rl_customerType,rl_phone;
    private Button btn_save;
    private TextView tv_name,tv_IDType,tv_IDnum,tv_customerType,tv_phone;
    private ProgressDialog progressDialog;
    private String action="";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(progressDialog!=null){
                progressDialog.dismiss();
            }
            switch (msg.what){
                case UPDATECONTACT_SUCCESS:
                    String result = (String) msg.obj;
                    String info ="修改";
                    if(action.equals("remove")){
                        info = "删除";
                    }
                    if(result.equals("1")){
                        Toast.makeText(UpdateContactActivity.this,info+"成功",Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(UpdateContactActivity.this,info+"失败",Toast.LENGTH_LONG).show();
                        break;
                    }
                    break;
                case UPDATECONTACT_ERROR:
                    Toast.makeText(UpdateContactActivity.this,"服务器错误",Toast.LENGTH_LONG).show();
                    break;
                default:break;
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatecontact);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rl_name = (RelativeLayout) findViewById(R.id.rl_name);
        rl_IDType = (RelativeLayout) findViewById(R.id.rl_IDType);
        rl_IDnum = (RelativeLayout) findViewById(R.id.rl_IDnum);
        rl_customerType = (RelativeLayout) findViewById(R.id.rl_customerType);
        rl_phone = (RelativeLayout) findViewById(R.id.rl_phone);
        btn_save = (Button) findViewById(R.id.btn_save);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_IDType = (TextView) findViewById(R.id.tv_IDType);
        tv_IDnum = (TextView) findViewById(R.id.tv_IDnum);
        tv_customerType = (TextView) findViewById(R.id.tv_customerType);
        tv_phone = (TextView) findViewById(R.id.tv_phone);


        Intent i = getIntent();

        Contact contact = (Contact) i.getSerializableExtra("contact");

        tv_name.setText(contact.getName());
        tv_IDType.setText(contact.getIdType());
        tv_IDnum.setText(contact.getId());
        tv_customerType.setText(contact.getType());
        tv_phone.setText(contact.getTel());


        rl_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setText(tv_name,"请输入姓名");
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
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = ProgressDialog.show(UpdateContactActivity.this,null,"修改中,请稍后",false,true);
                action = "update";
                contactThread.start();
            }
        });


    }

    private Thread contactThread = new Thread(){
        @Override
        public void run() {
            super.run();
            if(!NetworkUtils.check(UpdateContactActivity.this)){
                Toast.makeText(UpdateContactActivity.this,R.string.net_error,Toast.LENGTH_LONG).show();
                return;
            }
            try{
                HttpURLConnection conn = URLConnManager.getHttpURLConnection(Constant.HOST+ "/otn/Passenger");
                SharedPreferences sp = getSharedPreferences("user",MODE_PRIVATE);
                String cookie = sp.getString("cookie","");
                if(!TextUtils.isEmpty(cookie)){
                    conn.setRequestProperty("Cookie",cookie);
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("姓名",tv_name.getText().toString());
                    params.put("证件类型",tv_IDType.getText().toString());
                    params.put("证件号码",tv_IDnum.getText().toString());
                    params.put("乘客类型",tv_customerType.getText().toString());
                    params.put("电话",tv_phone.getText().toString());
                    params.put("action",action);
                    URLConnManager.postParams(conn.getOutputStream(),params);
                    conn.connect();
                    int code = conn.getResponseCode();
                    if(code==200){
                        InputStream in = conn.getInputStream();
                        String response = URLConnManager.converStreamToString(in);
                        Log.d(TAG,response);
                        Gson gson = new Gson();
                        String result = gson.fromJson(response,String.class);
                        Message msg = new Message();
                        msg.what = UPDATECONTACT_SUCCESS;
                        msg.obj = result;
                        handler.sendMessage(msg);
                        in.close();
                    }else{
                        handler.sendEmptyMessage(UPDATECONTACT_ERROR);
                    }
                    conn.disconnect();
                }
            }catch(Exception e){
                e.printStackTrace();
                handler.sendEmptyMessage(UPDATECONTACT_ERROR);
            }
        }

    };
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
        getMenuInflater().inflate(R.menu.menu_contactdelete,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.deleteContact:
                action="remove";
                contactThread.start();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
