package com.neu.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.vector.my12306.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhang on 2016/8/11.
 */
public class RegisterActivity extends AppCompatActivity {

    private RadioButton rb_order,rb_mine,rb_ticket;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rb_order = (RadioButton) findViewById(R.id.rb_order);
        rb_mine = (RadioButton) findViewById(R.id.rb_mine);
        rb_ticket = (RadioButton) findViewById(R.id.rb_ticket);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
    }
}
