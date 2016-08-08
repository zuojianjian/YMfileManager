package com.emindsoft.openthos.component;

import android.app.ProgressDialog;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.widget.TextView;

import com.emindsoft.openthos.MainActivity;
import com.emindsoft.openthos.R;
import com.emindsoft.openthos.bean.SearchInfo;
import com.emindsoft.openthos.fragment.SearchFragment;
import com.emindsoft.openthos.utils.L;
import com.emindsoft.openthos.utils.LocalCache;
import com.emindsoft.openthos.utils.T;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by zuojj on 16-8-8.
 */
public class SearchOnEditorActionListener implements TextView.OnEditorActionListener {
    private String LOG_TAG = "SearchOnQueryTextListener";
    private ProgressDialog progressDialog;
    //文件list集合
    private ArrayList<SearchInfo> mFileList = new ArrayList<>();
    //V4包下的fragment管理器
    FragmentManager manager;
    //    获取sd卡文件根目录
    private final static String rootPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath();
    //根目录文件
    File root = new File(rootPath);
    private MainActivity mainActivity;

    public SearchOnEditorActionListener(FragmentManager manager, MainActivity mainActivity) {
        this.manager = manager;
        this.mainActivity = mainActivity;
    }

    @Override
    public boolean onEditorAction(TextView input, int actionId, KeyEvent event) {
        if (mFileList != null && LocalCache.getSearchText() != null) {
            mFileList.clear();
            startSearch(input.toString().trim());
            //开启searchfragment
            if (mFileList.size() > 0) {
                startSearchFragment();
            } else {
                progressDialog.dismiss();
                T.showShort(mainActivity, "没有发现文件，请检查后重新输入！");
            }
        }
        assert mFileList != null;
        mFileList.clear();
        LocalCache.setSearchText(input.toString().trim());
        //搜索dialog
        showDialog();
        //开始搜索
        startSearch(input.toString().trim());
        L.e(LOG_TAG, mFileList.size() + "");
        //开启searchfragment
        if (mFileList.size() > 0) {
            startSearchFragment();
        } else {
            progressDialog.dismiss();
            T.showShort(mainActivity, "没有发现文件，请检查后重新输入！");
        }
        return false;
    }

    //搜索dialog
    private void showDialog() {
        progressDialog = new ProgressDialog(mainActivity);
        progressDialog.setMessage("loading...");
        //显示dialog
        progressDialog.show();
    }


    //搜索fragment
    private void startSearchFragment() {
        SearchFragment searchFragment = new SearchFragment(manager, mFileList);
        manager.popBackStack();
        manager.beginTransaction().replace(R.id.fl_mian, searchFragment).commit();
        //移除dialog
        progressDialog.dismiss();
    }

    // 开始搜索
    public void startSearch(final String text_search) {
        // 从根目录开始
        final File[] currentFiles = root.listFiles();
        // 记录当前路径下的所有文件夹的文件数组
        mFileList = searchFileFromDir(text_search, currentFiles);
    }

    //遍历文件夹匹配关键字
    private ArrayList<SearchInfo> searchFileFromDir(String text_search, File[] files) {
        StringBuilder str_builder = new StringBuilder();
        File[] currentFiles;
        for (File file : files) {
            SearchInfo searchInfo = new SearchInfo();
            // 如果当前是文件夹，逐层遍历所有文件
            if (file.isDirectory()) {
                currentFiles = file.listFiles();
                str_builder.append(searchFileFromDir(text_search, currentFiles));
            }
            // 获取文件及其文件夹
            String fileName = file.getName();
            // 获取文件及其文件夹路径
            String filePath = file.getPath();

            if (fileName.contains(text_search)) {
                //将遍历到的文件和文件夹添加到searchInfo中
                searchInfo.setFileName(fileName);
                searchInfo.setFilePath(filePath);
                if (mFileList.contains(fileName) && mFileList.contains(filePath)) {
                } else {
                    mFileList.add(searchInfo);
                }
            }
        }
        return mFileList;
    }
}
