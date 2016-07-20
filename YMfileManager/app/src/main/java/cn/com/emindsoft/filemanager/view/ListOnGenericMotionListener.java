package cn.com.emindsoft.filemanager.view;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import cm.com.emindsoft.filemanager.R;
import cn.com.emindsoft.filemanager.system.FileViewInteractionHub;
import cn.com.emindsoft.filemanager.utils.L;

/**
 * listview的item鼠标操作
 * Created by zuojj on 16-6-30.
 */
public class ListOnGenericMotionListener implements View.OnGenericMotionListener {
    private static boolean flag = false;// 用来判断是否已经执行双击事件
    private static int clickNum = 0;// 用来判断是否该执行双击事件
    private ListView file_path_list;
    private FileViewInteractionHub mFileViewInteractionHub;
    //是否为第一次点击
    private boolean isFrist = true;
    //上一次点击位置
    private int prePosition;

    //上次按下返回键的系统时间
    private long lastBackTime = 0;

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
                    ItemDoubbleOrSingle();
                }
                break;
            case MotionEvent.BUTTON_SECONDARY:    //BUTTON_SECONDARY鼠标右键点击
                //点击鼠标右键且让点击事件不起作用只弹出contextmenu
                file_path_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        flag = false;// 每次点击鼠标初始化双击事件执行标志为false

        if (clickNum == 1) {// 当clickNum==1时执行双击事件
            this.mouseDoubleClicked();// 执行双击事件
            clickNum = 0;// 初始化双击事件执行标志为0
            flag = true;// 双击事件已执行,事件标志为true
        }

        // 定义定时器
        Timer timer = new Timer();

        // 定时器开始执行,延时0.2秒后确定是否执行单击事件
        timer.schedule(new TimerTask() {
            private int n = 0;// 记录定时器执行次数

            public void run() {
                if (ListOnGenericMotionListener.flag) {// 如果双击事件已经执行,那么直接取消单击执行
                    n = 0;
                    ListOnGenericMotionListener.clickNum = 0;
                    this.cancel();
                    return;
                }
                if (n == 1) {// 定时器等待0.2秒后,双击事件仍未发生,执行单击事件
                    mouseSingleClicked();// 执行单击事件
                    ListOnGenericMotionListener.flag = true;
                    ListOnGenericMotionListener.clickNum = 0;
                    n = 0;
                    this.cancel();
                    return;
                }
                clickNum++;
                n++;
            }
        }, new Date(), 500);
    }


    /**
     * 鼠标单击事件
     */
    public void mouseSingleClicked() {
        file_path_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //添加选中的背景
                selectedItemBackground(parent, position);
                mFileViewInteractionHub.addDialogSelectedItem(position);
            }
        });
        L.e("Single Clicked!");
    }

    /**
     * 鼠标双击事件
     */
    public void mouseDoubleClicked() {
        file_path_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemBackground(parent, position);
                String doubleTag = "double";
                mFileViewInteractionHub.onListItemClick(parent, view, position, id, doubleTag);
                L.e("mFileViewInteractionHub");
            }
        });
        L.e("Doublc Clicked!");
    }

    private void selectedItemBackground(AdapterView<?> parent, int position) {
        if (isFrist) {
            parent.getChildAt(position).findViewById(R.id.ll_list_item_bg).setSelected(true);
            prePosition = position;
            isFrist = false;
        }
        if (!isFrist || position != prePosition) {
            if (parent.getChildAt(prePosition) != null) {
                parent.getChildAt(prePosition).findViewById(R.id.ll_list_item_bg).setSelected(false);
            }
            parent.getChildAt(position).findViewById(R.id.ll_list_item_bg).setSelected(true);
            prePosition = position;
        }
    }
}
