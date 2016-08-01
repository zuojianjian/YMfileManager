package com.emindsoft.filemanager.view;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.emindsoft.filemanager.R;
import com.emindsoft.filemanager.system.FileViewInteractionHub;

/**
 * listview的item鼠标操作
 * Created by zuojj on 16-6-30.
 */
public class ListOnGenericMotionListener implements View.OnGenericMotionListener {
    private ListView file_path_list;
    private FileViewInteractionHub mFileViewInteractionHub;
    private int mLastClickId;
    private long mLastClickTime = 0;

    public ListOnGenericMotionListener(ListView file_path_list, FileViewInteractionHub mFileViewInteractionHub) {
        this.mFileViewInteractionHub = mFileViewInteractionHub;
        this.file_path_list = file_path_list;
    }

    @Override
    public boolean onGenericMotion(View view, MotionEvent event) {
        switch (event.getButtonState()) {
            case MotionEvent.BUTTON_PRIMARY:   // BUTTON_PRIMARY鼠标左键点击
                if (event.getButtonState() == MotionEvent.BUTTON_PRIMARY) {
                    //每一个item的点击和双击的判断
                    file_path_list.setOnItemClickListener(new ListItemClick());
                }
                break;
            case MotionEvent.BUTTON_SECONDARY:    //BUTTON_SECONDARY鼠标右键点击
                //点击鼠标右键且让点击事件不起作用只弹出contextmenu
                file_path_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mFileViewInteractionHub.onListItemRightClick(parent, view, position, id);
                        view.setSelected(true);
                        parent.getChildAt(position).findViewById(R.id.rl_folder_bg).setSelected(true);
                        parent.getChildAt(position).findViewById(R.id.ll_folder_text_bg).setSelected(true);
                        mFileViewInteractionHub.addDialogSelectedItem(position);
                    }
                });
                mFileViewInteractionHub.shownContextDialog(mFileViewInteractionHub);
                break;
            case MotionEvent.BUTTON_TERTIARY:   //BUTTON_TERTIARY鼠标滚轮点击
                if (event.getButtonState() == MotionEvent.BUTTON_TERTIARY){
                    //每一个item的点击和双击的判断
                }
                break;
            case MotionEvent.ACTION_SCROLL:   //ACTION_SCROLL鼠标滚轮滑动
                mFileViewInteractionHub.MouseScrollAction(event);
                break;
            default:
                break;
        }
        return false;
    }

    private class ListItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mLastClickId == position && (Math.abs(System.currentTimeMillis() - mLastClickTime) < 1500)) {
                view.setSelected(false);

                String doubleTag = "double";
                mFileViewInteractionHub.onListItemClick(parent, view, position, id, doubleTag);
                mFileViewInteractionHub.clearSelection();
            } else {
                view.setSelected(true);
                parent.getChildAt(position).findViewById(R.id.rl_folder_bg).setSelected(true);
                parent.getChildAt(position).findViewById(R.id.ll_folder_text_bg).setSelected(true);
                mFileViewInteractionHub.addDialogSelectedItem(position);
                mLastClickTime = System.currentTimeMillis();
                mLastClickId = position;
            }
        }
    }
}
