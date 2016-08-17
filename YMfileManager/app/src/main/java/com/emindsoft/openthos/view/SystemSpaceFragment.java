package com.emindsoft.openthos.view;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import com.emindsoft.openthos.BaseFragment;
import com.emindsoft.openthos.MainActivity;
import com.emindsoft.openthos.R;
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
import com.emindsoft.openthos.utils.LocalCache;
import com.emindsoft.openthos.utils.T;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 系统空间存储页面
 * Created by zuojj on 16-5-19.
 */
public class SystemSpaceFragment extends BaseFragment implements
        IFileInteractionListener, MainActivity.IBackPressedListener {
    public static final String EXT_FILTER_KEY = "ext_filter";
    private static final String LOG_TAG = "SystemSpaceFragment";
    public static final String ROOT_DIRECTORY = "root_directory";
    public static final String PICK_FOLDER = "pick_folder";
    // private TextView mCurrentPathTextView;
    private ArrayAdapter<FileInfo> mAdapter;
    private FileViewInteractionHub mFileViewInteractionHub;
    private IFileInteractionListener mFileViewListener;
    //区分文件的工具类
    private FileCategoryHelper mFileCagetoryHelper;
    //设置不同文件图标的工具类
    private FileIconHelper mFileIconHelper;
    //文件list集合
    private ArrayList<FileInfo> mFileNameList = new ArrayList<>();
    private Activity mActivity;
    private View view;
    private boolean mBackspaceExit;
    ListView file_path_list;
    GridView file_path_grid;
    //    LinearLayout pick_operation_bar;
//    Button button_pick_confirm;
//    Button button_pick_cancel;
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
                    if (isCtrlPress){
                        mAdapter.clear();
                        initData();
                    }else {
                        mAdapter.clear();
                        initData();
                    }
//                    T.showShort(mActivity,isCtrlPress+"");
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
        if (mFileViewInteractionHub.getSelectedFileList() != null){

        }
        switch (tag) {
            case "pop_refresh":
                mFileViewInteractionHub.onOperationReferesh();
                break;
            case "pop_cancel_all":
                mFileViewInteractionHub.onOperationSelectAllOrCancel();
                break;
            case "pop_copy":
                if (mFileViewInteractionHub.getSelectedFileList() != null){
                    mFileViewInteractionHub.doOnOperationCopy();
                }
                T.showShort(mActivity,"请选择复制的文件");
                break;
            case "pop_delete":
                if (mFileViewInteractionHub.getSelectedFileList() != null){
                    mFileViewInteractionHub.onOperationDelete();
                }
                T.showShort(mActivity,"请选择要删除的文件");
                break;
            case "pop_send":
                if (mFileViewInteractionHub.getSelectedFileList() != null){
                    mFileViewInteractionHub.onOperationSend();
                }
                T.showShort(mActivity,"请选择发送文件");
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
                //清空数据集合，重新加载数据和布局（view视图的切换）
                mAdapter.clear();
                initData();
                mFileViewInteractionHub.clearSelection();
                break;
            case "list":
                //清空数据集合，重新加载数据和布局（view视图的切换）
                mAdapter.clear();
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
    public SystemSpaceFragment(String sdSpaceFragment, String directPath, ArrayList<FileInfo> fileInfoList, FileViewInteractionHub.CopyOrMove copyOrMove) {
        this.sdOrSystem = sdSpaceFragment;
        this.fileInfoList = fileInfoList;
        this.copyOrMove = copyOrMove;
        this.directorPath = directPath;
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
        file_path_list = (ListView) view.findViewById(R.id.file_path_list);
        file_path_grid = (GridView) view.findViewById(R.id.file_path_grid);
//        pick_operation_bar = (LinearLayout) view.findViewById(R.id.pick_operation_bar);
//        button_pick_confirm = (Button) view.findViewById(R.id.button_pick_confirm);
//        button_pick_cancel = (Button) view.findViewById(R.id.button_pick_cancel);
    }

    private void initData() {
        mActivity = getActivity();
        mFileCagetoryHelper = new FileCategoryHelper(mActivity);
        mFileViewInteractionHub = new FileViewInteractionHub(this);
        Intent intent = getActivity().getIntent();
//        String action = intent.getAction();
//        if (!TextUtils.isEmpty(action) && (action.equals(Intent.ACTION_PICK) || action.equals(Intent.ACTION_GET_CONTENT))) {
//            mFileViewInteractionHub.setMode(FileViewInteractionHub.Mode.Pick);
//
//            boolean pickFolder = intent.getBooleanExtra(PICK_FOLDER, false);
//            if (!pickFolder) {
//                String[] exts = intent.getStringArrayExtra(EXT_FILTER_KEY);
//                if (exts != null) {
//                    mFileCagetoryHelper.setCustomCategory(exts);
//                }
//            } else {
//                mFileCagetoryHelper.setCustomCategory(new String[]{} /*folder only*/);
//                pick_operation_bar.setVisibility(View.VISIBLE);
//
//                button_pick_confirm.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        try {
//                            Intent intent = Intent.parseUri(mFileViewInteractionHub.getCurrentPath(), 0);
//                            mActivity.setResult(Activity.RESULT_OK, intent);
//                            mActivity.finish();
//                        } catch (URISyntaxException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//                button_pick_cancel.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        mActivity.finish();
//                    }
//                });
//            }
//        } else {
//            mFileViewInteractionHub.setMode(FileViewInteractionHub.Mode.View);
//        }

        mFileIconHelper = new FileIconHelper(mActivity);
        if ("list".equals(LocalCache.getViewTag())) {
            mAdapter = new FileListAdapter(mActivity, R.layout.file_browser_item_list, mFileNameList, mFileViewInteractionHub,
                    mFileIconHelper, isCtrlPress);

        } else if ("grid".equals(LocalCache.getViewTag())) {
            mAdapter = new FileListAdapter(mActivity, R.layout.file_browser_item_grid, mFileNameList, mFileViewInteractionHub,
                    mFileIconHelper, isCtrlPress);

        }

        boolean baseSd = intent.getBooleanExtra(Constants.KEY_BASE_SD, !FileManagerPreferenceActivity.isReadRoot(mActivity));
        Log.i(LOG_TAG, "baseSd = " + baseSd);

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
        Log.i(LOG_TAG, "CurrentDir = " + currentDir);

//        mBackspaceExit = (uri != null)
//                && (TextUtils.isEmpty(action)
//                || (!action.equals(Intent.ACTION_PICK) && !action.equals(Intent.ACTION_GET_CONTENT)));

        if ("list".equals(LocalCache.getViewTag())) {
            file_path_list.setVisibility(View.VISIBLE);
            file_path_grid.setVisibility(View.GONE);
            file_path_list.setAdapter(mAdapter);
            file_path_list.setOnGenericMotionListener(new ListOnGenericMotionListener(file_path_list, mFileViewInteractionHub, isCtrlPress));
        } else if ("grid".equals(LocalCache.getViewTag())) {
            file_path_list.setVisibility(View.GONE);
            file_path_grid.setVisibility(View.VISIBLE);
            file_path_grid.setAdapter(mAdapter);
            //TODO
            file_path_grid.setOnGenericMotionListener(new GridOnGenericMotionListener(mActivity, file_path_grid, mFileViewInteractionHub, isCtrlPress));
//                file_path_grid.setLongClickable(true);
//                file_path_grid.setOnCreateContextMenuListener(mFileViewInteractionHub.mListViewContextMenuListener);
//                file_path_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        mFileViewInteractionHub.onListItemClick(parent, view, position, id);
//                    }
//                });
        }


        mFileViewInteractionHub.refreshFileList();

        if (fileInfoList != null && fileInfoList.size() > 0) {
            mFileViewInteractionHub.setCheckedFileList(fileInfoList, copyOrMove);
        }

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
        //更新UI
        updateUI();
        //设置optionmenu
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //当页面销毁时解绑广播接收器和当前view
        mActivity.unregisterReceiver(mReceiver);
    }

//    //准备创建optionmenu
//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        mFileViewInteractionHub.onPrepareOptionsMenu(menu);
//        super.onPrepareOptionsMenu(menu);
//    }
//
//    //创建optionmenu
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        mFileViewInteractionHub.onCreateOptionsMenu(menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    //回退操作
    public boolean onBack() {
        if (mBackspaceExit || !Util.isSDCardReady() || mFileViewInteractionHub == null) {
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
                    Log.i(LOG_TAG, "computeScrollPosition: update item: " + mPreviousPath + " " + firstVisiblePosition
                            + " stack count:" + mScrollPositionList.size());
                    pos = firstVisiblePosition;
                } else {
                    mScrollPositionList.add(new PathScrollPositionItem(mPreviousPath, firstVisiblePosition));
                    Log.i(LOG_TAG, "computeScrollPosition: add item: " + mPreviousPath + " " + firstVisiblePosition
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

        Log.i(LOG_TAG, "computeScrollPosition: result pos: " + path + " " + pos + " stack count:" + mScrollPositionList.size());
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

    //TODO
    @Override
    public boolean shouldHideMenu(int menu) {
        return false;
    }
//
//    public void copyFile(ArrayList<FileInfo> files) {
//        mFileViewInteractionHub.doOnOperationCopy(files);
//    }
//
//    public void refresh() {
//        if (mFileViewInteractionHub != null) {
//            mFileViewInteractionHub.refreshFileList();
//        }
//    }
//
//    public void moveToFile(ArrayList<FileInfo> files) {
//        mFileViewInteractionHub.moveFileFrom(files);
//    }

    public interface SelectFilesCallback {
        // files equals null indicates canceled
        void selected(ArrayList<FileInfo> files);
    }
//
//    public void startSelectFiles(SelectFilesCallback callback) {
//        mFileViewInteractionHub.startSelectFiles(callback);
//    }

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
}
