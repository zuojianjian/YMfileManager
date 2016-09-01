package com.emindsoft.openthos.view;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.emindsoft.openthos.R;
import com.emindsoft.openthos.system.FileViewInteractionHub;

/**
 * listview的item鼠标操作
 * Created by zuojj on 16-6-30.
 */
public class ListOnGenericMotionListener implements View.OnGenericMotionListener {
    private ListView file_path_list;
    private FileViewInteractionHub mFileViewInteractionHub;
    private int mLastClickId;
    private long mLastClickTime = 0;
    private boolean mIsCtrlPress;

    public ListOnGenericMotionListener(ListView file_path_list, FileViewInteractionHub mFileViewInteractionHub, boolean isCtrlPress) {
        this.mFileViewInteractionHub = mFileViewInteractionHub;
        this.file_path_list = file_path_list;
        this.mIsCtrlPress = isCtrlPress;
    }

    @Override
    public boolean onGenericMotion(View view, MotionEvent event) {
        switch (event.getButtonState()) {
            case MotionEvent.BUTTON_PRIMARY:   // BUTTON_PRIMARY鼠标左键点击
                if (event.getButtonState() == MotionEvent.BUTTON_PRIMARY) {
                    if (!mIsCtrlPress){
                        //每一个item的点击和双击的判断
                        file_path_list.setOnItemClickListener(new ListItemClick(event));
                    }
                }
                break;
            case MotionEvent.BUTTON_SECONDARY:    //BUTTON_SECONDARY鼠标右键点击
                //点击鼠标右键且让点击事件不起作用只弹出contextmenu
                file_path_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (mLastClickId != position){
                            mFileViewInteractionHub.clearSelection();
                        }
                        view.setSelected(true);
                        parent.getChildAt(position).findViewById(R.id.ll_list_item_bg).setSelected(true);
//                        mFileViewInteractionHub.addDialogSelectedItem(position);
                    }
                });
//                mFileViewInteractionHub.shownContextDialog(mFileViewInteractionHub);
                break;
            case MotionEvent.BUTTON_TERTIARY:   //BUTTON_TERTIARY鼠标滚轮点击
                if (event.getButtonState() == MotionEvent.BUTTON_TERTIARY){
                    //每一个item的点击和双击的判断
                    file_path_list.setOnItemClickListener(new ListItemClick(event));
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
        private MotionEvent event;
        public ListItemClick(MotionEvent event) {
            this.event = event;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mLastClickId == position && (Math.abs(System.currentTimeMillis() - mLastClickTime) < 1500)) {
                view.setSelected(false);

                String doubleTag = "double";
                mFileViewInteractionHub.onListItemClick(position, doubleTag, event, null);
                mFileViewInteractionHub.clearSelection();
            } else {
                view.setSelected(true);
                parent.getChildAt(position).findViewById(R.id.ll_list_item_bg).setSelected(true);
//                mFileViewInteractionHub.addDialogSelectedItem(position);
                mLastClickTime = System.currentTimeMillis();
                mLastClickId = position;
            }
        }
    }
}
