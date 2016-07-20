package cn.com.emindsoft.filemanager.component;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.com.emindsoft.filemanager.R;
import cn.com.emindsoft.filemanager.system.FileSortHelper;
import cn.com.emindsoft.filemanager.system.FileViewInteractionHub;
import cn.com.emindsoft.filemanager.utils.T;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 自定义dialog类，用于显示contextmenu
 * Created by zuojj on 16-6-29.
 */
public class SelectDialog extends AlertDialog implements View.OnClickListener {
    //contextMenu菜单
    @Bind(R.id.dialog_copy)
    TextView dialog_copy;
    @Bind(R.id.dialog_paste)
    TextView dialog_paste;
    @Bind(R.id.dialog_rename)
    TextView dialog_rename;
    @Bind(R.id.dialog_delete)
    TextView dialog_delete;
    @Bind(R.id.dialog_move)
    TextView dialog_move;
    @Bind(R.id.dialog_send)
    TextView dialog_send;
    @Bind(R.id.dialog_sort)
    TextView dialog_sort;
    @Bind(R.id.dialog_copy_path)
    TextView dialog_copy_path;
    @Bind(R.id.dialog_info)
    TextView dialog_info;
    @Bind(R.id.dialog_new_folder)
    TextView dialog_new_folder;
    @Bind(R.id.dialog_visibale_file)
    TextView dialog_visibale_file;
    //sort分类
    @Bind(R.id.dialog_sort_name)
    TextView dialog_sort_name;
    @Bind(R.id.dialog_sort_size)
    TextView dialog_sort_size;
    @Bind(R.id.dialog_sort_time)
    TextView dialog_sort_time;
    @Bind(R.id.dialog_sort_type)
    TextView dialog_sort_type;
    //线性布局
    @Bind(R.id.dialog_content_menu)
    LinearLayout dialog_content_menu;
    @Bind(R.id.dialog_sort_menu)
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
        ButterKnife.bind(SelectDialog.this);
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
        initView();
    }

    private void initView() {
        //contextmenu菜单点击事件
        dialog_copy.setOnClickListener(this);
        if (!isCopy){
            dialog_paste.setTextColor(Color.LTGRAY);
        }else {
            dialog_paste.setTextColor(Color.BLACK);
            dialog_paste.setOnClickListener(this);
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
                mFileViewInteractionHub.doOnOperationCopy();
                isCopy = true;
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_paste:  //粘贴
                mFileViewInteractionHub.onOperationButtonConfirm();
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_rename:  //重命名
                mFileViewInteractionHub.onOperationRename();
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_delete:  //删除
                mFileViewInteractionHub.onOperationDelete();
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_move:  //剪切
                mFileViewInteractionHub.onOperationMove();
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_send:  //发送
                mFileViewInteractionHub.onOperationSend();
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_sort:  //分类
                dialogFlags = 0;
                mFileViewInteractionHub.dismissContextDialog();
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.shownContextDialog(mFileViewInteractionHub);
                break;
            case R.id.dialog_info:  //属性
                mFileViewInteractionHub.onOperationInfo();
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_copy_path:  //复制路径
                mFileViewInteractionHub.onOperationCopyPath();
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.dismissContextDialog();
                T.showShort(context, "dialog_copy_path");
                break;
            case R.id.dialog_new_folder:  //创建文件夹
                mFileViewInteractionHub.onOperationCreateFolder();
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_visibale_file:  //显示隐藏文件
                mFileViewInteractionHub.onOperationShowSysFiles();
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.dismissContextDialog();
                break;

            //sort分类dialog
            case R.id.dialog_sort_name:  //名称
                mFileViewInteractionHub.onSortChanged(FileSortHelper.SortMethod.name);
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_sort_size:  //大小
                mFileViewInteractionHub.onSortChanged(FileSortHelper.SortMethod.size);
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_sort_time:  //日期
                mFileViewInteractionHub.onSortChanged(FileSortHelper.SortMethod.date);
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.dismissContextDialog();
                break;
            case R.id.dialog_sort_type:  //类型
                mFileViewInteractionHub.onSortChanged(FileSortHelper.SortMethod.type);
                mFileViewInteractionHub.clearSelection();
                mFileViewInteractionHub.dismissContextDialog();
                break;

        }
    }

}
