package com.emindsoft.openthos.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.emindsoft.openthos.R;
import com.emindsoft.openthos.system.FileViewInteractionHub;
import com.emindsoft.openthos.utils.L;
import com.emindsoft.openthos.utils.T;

/**
 * gridview的item鼠标操作
 * Created by zuojj on 16-6-30.
 */
public class GridOnGenericMotionListener implements View.OnGenericMotionListener {
    private GridView file_path_grid;
    private FileViewInteractionHub mFileViewInteractionHub;
    private int mLastClickId;
    private long mLastClickTime = 0;
    private Context context;
    private boolean mIsCtrlPress;

    public GridOnGenericMotionListener(Context mActivity, GridView file_path_grid, FileViewInteractionHub mFileViewInteractionHub, boolean isCtrlPress) {
        this.mFileViewInteractionHub = mFileViewInteractionHub;
        this.file_path_grid = file_path_grid;
        this.context = mActivity;
        this.mIsCtrlPress = isCtrlPress;
    }


    @Override
    public boolean onGenericMotion(View view, final MotionEvent event) {
        switch (event.getButtonState()) {
            case MotionEvent.BUTTON_PRIMARY:   // BUTTON_PRIMARY鼠标左键点击
                if (!mIsCtrlPress) {
                    L.e("!mIsCtrlPress_____________________",mIsCtrlPress+"");
                    T.showShort(context,"单选！");
                    file_path_grid.setOnItemClickListener(new GridLeftItemClickListener(event));
                }
                break;
            case MotionEvent.BUTTON_SECONDARY:    //BUTTON_SECONDARY鼠标右键点击
                //点击鼠标右键且让点击事件不起作用只弹出contextmenu
                file_path_grid.setOnItemClickListener(new GridRigntItemClickListener());
                mFileViewInteractionHub.shownContextDialog(mFileViewInteractionHub, event);
                break;
            case MotionEvent.BUTTON_TERTIARY:   //BUTTON_TERTIARY鼠标滚轮点击
                //每一个item的点击和双击的判断
                file_path_grid.setOnItemClickListener(new GridLeftItemClickListener(event));
                break;
            case MotionEvent.ACTION_SCROLL:   //ACTION_SCROLL鼠标滚轮滑动
                mFileViewInteractionHub.MouseScrollAction(event);
                break;
            case MotionEvent.ACTION_HOVER_ENTER: //ACTION_HOVER_ENTER 鼠标的悬停事件监听
                L.d("ACTION_HOVER_ENTER");
                break;
        }
        return false;
    }

    private class GridLeftItemClickListener implements AdapterView.OnItemClickListener {
        private MotionEvent event;

        public GridLeftItemClickListener(MotionEvent event) {
            this.event = event;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mLastClickId == position && (Math.abs(System.currentTimeMillis() - mLastClickTime) < 1200)) {
                String doubleTag = "double";
                mFileViewInteractionHub.onListItemClick(parent, view, position, id, doubleTag, event);
                mFileViewInteractionHub.clearSelection();
            } else {
                parent.getChildAt(position).findViewById(R.id.ll_grid_item_bg).setSelected(true);
                mFileViewInteractionHub.addDialogSelectedItem(position);
                mLastClickTime = System.currentTimeMillis();
                mLastClickId = position;
            }
        }
    }

    private class GridRigntItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            parent.getChildAt(position).findViewById(R.id.ll_grid_item_bg).setSelected(true);
            mFileViewInteractionHub.addDialogSelectedItem(position);
        }
    }
}
