package com.emindsoft.filemanager.component;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.emindsoft.filemanager.R;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 关于本应用介绍页面
 * Created by zuojj on 16-6-24.
 */
public class AboutActivity extends Activity implements View.OnClickListener {
    //初始化组件
    @Bind(R.id.iv_about_back)
    ImageView iv_about_back;
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
        iv_about_back.setOnClickListener(this);
        tv_public.setOnClickListener(this);
        tv_discuss.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        switch (view.getId()){
            case R.id.iv_about_back:
                finish();
                break;
            case R.id.tv_public:
                String url = "http://www.emindsoft.com.cn/";
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.tv_discuss:
                url = "http://www.emindsoft.com.cn/";
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
        }
    }
}
