package com.ygcompany.zuojj.ymfilemanager.component;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.ygcompany.zuojj.ymfilemanager.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 帮助和反馈页面
 * Created by zuojj on 16-6-24.
 */
public class HelpActivity extends Activity implements View.OnClickListener {
    //初始化组件
    @Bind(R.id.ll_help_back)
    LinearLayout ll_help_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(HelpActivity.this);
        initData();
    }

    private void initData() {
        ll_help_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_help_back:
                finish();
                break;
        }
    }
}

