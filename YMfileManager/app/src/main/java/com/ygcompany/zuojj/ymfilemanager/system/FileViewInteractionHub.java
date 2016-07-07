package com.ygcompany.zuojj.ymfilemanager.system;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.ygcompany.zuojj.ymfilemanager.R;
import com.ygcompany.zuojj.ymfilemanager.component.SelectDialog;
import com.ygcompany.zuojj.ymfilemanager.utils.L;
import com.ygcompany.zuojj.ymfilemanager.utils.LocalCache;
import com.ygcompany.zuojj.ymfilemanager.utils.T;
import com.ygcompany.zuojj.ymfilemanager.view.SystemSpaceFragment;

import java.io.File;
import java.util.ArrayList;

public class FileViewInteractionHub implements FileOperationHelper.IOperationProgressListener {
    private static final String LOG_TAG = "FileViewInteractionHub";

    private IFileInteractionListener mFileViewListener;

    //被选中的item集合
    private ArrayList<FileInfo> mCheckedFileNameList = new ArrayList<>();

    private FileOperationHelper mFileOperationHelper;

    private FileSortHelper mFileSortHelper;

    private View mConfirmOperationBar;

    private ProgressDialog progressDialog;

    private View mNavigationBar;

    private TextView mNavigationBarText;

    private Context mContext;

    // 当前是复制还是移动
    private CopyOrMove copyOrMoveMode;
    private SelectDialog selectDialog;
    private int selectedDialogItem;

    public enum Mode {
        View, Pick
    }

    public enum CopyOrMove {
        Copy, Move
    }

    public FileViewInteractionHub(IFileInteractionListener fileViewListener) {
        assert (fileViewListener != null);
        mFileViewListener = fileViewListener;
        setup();
        mFileOperationHelper = new FileOperationHelper(this);
        mFileSortHelper = new FileSortHelper();
        mContext = mFileViewListener.getContext();
    }

    private void showProgress(String msg) {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void sortCurrentList() {
        mFileViewListener.sortCurrentList(mFileSortHelper);
    }

    public boolean canShowCheckBox() {
        return mConfirmOperationBar.getVisibility() != View.VISIBLE;
    }

    private void showConfirmOperationBar(boolean show) {
        mConfirmOperationBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void addDialogSelectedItem(int position) {
        if (mCheckedFileNameList.size() == 0) {
            selectedDialogItem = position;
            if (selectedDialogItem != -1) {
                FileInfo fileInfo = mFileViewListener.getItem(selectedDialogItem);
                if (fileInfo != null) {
                    mCheckedFileNameList.add(fileInfo);
                }
            }
        }
    }

    // 获取选中的文件列表
    public ArrayList<FileInfo> getSelectedFileList() {
        return mCheckedFileNameList;
    }

    // 获取checkbox选中的文件列表
    public ArrayList<FileInfo> getCheckedFileList() {
        return mFileOperationHelper.getFileList();
    }

    // 设置选中的文件列表
    public void setCheckedFileList(ArrayList<FileInfo> fileInfoList, CopyOrMove copyOrMove) {
        if (fileInfoList != null && fileInfoList.size() > 0)
            mCheckedFileNameList.addAll(fileInfoList);
        switch (copyOrMove) {
            case Move:
                onOperationMove();
                break;
            default:
            case Copy:
                onOperationCopy();
                break;
        }
    }

    public CopyOrMove getCurCopyOrMoveMode() {
        return copyOrMoveMode;
    }

    public boolean canPaste() {
        return mFileOperationHelper.canPaste();
    }

    // operation finish notification
    @Override
    public void onFinish() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        mFileViewListener.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showConfirmOperationBar(false);
                clearSelection();
                refreshFileList();
            }
        });
    }

    public FileInfo getItem(int pos) {
        return mFileViewListener.getItem(pos);
    }

    public boolean isInSelection() {
        return mCheckedFileNameList.size() > 0;
    }

    public boolean isMoveState() {
        return mFileOperationHelper.isMoveState() || mFileOperationHelper.canPaste();
    }

    private void setup() {
        //listview中operation事件集合（如：删除，移动...）
        setupFileListView();
        //确定取消按钮
        setupOperationPane();
        //顶部导航栏（sd卡路径）
        setupNaivgationBar();
    }

    private void setupNaivgationBar() {
        mNavigationBar = mFileViewListener.getViewById(R.id.navigation_bar);
        mNavigationBarText = (TextView) mFileViewListener.getViewById(R.id.current_path_view);
    }

    // buttons
    private void setupOperationPane() {
        mConfirmOperationBar = mFileViewListener.getViewById(R.id.moving_operation_bar);
        setupClick(mConfirmOperationBar, R.id.button_moving_confirm);
        setupClick(mConfirmOperationBar, R.id.button_moving_cancel);
    }

    //底部contextmenu显示时的点击事件
    private void setupClick(View v, int id) {
        View button = (v != null ? v.findViewById(id) : mFileViewListener.getViewById(id));
        if (button != null)
            button.setOnClickListener(buttonClick);
    }

    private View.OnClickListener buttonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_operation_copy://复制
                    onOperationCopy();
                    break;
                case R.id.button_operation_move://移动
                    onOperationMove();
                    break;
                case R.id.button_operation_send://发送
                    onOperationSend();
                    break;
                case R.id.button_operation_delete://删除
                    onOperationDelete();
                    break;
                case R.id.button_operation_cancel://取消
                    onOperationSelectAllOrCancel();
                    break;
                case R.id.button_moving_confirm://提交
                    onOperationButtonConfirm();
                    break;
                case R.id.button_moving_cancel://取消移动
                    onOperationButtonCancel();
                    break;
            }
        }

    };

    //刷新列表
    public void onOperationReferesh() {
        refreshFileList();
        T.showShort(mContext, "刷新成功！");
    }

    //启动设置页面
    private void onOperationSetting() {
        Intent intent = new Intent(mContext, FileManagerPreferenceActivity.class);
        try {
            mContext.startActivity(intent);
            clearSelection();
        } catch (ActivityNotFoundException e) {
            Log.e(LOG_TAG, "fail to start setting: " + e.toString());
        }
    }

    //设置系统文件的显示和隐藏
    public void onOperationShowSysFiles() {
        Settings.instance().setShowDotAndHiddenFiles(!Settings.instance().getShowDotAndHiddenFiles());
        refreshFileList();
    }

    //如果全选则取消，如果没有则全选
    public void onOperationSelectAllOrCancel() {
        if (!isSelectedAll()) {
            onOperationSelectAll();
        } else {
            clearSelection();
        }
    }

    public void onOperationSelectAll() { //全选
        mCheckedFileNameList.clear();
        for (FileInfo f : mFileViewListener.getAllFiles()) {
            f.Selected = true;
            mCheckedFileNameList.add(f);
        }
        mFileViewListener.onDataChanged();
    }

    //TODO  返回上一级
    public boolean onOperationUpLevel() {
        if (mFileViewListener.onOperation(Constants.OPERATION_UP_LEVEL)) {
            return true;
        }
        if (!mRoot.equals(mCurrentPath)) {
            mCurrentPath = new File(mCurrentPath).getParent();
            refreshFileList();
            return true;
        }
        return false;
    }

    // 创建文件夹时获取输入字符
    public void onOperationCreateFolder() {
        TextInputDialog dialog = new TextInputDialog(mContext, mContext.getString(
                R.string.operation_create_folder), mContext.getString(R.string.operation_create_folder_message),
                mContext.getString(R.string.new_folder_name), new TextInputDialog.OnFinishListener() {
            @Override
            public boolean onFinish(String text) {
                return doCreateFolder(text);
            }
        });

        dialog.show();
    }

    //TODO（待用） 创建文件时获取输入字符(创建文件不能在长按选中时创建)   右键创建
    public void onOperationCreateFile() {
        TextInputDialog dialog = new TextInputDialog(mContext, mContext.getString(
                R.string.operation_create_file), mContext.getString(R.string.operation_create_file_message),
                mContext.getString(R.string.new_file_name), new TextInputDialog.OnFinishListener() {
            @Override
            public boolean onFinish(String text) {
                return doCreateFile(text);
            }
        });

        dialog.show();
    }

    //着手创建文件夹
    private boolean doCreateFolder(String text) {
        if (TextUtils.isEmpty(text)) {
            clearSelection();
            return false;
        }

        if (mFileOperationHelper.CreateFolder(mCurrentPath, text)) {
            mFileViewListener.addSingleFile(Util.GetFileInfo(Util.makePath(mCurrentPath, text)));
            if ("list".equals(LocalCache.getViewTag())) {
                mFileListView.setSelection(mFileListView.getCount() - 1);
            } else if ("grid".equals(LocalCache.getViewTag())) {
                mFileGridView.setSelection(mFileGridView.getCount() - 1);
            }

            clearSelection();
        } else {
            new AlertDialog.Builder(mContext).setMessage(mContext.getString(R.string.fail_to_create_folder))
                    .setPositiveButton(R.string.confirm, null).create().show();
            clearSelection();
            return false;
        }

        return true;
    }

    //着手创建文件
    private boolean doCreateFile(String text) {
        if (TextUtils.isEmpty(text)) {
            clearSelection();
            return false;
        }

        if (!TextUtils.isEmpty(text)) {
            mFileViewListener.addSingleFile(Util.GetFileInfo(Util.makePath(mCurrentPath, text)));
            clearSelection();
        } else {
            new AlertDialog.Builder(mContext).setMessage(mContext.getString(R.string.fail_to_create_folder))
                    .setPositiveButton(R.string.confirm, null).create().show();
            clearSelection();
            return false;
        }

        return true;
    }

    //TODO
    public void onSortChanged(FileSortHelper.SortMethod s) {
        if (mFileSortHelper.getSortMethod() != s) {
            mFileSortHelper.setSortMethog(s);
            sortCurrentList();
        }
    }

    //复制操作
    public void onOperationCopy() {
        copyOrMoveMode = CopyOrMove.Copy;
        onOperationCopy(getSelectedFileList());
    }

    public void onOperationCopy(ArrayList<FileInfo> files) {
        mFileOperationHelper.Copy(files);
        clearSelection();

        showConfirmOperationBar(true);
        View confirmButton = mConfirmOperationBar.findViewById(R.id.button_moving_confirm);
        confirmButton.setEnabled(false);
        // refresh to hide selected files
        refreshFileList();
    }

    //复制路径操作
    public void onOperationCopyPath() {
        if (getSelectedFileList().size() == 1) {
            copy(getSelectedFileList().get(0).filePath);
        }
        clearSelection();
    }

    private void copy(CharSequence text) {
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(
                Context.CLIPBOARD_SERVICE);
        cm.setText(text);
    }

    public void onOperationPaste() {
        if (mFileOperationHelper.Paste(mCurrentPath)) {
            showProgress(mContext.getString(R.string.operation_pasting));
        }
    }

    //移动操作
    public void onOperationMove() {
        mFileOperationHelper.StartMove(getSelectedFileList());
        clearSelection();
        showConfirmOperationBar(true);
        View confirmButton = mConfirmOperationBar.findViewById(R.id.button_moving_confirm);
        confirmButton.setEnabled(false);
        // refresh to hide selected files
        refreshFileList();
        copyOrMoveMode = CopyOrMove.Move;
    }

    public void refreshFileList() {
        clearSelection();
        //更新导航栏
        updateNavigationPane();
        // onRefreshFileList returns true indicates list has changed
        mFileViewListener.onRefreshFileList(mCurrentPath, mFileSortHelper);

        // update move operation button state
        updateConfirmButtons();
    }

    //更新选择那种方式的提交按钮显示
    private void updateConfirmButtons() {
        if (mConfirmOperationBar.getVisibility() == View.GONE)
            return;

        Button confirmButton = (Button) mConfirmOperationBar.findViewById(R.id.button_moving_confirm);
        int text = R.string.operation_paste;
        if (isSelectingFiles()) {
            confirmButton.setEnabled(mCheckedFileNameList.size() != 0);
            text = R.string.operation_send;
        } else if (isMoveState()) {
            confirmButton.setEnabled(mFileOperationHelper.canMove(mCurrentPath));
        }

        confirmButton.setText(text);
    }

    //更新导航栏
    private void updateNavigationPane() {
//        mNavigationBarText.setText(mFileViewListener.getDisplayPath(mCurrentPath));
        mNavigationBarText.setText(mFileViewListener.getDisplayPath(mCurrentPath));
    }

    //发送操作
    public void onOperationSend() {
        ArrayList<FileInfo> selectedFileList = getSelectedFileList();
        for (FileInfo f : selectedFileList) {
            if (f.IsDir) {
                AlertDialog dialog = new AlertDialog.Builder(mContext).setMessage(
                        R.string.error_info_cant_send_folder).setPositiveButton(R.string.confirm, null).create();
                dialog.show();
                return;
            }
        }

        Intent intent = IntentBuilder.buildSendFile(selectedFileList);
        if (intent != null) {
            try {
                mFileViewListener.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.e(LOG_TAG, "fail to view file: " + e.toString());
            }
        }
        clearSelection();
    }

    //重命名
    public void onOperationRename() {
        int pos = selectedDialogItem;
        if (pos == -1)
            return;

        if (getSelectedFileList().size() == 0)
            return;

        final FileInfo f = getSelectedFileList().get(0);
        clearSelection();

        TextInputDialog dialog = new TextInputDialog(mContext, mContext.getString(R.string.operation_rename),
                mContext.getString(R.string.operation_rename_message), f.fileName, new TextInputDialog.OnFinishListener() {
            @Override
            public boolean onFinish(String text) {
                return doRename(f, text);
            }

        });

        dialog.show();
    }

    private boolean doRename(final FileInfo f, String text) {
        if (TextUtils.isEmpty(text))
            return false;

        if (mFileOperationHelper.Rename(f, text)) {
            f.fileName = text;
            mFileViewListener.onDataChanged();
        } else {
            new AlertDialog.Builder(mContext).setMessage(mContext.getString(R.string.fail_to_rename))
                    .setPositiveButton(R.string.confirm, null).create().show();
            return false;
        }
        refreshFileList();
        return true;
    }

    private void notifyFileSystemChanged(String path) {
        if (path == null)
            return;
        final File f = new File(path);
        /*Build.VERSION_CODES.KITKAT*/
        if (Build.VERSION.SDK_INT >= 19) { //添加此判断，判断SDK版本是不是4.4或者高于4.4
            String[] paths;
            paths = new String[]{path};
            MediaScannerConnection.scanFile(mContext, paths, null, null);
        } else {
            final Intent intent;
            if (f.isDirectory()) {
                intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
                intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver");
                intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
                Log.v(LOG_TAG, "directory changed, send broadcast:" + intent.toString());
            } else {
                intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(new File(path)));
                Log.v(LOG_TAG, "file changed, send broadcast:" + intent.toString());
            }
            mContext.sendBroadcast(intent);
        }
    }

    //删除操作
    public void onOperationDelete() {
        doOperationDelete(getSelectedFileList());
    }
//
//    public void onOperationDelete(int position) {
//        FileInfo file = mFileViewListener.getItem(position);
//        if (file == null)
//            return;
//
//        ArrayList<FileInfo> selectedFileList = new ArrayList<FileInfo>();
//        selectedFileList.add(file);
//        doOperationDelete(selectedFileList);
//    }

    private void doOperationDelete(final ArrayList<FileInfo> selectedFileList) {
        final ArrayList<FileInfo> selectedFiles = new ArrayList<>(selectedFileList);
        Dialog dialog = new AlertDialog.Builder(mContext)
                .setMessage(mContext.getString(R.string.operation_delete_confirm_message))
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mFileOperationHelper.Delete(selectedFiles)) {
                            showProgress(mContext.getString(R.string.operation_deleting));
                        }
                        clearSelection();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearSelection();
                    }
                }).create();
        dialog.show();
    }

    //显示当前文件的信息
    public void onOperationInfo() {
        if (getSelectedFileList().size() == 0)
            return;

        FileInfo file = getSelectedFileList().get(0);
        if (file == null)
            return;

        InformationDialog dialog = new InformationDialog(mContext, file, mFileViewListener
                .getFileIconHelper());
        dialog.show();
        clearSelection();
    }

    //确定
    public void onOperationButtonConfirm() {
        if (isSelectingFiles()) {
            mSelectFilesCallback.selected(mCheckedFileNameList);
            mSelectFilesCallback = null;
            clearSelection();
        } else if (mFileOperationHelper.isMoveState()) {
            if (mFileOperationHelper.EndMove(mCurrentPath)) {
                showProgress(mContext.getString(R.string.operation_moving));
            }
        } else {
            onOperationPaste();
        }
    }

    //取消
    public void onOperationButtonCancel() {
        mFileOperationHelper.clear();
        showConfirmOperationBar(false);
        if (isSelectingFiles()) {
            mSelectFilesCallback.selected(null);
            mSelectFilesCallback = null;
            clearSelection();
        } else if (mFileOperationHelper.isMoveState()) {
            // refresh to show previously selected hidden files
            mFileOperationHelper.EndMove(null);
            refreshFileList();
        } else {
            refreshFileList();
        }
    }

    // File List view setup
    private GridView mFileGridView;
    private ListView mFileListView;

    //list和grid监听鼠标的点击事件
    private void setupFileListView() {
        final String title = LocalCache.getViewTag();
        if ("list".equals(title)) {
            mFileListView = (ListView) mFileViewListener.getViewById(R.id.file_path_list);
        } else if ("grid".equals(title)) {
            mFileGridView = (GridView) mFileViewListener.getViewById(R.id.file_path_grid);
        }

    }

    private FileViewInteractionHub.Mode mCurrentMode;

    private String mCurrentPath;

    private String mRoot;

    private SystemSpaceFragment.SelectFilesCallback mSelectFilesCallback;

    public boolean isFileSelected(String filePath) {
        return mFileOperationHelper.isFileSelected(filePath);
    }

    //上次按下返回键的系统时间
    private long lastBackTime = 0;

    //listview或gridview的item左键点击事件监听
    public void onListItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileInfo lFileInfo = mFileViewListener.getItem(position);
        //系统当前时间
        long currentBackTime = System.currentTimeMillis();
        if (lFileInfo == null) {
            Log.e(LOG_TAG, "file does not exist on position:" + position);
            return;
        }

        if (isInSelection()) {
            boolean selected = lFileInfo.Selected;
            if (selected) {
                mCheckedFileNameList.remove(lFileInfo);
            } else {
                mCheckedFileNameList.add(lFileInfo);
            }
            lFileInfo.Selected = !selected;
            return;
        }

        if (!lFileInfo.IsDir) {
            if (mCurrentMode == Mode.Pick) {
                mFileViewListener.onPick(lFileInfo);
            } else {
                //打开文件
                viewFile(lFileInfo);
            }
        }else if (currentBackTime - lastBackTime < 800){ //打开文件夹
            mCurrentPath = getAbsoluteName(mCurrentPath, lFileInfo.fileName);
            refreshFileList();
        }
        lastBackTime = currentBackTime;
    }

    //listview或gridview的item鼠标右键点击不做任何操作
    public void onListItemRightClick(AdapterView<?> parent, View view, int position, long id) {
//        FileInfo lFileInfo = mFileViewListener.getItem(position);
//        if (lFileInfo == null) {
//            Log.e(LOG_TAG, "file does not exist on position:" + position);
//            return;
//        }
//        if (isInSelection()) {
//            boolean selected = lFileInfo.Selected;
//            if (selected) {
//                mCheckedFileNameList.remove(lFileInfo);
//            } else {
//                mCheckedFileNameList.add(lFileInfo);
//            }
//            lFileInfo.Selected = !selected;
//            return;
//        }
//        if (mCurrentMode == Mode.Pick) {
//            mFileViewListener.onPick(lFileInfo);
//        }else {
//            return;
//        }
//        mCurrentPath = getAbsoluteName(mCurrentPath, lFileInfo.fileName);
        refreshFileList();
    }

    public void setMode(Mode m) {
        mCurrentMode = m;
    }

    public Mode getMode() {
        return mCurrentMode;
    }

    public void setRootPath(String path) {
        mRoot = path;
        mCurrentPath = path;
    }
//
//    public String getRootPath() {
//        return mRoot;
//    }

    //获取当前路径
    public String getCurrentPath() {
        return mCurrentPath;
    }

    //设置当前路径
    public void setCurrentPath(String path) {
        mCurrentPath = path;
    }

    //获取绝对路径
    private String getAbsoluteName(String path, String name) {
        return path.equals(Constants.ROOT_PATH) ? path + name : path + File.separator + name;
    }

    // check or uncheck
    public boolean onCheckItem(FileInfo f, View v) {
        if (isMoveState())
            return false;

        if (isSelectingFiles() && f.IsDir)
            return false;

        if (f.Selected) {
            mCheckedFileNameList.add(f);
        } else {
            mCheckedFileNameList.remove(f);
        }
        return true;
    }

    private boolean isSelectingFiles() {
        return mSelectFilesCallback != null;
    }

    public boolean isSelectedAll() {  //      是否全部选中
        return mFileViewListener.getItemCount() != 0 && mCheckedFileNameList.size() == mFileViewListener.getItemCount();
    }

    //清空集合选中数据通知更新
    public void clearSelection() {
        if (mCheckedFileNameList.size() > 0) {
            for (FileInfo f : mCheckedFileNameList) {
                if (f == null) {
                    continue;
                }
                f.Selected = false;
            }
            mCheckedFileNameList.clear();
            mFileViewListener.onDataChanged();
        }
    }

    //查看文件
    private void viewFile(FileInfo lFileInfo) {
        try {
            IntentBuilder.viewFile(mContext, lFileInfo.filePath);
        } catch (ActivityNotFoundException e) {
            Log.e(LOG_TAG, "fail to view file: " + e.toString());
        }
    }

    //回退 如果全选则取消
    public boolean onBackPressed() {
        if (isInSelection()) {
            clearSelection();
        } else if (!onOperationUpLevel()) {
            return false;
        }
        return true;
    }

//    public void copyFile(ArrayList<FileInfo> files) {
//        mFileOperationHelper.Copy(files);
//    }
//
//    public void moveFileFrom(ArrayList<FileInfo> files) {
//        mFileOperationHelper.StartMove(files);
//        showConfirmOperationBar(true);
//        updateConfirmButtons();
//        // refresh to hide selected files
//        refreshFileList();
//    }

    @Override
    public void onFileChanged(String path) {
        notifyFileSystemChanged(path);
    }
//
//    public void startSelectFiles(SystemSpaceFragment.SelectFilesCallback callback) {
//        mSelectFilesCallback = callback;
//        showConfirmOperationBar(true);
//        updateConfirmButtons();
//    }

    //鼠标滚轮滑动
    public void MouseScrollAction(MotionEvent event) {
        if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f) {
            L.i("fortest::onGenericMotionEvent", "down");
            T.showShort(mContext, "向下滚动...");
        }
        //获得垂直坐标上的滚动方向,也就是滚轮向上滚
        else {
            L.i("fortest::onGenericMotionEvent", "up");
            T.showShort(mContext, "向上滚动...");
        }
    }

    //显示自定义dialog
    public void shownContextDialog(FileViewInteractionHub mFileViewInteractionHub) {
        //创建Dialog并设置样式主题
        selectDialog = new SelectDialog(mContext, R.style.dialog, mFileViewInteractionHub);
        selectDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
        selectDialog.show();
        //显示区域范围的设置在显示dialog之后，不然会不起作用
        Window win = selectDialog.getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        params.height = 380; // 高度设置
        params.width = 190; // 宽度设置
        params.x = 30;

        win.setAttributes(params);
    }

    public void dismissContextDialog() {
        selectDialog.dismiss();
        clearSelection();
        refreshFileList();
    }

}
