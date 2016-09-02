package com.neu.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.neu.adapter.MyFragmentAdapter;
import com.neu.fragment.MyFragment;
import com.neu.fragment.OrderFragment;
import com.neu.fragment.TicketFragment;
import com.vector.my12306.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private RadioGroup radioGroup;
    private RadioButton rb_order,rb_mine,rb_ticket;
    private MyFragment myFragment;
    private OrderFragment orderFragment;
    private TicketFragment ticketFragment;
    private List<Fragment> fragmentList;
    private MyFragmentAdapter myFragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        rb_mine = (RadioButton) findViewById(R.id.rb_mine);
        rb_order = (RadioButton) findViewById(R.id.rb_order);
        rb_ticket = (RadioButton) findViewById(R.id.rb_ticket);

        radioGroup.setOnCheckedChangeListener(new MyCheckedChangeListener());
        myFragment = new MyFragment();
        orderFragment = new OrderFragment();
        ticketFragment = new TicketFragment();

        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(ticketFragment);
        fragmentList.add(orderFragment);
        fragmentList.add(myFragment);

        myFragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(),fragmentList);
        viewPager.setAdapter(myFragmentAdapter);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setCurrentItem(0);
        radioGroup.check(R.id.rb_ticket);
    }

    private class MyCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.rb_ticket:
                    viewPager.setCurrentItem(0,false);
                    break;
                case R.id.rb_order:
                    viewPager.setCurrentItem(1,false);
                    break;
                case R.id.rb_mine:
                viewPager.setCurrentItem(2,false);
                    break;
                default:break;
            }
        }
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0:
                    radioGroup.check(R.id.rb_ticket);
                    break;
                case 1:
                    radioGroup.check(R.id.rb_order);
                    break;
                case 2:
                    radioGroup.check(R.id.rb_mine);
                    break;
                default:break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }


}
