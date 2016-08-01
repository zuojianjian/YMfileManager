package com.emindsoft.filemanager.view;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.emindsoft.filemanager.R;
import com.emindsoft.filemanager.system.FileViewInteractionHub;
import com.emindsoft.filemanager.utils.L;

/**
 * gridview的item鼠标操作
 * Created by zuojj on 16-6-30.
 */
public class GridOnGenericMotionListener implements View.OnGenericMotionListener {
    private static boolean flag = false;// 用来判断是否已经执行双击事件
    private static int clickNum = 0;// 用来判断是否该执行双击事件

    private GridView file_path_grid;
    private FileViewInteractionHub mFileViewInteractionHub;
    //是否为第一次点击
    private boolean isFrist = true;
    //上一次点击位置
    private int prePosition;
    private int  mLastClickId;
    private long mLastClickTime = 0;

    public GridOnGenericMotionListener(GridView file_path_grid, FileViewInteractionHub mFileViewInteractionHub) {
        this.mFileViewInteractionHub = mFileViewInteractionHub;
        this.file_path_grid = file_path_grid;
    }


    @Override
    public boolean onGenericMotion(View view, final MotionEvent event) {
        switch (event.getButtonState()) {
            case MotionEvent.BUTTON_PRIMARY:   // BUTTON_PRIMARY鼠标左键点击
//                if (mFileViewInteractionHub.getSelectedFileList() != null){
//                    //每一个item的单击和双击的判断
//                    mFileViewInteractionHub.clearSelection();
//                    ItemDoubbleOrSingle(file_path_grid);
//                }
                file_path_grid.setOnItemClickListener(new GridItemClick());
                break;
            case MotionEvent.BUTTON_SECONDARY:    //BUTTON_SECONDARY鼠标右键点击
                //点击鼠标右键且让点击事件不起作用只弹出contextmenu
                file_path_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                    //每一个item的点击和双击的判断
                file_path_grid.setOnItemClickListener(new GridItemClick());
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

    private class GridItemClick implements AdapterView.OnItemClickListener {
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

//    private void ItemDoubbleOrSingle(final GridView file_path_grid) {
//        flag = false;// 每次点击鼠标初始化双击事件执行标志为false
//
//        if (clickNum == 1) {// 当clickNum==1时执行双击事件
//            mouseDoubleClicked(file_path_grid);// 执行双击事件
//            clickNum = 0;// 初始化双击事件执行标志为0
//            flag = true;// 双击事件已执行,事件标志为true
//        }
//        // 定义定时器
//        Timer timer = new Timer();
//
//        // 定时器开始执行,延时0.2秒后确定是否执行单击事件
//        timer.schedule(new TimerTask() {
//            private int n = 0;// 记录定时器执行次数
//
//            public void run() {
//                if (GridOnGenericMotionListener.flag) {// 如果双击事件已经执行,那么直接取消单击执行
//                    n = 0;
//                    GridOnGenericMotionListener.clickNum = 0;
//                    this.cancel();
//                    return;
//                }
//                if (n == 1) {// 定时器等待0.2秒后,双击事件仍未发生,执行单击事件
//                    mouseSingleClicked(file_path_grid);// 执行单击事件
//                    GridOnGenericMotionListener.flag = true;
//                    GridOnGenericMotionListener.clickNum = 0;
//                    n = 0;
//                    this.cancel();
//                    return;
//                }
//                clickNum++;
//                n++;
//            }
//        }, new Date(), 500);
//    }

//    private void selectedItemBackground(AdapterView<?> parent, int position) {
//        if (isFrist) {
//            parent.getChildAt(position).findViewById(R.id.rl_folder_bg).setSelected(true);
//            parent.getChildAt(position).findViewById(R.id.ll_folder_text_bg).setSelected(true);
//            prePosition = position;
//            isFrist = false;
//        }
//        if (!isFrist || position != prePosition) {
//            if (parent.getChildAt(prePosition) != null) {
//
//                parent.getChildAt(prePosition).findViewById(R.id.rl_folder_bg).setSelected(false);
//                parent.getChildAt(prePosition).findViewById(R.id.ll_folder_text_bg).setSelected(false);
//            }
//            parent.getChildAt(position).findViewById(R.id.rl_folder_bg).setSelected(true);
//            parent.getChildAt(position).findViewById(R.id.ll_folder_text_bg).setSelected(true);
//            prePosition = position;
//        }
//    }

//
//    /**
//     * 鼠标单击事件
//     * @param file_path_grid
//     */
//    public void mouseSingleClicked(GridView file_path_grid) {
//        file_path_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                mFileViewInteractionHub.clearSelection();
//                //添加选中的背景
//                selectedItemBackground(parent, position);
//                mFileViewInteractionHub.addDialogSelectedItem(position);
//                L.e("Real Single Clicked!");
//            }
//        });
//        L.e("Single Clicked!");
//    }
//
//    /**
//     * 鼠标双击事件
//     * @param file_path_grid
//     *
//     */
//    public void mouseDoubleClicked(GridView file_path_grid) {
//        file_path_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mFileViewInteractionHub.clearSelection();
//                selectedItemBackground(parent, position);
//                String doubleTag = "double";
//                mFileViewInteractionHub.onListItemClick(parent, view, position, id, doubleTag);
//            }
//        });
//        L.e("Doublc Clicked!");
//    }
}
