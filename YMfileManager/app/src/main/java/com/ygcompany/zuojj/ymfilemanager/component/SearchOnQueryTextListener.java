package com.ygcompany.zuojj.ymfilemanager.component;

import android.app.ProgressDialog;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.widget.SearchView;

import com.ygcompany.zuojj.ymfilemanager.MainActivity;
import com.ygcompany.zuojj.ymfilemanager.R;
import com.ygcompany.zuojj.ymfilemanager.bean.SearchInfo;
import com.ygcompany.zuojj.ymfilemanager.fragment.SearchFragment;
import com.ygcompany.zuojj.ymfilemanager.utils.LocalCache;
import com.ygcompany.zuojj.ymfilemanager.utils.T;

import java.io.File;
import java.util.ArrayList;

/**
 * 搜索功能实现
 * Created by zuojj on 16-6-24.
 */
public class SearchOnQueryTextListener implements SearchView.OnQueryTextListener {
    //搜索的dialog
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

    public SearchOnQueryTextListener(FragmentManager manager, MainActivity mainActivity) {
        this.manager = manager;
        this.mainActivity = mainActivity;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {   // 当点击搜索按钮时触发该方法
        if (null!=LocalCache.getInstance(mainActivity).getSearchText() &&
                !LocalCache.getInstance(mainActivity).getSearchText().equals(query.trim())){
           mFileList.clear();
            startSearch(query.trim());
            //开启searchfragment
            if (mFileList.size() > 0) {
                progressDialog.dismiss();
                startSearchFragment();
            } else {
                progressDialog.dismiss();
                T.showShort(mainActivity, "没有发现文件，请检查后重新输入！");
            }
        }
        if (null != query.trim()) {
            LocalCache.getInstance(mainActivity).setSearchText(query.trim());
            //搜索dialog
            showDialog();
            //开始搜索
            startSearch(query.trim());
            //开启searchfragment
            if (mFileList.size() > 0) {
                progressDialog.dismiss();
                startSearchFragment();
            } else {
                progressDialog.dismiss();
                T.showShort(mainActivity, "没有发现文件，请检查后重新输入！");
            }
        } else {
            T.showShort(mainActivity, "请输入搜索内容...");
        }

        return true;
    }

    // 当搜索内容改变时触发该方法
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    //搜索dialog
    private void showDialog() {
        progressDialog = new ProgressDialog(mainActivity);
        progressDialog.setTitle("搜索中");
        progressDialog.setMessage("loading...");
        //显示dialog
        progressDialog.show();
    }


    //搜索fragment
    private void startSearchFragment() {
        SearchFragment searchFragment = new SearchFragment(manager, mFileList);
        manager.beginTransaction().replace(R.id.fl_mian, searchFragment)
                .addToBackStack(null).commit();
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
        File[] currentFiles = null;
        for (int i = 0; i < files.length; i++) {
            SearchInfo searchInfo = new SearchInfo();
            // 如果当前是文件夹，逐层遍历所有文件
            if (files[i].isDirectory()) {
                currentFiles = files[i].listFiles();
                str_builder.append(searchFileFromDir(text_search, currentFiles));
            }
            // 获取文件及其文件夹
            String fileName = files[i].getName();
            // 获取文件及其文件夹路径
            String filePath = files[i].getPath();

            if (fileName.indexOf(text_search) >= 0) {
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
