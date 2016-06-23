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
    private TextView pop_refresh,pop_cancel_all,pop_copy,pop_delete,pop_send,pop_create,pop_exit;

    public PopWinShare(MainActivity mainActivity, View.OnClickListener paramOnClickListener, int paramInt1, int paramInt2){
        super(mainActivity);
        //窗口布局
        mainView = LayoutInflater.from(mainActivity).inflate(R.layout.popwin_share, null);
        //初始化
        pop_refresh = (TextView) mainView.findViewById(R.id.pop_refresh);
        pop_cancel_all = (TextView) mainView.findViewById(R.id.pop_cancel_all);
        pop_copy = (TextView) mainView.findViewById(R.id.pop_copy);
        pop_delete = (TextView) mainView.findViewById(R.id.pop_delete);
        pop_send = (TextView) mainView.findViewById(R.id.pop_send);
        pop_create = (TextView) mainView.findViewById(R.id.pop_create);
        pop_exit = (TextView) mainView.findViewById(R.id.pop_exit);
        //设置每个子布局的事件监听器
        if (paramOnClickListener != null){
            pop_refresh.setOnClickListener(paramOnClickListener);
            pop_cancel_all.setOnClickListener(paramOnClickListener);
            pop_copy.setOnClickListener(paramOnClickListener);
            pop_delete.setOnClickListener(paramOnClickListener);
            pop_send.setOnClickListener(paramOnClickListener);
            pop_create.setOnClickListener(paramOnClickListener);
            pop_exit.setOnClickListener(paramOnClickListener);
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
