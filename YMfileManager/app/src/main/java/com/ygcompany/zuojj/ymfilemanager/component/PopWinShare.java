package com.ygcompany.zuojj.ymfilemanager.component;

import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ygcompany.zuojj.ymfilemanager.MainActivity;
import com.ygcompany.zuojj.ymfilemanager.R;

/**
 * 自定义PopupWindow用于设置分类
 * Created by zuojj on 16-5-23.
 */
public class PopWinShare extends PopupWindow {
    private View mainView;
    private TextView pop_size,pop_data,pop_type,pop_name;

    public PopWinShare(MainActivity mainActivity, View.OnClickListener paramOnClickListener, int paramInt1, int paramInt2){
        super(mainActivity);
        //窗口布局
        mainView = LayoutInflater.from(mainActivity).inflate(R.layout.popwin_share, null);
        //初始化
        pop_name = (TextView) mainView.findViewById(R.id.pop_name);
        pop_size = (TextView) mainView.findViewById(R.id.pop_size);
        pop_data = (TextView) mainView.findViewById(R.id.pop_data);
        pop_type = (TextView) mainView.findViewById(R.id.pop_type);
        //设置每个子布局的事件监听器
        if (paramOnClickListener != null){
            pop_size.setOnClickListener(paramOnClickListener);
            pop_data.setOnClickListener(paramOnClickListener);
            pop_type.setOnClickListener(paramOnClickListener);
            pop_name.setOnClickListener(paramOnClickListener);
        }
        setContentView(mainView);
        //设置宽度
        setWidth(paramInt1);
        //设置高度
        setHeight(paramInt2);
        //设置显示隐藏动画
        setAnimationStyle(R.style.AnimTools);
        //设置背景透明
        setBackgroundDrawable(new ColorDrawable());
    }
}
