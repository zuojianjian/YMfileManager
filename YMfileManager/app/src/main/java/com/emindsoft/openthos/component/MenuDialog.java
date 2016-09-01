package com.emindsoft.openthos.component;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.emindsoft.openthos.R;
import com.emindsoft.openthos.system.FileViewInteractionHub;
import com.emindsoft.openthos.utils.T;

/**
 * Created by zuojj on 16-8-9.
 */
public class MenuDialog extends Dialog implements View.OnClickListener {
    //contextMenu菜单
    private TextView dialog_copy;
    private TextView dialog_paste;
    private TextView dialog_rename;
    private TextView dialog_delete;
    private TextView dialog_move;
    private TextView dialog_send;
    private TextView dialog_sort;
    private TextView dialog_copy_path;
    private TextView dialog_info;
    private TextView dialog_new_folder;
    private TextView dialog_new_file;
    private TextView dialog_visibale_file;
    //上下文
    private Context context;
    private FileViewInteractionHub mFileViewInteractionHub;
    private static boolean isCopy = false;
    private int newX;
    private int newY;
    private MenuSecondDialog menuSecondDialog;

    public MenuDialog(Context mContext, int id, FileViewInteractionHub mFileViewInteractionHub) {
        super(mContext);
        this.context = mContext;
        this.mFileViewInteractionHub = mFileViewInteractionHub;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_dialog);
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
        //contextmenu菜单点击事件
        dialog_copy.setOnClickListener(this);
        if (!isCopy || mFileViewInteractionHub.getSelectedFileList() == null){
            dialog_paste.setTextColor(Color.LTGRAY);
        }else {
            dialog_paste.setTextColor(Color.BLACK);
            dialog_paste.setOnClickListener(this);
            isCopy = false;
        }
        dialog_rename.setOnClickListener(this);
        dialog_delete.setOnClickListener(this);
        dialog_move.setOnClickListener(this);
        dialog_send.setOnClickListener(this);
        dialog_sort.setOnClickListener(this);
        dialog_info.setOnClickListener(this);
        dialog_new_folder.setOnClickListener(this);
        dialog_new_file.setOnClickListener(this);
        dialog_copy_path.setOnClickListener(this);
        dialog_visibale_file.setOnClickListener(this);
    }

    private void initView() {
        dialog_copy = (TextView) findViewById(R.id.dialog_copy);
        dialog_paste = (TextView) findViewById(R.id.dialog_paste);
        dialog_rename = (TextView) findViewById(R.id.dialog_rename);
        dialog_delete = (TextView) findViewById(R.id.dialog_delete);
        dialog_move = (TextView) findViewById(R.id.dialog_move);
        dialog_send = (TextView) findViewById(R.id.dialog_send);
        dialog_sort = (TextView) findViewById(R.id.dialog_sort);
        dialog_copy_path = (TextView) findViewById(R.id.dialog_copy_path);
        dialog_info = (TextView) findViewById(R.id.dialog_info);
        dialog_new_folder = (TextView) findViewById(R.id.dialog_new_folder);
        dialog_new_file = (TextView) findViewById(R.id.dialog_new_file);
        dialog_visibale_file = (TextView) findViewById(R.id.dialog_visibale_file);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //contextmenu菜单点击事件
            case R.id.dialog_copy:  //复制
                try {
                    mFileViewInteractionHub.doOnOperationCopy();
                    isCopy = true;
                    mFileViewInteractionHub.dismissContextDialog();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.dialog_paste:  //粘贴
                mFileViewInteractionHub.getSelectedFileList();
                mFileViewInteractionHub.onOperationButtonConfirm();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_rename:  //重命名
                mFileViewInteractionHub.onOperationRename();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_delete:  //删除
                mFileViewInteractionHub.onOperationDelete();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_move:  //剪切
                mFileViewInteractionHub.onOperationMove();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_send:  //发送
                mFileViewInteractionHub.onOperationSend();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_sort:  //分类
                mFileViewInteractionHub.dismissContextDialog();
                menuSecondDialog = new MenuSecondDialog(context,R.style.menu_dialog,mFileViewInteractionHub);
                menuSecondDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                menuSecondDialog.showSecondDialog(newX,newY,210,160);
                break;
            case R.id.dialog_info:  //属性
                mFileViewInteractionHub.onOperationInfo();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_copy_path:  //复制路径
                mFileViewInteractionHub.onOperationCopyPath();
                mFileViewInteractionHub.dismissContextDialog();
                T.showShort(context, "dialog_copy_path");
                break;
            case R.id.dialog_new_folder:  //创建文件夹
                mFileViewInteractionHub.onOperationCreateFolder();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_new_file:  //创建文件
                mFileViewInteractionHub.onOperationCreateFile();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_visibale_file:  //显示隐藏文件
                mFileViewInteractionHub.onOperationShowSysFiles();
                mFileViewInteractionHub.dismissContextDialog();
                break;
        }
    }

    public void showDialog(int x, int y, int height, int width) {
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
        lp.x = x+220;
        lp.y = y+50;
//        lp.alpha = 0.9f; // 透明度

        newX = x;
        newY = y;
        dialogWindow.setAttributes(lp);
    }
}
