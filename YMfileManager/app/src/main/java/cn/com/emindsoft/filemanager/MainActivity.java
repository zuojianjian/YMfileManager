package cn.com.emindsoft.filemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import cm.com.emindsoft.filemanager.R;

import cn.com.emindsoft.filemanager.component.PopOnClickLintener;
import cn.com.emindsoft.filemanager.component.PopWinShare;
import cn.com.emindsoft.filemanager.component.SearchOnQueryTextListener;
import cn.com.emindsoft.filemanager.fragment.DeskFragment;
import cn.com.emindsoft.filemanager.fragment.MusicFragment;
import cn.com.emindsoft.filemanager.fragment.OnlineNeighborFragment;
import cn.com.emindsoft.filemanager.fragment.PictrueFragment;
import cn.com.emindsoft.filemanager.fragment.SdStorageFragment;
import cn.com.emindsoft.filemanager.fragment.VideoFragment;
import cn.com.emindsoft.filemanager.utils.DisplayUtil;
import cn.com.emindsoft.filemanager.utils.L;
import cn.com.emindsoft.filemanager.utils.LocalCache;
import cn.com.emindsoft.filemanager.utils.T;
import cn.com.emindsoft.filemanager.view.SystemSpaceFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 主界面点击左侧边栏contentFragment进行切换
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
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

    private static final String USB_SPACE_FRAGMENT = "usb_space_fragment";
    //V4包下的fragment管理器
    FragmentManager manager = getSupportFragmentManager();
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
    private UsbConnectReceiver receiver;
    private SearchView search_view;
    private boolean isCtrl = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //绑定viewe
        ButterKnife.bind(MainActivity.this);
        //初始化时默认传入grid视图,实例化不能去掉,否则会报空指针异常
        LocalCache.getInstance(MainActivity.this).setViewTag("grid");
        //初始化fragment
        initFragemnt();
        //初始化数据设置点击监听
        initView();
        registerReceiver();
    }

    /**
     * 动态注册
     */
    private void registerReceiver() {
        /*动态方式注册广播接收者*/
        receiver = new UsbConnectReceiver();
        IntentFilter filter = new IntentFilter();

        filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        filter.addCategory("android.intent.category.DEFAULT");
        this.registerReceiver(receiver, filter);
    }

    //初始化fragment
    private void initFragemnt() {
        if (sdStorageFragment == null){
            sdStorageFragment = new SdStorageFragment(manager, null);
        }
        if (deskFragment == null){
            deskFragment = new DeskFragment();
        }
        if (musicFragment == null){
            musicFragment = new MusicFragment();
        }
        if (videoFragment == null){
            videoFragment = new VideoFragment();
        }
        if (pictrueFragment == null){
            pictrueFragment = new PictrueFragment();
        }
        if (usbStorageFragment == null){
            usbStorageFragment = new SystemSpaceFragment(USB_SPACE_FRAGMENT, null, null, null);
        }
        if (onlineNeighborFragment == null){
            onlineNeighborFragment = new OnlineNeighborFragment();
        }

    }

    //设置左侧侧边栏的点击监听
    private void initView() {
        search_view = (SearchView) findViewById(R.id.search_view);
        curFragment = sdStorageFragment;
        tv_desk.setOnClickListener(this);  //桌面
        tv_music.setOnClickListener(this);  //音乐
        tv_video.setOnClickListener(this);  //视频
        tv_computer.setOnClickListener(this);  //计算机
        tv_picture.setOnClickListener(this);  //图片
        tv_net_service.setOnClickListener(this);  //网上邻居
        iv_menu.setOnClickListener(this);  //选项菜单的点击监听
        iv_switch_view.setOnClickListener(this);  //切换视图list或者grid
        iv_back.setOnClickListener(this);  //回退监听
        iv_fresh.setOnClickListener(this);  //刷新操作
        iv_setting.setOnClickListener(this);  //设置中心

        //默认选中的第一个页面
        tv_computer.performClick();
        // 设置搜索监听
        search_view.setOnQueryTextListener(new SearchOnQueryTextListener(manager, MainActivity.this));

    }

    /**
     * usb 广播接收器
     */
    class UsbConnectReceiver extends BroadcastReceiver {
        private static final String TAG = "UsbConnectReceiver";
        private static final String USB_DEVICE_ATTACHED = "usb_device_attached";
        private static final String USB_DEVICE_DETACHED = "usb_device_detached";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            L.d(TAG, "intent.getAction() ->" + action);

            if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) { //usb连接
                tv_storage.setVisibility(View.VISIBLE);
                tv_storage.setOnClickListener(MainActivity.this);  //内存存储
                sdStorageFragment = new SdStorageFragment(manager, USB_DEVICE_ATTACHED);
                setSelectedBackground(R.id.tv_computer);
                manager.popBackStack();
                curFragment = sdStorageFragment;
                manager.beginTransaction().replace(R.id.fl_mian, sdStorageFragment).commit();
                T.showShort(MainActivity.this, "USB设备已连接～");

            } else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) { //usb断开
//                T.showShort(context, USB_DEVICE_DETACHED);
                tv_storage.setVisibility(View.GONE);
                sdStorageFragment = new SdStorageFragment(manager, USB_DEVICE_DETACHED);
                setSelectedBackground(R.id.tv_computer);
                manager.popBackStack();
                curFragment = sdStorageFragment;
                manager.beginTransaction().replace(R.id.fl_mian, sdStorageFragment).commit();
                T.showShort(MainActivity.this, "USB设备已断开连接～");
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断backspace是回退还是删除操作
        if (keyCode == KeyEvent.KEYCODE_DEL && !search_view.hasFocus()) {
            onBackPressed();
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER){
            L.e("KEYCODE_ENTER","KEYCODE_ENTER");
            //TODO
        }
        if (keyCode == KeyEvent.KEYCODE_CTRL_LEFT || keyCode == KeyEvent.KEYCODE_CTRL_RIGHT){
            isCtrl = true;
        }
        if (isCtrl && keyCode == KeyEvent.KEYCODE_X){
            sendBroadcastMessage("iv_menu","pop_cut");
            T.showShort(this,"剪切成功");
            isCtrl = false;
        }if (isCtrl && keyCode == KeyEvent.KEYCODE_C){
            sendBroadcastMessage("iv_menu","pop_copy");
            T.showShort(this,"复制成功");
            isCtrl = false;
        }if (isCtrl && keyCode == KeyEvent.KEYCODE_V){
            sendBroadcastMessage("iv_menu","pop_paste");
            T.showShort(this,"粘贴成功");
            isCtrl = false;
        }if (isCtrl && keyCode == KeyEvent.KEYCODE_Z){
            sendBroadcastMessage("iv_menu","pop_cacel");
            T.showShort(this,"取消成功");
            isCtrl = false;
        }if (isCtrl && keyCode == KeyEvent.KEYCODE_D){
            sendBroadcastMessage("iv_menu","pop_delete");
            T.showShort(this,"删除成功");
            isCtrl = false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_desk:   //桌面页面
                startAndSettingFragment(R.id.tv_desk,manager,deskFragment);
                break;
            case R.id.tv_music:  //音乐页面
                startAndSettingFragment(R.id.tv_music,manager,musicFragment);
                break;
            case R.id.tv_video:  //视频页面
                startAndSettingFragment(R.id.tv_video,manager,videoFragment);
                break;
            case R.id.tv_picture:   //图片页面
                startAndSettingFragment(R.id.tv_picture,manager,pictrueFragment);
                break;
            case R.id.tv_computer:  //计算机页面
                startAndSettingFragment(R.id.tv_computer,manager,sdStorageFragment);
                break;
            case R.id.tv_storage:    //USB页面
                startAndSettingFragment(R.id.tv_storage,manager,usbStorageFragment);
                break;
            case R.id.tv_net_service:   //网上邻居页面
                startAndSettingFragment(R.id.tv_net_service,manager,onlineNeighborFragment);
                break;
            case R.id.iv_menu:  //选项菜单popwindow
                if (manager.getBackStackEntryCount() < 1) {
                    T.showShort(MainActivity.this, "当前页面不支持此操作！");
                } else {
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
                sendBroadcastMessage("iv_switch_view",null);
                break;
        }
    }

    //启动设置各个fragment页面
    private void startAndSettingFragment(int id, FragmentManager manager, Fragment fragment) {
        //设置选中文本背景
        setSelectedBackground(id);
        //当回退到主界面时，再执行回退时清空回退栈结束当前页面
        manager.popBackStack();
        curFragment = fragment;
        //替换当前fragment并加入回退栈
        manager.beginTransaction().replace(R.id.fl_mian, fragment).commit();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()){
            sendBroadcastMessage("isTouchEvent",null);
        }
        return super.onTouchEvent(event);
    }

    //设置选中文本背景（左侧侧边栏）
    private void setSelectedBackground(int id) {
        switch (id) {
            //默认选择computer页面
            case R.id.tv_computer:
                tv_music.setSelected(false);
                tv_desk.setSelected(false);
                tv_video.setSelected(false);
                tv_computer.setSelected(true);
                tv_picture.setSelected(false);
                tv_storage.setSelected(false);
                tv_net_service.setSelected(false);
                break;
            case R.id.tv_desk:
                tv_desk.setSelected(true);
                tv_music.setSelected(false);
                tv_video.setSelected(false);
                tv_computer.setSelected(false);
                tv_picture.setSelected(false);
                tv_storage.setSelected(false);
                tv_net_service.setSelected(false);
                break;
            case R.id.tv_music:
                tv_music.setSelected(true);
                tv_desk.setSelected(false);
                tv_video.setSelected(false);
                tv_computer.setSelected(false);
                tv_picture.setSelected(false);
                tv_storage.setSelected(false);
                tv_net_service.setSelected(false);
                break;
            case R.id.tv_video:
                tv_music.setSelected(false);
                tv_desk.setSelected(false);
                tv_video.setSelected(true);
                tv_computer.setSelected(false);
                tv_picture.setSelected(false);
                tv_storage.setSelected(false);
                tv_net_service.setSelected(false);
                break;
            case R.id.tv_picture:
                tv_music.setSelected(false);
                tv_desk.setSelected(false);
                tv_video.setSelected(false);
                tv_computer.setSelected(false);
                tv_picture.setSelected(true);
                tv_storage.setSelected(false);
                tv_net_service.setSelected(false);
                break;
            case R.id.tv_storage:
                tv_music.setSelected(false);
                tv_desk.setSelected(false);
                tv_video.setSelected(false);
                tv_computer.setSelected(false);
                tv_picture.setSelected(false);
                tv_storage.setSelected(true);
                tv_net_service.setSelected(false);
                break;
            case R.id.tv_net_service:
                tv_music.setSelected(false);
                tv_desk.setSelected(false);
                tv_video.setSelected(false);
                tv_computer.setSelected(false);
                tv_picture.setSelected(false);
                tv_storage.setSelected(false);
                tv_net_service.setSelected(true);
                break;
            default:
                break;
        }
    }

    /**
     * 发送广播
     *
     * @param name 按钮点击的标识
     */
    private void sendBroadcastMessage(String name,String tag) {
        Intent intent = new Intent();
        switch (name) {
            case "iv_switch_view":
                intent.setAction("com.switchview");
                break;
            case "iv_fresh":
                intent.setAction("com.refreshview");
                break;
            case "isTouchEvent":
                intent.setAction("com.isTouchEvent");
                break;
            case "iv_menu":
                intent.setAction("com.switchmenu");
                intent.putExtra("pop_menu", tag);
                break;
        }
        sendBroadcast(intent);
    }

    //顶部切换视图按钮
    private void switchListOrgrid() {
        if (!"list".equals(LocalCache.getViewTag())) {
            LocalCache.setViewTag("list");
            iv_switch_view.setSelected(true);
            T.showShort(MainActivity.this, "已切换为列表视图！");
        } else if (!"grid".equals(LocalCache.getViewTag())) {
            LocalCache.setViewTag("grid");
            iv_switch_view.setSelected(false);
            T.showShort(MainActivity.this, "已切换为网格视图！");
        }
    }

    /**
     * 选项菜单popwindow
     *
     * @param menu_tag 根据tag选择按钮popwindow
     */
    private void shownPopWidndow(String menu_tag) {
        popWinShare = null;
        //自定义的单击事件
        PopOnClickLintener paramOnClickListener = new PopOnClickLintener(menu_tag, MainActivity.this, manager);
        if (menu_tag.equals("iv_menu")) {
            popWinShare = new PopWinShare(MainActivity.this, paramOnClickListener,
                    DisplayUtil.dip2px(MainActivity.this, 125), DisplayUtil.dip2px(MainActivity.this, 260), menu_tag);
            //设置默认获取焦点
            popWinShare.setFocusable(true);
            //以某个控件的x和y的偏移量位置开始显示窗口
            popWinShare.showAsDropDown(this.iv_menu, -60, 10);

        } else if (menu_tag.equals("iv_setting")) {
            popWinShare = new PopWinShare(MainActivity.this, paramOnClickListener,
                    DisplayUtil.dip2px(MainActivity.this, 120), DisplayUtil.dip2px(MainActivity.this, 160), menu_tag);
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

    //移除poupwindow
    public void DismissPopwindow(){
        popWinShare.dismiss();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
         // 解绑注册
        this.unregisterReceiver(receiver);
    }

}


