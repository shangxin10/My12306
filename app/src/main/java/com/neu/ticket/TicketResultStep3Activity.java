package com.neu.ticket;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.neu.adapter.CustomerCheckedAdapter;
import com.neu.entity.Contact;
import com.neu.entity.Seat;
import com.neu.entity.Train;
import com.vector.my12306.R;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2016/9/1.
 */
public class TicketResultStep3Activity extends AppCompatActivity{
    private String TAG = "TicketResultStep3Activity";
    private TextView fromStationName,toStationName,starttime,endtime,durationtime,trainNum;
    private TextView seatName,seatNum,seatprice;
    private LinearLayout ll_addContact;
    private List<Contact> contactList;
    private CustomerCheckedAdapter customerCheckedAdapter;
    private Map<Integer,Contact> contactMap;
    private ListView lv_customer;
    private TextView tv_money;
    private Button btn_commit;
    private Train train;
    private Seat seat;
    private float price;
    private DecimalFormat format = new DecimalFormat("0.0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketresult3);
        setTitle("车票预订3/5");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fromStationName = (TextView) findViewById(R.id.fromStationName);
        toStationName = (TextView) findViewById(R.id.toStationName);
        starttime = (TextView) findViewById(R.id.starttime);
        endtime = (TextView) findViewById(R.id.endtime);
        durationtime = (TextView) findViewById(R.id.durationtime);
        trainNum = (TextView) findViewById(R.id.trainNum);
        seatName = (TextView) findViewById(R.id.seatName);
        seatNum = (TextView) findViewById(R.id.seatNum);
        seatprice = (TextView) findViewById(R.id.seatprice);
        tv_money = (TextView) findViewById(R.id.tv_money);
        ll_addContact = (LinearLayout) findViewById(R.id.ll_addContact);
        btn_commit = (Button) findViewById(R.id.btn_commit);
        lv_customer = (ListView) findViewById(R.id.lv_customer);

        contactList = new ArrayList<Contact>();
        contactMap = new HashMap<Integer, Contact>();
        customerCheckedAdapter = new CustomerCheckedAdapter(this,contactList);
        customerCheckedAdapter.setPriceListener(new MyPriceListener());
        lv_customer.setAdapter(customerCheckedAdapter);
        Intent i = getIntent();

        train = (Train) i.getSerializableExtra("train");
        if(train!=null){
            fromStationName.setText(train.getFromStationName());
            toStationName.setText(train.getToStationName());
            starttime.setText(train.getStartTime());
            endtime.setText(train.getArriveTime());
            durationtime.setText(train.getDurationTime());
            trainNum.setText(train.getTrainNo());
        }
        seat = (Seat) i.getSerializableExtra("seat");
        if(seat!=null){
            seatName.setText(seat.getSeatName());
            seatNum.setText(String.valueOf(seat.getSeatNum()));
            seatprice.setText("￥"+seat.getSeatPrice());
            price = seat.getSeatPrice();
        }

        ll_addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TicketResultStep3Activity.this,CustomerActivity.class);
                i.putExtra("contactMap", (Serializable)contactMap);
                startActivityForResult(i,100);
            }
        });
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contactList==null||contactList.size()==0){
                    Toast.makeText(TicketResultStep3Activity.this,"请选择乘车人",Toast.LENGTH_LONG).show();
                    return;
                }
                Intent i = new Intent(TicketResultStep3Activity.this,TicketResultStep4Activity.class);
                i.putExtra("train",train);
                i.putExtra("contactList", (Serializable) contactList);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==100){
            if(resultCode==101){
                Log.d(TAG,"====");
                 contactMap = (Map<Integer, Contact>) data.getSerializableExtra("contactMap");
                 contactList.clear();
                for(Map.Entry<Integer,Contact> entry:contactMap.entrySet()){
                    contactList.add(entry.getValue());
                    Log.d(TAG,entry.getValue().toString());
                }
                customerCheckedAdapter.notifyDataSetChanged();
                tv_money.setText("￥"+format.format(price*contactList.size())+"元");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
    private class MyPriceListener implements CustomerCheckedAdapter.PriceListener{

        @Override
        public void delete(int position,int count) {
            tv_money.setText("￥"+format.format(price*count)+"元");
            contactMap.remove(position);
        }
    }
}
