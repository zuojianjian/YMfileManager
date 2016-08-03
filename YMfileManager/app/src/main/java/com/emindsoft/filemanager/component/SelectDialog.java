package com.emindsoft.filemanager.component;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emindsoft.filemanager.R;
import com.emindsoft.filemanager.system.FileSortHelper;
import com.emindsoft.filemanager.system.FileViewInteractionHub;
import com.emindsoft.filemanager.utils.T;

/**
 * 自定义dialog类，用于显示contextmenu
 * Created by zuojj on 16-6-29.
 */
public class SelectDialog extends AlertDialog implements View.OnClickListener {
    //contextMenu菜单
    TextView dialog_copy;
    TextView dialog_paste;
    TextView dialog_rename;
    TextView dialog_delete;
    TextView dialog_move;
    TextView dialog_send;
    TextView dialog_sort;
    TextView dialog_copy_path;
    TextView dialog_info;
    TextView dialog_new_folder;
    TextView dialog_visibale_file;
    //sort分类
    TextView dialog_sort_name;
    TextView dialog_sort_size;
    TextView dialog_sort_time;
    TextView dialog_sort_type;
    //线性布局
    LinearLayout dialog_content_menu;
    LinearLayout dialog_sort_menu;
    //上下文
    private Context context;
    private FileViewInteractionHub mFileViewInteractionHub;
    //选择dialog的标识
    private static int dialogFlags = -1;
    private static boolean isCopy = false;

    public SelectDialog(Context mContext, int dialog, FileViewInteractionHub mFileViewInteractionHub) {
        super(mContext, dialog);
        this.context = mContext;
        this.mFileViewInteractionHub = mFileViewInteractionHub;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_dialog);
        initView();
        //判断显示哪一个dialog
        if (dialogFlags == -1) {
            dialog_content_menu.setVisibility(View.VISIBLE);
        } else if (dialogFlags == 0) {
            dialog_sort_menu.setVisibility(View.VISIBLE);
            dialog_content_menu.setVisibility(View.GONE);
            dialogFlags = -1;
        }
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
        dialog_visibale_file = (TextView) findViewById(R.id.dialog_visibale_file);
        dialog_sort_name = (TextView) findViewById(R.id.dialog_sort_name);
        dialog_sort_size = (TextView) findViewById(R.id.dialog_sort_size);
        dialog_sort_time = (TextView) findViewById(R.id.dialog_sort_time);
        dialog_sort_type = (TextView) findViewById(R.id.dialog_sort_type);

        dialog_content_menu = (LinearLayout) findViewById(R.id.dialog_content_menu);
        dialog_sort_menu = (LinearLayout) findViewById(R.id.dialog_sort_menu);
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
        dialog_copy_path.setOnClickListener(this);
        dialog_visibale_file.setOnClickListener(this);

        //sort分类dialog
        dialog_sort_name.setOnClickListener(this);
        dialog_sort_size.setOnClickListener(this);
        dialog_sort_time.setOnClickListener(this);
        dialog_sort_type.setOnClickListener(this);

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
                dialogFlags = 0;
                mFileViewInteractionHub.dismissContextDialog();
                mFileViewInteractionHub.shownContextDialog(mFileViewInteractionHub);
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
            case R.id.dialog_visibale_file:  //显示隐藏文件
                mFileViewInteractionHub.onOperationShowSysFiles();
                mFileViewInteractionHub.dismissContextDialog();
                break;

            //sort分类dialog
            case R.id.dialog_sort_name:  //名称
                mFileViewInteractionHub.onSortChanged(FileSortHelper.SortMethod.name);
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_sort_size:  //大小
                mFileViewInteractionHub.onSortChanged(FileSortHelper.SortMethod.size);
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_sort_time:  //日期
                mFileViewInteractionHub.onSortChanged(FileSortHelper.SortMethod.date);
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_sort_type:  //类型
                mFileViewInteractionHub.onSortChanged(FileSortHelper.SortMethod.type);
                mFileViewInteractionHub.dismissContextDialog();
                break;
            default:
                break;
        }
    }

}
