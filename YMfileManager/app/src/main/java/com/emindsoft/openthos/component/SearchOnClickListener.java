package com.emindsoft.openthos.component;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.view.View;

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
 * Created by zuojj on 16-8-11.
 */
public class SearchOnClickListener implements View.OnClickListener {
    private String LOG_TAG = "SearchOnQueryTextListener";
    private ProgressDialog progressDialog;
    //文件list集合
    private ArrayList<SearchInfo> mFileList = new ArrayList<>();
    //V4包下的fragment管理器
    FragmentManager manager;
    //    获取sd卡文件根目录
    private final static String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    //根目录文件
    File root = new File(rootPath);
    private Context context;
    private String input;

    public SearchOnClickListener(FragmentManager manager, Editable text, MainActivity context) {
        this.manager = manager;
        this.context = context;
        this.input = text.toString();
    }

    @Override
    public void onClick(View v) {
        if (mFileList.size() >0 && LocalCache.getSearchText() != null) {
            mFileList.clear();
            startSearch(input.trim());
            //开启searchfragment
            if (mFileList.size() > 0) {
                startSearchFragment();
            } else {
                if (progressDialog != null){
                    progressDialog.dismiss();
                }
                T.showShort(context, "没有发现文件，请检查后重新输入！");
            }
        }
        assert mFileList != null;
        mFileList.clear();
        LocalCache.setSearchText(input.trim());
        //开始搜索
        startSearch(input.trim());
        L.e(LOG_TAG, mFileList.size() + "");
        //开启searchfragment
        if (mFileList.size() > 0) {
            startSearchFragment();
        } else {
            progressDialog.dismiss();
            T.showShort(context, "没有发现文件，请检查后重新输入！");
        }
    }

    //搜索dialog
    private void showDialog() {
        progressDialog = new ProgressDialog(context);
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
        //搜索dialog
        showDialog();
        // 从根目录开始
        if (root.exists() && root.isDirectory()) {
            final File[] currentFiles = root.listFiles();
            // 记录当前路径下的所有文件夹的文件数组
            mFileList = searchFileFromDir(text_search, currentFiles);
        }
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
