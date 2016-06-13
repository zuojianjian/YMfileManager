package com.ygcompany.zuojj.ymfilemanager.view;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ygcompany.zuojj.ymfilemanager.BaseFragment;
import com.ygcompany.zuojj.ymfilemanager.MainActivity;
import com.ygcompany.zuojj.ymfilemanager.R;
import com.ygcompany.zuojj.ymfilemanager.system.Constants;
import com.ygcompany.zuojj.ymfilemanager.system.FileCategoryHelper;
import com.ygcompany.zuojj.ymfilemanager.system.FileIconHelper;
import com.ygcompany.zuojj.ymfilemanager.system.FileInfo;
import com.ygcompany.zuojj.ymfilemanager.system.FileListAdapter;
import com.ygcompany.zuojj.ymfilemanager.system.FileManagerPreferenceActivity;
import com.ygcompany.zuojj.ymfilemanager.system.FileSortHelper;
import com.ygcompany.zuojj.ymfilemanager.system.FileViewInteractionHub;
import com.ygcompany.zuojj.ymfilemanager.system.IFileInteractionListener;
import com.ygcompany.zuojj.ymfilemanager.system.Settings;
import com.ygcompany.zuojj.ymfilemanager.system.Util;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 系统空间存储页面
 * Created by zuojj on 16-5-19.
 */
public class SystemSpaceFragment extends BaseFragment implements
        IFileInteractionListener ,MainActivity.IBackPressedListener {
    public static final String EXT_FILTER_KEY = "ext_filter";

    private static final String LOG_TAG = "SystemSpaceFragment";

    public static final String EXT_FILE_FIRST_KEY = "ext_file_first";

    public static final String ROOT_DIRECTORY = "root_directory";

    public static final String PICK_FOLDER = "pick_folder";

    // private TextView mCurrentPathTextView;
    private ArrayAdapter<FileInfo> mAdapter;

    private FileViewInteractionHub mFileViewInteractionHub;

    //区分文件的工具类
    private FileCategoryHelper mFileCagetoryHelper;

    //设置不同文件图标的工具类
    private FileIconHelper mFileIconHelper;

    //文件list集合
    private ArrayList<FileInfo> mFileNameList = new ArrayList<FileInfo>();

    private Activity mActivity;

    private View view;

    //获得SD卡的存储目录
    private static final String sdDir = Util.getSdDirectory();

    //传入的标识和路径
    private String sdOrSystem;
    private String directorPath;

    // 传进来的根目录
    private String curRootDir = "";
    // 传进来的选中文件列表
    private ArrayList<FileInfo> fileInfoList = null;
    // 传进来的复制和移动模式的标志
    FileViewInteractionHub.CopyOrMove copyOrMove = null;

    // memorize the scroll positions of previous paths
    private ArrayList<PathScrollPositionItem> mScrollPositionList = new ArrayList<PathScrollPositionItem>();
    private String mPreviousPath;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.v(LOG_TAG, "received broadcast:" + intent.toString());
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED) || action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }
        }
    };

    private boolean mBackspaceExit;

    @Bind(R.id.file_path_list)
    ListView file_path_list;
    @Bind(R.id.pick_operation_bar)
    LinearLayout pick_operation_bar;
    @Bind(R.id.button_pick_confirm)
    Button button_pick_confirm;
    @Bind(R.id.button_pick_cancel)
    Button button_pick_cancel;

    public SystemSpaceFragment(String sdSpaceFragment,String directPath, ArrayList<FileInfo> fileInfoList, FileViewInteractionHub.CopyOrMove copyOrMove) {
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.system_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mActivity = getActivity();
        mFileCagetoryHelper = new FileCategoryHelper(mActivity);
        mFileViewInteractionHub = new FileViewInteractionHub(this);
        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action) && (action.equals(Intent.ACTION_PICK) || action.equals(Intent.ACTION_GET_CONTENT))) {
            mFileViewInteractionHub.setMode(FileViewInteractionHub.Mode.Pick);

            boolean pickFolder = intent.getBooleanExtra(PICK_FOLDER, false);
            if (!pickFolder) {
                String[] exts = intent.getStringArrayExtra(EXT_FILTER_KEY);
                if (exts != null) {
                    mFileCagetoryHelper.setCustomCategory(exts);
                }
            } else {
                mFileCagetoryHelper.setCustomCategory(new String[]{} /*folder only*/);
                pick_operation_bar.setVisibility(View.VISIBLE);

                button_pick_confirm.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            Intent intent = Intent.parseUri(mFileViewInteractionHub.getCurrentPath(), 0);
                            mActivity.setResult(Activity.RESULT_OK, intent);
                            mActivity.finish();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                });

                button_pick_cancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        mActivity.finish();
                    }
                });
            }
        } else {
            mFileViewInteractionHub.setMode(FileViewInteractionHub.Mode.View);
        }

//        file_path_list = (GridView) view.findViewById(R.id.file_path_list);
        mFileIconHelper = new FileIconHelper(mActivity);
        mAdapter = new FileListAdapter(mActivity, R.layout.file_browser_item, mFileNameList, mFileViewInteractionHub,
                mFileIconHelper);

        boolean baseSd = intent.getBooleanExtra(Constants.KEY_BASE_SD, !FileManagerPreferenceActivity.isReadRoot(mActivity));
        Log.i(LOG_TAG, "baseSd = " + baseSd);

        String rootDir = intent.getStringExtra(ROOT_DIRECTORY);
        if (!TextUtils.isEmpty(rootDir)) {
            if (baseSd && this.sdDir.startsWith(rootDir)) {
                rootDir = this.sdDir;
            }
        } else {
            rootDir = baseSd ? this.sdDir : Constants.ROOT_PATH;
        }
        mFileViewInteractionHub.setRootPath(rootDir);

        //获取currentDir路径为根路径 sdOrSystem为路径选择标识
        String currentDir = FileManagerPreferenceActivity.getPrimaryFolder(mActivity,sdOrSystem,directorPath);
        Uri uri = intent.getData();
        if (uri != null) {
            if (baseSd && this.sdDir.startsWith(uri.getPath())) {
                currentDir = this.sdDir;
            } else {
                currentDir = uri.getPath();
            }
        }
        mFileViewInteractionHub.setCurrentPath(currentDir);
        curRootDir = currentDir;
        Log.i(LOG_TAG, "CurrentDir = " + currentDir);

        mBackspaceExit = (uri != null)
                && (TextUtils.isEmpty(action)
                || (!action.equals(Intent.ACTION_PICK) && !action.equals(Intent.ACTION_GET_CONTENT)));

        file_path_list.setAdapter(mAdapter);
        mFileViewInteractionHub.refreshFileList();

        if (fileInfoList != null && fileInfoList.size() > 0) {
            mFileViewInteractionHub.setCheckedFileList(fileInfoList, copyOrMove);
        }

        //设置意图过滤器，接收SD卡是否挂载的广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addDataScheme("file");
        mActivity.registerReceiver(mReceiver, intentFilter);

        //更新UI
        updateUI();
        //设置optionmenu
        setHasOptionsMenu(true);
    }

    //TODO（待定）
    public void switchType(int popTag) {
        if (popTag == 0) {
            mFileViewInteractionHub.onSortChanged(FileSortHelper.SortMethod.name);
        } else if (popTag == 1) {
            mFileViewInteractionHub.onSortChanged(FileSortHelper.SortMethod.size);
        } else if (popTag == 2) {
            mFileViewInteractionHub.onSortChanged(FileSortHelper.SortMethod.date);
        } else if (popTag == 3) {
            mFileViewInteractionHub.onSortChanged(FileSortHelper.SortMethod.type);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.unregisterReceiver(mReceiver);
        ButterKnife.unbind(this);
    }

    //准备创建optionmenu
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mFileViewInteractionHub.onPrepareOptionsMenu(menu);
        super.onPrepareOptionsMenu(menu);
    }

    //创建optionmenu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mFileViewInteractionHub.onCreateOptionsMenu(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //回退操作
    @Override
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
        if(mPreviousPath!=null) {
            if (path.startsWith(mPreviousPath)) {
                int firstVisiblePosition = file_path_list.getFirstVisiblePosition();
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
                boolean isLast = false;
                for (i = 0; i < mScrollPositionList.size(); i++) {
                    if (!path.startsWith(mScrollPositionList.get(i).path)) {
                        break;
                    }
                }
                // navigate to a totally new branch, not in current stack
                if (i > 0) {
                    pos = mScrollPositionList.get(i - 1).pos;
                }

                for (int j = mScrollPositionList.size() - 1; j >= i-1 && j>=0; j--) {
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
        file_path_list.post(new Runnable() {
            @Override
            public void run() {
                file_path_list.setSelection(pos);
            }
        });
        return true;
    }

    private void updateUI() {
        boolean sdCardReady = Util.isSDCardReady();
        View noSdView = view.findViewById(R.id.sd_not_available_page);
        noSdView.setVisibility(sdCardReady ? View.GONE : View.VISIBLE);

        View navigationBar = view.findViewById(R.id.navigation_bar);
        navigationBar.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);
        file_path_list.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);

        if(sdCardReady) {
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
            return;
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

//    //显示底部导航栏
//    @Override
//    public String getDisplayPath(String path) {
//        if (path.startsWith(this.sdDir) && !FileManagerPreferenceActivity.showRealPath(mActivity)) {
//            return getString(R.string.sd_folder) + path.substring(this.sdDir.length());
//        } else {
//            return path;
//        }
//    }
    //根据传入页面标志,显示底部导航栏
    @Override
    public String getDisplayPath(String path) {
        int id = 0;
        String paths = path;
        if (sdOrSystem.equals("system_space_fragment")){
            id = R.string.tab_system_category;
            paths = path.substring(7);
        }else if (sdOrSystem.equals("sd_space_fragment")){
            paths = path.substring(11);
            id = R.string.tab_sd_category;
        }else if (sdOrSystem.equals("usb_space_fragment")){
            id = R.string.tab_usb_category;
            paths = "";
        }else if (sdOrSystem.equals("yun_space_fragment")){
            id = R.string.tab_yun_category;
            paths = "";
        }else if (sdOrSystem.equals("search_fragment")){
            id = R.string.qi_ta;
        }

        return getString(id) + paths;
    }

    @Override
    public String getRealPath(String displayPath) {
        final String perfixName = getString(R.string.sd_folder);
        if (displayPath.startsWith(perfixName)) {
            return this.sdDir + displayPath.substring(perfixName.length());
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

    public void copyFile(ArrayList<FileInfo> files) {
        mFileViewInteractionHub.onOperationCopy(files);
    }

    public void refresh() {
        if (mFileViewInteractionHub != null) {
            mFileViewInteractionHub.refreshFileList();
        }
    }

    public void moveToFile(ArrayList<FileInfo> files) {
        mFileViewInteractionHub.moveFileFrom(files);
    }

    public interface SelectFilesCallback {
        // files equals null indicates canceled
        void selected(ArrayList<FileInfo> files);
    }

    public void startSelectFiles(SelectFilesCallback callback) {
        mFileViewInteractionHub.startSelectFiles(callback);
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
     * @return
     */
    public boolean canGoBack() {
        String currentPath = mFileViewInteractionHub.getCurrentPath();
        if (currentPath.trim().equals(curRootDir.trim())) {
            return false;
        }
        return true;
    }

    /**
     * 执行回退操作
     */
    public void goBack() {
       mFileViewInteractionHub.onBackPressed();
    }

    // 获取当前选中的文件列表
    public ArrayList<FileInfo> getFileInfoList(){
        return mFileViewInteractionHub.getCheckedFileList();
    }

    // 当前是移动还是复制操作
    public FileViewInteractionHub.CopyOrMove getCurCopyOrMoveMode() {
        return mFileViewInteractionHub.getCurCopyOrMoveMode();
    }
}
