package com.ygcompany.zuojj.ymfilemanager.view;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ygcompany.zuojj.ymfilemanager.system.FileViewInteractionHub;

/**
 * listview的item鼠标操作
 * Created by zuojj on 16-6-30.
 */
public class ListOnGenericMotionListener implements View.OnGenericMotionListener {
    private ListView file_path_list;
    private FileViewInteractionHub mFileViewInteractionHub;

    public ListOnGenericMotionListener(ListView file_path_list, FileViewInteractionHub mFileViewInteractionHub) {
        this.mFileViewInteractionHub = mFileViewInteractionHub;
        this.file_path_list = file_path_list;
    }

    @Override
    public boolean onGenericMotion(View view, MotionEvent event) {
        switch (event.getButtonState()) {
            case MotionEvent.BUTTON_PRIMARY:   // BUTTON_PRIMARY鼠标左键点击
                file_path_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mFileViewInteractionHub.onListItemClick(parent, view, position, id);
                    }
                });
                break;
            case MotionEvent.BUTTON_SECONDARY:    //BUTTON_SECONDARY鼠标右键点击
                //点击鼠标右键且让点击事件不起作用只弹出contextmenu
                file_path_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mFileViewInteractionHub.onListItemRightClick(parent, view, position, id);
                    }
                });
                mFileViewInteractionHub.shownContextDialog(mFileViewInteractionHub);
                break;
            case MotionEvent.BUTTON_TERTIARY:   //BUTTON_TERTIARY鼠标滚轮点击
                file_path_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mFileViewInteractionHub.onListItemClick(parent, view, position, id);
                    }
                });
                break;
            case MotionEvent.ACTION_SCROLL:   //ACTION_SCROLL鼠标滚轮滑动
                mFileViewInteractionHub.MouseScrollAction(event);
                break;
        }
        return false;
    }
}
