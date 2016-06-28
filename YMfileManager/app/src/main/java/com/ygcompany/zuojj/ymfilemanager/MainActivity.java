package com.ygcompany.zuojj.ymfilemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.ygcompany.zuojj.ymfilemanager.component.OnClickLintener;
import com.ygcompany.zuojj.ymfilemanager.component.PopWinShare;
import com.ygcompany.zuojj.ymfilemanager.component.SearchOnQueryTextListener;
import com.ygcompany.zuojj.ymfilemanager.fragment.DeskFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.MusicFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.OnlineNeighborFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.PictrueFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.SdStorageFragment;
import com.ygcompany.zuojj.ymfilemanager.fragment.VideoFragment;
import com.ygcompany.zuojj.ymfilemanager.system.FileInfo;
import com.ygcompany.zuojj.ymfilemanager.system.FileViewInteractionHub;
import com.ygcompany.zuojj.ymfilemanager.utils.DisplayUtil;
import com.ygcompany.zuojj.ymfilemanager.utils.LocalCache;
import com.ygcompany.zuojj.ymfilemanager.utils.T;
import com.ygcompany.zuojj.ymfilemanager.view.SystemSpaceFragment;

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
    TextView tv_desk;
    @Bind(R.id.tv_music)
    TextView tv_music;
    @Bind(R.id.tv_video)
    TextView tv_video;
    @Bind(R.id.tv_computer)
    TextView tv_computer;
    @Bind(R.id.tv_picture)
    TextView tv_picture;
    @Bind(R.id.tv_storage)
    TextView tv_storage;
    @Bind(R.id.tv_net_service)
    TextView tv_net_service;
    @Bind(R.id.iv_menu)
    ImageView iv_menu;
    @Bind(R.id.iv_switch_view)
    ImageView iv_switch_view;
    @Bind(R.id.iv_back)
    ImageView iv_back;
    @Bind(R.id.iv_fresh)
    ImageView iv_fresh;
    @Bind(R.id.iv_setting)
    ImageView iv_setting;

    private static final String SD_STORAGE_FRAGMENT = "sd_storage_fragment";
    private static final String USB_SPACE_FRAGMENT = "usb_space_fragment";
    //V4包下的fragment管理器
    FragmentManager manager = getSupportFragmentManager();
    //搜索文件
    private SearchView search_view;
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
    private SystemSpaceFragment usbStorageFragment;
    private OnlineNeighborFragment onlineNeighborFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定view
        ButterKnife.bind(MainActivity.this);
        //初始化时默认传入grid视图
        LocalCache.getInstance(this).setViewTag("grid");
        //默认选中computer
        tv_computer.setSelected(true);
        //初始化fragment
        initFragemnt();
        //初始化数据设置点击监听
        initView();
    }

    //初始化fragment
    private void initFragemnt() {
        ArrayList<FileInfo> fileInfoArrayList = null;
        FileViewInteractionHub.CopyOrMove copyOrMove = null;
        sdStorageFragment = new SdStorageFragment(manager, SD_STORAGE_FRAGMENT);
        deskFragment = new DeskFragment();
        musicFragment = new MusicFragment();
        videoFragment = new VideoFragment();
        pictrueFragment = new PictrueFragment();
        usbStorageFragment = new SystemSpaceFragment(USB_SPACE_FRAGMENT, null, fileInfoArrayList, copyOrMove);
        onlineNeighborFragment = new OnlineNeighborFragment();
    }

    //设置左侧侧边栏的点击监听
    private void initView() {
        manager.beginTransaction().replace(R.id.fl_mian, sdStorageFragment).commit();
        search_view = (SearchView) findViewById(R.id.search_view);
        curFragment = sdStorageFragment;
        tv_desk.setOnClickListener(this);  //桌面
        tv_music.setOnClickListener(this);  //音乐
        tv_video.setOnClickListener(this);  //视频
        tv_computer.setOnClickListener(this);  //计算机
        tv_picture.setOnClickListener(this);  //图片
        tv_storage.setOnClickListener(this);  //内存存储
        tv_net_service.setOnClickListener(this);  //网上邻居
        iv_menu.setOnClickListener(this);  //选项菜单的点击监听
        iv_switch_view.setOnClickListener(this);  //切换视图list或者grid
        iv_back.setOnClickListener(this);  //回退监听
        iv_fresh.setOnClickListener(this);  //刷新操作
        iv_setting.setOnClickListener(this);  //设置中心
        // 设置搜索监听
        search_view.setOnQueryTextListener(new SearchOnQueryTextListener(manager,MainActivity.this));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_desk:   //桌面页面
                //当回退到主界面时，再执行回退时清空回退栈结束当前页面
                tv_desk.setSelected(true);
                tv_music.setSelected(false);
                tv_video.setSelected(false);
                tv_computer.setSelected(false);
                tv_picture.setSelected(false);
                tv_storage.setSelected(false);
                tv_net_service.setSelected(false);
                manager.popBackStack();
                curFragment = deskFragment;
                //替换当前fragment并加入回退栈
                manager.beginTransaction().replace(R.id.fl_mian, deskFragment).commit();
                break;
            case R.id.tv_music:  //音乐页面
                tv_music.setSelected(true);
                tv_desk.setSelected(false);
                tv_video.setSelected(false);
                tv_computer.setSelected(false);
                tv_picture.setSelected(false);
                tv_storage.setSelected(false);
                tv_net_service.setSelected(false);
                manager.popBackStack();
                curFragment = musicFragment;
                manager.beginTransaction().replace(R.id.fl_mian, musicFragment).commit();
                break;
            case R.id.tv_video:  //视频页面
                tv_music.setSelected(false);
                tv_desk.setSelected(false);
                tv_video.setSelected(true);
                tv_computer.setSelected(false);
                tv_picture.setSelected(false);
                tv_storage.setSelected(false);
                tv_net_service.setSelected(false);
                manager.popBackStack();
                curFragment = videoFragment;
                manager.beginTransaction().replace(R.id.fl_mian, videoFragment).commit();
                break;
            case R.id.tv_picture:   //图片页面
                tv_music.setSelected(false);
                tv_desk.setSelected(false);
                tv_video.setSelected(false);
                tv_computer.setSelected(false);
                tv_picture.setSelected(true);
                tv_storage.setSelected(false);
                tv_net_service.setSelected(false);
                manager.popBackStack();
                curFragment = pictrueFragment;
                manager.beginTransaction().replace(R.id.fl_mian, pictrueFragment).commit();
                break;
            case R.id.tv_computer:  //计算机页面
                tv_music.setSelected(false);
                tv_desk.setSelected(false);
                tv_video.setSelected(false);
                tv_computer.setSelected(true);
                tv_picture.setSelected(false);
                tv_storage.setSelected(false);
                tv_net_service.setSelected(false);
                manager.popBackStack();
                curFragment = sdStorageFragment;
                manager.beginTransaction().replace(R.id.fl_mian, sdStorageFragment).commit();
                break;
            case R.id.tv_storage:    //USB页面
                tv_music.setSelected(false);
                tv_desk.setSelected(false);
                tv_video.setSelected(false);
                tv_computer.setSelected(false);
                tv_picture.setSelected(false);
                tv_storage.setSelected(true);
                tv_net_service.setSelected(false);
                manager.popBackStack();
                curFragment = usbStorageFragment;
                manager.beginTransaction().replace(R.id.fl_mian, usbStorageFragment).commit();
                break;
            case R.id.tv_net_service:   //网上邻居页面
                tv_music.setSelected(false);
                tv_desk.setSelected(false);
                tv_video.setSelected(false);
                tv_computer.setSelected(false);
                tv_picture.setSelected(false);
                tv_storage.setSelected(false);
                tv_net_service.setSelected(true);
                manager.popBackStack();
                curFragment = onlineNeighborFragment;
                manager.beginTransaction().replace(R.id.fl_mian, onlineNeighborFragment).commit();
                break;
            case R.id.iv_menu:  //选项菜单popwindow
                if (manager.getBackStackEntryCount() < 1){
                    T.showShort(MainActivity.this,"当前页面不支持此操作！");
                }else {
                    shownPopWidndow("iv_menu");
                }
                break;
            case R.id.iv_back:   //返回
                onBackPressed();
                break;
            case R.id.iv_fresh:   //刷新:
                if (manager.getBackStackEntryCount() < 1) {
                    T.showShort(MainActivity.this, "当前页面不支持刷新操作");
                }
                sendBroadcastMessage("iv_fresh",null);
                break;
            case R.id.iv_setting:   //设置
                shownPopWidndow("iv_setting");
                break;
            case R.id.iv_switch_view:  //切换视图
                //顶部切换视图按钮
                switchListOrgrid();
                //发送广播通知视图切换
                sendBroadcastMessage("iv_switch_view", null);
                break;
        }
    }

    /**
     * 发送广播
     * @param name 按钮点击的标识
     * @param tag  要传递的字段
     */
    private void sendBroadcastMessage(String name, String tag) {
        Intent intent = new Intent();
        if (name.equals("iv_switch_view")) {
            intent.setAction("com.switchview");
        } else if (name.equals("iv_fresh")){
            intent.setAction("com.refreshview");
        }
        sendBroadcast(intent);
    }
    //顶部切换视图按钮
    private void switchListOrgrid() {
        if (LocalCache.getInstance(this).getViewTag() != "list") {
            LocalCache.getInstance(this).setViewTag("list");
            iv_switch_view.setSelected(true);
            T.showShort(MainActivity.this,"已切换为列表视图！");
        } else if ((LocalCache.getInstance(this).getViewTag()) != "grid") {
            LocalCache.getInstance(this).setViewTag("grid");
            iv_switch_view.setSelected(false);
            T.showShort(MainActivity.this,"已切换为网格视图！");
        }
    }

    /**
     * 选项菜单popwindow
     * @param menu_tag 根据tag选择按钮popwindow
     */
    private void shownPopWidndow(String menu_tag) {
        popWinShare = null;
        if (popWinShare == null) {
            //自定义的单击事件
            OnClickLintener paramOnClickListener = new OnClickLintener(menu_tag,MainActivity.this);
            if (menu_tag.equals("iv_menu")){
                popWinShare = new PopWinShare(MainActivity.this, paramOnClickListener,
                        DisplayUtil.dip2px(MainActivity.this, 125), DisplayUtil.dip2px(MainActivity.this, 260), menu_tag);
                //设置默认获取焦点
                popWinShare.setFocusable(true);
                //以某个控件的x和y的偏移量位置开始显示窗口
                popWinShare.showAsDropDown(this.iv_menu, -60, 10);

            }else if (menu_tag.equals("iv_setting")){
                popWinShare = new PopWinShare(MainActivity.this, paramOnClickListener,
                        DisplayUtil.dip2px(MainActivity.this, 120), DisplayUtil.dip2px(MainActivity.this, 160),menu_tag);
                //设置默认获取焦点
                popWinShare.setFocusable(true);
                //以某个控件的x和y的偏移量位置开始显示窗口
                popWinShare.showAsDropDown(this.iv_setting, -15, 10);
            }
            //如果窗口存在，则更新
            popWinShare.update();
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
    }

    /**
     * 回退监听,当处于回退栈区域时执行弹栈回退,
     * 处于文件夹详情区域时执行文件的goBack操作
     */
    @Override
    public void onBackPressed() {
        //各个fragment回退监听
        manager.findFragmentById(R.id.fl_mian);
        if ((curFragment != null) && (curFragment == sdStorageFragment)) {
            if (sdStorageFragment.canGoBack()) {
                sdStorageFragment.goBack();
            } else {
                if (manager.getBackStackEntryCount() >= 1) {
                    manager.popBackStack();
                } else {
                    finish();
                }
            }
        } else {
            if (manager.getBackStackEntryCount() >= 1) {
                manager.popBackStack();
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


