package com.emindsoft.filemanager.component;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emindsoft.filemanager.R;

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
    @Bind(R.id.tv_public_official)
    TextView tv_public_official;
    @Bind(R.id.tv_official_help)
    TextView tv_official_help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(HelpActivity.this);
        initData();
    }

    private void initData() {
        ll_help_back.setOnClickListener(this);
        tv_public_official.setOnClickListener(this);
        tv_official_help.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        switch (view.getId()) {
            case R.id.ll_help_back:
                finish();
                break;
            case R.id.tv_official_help:
                String url = "http://www.emindsoft.com.cn/";
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.tv_public_official:
                url = "http://www.emindsoft.com.cn/";
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
        }
    }
}

