package com.emindsoft.openthos.component;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.emindsoft.openthos.R;
import com.emindsoft.openthos.system.FileSortHelper;
import com.emindsoft.openthos.system.FileViewInteractionHub;

/**
 * Created by zuojj on 16-8-10.
 */
public class MenuSecondDialog extends Dialog implements View.OnClickListener {
    //sort分类
    private TextView dialog_sort_name;
    private TextView dialog_sort_size;
    private TextView dialog_sort_time;
    private TextView dialog_sort_type;

    FileViewInteractionHub mFileViewInteractionHub;

    public MenuSecondDialog(Context context, int i, FileViewInteractionHub mFileViewInteractionHub) {
        super(context);
        this.mFileViewInteractionHub = mFileViewInteractionHub;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_sort_dialog);
        initView();
        //点击其他区域时清除选中项，否则会点击不响应
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.refreshFileList();
            }
        });
        initData();
    }

    private void initData() {
        //sort分类dialog
        dialog_sort_name.setOnClickListener(this);
        dialog_sort_size.setOnClickListener(this);
        dialog_sort_time.setOnClickListener(this);
        dialog_sort_type.setOnClickListener(this);
    }

    private void initView() {
        dialog_sort_name = (TextView) findViewById(R.id.dialog_sort_name);
        dialog_sort_size = (TextView) findViewById(R.id.dialog_sort_size);
        dialog_sort_time = (TextView) findViewById(R.id.dialog_sort_time);
        dialog_sort_type = (TextView) findViewById(R.id.dialog_sort_type);
    }

    public void showSecondDialog(int x, int y, int height, int width) {
        show();
        /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置,
         * 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        /*
         * lp.x与lp.y表示相对于原始位置的偏移.
         * 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
         * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略.
         * 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
         * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略.
         * 当参数值包含Gravity.CENTER_HORIZONTAL时
         * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动.
         * 当参数值包含Gravity.CENTER_VERTICAL时
         * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向右移动,负值向左移动.
         * gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
         * Gravity.CENTER_VERTICAL.
         *
         * 本来setGravity的参数值为Gravity.LEFT | Gravity.TOP时对话框应出现在程序的左上角,但在
         * 我手机上测试时发现距左边与上边都有一小段距离,而且垂直坐标把程序标题栏也计算在内了,
         * Gravity.LEFT, Gravity.TOP, Gravity.BOTTOM与Gravity.RIGHT都是如此,据边界有一小段距离
         */
        // 新位置Y坐标
        lp.width = width; // 宽度
        lp.height = height; // 高度
        lp.x = x + 220;
        lp.y = y + 50;
//        lp.alpha = 0.9f; // 透明度
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //sort分类dialog
            case R.id.dialog_sort_name:  //名称
                mFileViewInteractionHub.onSortChanged(FileSortHelper.SortMethod.name);
                this.dismiss();
                break;
            case R.id.dialog_sort_size:  //大小
                mFileViewInteractionHub.onSortChanged(FileSortHelper.SortMethod.size);
                this.dismiss();
                break;
            case R.id.dialog_sort_time:  //日期
                mFileViewInteractionHub.onSortChanged(FileSortHelper.SortMethod.date);
                this.dismiss();
                break;
            case R.id.dialog_sort_type:  //类型
                mFileViewInteractionHub.onSortChanged(FileSortHelper.SortMethod.type);
                this.dismiss();
                break;
            default:
                break;
        }
    }
}
