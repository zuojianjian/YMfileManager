package com.ygcompany.zuojj.ymfilemanager.view;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ygcompany.zuojj.ymfilemanager.R;
import com.ygcompany.zuojj.ymfilemanager.system.FileViewInteractionHub;

/**
 * gridview的item鼠标操作
 * Created by zuojj on 16-6-30.
 */
public class GridOnGenericMotionListener implements View.OnGenericMotionListener {
    private GridView file_path_grid;
    private FileViewInteractionHub mFileViewInteractionHub;
    //是否为第一次点击
    private boolean isFrist = true;
    //上一次点击位置
    private int prePosition;

    //上次按下返回键的系统时间
    private long lastBackTime = 0;
    //当前按下返回键的系统时间
    private long currentBackTime = 0;

    public GridOnGenericMotionListener(GridView file_path_grid, FileViewInteractionHub mFileViewInteractionHub) {
        this.mFileViewInteractionHub = mFileViewInteractionHub;
        this.file_path_grid = file_path_grid;
    }


    @Override
    public boolean onGenericMotion(View view, final MotionEvent event) {
        switch (event.getButtonState()) {
            case MotionEvent.BUTTON_PRIMARY:   // BUTTON_PRIMARY鼠标左键点击
                if (event.getButtonState() == MotionEvent.BUTTON_PRIMARY) {
                    //每一个item的点击和双击的判断
                    ItemDoubbleOrSingle();
                }
                break;
            case MotionEvent.BUTTON_SECONDARY:    //BUTTON_SECONDARY鼠标右键点击
                //点击鼠标右键且让点击事件不起作用只弹出contextmenu
                file_path_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mFileViewInteractionHub.onListItemRightClick(parent, view, position, id);
                        selectedItemBackground(parent, position);
                        mFileViewInteractionHub.addDialogSelectedItem(position);
                    }
                });
                mFileViewInteractionHub.shownContextDialog(mFileViewInteractionHub);
                break;
            case MotionEvent.BUTTON_TERTIARY:   //BUTTON_TERTIARY鼠标滚轮点击
                if (event.getButtonState() == MotionEvent.BUTTON_TERTIARY){
                    //每一个item的点击和双击的判断
                    ItemDoubbleOrSingle();
                }
                break;
            case MotionEvent.ACTION_SCROLL:   //ACTION_SCROLL鼠标滚轮滑动
                mFileViewInteractionHub.MouseScrollAction(event);
                break;
        }
        return false;
    }

    private void ItemDoubbleOrSingle() {
        //获取当前系统时间的毫秒数
        currentBackTime = System.currentTimeMillis();
        //比较上次按下返回键和当前按下返回键的时间差，如果大于2秒，则提示再按一次退出
        if (currentBackTime - lastBackTime > 800) {
            file_path_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //添加选中的背景
                    selectedItemBackground(parent, position);
                }
            });
            lastBackTime = currentBackTime;
        } else { //如果两次按下的时间差小于1秒，则退出程序
            file_path_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mFileViewInteractionHub.onListItemClick(parent, view, position, id);
                }
            });
        }
    }

    private void selectedItemBackground(AdapterView<?> parent, int position) {
        if (!isFrist){
            parent.getChildAt(prePosition).findViewById(R.id.rl_folder_bg).setSelected(false);
            parent.getChildAt(prePosition).findViewById(R.id.ll_folder_text_bg).setSelected(false);
        }else {
            parent.getChildAt(position).findViewById(R.id.rl_folder_bg).setSelected(true);
            parent.getChildAt(position).findViewById(R.id.ll_folder_text_bg).setSelected(true);
            prePosition = position;
            isFrist = false;
        }
        if (!isFrist){
            parent.getChildAt(position).findViewById(R.id.rl_folder_bg).setSelected(true);
            parent.getChildAt(position).findViewById(R.id.ll_folder_text_bg).setSelected(true);
            prePosition = position;
        }
    }
}
