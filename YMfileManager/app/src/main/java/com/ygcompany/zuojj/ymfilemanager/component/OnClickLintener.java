package com.ygcompany.zuojj.ymfilemanager.component;

import android.content.Intent;
import android.view.View;

import com.ygcompany.zuojj.ymfilemanager.MainActivity;
import com.ygcompany.zuojj.ymfilemanager.R;

/**
 * Created by zuojj on 16-6-24.
 */
public class OnClickLintener implements View.OnClickListener {
    //顶部poupwindow各个item标识
    private static final String POP_REFRESH = "pop_refresh";
    private static final String POP_CANCEL_ALL = "pop_cancel_all";
    private static final String POP_COPY = "pop_copy";
    private static final String POP_DELETE = "pop_delete";
    private static final String POP_SEND = "pop_send";
    private static final String POP_CREATE = "pop_create";
    private static final String VIEW_OR_DISMISS = "view_or_dismiss";
    //选项菜单的点击监听
    private String menu_tag;
    private MainActivity mainActivity;
    private Intent intent;

    public OnClickLintener(String menu_tag, MainActivity mainActivity) {
        this.menu_tag = menu_tag;
        this.mainActivity = mainActivity;
    }

    @Override
    public void onClick(View view) {
        if (menu_tag.equals("iv_menu")) {  //title右侧选项菜单栏
            switch (view.getId()) {
                case R.id.pop_menu_refresh: //刷新
                    //发送广播通知选中哪个item
                    sendBroadcastMessage("iv_menu", POP_REFRESH);
                    break;
                case R.id.pop_menu_cancel_all: //全选或取消
                    sendBroadcastMessage("iv_menu", POP_CANCEL_ALL);
                    break;
                case R.id.pop_menu_copy:  //复制
                    sendBroadcastMessage("iv_menu", POP_COPY);
                    break;
                case R.id.pop_menu_delete: //删除
                    sendBroadcastMessage("iv_menu", POP_DELETE);
                    break;
                case R.id.pop_menu_send:  //发送
                    sendBroadcastMessage("iv_menu", POP_SEND);
                    break;
                case R.id.pop_menu_create:  //创建
                    sendBroadcastMessage("iv_menu", POP_CREATE);
                    break;
                case R.id.pop_menu_exit: //退出
                    mainActivity.finish();
                    break;
                default:
                    break;
            }
        } else if (menu_tag.equals("iv_setting")) {  //title左侧菜单栏
            switch (view.getId()) {
                case R.id.pop_setting_view: //显示隐藏文件
                    //发送广播通知选中哪个item
                    sendBroadcastMessage("iv_menu", VIEW_OR_DISMISS);
                    break;
                case R.id.pop_setting_relative:  //关于
                    intent = new Intent(mainActivity,AboutActivity.class);
                    mainActivity.startActivity(intent);
                    break;
                case R.id.pop_setting_help:  //帮助和反馈
                    intent = new Intent(mainActivity,HelpActivity.class);
                    mainActivity.startActivity(intent);
                    break;
                case R.id.pop_setting_exit:  //退出
                    mainActivity.finish();
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 发送广播
     * @param name 哪一个按钮点击的标识
     * @param tag  要传递的字段
     */
    private void sendBroadcastMessage(String name, String tag) {
        Intent intent = new Intent();
        if (name.equals("iv_menu")) {
            intent.setAction("com.switchmenu");
            intent.putExtra("pop_menu", tag);
        }
        mainActivity.sendBroadcast(intent);
    }
}
