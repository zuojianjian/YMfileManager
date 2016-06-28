package com.ygcompany.zuojj.ymfilemanager.component;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ygcompany.zuojj.ymfilemanager.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 关于本应用介绍页面
 * Created by zuojj on 16-6-24.
 */
public class AboutActivity extends Activity implements View.OnClickListener {
    //初始化组件
    @Bind(R.id.ll_about_back)
    LinearLayout ll_about_back;
    @Bind(R.id.tv_public)
    TextView tv_public;
    @Bind(R.id.tv_discuss)
    TextView tv_discuss;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(AboutActivity.this);
        initData();
    }

    private void initData() {
        ll_about_back.setOnClickListener(this);
        tv_public.setOnClickListener(this);
        tv_discuss.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_about_back:
                finish();
                break;
            case R.id.tv_public:
                //TODO
                break;
            case R.id.tv_discuss:
                //TODO
                break;
        }
    }
}
