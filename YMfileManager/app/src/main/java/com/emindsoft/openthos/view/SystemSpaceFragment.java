package com.emindsoft.openthos.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.emindsoft.openthos.BaseFragment;
import com.emindsoft.openthos.MainActivity;
import com.emindsoft.openthos.R;
import com.emindsoft.openthos.drag.DragGridView;
import com.emindsoft.openthos.drag.DragListView;
import com.emindsoft.openthos.system.Constants;
import com.emindsoft.openthos.system.FileCategoryHelper;
import com.emindsoft.openthos.system.FileIconHelper;
import com.emindsoft.openthos.system.FileInfo;
import com.emindsoft.openthos.system.FileListAdapter;
import com.emindsoft.openthos.system.FileManagerPreferenceActivity;
import com.emindsoft.openthos.system.FileSortHelper;
import com.emindsoft.openthos.system.FileViewInteractionHub;
import com.emindsoft.openthos.system.IFileInteractionListener;
import com.emindsoft.openthos.system.Settings;
import com.emindsoft.openthos.system.Util;
import com.emindsoft.openthos.utils.L;
import com.emindsoft.openthos.utils.LocalCache;
import com.emindsoft.openthos.utils.T;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 系统空间存储页面
 * Created by zuojj on 16-5-19.
 */
public class SystemSpaceFragment extends BaseFragment implements
        IFileInteractionListener, MainActivity.IBackPressedListener {
    private static final String TAG = SystemSpaceFragment.class.getSimpleName();
    public static final String ROOT_DIRECTORY = "root_directory";
    private FileListAdapter mAdapter;
    private FileViewInteractionHub mFileViewInteractionHub;
    //区分文件的工具类
    private FileCategoryHelper mFileCagetoryHelper;
    //设置不同文件图标的工具类
    private FileIconHelper mFileIconHelper;
    //文件list集合
    private ArrayList<FileInfo> mFileNameList = new ArrayList<>();
    private Activity mActivity;
    private View view;
    private DragListView file_path_list;
    private DragGridView file_path_grid;
    //获得SD卡的存储目录
    private static final String sdDir = Util.getSdDirectory();
    //传入的标识和路径区分加载文件目录位置
    private String sdOrSystem;
    private String directorPath;
    // 传进来的根目录
    private String curRootDir = "";
    // 传进来的选中文件列表
    private ArrayList<FileInfo> fileInfoList = null;
    // 传进来的复制和移动模式的标志
    FileViewInteractionHub.CopyOrMove copyOrMove = null;
    //是否将要执行多选的判断
    private boolean isCtrlPress;
    //鼠标右键标识
    private String mouseRightTag = "mouse";

    // memorize the scroll positions of previous paths
    private ArrayList<PathScrollPositionItem> mScrollPositionList = new ArrayList<>();
    private String mPreviousPath;
    //广播接收器,根据（MainActivity）发送广播的action匹配
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case "com.switchview":
                    if (null != intent.getExtras().getString("switch_view")) {
                        String switch_view = intent.getExtras().getString("switch_view");
                        selectorMenuId(switch_view);
                    }
                    break;
                case "com.switchmenu": // 顶部菜单栏操作
                    if (null != intent.getExtras().getString("pop_menu")) {
                        String pop_menu = intent.getExtras().getString("pop_menu");
                        selectorMenuId(pop_menu);
                    }
                    break;
                case "com.isCtrlPress": // 是否执行多选
                    isCtrlPress = intent.getExtras().getBoolean("is_ctrl_press");
                    break;
                case Intent.ACTION_MEDIA_MOUNTED:
                case Intent.ACTION_MEDIA_UNMOUNTED:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI();
                        }
                    });
                    break;
            }
        }
    };

    //根据广播接收器收到的menu字段执行相应的操作
    private void selectorMenuId(String tag) {
        if (mFileViewInteractionHub.getSelectedFileList() != null) {

        }
        switch (tag) {
            case "pop_refresh":
                mFileViewInteractionHub.onOperationReferesh();
                break;
            case "pop_cancel_all":
                mFileViewInteractionHub.onOperationSelectAllOrCancel();
                break;
            case "pop_copy":
                if (mFileViewInteractionHub.getSelectedFileList() != null) {
                    mFileViewInteractionHub.doOnOperationCopy();
                }
                T.showShort(mActivity, "请选择复制的文件");
                break;
            case "pop_delete":
                if (mFileViewInteractionHub.getSelectedFileList() != null) {
                    mFileViewInteractionHub.onOperationDelete();
                }
                break;
            case "pop_send":
                if (mFileViewInteractionHub.getSelectedFileList() != null) {
                    mFileViewInteractionHub.onOperationSend();
                }
                T.showShort(mActivity, "请选择发送文件");
                break;
            case "pop_create":
                mFileViewInteractionHub.onOperationCreateFolder();
                break;
            case "view_or_dismiss":
                mFileViewInteractionHub.onOperationShowSysFiles();
                break;
            case "pop_cut":
                mFileViewInteractionHub.onOperationMove();
                break;
            case "pop_paste":
                mFileViewInteractionHub.onOperationButtonConfirm();
                break;
            case "pop_cacel":
                mFileViewInteractionHub.onOperationButtonCancel();
                break;
            case "grid":
            case "list":
                //清空数据集合，重新加载数据和布局（view视图的切换）
                initData();
                mFileViewInteractionHub.clearSelection();
                break;
        }
    }

    /**
     * 各个页面的构造参数
     *
     * @param sdSpaceFragment 传入的标识和路径区分加载文件目录位置
     * @param directPath      传入的外接U盘的地址
     * @param fileInfoList    传进来的选中文件列表
     * @param copyOrMove      传进来的复制和移动模式的标志
     */
    @SuppressLint({"NewApi", "ValidFragment"})
    public SystemSpaceFragment(String sdSpaceFragment, String directPath, ArrayList<FileInfo> fileInfoList, FileViewInteractionHub.CopyOrMove copyOrMove) {
        this.sdOrSystem = sdSpaceFragment;
        this.fileInfoList = fileInfoList;
        this.copyOrMove = copyOrMove;
        this.directorPath = directPath;
    }
    @SuppressLint({"NewApi", "ValidFragment"})
    public SystemSpaceFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.system_fragment_layout, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        file_path_list = (DragListView) view.findViewById(R.id.file_path_list);
        file_path_grid = (DragGridView) view.findViewById(R.id.file_path_grid);
        //TODO  delete
    }

    private void initData() {
        mActivity = getActivity();
        mFileCagetoryHelper = new FileCategoryHelper(mActivity);
        mFileViewInteractionHub = new FileViewInteractionHub(this);
        Intent intent = getActivity().getIntent();
        //TODO  delete
        mFileIconHelper = new FileIconHelper(mActivity);
        if ("list".equals(LocalCache.getViewTag())) {
            mAdapter = new FileListAdapter(mActivity, R.layout.file_browser_item_list, mFileNameList, mFileViewInteractionHub,
                    mFileIconHelper);

        } else if ("grid".equals(LocalCache.getViewTag())) {
            mAdapter = new FileListAdapter(mActivity, R.layout.file_browser_item_grid, mFileNameList, mFileViewInteractionHub,
                    mFileIconHelper);
        }

        boolean baseSd = intent.getBooleanExtra(Constants.KEY_BASE_SD, !FileManagerPreferenceActivity.isReadRoot(mActivity));
        Log.i(TAG, "baseSd = " + baseSd);

        String rootDir = intent.getStringExtra(ROOT_DIRECTORY);
        if (!TextUtils.isEmpty(rootDir)) {
            if (baseSd && sdDir.startsWith(rootDir)) {
                rootDir = sdDir;
            }
        } else {
            rootDir = baseSd ? sdDir : Constants.ROOT_PATH;
        }
        mFileViewInteractionHub.setRootPath(rootDir);

        //获取currentDir路径为根路径 sdOrSystem为路径选择标识
        String currentDir = FileManagerPreferenceActivity.getPrimaryFolder(mActivity, sdOrSystem, directorPath);
        Uri uri = intent.getData();
        if (uri != null) {
            if (baseSd && sdDir.startsWith(uri.getPath())) {
                currentDir = sdDir;
            } else {
                currentDir = uri.getPath();
            }
        }
        mFileViewInteractionHub.setCurrentPath(currentDir);
        curRootDir = currentDir;
        Log.i(TAG, "CurrentDir = " + currentDir);
        //TODO delete

        operatorData();

        if (fileInfoList != null && fileInfoList.size() > 0) {
            mFileViewInteractionHub.setCheckedFileList(fileInfoList, copyOrMove);
        }

        //初始化接收器
        initReciever();
        //更新UI
        updateUI();
        //设置optionmenu
        setHasOptionsMenu(true);
    }

    private void operatorData() {
        if ("list".equals(LocalCache.getViewTag())) {
            file_path_grid.setVisibility(View.GONE);
            file_path_list.setVisibility(View.VISIBLE);
            file_path_list.setAdapter(mAdapter);
            file_path_list.setOnDragChangeListener(new DragListView.OnChanageListener() {
                @Override
                public void onChange(int from, int to) { //from (move file position) : to (subject folder)
                    FileInfo fileInfo = mFileViewInteractionHub.getItem(to);
                    if (to != -1 && fileInfo.IsDir) {
                        mFileViewInteractionHub.addDragSelectedItem(from);
                        mFileViewInteractionHub.onOperationMove();
                        mFileViewInteractionHub.onOperationDragConfirm(fileInfo.filePath);
                        L.e("from____________to", from + "______________" + to);
                    }
                }
            });
            file_path_list.setOnGenericMotionListener(new MouseListOnGenericMotionListener());
        } else if ("grid".equals(LocalCache.getViewTag())) {
            file_path_list.setVisibility(View.GONE);
            file_path_grid.setVisibility(View.VISIBLE);
            file_path_grid.setAdapter(mAdapter);
            file_path_grid.setOnDragChangeListener(new DragGridView.OnChanageListener() {
                @Override
                public void onChange(int from, int to) { //from (move file position) : to (subject folder)
                    FileInfo fileInfo = mFileViewInteractionHub.getItem(to);
                    if (to != -1 && fileInfo.IsDir) {
                        mFileViewInteractionHub.addDragSelectedItem(from);
                        mFileViewInteractionHub.onOperationMove();
                        mFileViewInteractionHub.onOperationDragConfirm(fileInfo.filePath);
                        L.e("from____________to", from + "______________" + to);
                    }
                }
            });
            file_path_grid.setOnGenericMotionListener(new MouseGridOnGenericMotionListener());
        }
    }

    private void initReciever() {
        //设置意图过滤器
        IntentFilter intentFilter = new IntentFilter();
        //接收视图切换的广播
        intentFilter.addAction("com.switchview");
        //接收主界面选项菜单的广播
        intentFilter.addAction("com.switchmenu");
        //判断是否为触摸事件来区分触摸和鼠标事件操作
        intentFilter.addAction("com.isTouchEvent");
        //判断是否多选标识
        intentFilter.addAction("com.isCtrlPress");
        //接收SD卡是否挂载的广播
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        //注册广播接收器
        mActivity.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //当页面销毁时解绑广播接收器和当前view
        mActivity.unregisterReceiver(mReceiver);
    }

    //回退操作
    public boolean onBack() {
        if (!Util.isSDCardReady() || mFileViewInteractionHub == null) {
            return false;
        }
        return mFileViewInteractionHub.onBackPressed();
    }

    private class PathScrollPositionItem {
        String path;
        int pos;

        PathScrollPositionItem(String s, int p) {
            path = s;
            pos = p;
        }
    }

    // execute before change, return the memorized scroll position
    //返回滚动前的位置
    private int computeScrollPosition(String path) {
        int pos = 0;
        if (mPreviousPath != null) {
            if (path.startsWith(mPreviousPath)) {
                int firstVisiblePosition = 0;
                if ("list".equals(LocalCache.getViewTag())) {
                    firstVisiblePosition = file_path_list.getFirstVisiblePosition();
                } else if ("grid".equals(LocalCache.getViewTag())) {
                    firstVisiblePosition = file_path_grid.getFirstVisiblePosition();
                }
                if (mScrollPositionList.size() != 0
                        && mPreviousPath.equals(mScrollPositionList.get(mScrollPositionList.size() - 1).path)) {
                    mScrollPositionList.get(mScrollPositionList.size() - 1).pos = firstVisiblePosition;
                    Log.i(TAG, "computeScrollPosition: update item: " + mPreviousPath + " " + firstVisiblePosition
                            + " stack count:" + mScrollPositionList.size());
                    pos = firstVisiblePosition;
                } else {
                    mScrollPositionList.add(new PathScrollPositionItem(mPreviousPath, firstVisiblePosition));
                    Log.i(TAG, "computeScrollPosition: add item: " + mPreviousPath + " " + firstVisiblePosition
                            + " stack count:" + mScrollPositionList.size());
                }
            } else {
                int i;
                for (i = 0; i < mScrollPositionList.size(); i++) {
                    if (!path.startsWith(mScrollPositionList.get(i).path)) {
                        break;
                    }
                }
                // navigate to a totally new branch, not in current stack
                if (i > 0) {
                    pos = mScrollPositionList.get(i - 1).pos;
                }

                for (int j = mScrollPositionList.size() - 1; j >= i - 1 && j >= 0; j--) {
                    mScrollPositionList.remove(j);
                }
            }
        }

        Log.i(TAG, "computeScrollPosition: result pos: " + path + " " + pos + " stack count:" + mScrollPositionList.size());
        mPreviousPath = path;
        return pos;
    }

    //刷新列表
    public boolean onRefreshFileList(String path, FileSortHelper sort) {
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            return false;
        }
        final int pos = computeScrollPosition(path);
        ArrayList<FileInfo> fileList = mFileNameList;
        fileList.clear();

        File[] listFiles = file.listFiles(mFileCagetoryHelper.getFilter());
        if (listFiles == null)
            return true;

        for (File child : listFiles) {
            // do not show selected file if in move state
            if (mFileViewInteractionHub.isMoveState() && mFileViewInteractionHub.isFileSelected(child.getPath()))
                continue;

            String absolutePath = child.getAbsolutePath();
            if (Util.isNormalFile(absolutePath) && Util.shouldShowFile(absolutePath)) {
                FileInfo lFileInfo = Util.GetFileInfo(child,
                        mFileCagetoryHelper.getFilter(), Settings.instance().getShowDotAndHiddenFiles());
                if (lFileInfo != null) {
                    fileList.add(lFileInfo);
                }
            }
        }

        sortCurrentList(sort);
        showEmptyView(fileList.size() == 0);
        if ("list".equals(LocalCache.getViewTag())) {
            file_path_list.post(new Runnable() {
                @Override
                public void run() {
                    file_path_list.setSelection(pos);
                }
            });
        } else if ("grid".equals(LocalCache.getViewTag())) {
            file_path_grid.post(new Runnable() {
                @Override
                public void run() {
                    file_path_grid.setSelection(pos);
                }
            });
        }
        return true;
    }

    private void updateUI() {
        boolean sdCardReady = Util.isSDCardReady();
        View noSdView = view.findViewById(R.id.sd_not_available_page);
        noSdView.setVisibility(sdCardReady ? View.GONE : View.VISIBLE);
        if ("list".equals(LocalCache.getViewTag())) {
            file_path_list.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);
        } else if ("grid".equals(LocalCache.getViewTag())) {
            file_path_grid.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);
        }

        if (sdCardReady) {
            mFileViewInteractionHub.refreshFileList();
        }
    }

    private void showEmptyView(boolean show) {
        View emptyView = view.findViewById(R.id.empty_view);
        if (emptyView != null)
            emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public View getViewById(int id) {

        return view.findViewById(id);
    }

    @Override
    public Context getContext() {
        return mActivity;
    }

    @Override
    public void onDataChanged() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }

        });
    }

    @Override
    public void onPick(FileInfo f) {
        try {
            Intent intent = Intent.parseUri(Uri.fromFile(new File(f.filePath)).toString(), 0);
            mActivity.setResult(Activity.RESULT_OK, intent);
            mActivity.finish();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean shouldShowOperationPane() {
        return true;
    }

    @Override
    public boolean onOperation(int id) {
        return false;
    }

    // 显示底部导航栏
    @Override
    public String getDisplayPath(String path) {
        if (path.startsWith(this.sdDir) && !FileManagerPreferenceActivity.showRealPath(mActivity)) {
            return getString(R.string.sd_folder) + path.substring(this.sdDir.length());
        } else {
            return path;
        }
    }

    @Override
    public String getRealPath(String displayPath) {
        final String perfixName = getString(R.string.sd_folder);
        if (displayPath.startsWith(perfixName)) {
            return sdDir + displayPath.substring(perfixName.length());
        } else {
            return displayPath;
        }
    }

    @Override
    public boolean onNavigation(String path) {
        return false;
    }

    @Override
    public boolean shouldHideMenu(int menu) {
        return false;
    }
    //TODO  copyFile

    public interface SelectFilesCallback {
        // files equals null indicates canceled
        void selected(ArrayList<FileInfo> files);
    }

    @Override
    public FileIconHelper getFileIconHelper() {
        return mFileIconHelper;
    }

    public boolean setPath(String location) {
        if (!location.startsWith(mFileViewInteractionHub.getRootPath())) {
            return false;
        }
        mFileViewInteractionHub.setCurrentPath(location);
        mFileViewInteractionHub.refreshFileList();
        return true;
    }

    @Override
    public FileInfo getItem(int pos) {
        if (pos < 0 || pos > mFileNameList.size() - 1)
            return null;
        return mFileNameList.get(pos);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void sortCurrentList(FileSortHelper sort) {
        Collections.sort(mFileNameList, sort.getComparator());
        onDataChanged();
    }

    @Override
    public ArrayList<FileInfo> getAllFiles() {
        return mFileNameList;
    }

    @Override
    public void addSingleFile(FileInfo file) {
        mFileNameList.add(file);
        onDataChanged();
    }

    @Override
    public int getItemCount() {
        return mFileNameList.size();
    }

    @Override
    public void runOnUiThread(Runnable r) {
        mActivity.runOnUiThread(r);
    }

    /**
     * 当前是否可以发生回退操作
     *
     * @return
     */
    public boolean canGoBack() {
        String currentPath = mFileViewInteractionHub.getCurrentPath();
        return !currentPath.trim().equals(curRootDir.trim());
    }

    /**
     * 执行回退操作
     */
    public void goBack() {
        mFileViewInteractionHub.onBackPressed();
    }

    // 获取当前选中的文件列表
    public ArrayList<FileInfo> getFileInfoList() {
        return mFileViewInteractionHub.getCheckedFileList();
    }

    // 当前是移动还是复制操作
    public FileViewInteractionHub.CopyOrMove getCurCopyOrMoveMode() {
        return mFileViewInteractionHub.getCurCopyOrMoveMode();
    }

    private int mLastClickId;
    private long mLastClickTime = 0;

    public class OnitemClickListener implements AdapterView.OnItemClickListener {
        MotionEvent motionEvent;
        public OnitemClickListener(MotionEvent event) {
            motionEvent = event;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            FileInfo fileInfo = mAdapter.getFileInfoList().get(position);
            fileInfo.Selected = true;
            List<Integer> integerList = mAdapter.getSelectFileInfoList();
            if (isCtrlPress) {
                if (integerList.contains(position)) {
                    integerList.remove(new Integer(position));
                    mFileViewInteractionHub.removeDialogSelectedItem(fileInfo);
                } else {
                    integerList.add(position);
                    mFileViewInteractionHub.addDialogSelectedItem(fileInfo);
                }
            } else {
                if (mouseRightTag.equals("mouse") && mLastClickId == position && (Math.abs(System.currentTimeMillis() - mLastClickTime) < 1000)) {
                    T.showShort(mActivity, "double！");
                    String doubleTag = "double";
                    mFileViewInteractionHub.onListItemClick(position, doubleTag, motionEvent, fileInfo);
                    mFileViewInteractionHub.clearSelection();
                }
                mLastClickTime = System.currentTimeMillis();
                mLastClickId = position;
                integerList.clear();
                mFileViewInteractionHub.clearSelection();
                integerList.add(position);
                mFileViewInteractionHub.addDialogSelectedItem(fileInfo);
                mouseRightTag = "mouse";
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    private class MouseGridOnGenericMotionListener implements View.OnGenericMotionListener {
        @Override
        public boolean onGenericMotion(View v, MotionEvent event) {
            switch (event.getButtonState()) {
                case MotionEvent.BUTTON_PRIMARY:   // BUTTON_PRIMARY鼠标左键点击
                    file_path_grid.setOnItemClickListener(new OnitemClickListener(event));
                    break;
                case MotionEvent.BUTTON_SECONDARY:    //BUTTON_SECONDARY鼠标右键点击
                    mouseRightTag = "button_secondary";
                    file_path_grid.setOnItemClickListener(new OnitemClickListener(event));
                    mFileViewInteractionHub.shownContextDialog(mFileViewInteractionHub, event);
                    break;
                case MotionEvent.BUTTON_TERTIARY:   //BUTTON_TERTIARY鼠标滚轮点击
                    file_path_grid.setOnItemClickListener(new OnitemClickListener(event));
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
    }
    private class MouseListOnGenericMotionListener implements View.OnGenericMotionListener {
        @Override
        public boolean onGenericMotion(View v, MotionEvent event) {
            switch (event.getButtonState()) {
                case MotionEvent.BUTTON_PRIMARY:   // BUTTON_PRIMARY鼠标左键点击
                    file_path_list.setOnItemClickListener(new OnitemClickListener(event));
                    break;
                case MotionEvent.BUTTON_SECONDARY:    //BUTTON_SECONDARY鼠标右键点击
                    mouseRightTag = "button_secondary";
                    file_path_list.setOnItemClickListener(new OnitemClickListener(event));
                    mFileViewInteractionHub.shownContextDialog(mFileViewInteractionHub, event);
                    break;
                case MotionEvent.BUTTON_TERTIARY:   //BUTTON_TERTIARY鼠标滚轮点击
                    file_path_list.setOnItemClickListener(new OnitemClickListener(event));
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
    }
}
