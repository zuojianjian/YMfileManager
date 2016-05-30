package com.ygcompany.zuojj.ymfilemanager.system;

import android.R.drawable;
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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.ygcompany.zuojj.ymfilemanager.R;
import com.ygcompany.zuojj.ymfilemanager.view.SystemSpaceFragment;

import java.io.File;
import java.util.ArrayList;

public class FileViewInteractionHub implements FileOperationHelper.IOperationProgressListener {
    private static final String LOG_TAG = "FileViewInteractionHub";

    private IFileInteractionListener mFileViewListener;

    private ArrayList<FileInfo> mCheckedFileNameList = new ArrayList<FileInfo>();

    private FileOperationHelper mFileOperationHelper;

    private FileSortHelper mFileSortHelper;

    private View mConfirmOperationBar;

    private ProgressDialog progressDialog;

    private Context mContext;

    public enum Mode {
        View, Pick
    };

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

    private void showConfirmOperationBar(boolean show) {
        mConfirmOperationBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void addContextMenuSelectedItem() {
        if (mCheckedFileNameList.size() == 0) {
            int pos = mListViewContextMenuSelectedItem;
            if (pos != -1) {
                FileInfo fileInfo = mFileViewListener.getItem(pos);
                if (fileInfo != null) {
                    mCheckedFileNameList.add(fileInfo);
                }
            }
        }
    }

    public ArrayList<FileInfo> getSelectedFileList() {
        return mCheckedFileNameList;
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
    }

    // buttons
    private void setupOperationPane() {
        mConfirmOperationBar = mFileViewListener.getViewById(R.id.moving_operation_bar);
        setupClick(mConfirmOperationBar, R.id.button_moving_confirm);
        setupClick(mConfirmOperationBar, R.id.button_moving_cancel);
    }

    private void setupClick(View v, int id) {
        View button = (v != null ? v.findViewById(id) : mFileViewListener.getViewById(id));
        if (button != null)
            button.setOnClickListener(buttonClick);
    }

    private OnClickListener buttonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_moving_confirm:
                    onOperationButtonConfirm();
                    break;
                case R.id.button_moving_cancel:
                    onOperationButtonCancel();
                    break;
            }
        }

    };

    private void onOperationReferesh() {
        refreshFileList();
    }

    private void onOperationFavorite() {
        String path = mCurrentPath;

        if (mListViewContextMenuSelectedItem != -1) {
            path = mFileViewListener.getItem(mListViewContextMenuSelectedItem).filePath;
        }
    }

    private void onOperationSetting() {
        Intent intent = new Intent(mContext, FileManagerPreferenceActivity.class);
        if (intent != null) {
            try {
                mContext.startActivity(intent);
                clearSelection();
            } catch (ActivityNotFoundException e) {
                Log.e(LOG_TAG, "fail to start setting: " + e.toString());
            }
        }
    }

    private void onOperationShowSysFiles() {
        Settings.instance().setShowDotAndHiddenFiles(!Settings.instance().getShowDotAndHiddenFiles());
        refreshFileList();
    }

    public void onOperationSelectAllOrCancel() {
        if (!isSelectedAll()) {
            onOperationSelectAll();
        } else {
            clearSelection();
        }
    }

    public void onOperationSelectAll() {
        mCheckedFileNameList.clear();
        for (FileInfo f : mFileViewListener.getAllFiles()) {
            f.Selected = true;
            mCheckedFileNameList.add(f);
        }
        mFileViewListener.onDataChanged();
    }

    private OnClickListener navigationClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            String path = (String) v.getTag();
            assert (path != null);
            if (mFileViewListener.onNavigation(path))
                return;

            if(path.isEmpty()){
                mCurrentPath = mRoot;
            } else{
                mCurrentPath = path;
            }
            refreshFileList();
        }

    };

    //TODO 创建文件夹时获取输入字符
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

    private boolean doCreateFolder(String text) {
        if (TextUtils.isEmpty(text)){
            clearSelection();
            return false;
        }

        if (mFileOperationHelper.CreateFolder(mCurrentPath, text)) {
            mFileViewListener.addSingleFile(Util.GetFileInfo(Util.makePath(mCurrentPath, text)));
            mFileListView.setSelection(mFileListView.getCount() - 1);
            clearSelection();
        } else {
            new AlertDialog.Builder(mContext).setMessage(mContext.getString(R.string.fail_to_create_folder))
                    .setPositiveButton(R.string.confirm, null).create().show();
            clearSelection();
            return false;
        }

        return true;
    }

    public void onOperationSearch() {

    }

    public void onSortChanged(FileSortHelper.SortMethod s) {
        if (mFileSortHelper.getSortMethod() != s) {
            mFileSortHelper.setSortMethog(s);
            sortCurrentList();
        }
    }

    public void onOperationCopy() {
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

    private void onOperationPaste() {
        if (mFileOperationHelper.Paste(mCurrentPath)) {
            showProgress(mContext.getString(R.string.operation_pasting));
        }
    }

    public void onOperationMove() {
        mFileOperationHelper.StartMove(getSelectedFileList());
        clearSelection();
        showConfirmOperationBar(true);
        View confirmButton = mConfirmOperationBar.findViewById(R.id.button_moving_confirm);
        confirmButton.setEnabled(false);
        // refresh to hide selected files
        refreshFileList();
    }

    public void refreshFileList() {
        clearSelection();

        // onRefreshFileList returns true indicates list has changed
        mFileViewListener.onRefreshFileList(mCurrentPath, mFileSortHelper);

        // update move operation button state
        updateConfirmButtons();

    }

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

    public void onOperationRename() {
        int pos = mListViewContextMenuSelectedItem;
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

        return true;
    }

    private void notifyFileSystemChanged(String path) {
        if (path == null)
            return;
        final File f = new File(path);
        if (Build.VERSION.SDK_INT >= 19 /*Build.VERSION_CODES.KITKAT*/) { //添加此判断，判断SDK版本是不是4.4或者高于4.4
            String[] paths = new String[]{Environment.getExternalStorageDirectory().toString()};
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

    public void onOperationDelete() {
        doOperationDelete(getSelectedFileList());
    }

    public void onOperationDelete(int position) {
        FileInfo file = mFileViewListener.getItem(position);
        if (file == null)
            return;

        ArrayList<FileInfo> selectedFileList = new ArrayList<FileInfo>();
        selectedFileList.add(file);
        doOperationDelete(selectedFileList);
    }

    private void doOperationDelete(final ArrayList<FileInfo> selectedFileList) {
        final ArrayList<FileInfo> selectedFiles = new ArrayList<FileInfo>(selectedFileList);
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

    //TODO 中间context长按menu
    private OnCreateContextMenuListener mListViewContextMenuListener = new OnCreateContextMenuListener() {
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            if (isInSelection() || isMoveState())
                return;
            clearSelection();

            AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
            //TODO 二级分类menu
            SubMenu sortMenu = menu.addSubMenu(0, MENU_SORT, 0, R.string.menu_item_sort).setIcon(
                    R.drawable.ic_menu_sort);
            addMenuItem(sortMenu, MENU_SORT_NAME, 0, R.string.menu_item_sort_name);
            addMenuItem(sortMenu, MENU_SORT_SIZE, 1, R.string.menu_item_sort_size);
            addMenuItem(sortMenu, MENU_SORT_DATE, 2, R.string.menu_item_sort_date);
            addMenuItem(sortMenu, MENU_SORT_TYPE, 3, R.string.menu_item_sort_type);
            sortMenu.setGroupCheckable(0, true, true);
            sortMenu.getItem(0).setChecked(true);

            addMenuItem(menu, Constants.MENU_NEW_FOLDER, 0, R.string.operation_create_folder);
            addMenuItem(menu, Constants.MENU_COPY, 0, R.string.operation_copy);
            addMenuItem(menu, Constants.MENU_COPY_PATH, 0, R.string.operation_copy_path);
             addMenuItem(menu, Constants.MENU_PASTE, 0,
             R.string.operation_paste);
            addMenuItem(menu, Constants.MENU_MOVE, 0, R.string.operation_move);
            addMenuItem(menu, MENU_SEND, 0, R.string.operation_send);
            addMenuItem(menu, MENU_RENAME, 0, R.string.operation_rename);
            addMenuItem(menu, MENU_DELETE, 0, R.string.operation_delete);
            addMenuItem(menu, MENU_INFO, 0, R.string.operation_info);

            addMenuItem(menu, Constants.MENU_SHOWHIDE, 0, R.string.operation_show_sys,
                    R.drawable.ic_menu_show_sys);
            addMenuItem(menu, MENU_REFRESH, 0, R.string.operation_refresh,
                    R.drawable.ic_menu_refresh);
            addMenuItem(menu, MENU_SETTING, 0, R.string.menu_setting, drawable.ic_menu_preferences);

            if (!canPaste()) {
                MenuItem menuItem = menu.findItem(Constants.MENU_PASTE);
                if (menuItem != null)
                    menuItem.setEnabled(false);
            }
        }
    };

    // File List view setup
    private ListView mFileListView;

    private int mListViewContextMenuSelectedItem;

    private void setupFileListView() {
        mFileListView = (ListView) mFileViewListener.getViewById(R.id.file_path_list);
        mFileListView.setLongClickable(true);
        mFileListView.setOnCreateContextMenuListener(mListViewContextMenuListener);
        mFileListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(parent, view, position, id);
            }
        });
    }

    // menu
    private static final int MENU_SEARCH = 1;

    private static final int MENU_SORT = 3;

    private static final int MENU_SEND = 7;

    private static final int MENU_RENAME = 8;

    private static final int MENU_DELETE = 9;

    private static final int MENU_INFO = 10;

    private static final int MENU_SORT_NAME = 11;

    private static final int MENU_SORT_SIZE = 12;

    private static final int MENU_SORT_DATE = 13;

    private static final int MENU_SORT_TYPE = 14;

    private static final int MENU_REFRESH = 15;

    private static final int MENU_SELECTALL = 16;

    private static final int MENU_SETTING = 17;

    private static final int MENU_EXIT = 18;
//TODO  the bottom and context menu的点击事件
    private OnMenuItemClickListener menuItemClick = new OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            mListViewContextMenuSelectedItem = info != null ? info.position : -1;

            int itemId = item.getItemId();
            if (mFileViewListener.onOperation(itemId)) {
                return true;
            }

            addContextMenuSelectedItem();

            switch (itemId) {
                case MENU_SEARCH:
                    onOperationSearch();
                    break;
                case Constants.MENU_NEW_FOLDER:
                    onOperationCreateFolder();
                    break;
                case MENU_REFRESH:
                    onOperationReferesh();
                    break;
                case MENU_SELECTALL:
                    onOperationSelectAllOrCancel();
                    break;
                case Constants.MENU_SHOWHIDE:
                    onOperationShowSysFiles();
                    break;
                case Constants.MENU_FAVORITE:
                    onOperationFavorite();
                    break;
                case MENU_SETTING:
                    onOperationSetting();
                    break;
                case MENU_EXIT:
//                    finish();
                    break;
                // sort
                case MENU_SORT_NAME:
                    item.setChecked(true);
                    onSortChanged(FileSortHelper.SortMethod.name);
                    break;
                case MENU_SORT_SIZE:
                    item.setChecked(true);
                    onSortChanged(FileSortHelper.SortMethod.size);
                    break;
                case MENU_SORT_DATE:
                    item.setChecked(true);
                    onSortChanged(FileSortHelper.SortMethod.date);
                    break;
                case MENU_SORT_TYPE:
                    item.setChecked(true);
                    onSortChanged(FileSortHelper.SortMethod.type);
                    break;
                case Constants.MENU_COPY:
                    onOperationCopy();
                    break;
                case Constants.MENU_COPY_PATH:
                    onOperationCopyPath();
                    break;
                case Constants.MENU_PASTE:
                    onOperationPaste();
                    break;
                case Constants.MENU_MOVE:
                    onOperationMove();
                    break;
                case MENU_SEND:
                    onOperationSend();
                    break;
                case MENU_RENAME:
                    onOperationRename();
                    break;
                case MENU_DELETE:
                    onOperationDelete();
                    break;
                case MENU_INFO:
                    onOperationInfo();
                    break;
                default:
                    return false;
            }

            mListViewContextMenuSelectedItem = -1;
            return true;
        }

    };

    private FileViewInteractionHub.Mode mCurrentMode;

    private String mCurrentPath;

    private String mRoot;

    private SystemSpaceFragment.SelectFilesCallback mSelectFilesCallback;

    //TODO  创建list底部menu
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void addMenuItem(Menu menu, int itemId, int order, int string) {
        addMenuItem(menu, itemId, order, string, -1);
    }

    private void addMenuItem(Menu menu, int itemId, int order, int string, int iconRes) {
        if (!mFileViewListener.shouldHideMenu(itemId)) {
            MenuItem item = menu.add(0, itemId, order, string).setOnMenuItemClickListener(menuItemClick);
            if (iconRes > 0) {
                item.setIcon(iconRes);
            }
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        updateMenuItems(menu);
        return true;
    }

    private void updateMenuItems(Menu menu) {
        menu.findItem(MENU_SELECTALL).setTitle(
                isSelectedAll() ? R.string.operation_cancel_selectall : R.string.operation_selectall);
        menu.findItem(MENU_SELECTALL).setEnabled(mCurrentMode != Mode.Pick);

        MenuItem menuItem = menu.findItem(Constants.MENU_SHOWHIDE);
        if (menuItem != null) {
            menuItem.setTitle(Settings.instance().getShowDotAndHiddenFiles() ? R.string.operation_hide_sys
                    : R.string.operation_show_sys);
        }
    }

    public boolean isFileSelected(String filePath) {
        return mFileOperationHelper.isFileSelected(filePath);
    }

    public void onListItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileInfo lFileInfo = mFileViewListener.getItem(position);

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
                viewFile(lFileInfo);
            }
            return;
        }

        mCurrentPath = getAbsoluteName(mCurrentPath, lFileInfo.fileName);
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

    public String getRootPath() {
        return mRoot;
    }

    public String getCurrentPath() {
        return mCurrentPath;
    }

    public void setCurrentPath(String path) {
        mCurrentPath = path;
    }

    private String getAbsoluteName(String path, String name) {
        return path.equals(Constants.ROOT_PATH) ? path + name : path + File.separator + name;
    }

    // check or uncheck
    public boolean onCheckItem(FileInfo f, View v) {
        if (isMoveState())
            return false;

        if(isSelectingFiles() && f.IsDir)
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

    public boolean isSelectedAll() {
        return mFileViewListener.getItemCount() != 0 && mCheckedFileNameList.size() == mFileViewListener.getItemCount();
    }
    
    public boolean isSelected() {
        return mCheckedFileNameList.size() != 0;
    }

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

    private void viewFile(FileInfo lFileInfo) {
        try {
            IntentBuilder.viewFile(mContext, lFileInfo.filePath);
        } catch (ActivityNotFoundException e) {
            Log.e(LOG_TAG, "fail to view file: " + e.toString());
        }
    }


    public void copyFile(ArrayList<FileInfo> files) {
        mFileOperationHelper.Copy(files);
    }

    public void moveFileFrom(ArrayList<FileInfo> files) {
        mFileOperationHelper.StartMove(files);
        showConfirmOperationBar(true);
        updateConfirmButtons();
        // refresh to hide selected files
        refreshFileList();
    }

    @Override
    public void onFileChanged(String path) {
        notifyFileSystemChanged(path);
    }

    public void startSelectFiles(SystemSpaceFragment.SelectFilesCallback callback) {
        mSelectFilesCallback = callback;
        showConfirmOperationBar(true);
        updateConfirmButtons();
    }

}
