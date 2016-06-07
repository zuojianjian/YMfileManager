package com.ygcompany.zuojj.ymfilemanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ygcompany.zuojj.ymfilemanager.component.PopWinShare;
import com.ygcompany.zuojj.ymfilemanager.fragment.DeskFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.MusicFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.OnlineNeighborFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.PersonalStorageFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.PictrueFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.SdStorageFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.VideoFragment;
import com.ygcompany.zuojj.ymfilemanager.utils.DisplayUtil;
import com.ygcompany.zuojj.ymfilemanager.utils.T;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 主界面点击左侧边栏contentFragment进行切换
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String SD_STORAGE_FRAGMENT = "sd_storage_fragment";
    //V4包下的fragment管理器
    FragmentManager manager = getSupportFragmentManager();
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
    @Bind(R.id.iv_back)
    ImageView iv_back;
    @Bind(R.id.iv_menu)
    ImageView iv_menu;

    private PopWinShare popWinShare;

    // 当前正在展示的页面
    private Fragment curFragment = null;
    //
    private SdStorageFragment sdStorageFragment = null;

    //
    private DeskFragment deskFragment;
    private MusicFragment musicFragment;
    //
    private VideoFragment videoFragment;
    private PictrueFragment pictrueFragment;
    //
    private PersonalStorageFragment personalStorageFragment;
    private OnlineNeighborFragment onlineNeighborFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定view
        ButterKnife.bind(MainActivity.this);
        //初始化fragment
        initFragemnt();
        //初始化数据设置点击监听
        initView();
    }

    //初始化fragment
    private void initFragemnt() {
        sdStorageFragment = new SdStorageFragment(manager,SD_STORAGE_FRAGMENT);
        deskFragment =  new DeskFragment();
        musicFragment = new MusicFragment();
        videoFragment =  new VideoFragment();
        pictrueFragment = new PictrueFragment();
        personalStorageFragment =  new PersonalStorageFragment();
        onlineNeighborFragment = new OnlineNeighborFragment();

    }

    //设置左侧侧边栏的点击监听
    private void initView() {
        manager.beginTransaction().replace(R.id.fl_mian, sdStorageFragment).commit();
        tv_desk.setOnClickListener(this);
        tv_music.setOnClickListener(this);
        tv_video.setOnClickListener(this);
        tv_picture.setOnClickListener(this);
        tv_android.setOnClickListener(this);
        tv_storage.setOnClickListener(this);
        tv_net_service.setOnClickListener(this);
        //回退和选项菜单的点击监听
        iv_back.setOnClickListener(this);
        iv_menu.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.tv_desk:
                //当回退到主界面时，再执行回退时清空回退栈结束当前页面
                manager.popBackStack();
                curFragment = deskFragment;
                //替换当前fragment并加入回退栈
                manager.beginTransaction().replace(R.id.fl_mian,deskFragment ).commit();
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
                manager.beginTransaction().replace(R.id.fl_mian,sdStorageFragment).commit();
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
            case R.id.iv_back:
                //回退栈的回退监听，当站内fragment>=1时回退一级
                onBackPressed();
                break;
            case R.id.iv_menu:
                T.showShort(MainActivity.this,"iv_menu");
                //菜单popwindow
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

    //回退监听
    @Override
    public void onBackPressed() {
        //各个fragment回退监听
        FragmentManager fm = getSupportFragmentManager();
        fm.findFragmentById(R.id.fl_mian);
        if (fm.getBackStackEntryCount() >= 1) {
            fm.popBackStack();
        } else {
            finish();
        }
//        if ((curFragment != null) && (curFragment == sdStorageFragment)) {
//            if (sdStorageFragment.canGoBack()) {
//                sdStorageFragment.goBack();
//            }
//            else {
//                if (fm.getBackStackEntryCount() >= 1) {
//                    fm.popBackStack();
//                } else {
//                    finish();
//                }
//                return;
//            }
//        } else {

//            return;
//        }

//        filelist页面的回退监听
//        IBackPressedListener backPressedListener = (IBackPressedListener) mTabsAdapter
//                .getItem(mViewPager.getCurrentItem());
//        if (!backPressedListener.onBack()) {
//            super.onBackPressed();
//        }
    }

    //选项菜单的点击监听
    private class OnClickLintener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

        }
    }

    //filelist页面的回退监听接口
    public interface IBackPressedListener {
        /**
         * 处理back事件。
         * @return True: 表示已经处理; False: 没有处理，让基类处理。
         */
        boolean onBack();
    }

}


