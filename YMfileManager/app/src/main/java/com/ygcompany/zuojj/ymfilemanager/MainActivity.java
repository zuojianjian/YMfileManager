package com.ygcompany.zuojj.ymfilemanager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.ygcompany.zuojj.ymfilemanager.bean.SearchInfo;
import com.ygcompany.zuojj.ymfilemanager.component.PopWinShare;
import com.ygcompany.zuojj.ymfilemanager.fragment.DeskFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.MusicFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.OnlineNeighborFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.PersonalStorageFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.PictrueFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.SdStorageFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.SearchFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.VideoFragment;
import com.ygcompany.zuojj.ymfilemanager.utils.DisplayUtil;
import com.ygcompany.zuojj.ymfilemanager.utils.T;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 主界面点击左侧边栏contentFragment进行切换
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    //初始化控件
    @Bind(R.id.fl_mian)
    FrameLayout fl_mian;
    @Bind(R.id.tv_desk)
    LinearLayout tv_desk;
    @Bind(R.id.tv_music)
    LinearLayout tv_music;
    @Bind(R.id.tv_video)
    LinearLayout tv_video;
    @Bind(R.id.tv_picture)
    LinearLayout tv_picture;
    @Bind(R.id.tv_android)
    LinearLayout tv_android;
    @Bind(R.id.tv_storage)
    LinearLayout tv_storage;
    @Bind(R.id.tv_net_service)
    LinearLayout tv_net_service;
    @Bind(R.id.iv_menu)
    ImageView iv_menu;

    private static final String SD_STORAGE_FRAGMENT = "sd_storage_fragment";
    private static final String MAIN_ACTIVITY = "main_activity";
    //V4包下的fragment管理器
    FragmentManager manager = getSupportFragmentManager();
    //搜索文件
    private SearchView search_view;
    //搜索的dialog
    private ProgressDialog progressDialog;
//    获取sd卡文件根目录
    private final static String rootPath = Environment.getExternalStorageDirectory()
            .getAbsolutePath();
    //根目录文件
    File root = new File(rootPath);
    //文件list集合
    private ArrayList<SearchInfo> mFileList = new ArrayList<>();

    //选择控制栏
    private PopWinShare popWinShare;
    // 当前正在展示的页面
    private Fragment curFragment = null;
    //侧边栏各个fragment页面
    private SdStorageFragment sdStorageFragment = null;
    private DeskFragment deskFragment;
    private MusicFragment musicFragment;
    private VideoFragment videoFragment;
    private PictrueFragment pictrueFragment;
    private PersonalStorageFragment personalStorageFragment;
    private OnlineNeighborFragment onlineNeighborFragment;
    private SearchFragment searchFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定view
        ButterKnife.bind(MainActivity.this);
//
//        final ActionBar bar = getActionBar();
//        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);

        //初始化fragment
        initFragemnt();
        //初始化数据设置点击监听
        initView();
    }

    //初始化fragment
    private void initFragemnt() {
        sdStorageFragment = new SdStorageFragment(manager, SD_STORAGE_FRAGMENT);
        deskFragment = new DeskFragment();
        musicFragment = new MusicFragment();
        videoFragment = new VideoFragment();
        pictrueFragment = new PictrueFragment();
        personalStorageFragment = new PersonalStorageFragment();
        onlineNeighborFragment = new OnlineNeighborFragment();
        searchFragment = new SearchFragment(manager,mFileList);
    }

    //设置左侧侧边栏的点击监听
    private void initView() {
        manager.beginTransaction().replace(R.id.fl_mian, sdStorageFragment).commit();
        search_view = (SearchView) findViewById(R.id.search_view);
        curFragment = sdStorageFragment;
        tv_desk.setOnClickListener(this);
        tv_music.setOnClickListener(this);
        tv_video.setOnClickListener(this);
        tv_picture.setOnClickListener(this);
        tv_android.setOnClickListener(this);
        tv_storage.setOnClickListener(this);
        tv_net_service.setOnClickListener(this);
        //回退和选项菜单的点击监听
        iv_menu.setOnClickListener(this);
        // 设置搜索文本监听
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                mFileList.clear();
                //搜索字段
                final String text_search = query.trim();
                if (TextUtils.isEmpty(text_search)) {
                    T.showLong(MainActivity.this, "请输入要搜索的内容..."+root);
                    return false;
                } else showDialog();
                //开始搜索
                startSearch(text_search);
                //开启searchfragment
                if (mFileList.size()>0){
                    progressDialog.dismiss();
                    startSearchFragment();
                }else {
                    progressDialog.dismiss();
                    T.showShort(MainActivity.this,"没有发现文件，请检查后重新输入！");
                }
                return true;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void startSearchFragment() {
        curFragment = searchFragment;
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
                if (mFileList.contains(fileName) && mFileList.contains(filePath)){
                }else {
                    mFileList.add(searchInfo);
                }
            }
        }
        return mFileList;
    }

    private void showDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("搜索中");
        progressDialog.setMessage("loading...");
        //显示dialog
        progressDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_desk:
                //当回退到主界面时，再执行回退时清空回退栈结束当前页面
                manager.popBackStack();
                curFragment = deskFragment;
                //替换当前fragment并加入回退栈
                manager.beginTransaction().replace(R.id.fl_mian, deskFragment).commit();
                break;
            case R.id.tv_music:
                manager.popBackStack();
                curFragment = musicFragment;
                manager.beginTransaction().replace(R.id.fl_mian, musicFragment).commit();
                break;
            case R.id.tv_video:
                manager.popBackStack();
                curFragment = videoFragment;
                manager.beginTransaction().replace(R.id.fl_mian, videoFragment).commit();
                break;
            case R.id.tv_picture:
                manager.popBackStack();
                curFragment = pictrueFragment;
                manager.beginTransaction().replace(R.id.fl_mian, pictrueFragment).commit();
                break;
            case R.id.tv_android:
                manager.popBackStack();
                curFragment = sdStorageFragment;
                manager.beginTransaction().replace(R.id.fl_mian, sdStorageFragment).commit();
                break;
            case R.id.tv_storage:
                manager.popBackStack();
                curFragment = personalStorageFragment;
                manager.beginTransaction().replace(R.id.fl_mian, personalStorageFragment).commit();
                break;
            case R.id.tv_net_service:
                manager.popBackStack();
                curFragment = onlineNeighborFragment;
                manager.beginTransaction().replace(R.id.fl_mian, onlineNeighborFragment).commit();
                break;
            case R.id.iv_menu:
                //选项菜单popwindow
                shownPopWidndow();
                break;
        }
    }

    //选项菜单popwindow
    private void shownPopWidndow() {
        if (popWinShare == null) {
            //自定义的单击事件
            OnClickLintener paramOnClickListener = new OnClickLintener();
            popWinShare = new PopWinShare(MainActivity.this, paramOnClickListener,
                    DisplayUtil.dip2px(MainActivity.this, 120), DisplayUtil.dip2px(MainActivity.this, 160));
            //监听窗口的焦点事件，点击窗口外面则取消显示
            popWinShare.getContentView().setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        popWinShare.dismiss();
                    }
                }
            });
        }
        //设置默认获取焦点
        popWinShare.setFocusable(true);
        //以某个控件的x和y的偏移量位置开始显示窗口
        popWinShare.showAsDropDown(iv_menu, 10, 10);
        //如果窗口存在，则更新
        popWinShare.update();
    }

    //选项菜单的点击监听
    private class OnClickLintener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.pop_select_all:
                    break;
                case R.id.pop_select_cancel:
                    break;
                case R.id.pop_copy:
                    break;
                case R.id.pop_delete:
                    break;
                case R.id.pop_move:
                    break;
                case R.id.pop_send:
                    break;
                case R.id.pop_create:
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 回退监听,当处于回退栈区域时执行弹栈回退,
     * 处于文件夹详情区域时执行文件的goBack操作
     */
    @Override
    public void onBackPressed() {
        //各个fragment回退监听
        FragmentManager fm = getSupportFragmentManager();
        fm.findFragmentById(R.id.fl_mian);
        if ((curFragment != null) && (curFragment == sdStorageFragment)) {
            if (sdStorageFragment.canGoBack()) {
                sdStorageFragment.goBack();
            } else {
                if (fm.getBackStackEntryCount() >= 1) {
                    fm.popBackStack();
                } else {
                    finish();
                }
            }
        } else {
            if (fm.getBackStackEntryCount() >= 1) {
                fm.popBackStack();
            } else {
                finish();
            }
        }
    }
    //filelist页面的回退监听接口
    public interface IBackPressedListener {
        /**
         * 处理back事件。
         *
         * @return True: 表示已经处理; False: 没有处理，让基类处理。
         */
        boolean onBack();
    }
}


