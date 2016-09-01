package com.emindsoft.openthos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.emindsoft.openthos.component.PopOnClickLintener;
import com.emindsoft.openthos.component.PopWinShare;
import com.emindsoft.openthos.component.SearchOnClickListener;
import com.emindsoft.openthos.component.SearchOnEditorActionListener;
import com.emindsoft.openthos.fragment.DeskFragment;
import com.emindsoft.openthos.fragment.MusicFragment;
import com.emindsoft.openthos.fragment.OnlineNeighborFragment;
import com.emindsoft.openthos.fragment.PictrueFragment;
import com.emindsoft.openthos.fragment.SdStorageFragment;
import com.emindsoft.openthos.fragment.VideoFragment;
import com.emindsoft.openthos.system.Util;
import com.emindsoft.openthos.utils.DisplayUtil;
import com.emindsoft.openthos.utils.L;
import com.emindsoft.openthos.utils.LocalCache;
import com.emindsoft.openthos.utils.T;
import com.emindsoft.openthos.view.SystemSpaceFragment;

/**
 * 主界面点击左侧边栏contentFragment进行切换
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_desk;
    private TextView tv_music;
    private TextView tv_video;
    private TextView tv_computer;
    private TextView tv_picture;
    private TextView tv_storage;
    private TextView tv_net_service;
    private ImageView iv_list_view;
    private ImageView iv_grid_view;
    private ImageView iv_back;
    private ImageView iv_setting;
    private EditText et_nivagation;
    private EditText et_search_view;
    private ImageView iv_search_view;

    private static final String USB_SPACE_FRAGMENT = "usb_space_fragment";
    //usb设备链接标识
    private static final String USB_DEVICE_ATTACHED = "usb_device_attached";
    private static final String USB_DEVICE_DETACHED = "usb_device_detached";
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
    private OnlineNeighborFragment onlineNeighborFragment;
    private UsbConnectReceiver receiver;
    private String[] usbs;
    private boolean mIsMutiSelect;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case UsbConnectReceiver.USB_STATE_ON:
                        initUsb(UsbConnectReceiver.USB_STATE_ON);
                        break;
                    case UsbConnectReceiver.USB_STATE_OFF:
                        initUsb(UsbConnectReceiver.USB_STATE_OFF);
                        break;
                    case 2:
                        initUsb(0);
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };
    private boolean isFirst = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化时默认传入grid视图,实例化不能去掉,否则会报空指针异常
        LocalCache.getInstance(MainActivity.this).setViewTag("grid");
        //初始化控件
        initView();
        //初始化fragment
        initFragemnt();
        //初始化数据设置点击监听
        initData();
        //判断usb链接
        initUsb(-1);
        curFragment = sdStorageFragment;
    }

    private void initView() {
        tv_desk = (TextView) findViewById(R.id.tv_desk);
        tv_music = (TextView) findViewById(R.id.tv_music);
        tv_video = (TextView) findViewById(R.id.tv_video);
        tv_computer = (TextView) findViewById(R.id.tv_computer);
        tv_picture = (TextView) findViewById(R.id.tv_picture);
        tv_storage = (TextView) findViewById(R.id.tv_storage);
        tv_net_service = (TextView) findViewById(R.id.tv_net_service);
        iv_list_view = (ImageView) findViewById(R.id.iv_list_view);
        iv_grid_view = (ImageView) findViewById(R.id.iv_grid_view);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_setting = (ImageView) findViewById(R.id.iv_setting);
        et_nivagation = (EditText) findViewById(R.id.et_nivagation);
        iv_search_view = (ImageView) findViewById(R.id.iv_search);
        iv_grid_view.setSelected(true);
    }

    private void initUsb(int flags) {
        String[] cmd = new String[]{"df"};
        usbs = Util.execUsb(cmd);
        //用于判断程序未启动时,usb已连接的情况
        if (usbs != null && usbs.length > 0 && flags != 0 && flags != 1) {
            sendMsg(2);
        }
        if (flags == UsbConnectReceiver.USB_STATE_ON || flags == 2) {
            T.showShort(MainActivity.this, "USB设备已连接");
            tv_storage.setVisibility(View.VISIBLE);
            tv_storage.setOnClickListener(MainActivity.this);  //内存存储
            sdStorageFragment = new SdStorageFragment(manager, USB_DEVICE_ATTACHED, MainActivity.this);
            setSelectedBackground(R.id.tv_computer);
            manager.beginTransaction().replace(R.id.fl_mian, sdStorageFragment).commit();
        } else if (flags == UsbConnectReceiver.USB_STATE_OFF) {
            tv_storage.setVisibility(View.GONE);
            tv_storage.setVisibility(View.GONE);
            sdStorageFragment = new SdStorageFragment(manager, USB_DEVICE_DETACHED, MainActivity.this);
            setSelectedBackground(R.id.tv_computer);
            manager.beginTransaction().replace(R.id.fl_mian, sdStorageFragment).commit();
        }
    }

    //初始化fragment
    private void initFragemnt() {
        receiver = new UsbConnectReceiver(this);

        if (sdStorageFragment == null) {
            sdStorageFragment = new SdStorageFragment(manager, null, MainActivity.this);
        }
        if (deskFragment == null) {
            deskFragment = new DeskFragment();
        }
        if (musicFragment == null) {
            musicFragment = new MusicFragment();
        }
        if (videoFragment == null) {
            videoFragment = new VideoFragment();
        }
        if (pictrueFragment == null) {
            pictrueFragment = new PictrueFragment(manager);
        }
        if (onlineNeighborFragment == null) {
            onlineNeighborFragment = new OnlineNeighborFragment();
        }

    }

    //设置左侧侧边栏的点击监听
    private void initData() {
        et_search_view = (EditText) findViewById(R.id.search_view);
        tv_desk.setOnClickListener(this);  //桌面
        tv_music.setOnClickListener(this);  //音乐
        tv_video.setOnClickListener(this);  //视频
        tv_computer.setOnClickListener(this);  //计算机
        tv_picture.setOnClickListener(this);  //图片
        tv_net_service.setOnClickListener(this);  //网上邻居
        iv_list_view.setOnClickListener(this);  //选项菜单的点击监听
        iv_grid_view.setOnClickListener(this);  //切换视图list或者grid
        iv_back.setOnClickListener(this);  //回退监听
        iv_setting.setOnClickListener(this);  //设置中心

        //默认选中的第一个页面
        tv_computer.performClick();
        // 设置搜索监听
//        search_view.addTextChangedListener(new EditTextChangeListener(manager,MainActivity.this));
        et_search_view.setOnEditorActionListener(new SearchOnEditorActionListener(manager, et_search_view.getText(), MainActivity.this));
        iv_search_view.setOnClickListener(new SearchOnClickListener(manager, et_search_view.getText(), MainActivity.this));  //搜索点击监听

    }

    @Override
    protected void onStart() {
        receiver.registerReceiver();
        super.onStart();
    }

    /**
     * usb 广播接收器
     */
    public class UsbConnectReceiver extends BroadcastReceiver {
        private static final String TAG = "UsbConnectReceiver";
        MainActivity execactivity;

        public static final int USB_STATE_ON = 0;
        public static final int USB_STATE_OFF = 1;
        public IntentFilter filter = new IntentFilter();

        public UsbConnectReceiver(Context context) {
            execactivity = (MainActivity) context;
            filter.addAction(Intent.ACTION_MEDIA_CHECKING);
            filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            filter.addAction(Intent.ACTION_MEDIA_EJECT);
            filter.addAction(Intent.ACTION_MEDIA_REMOVED);
            filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);

            filter.addDataScheme("file");
        }

        public Intent registerReceiver() {
            return execactivity.registerReceiver(this, this.filter);
        }

        public void unregisterReceiver() {
            execactivity.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED) ||
                    intent.getAction().equals(Intent.ACTION_MEDIA_CHECKING)) {
                sendMsg(USB_STATE_ON);
            } else if (action.equals(Intent.ACTION_MEDIA_REMOVED) ||
                    action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                sendMsg(USB_STATE_OFF);
            }
        }
    }

    private void sendMsg(int flags) {
        Message msg = new Message();
        msg.what = flags;
        handler.sendMessage(msg);

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        mIsMutiSelect = false;
        if (!mIsMutiSelect && !isFirst){
            sendBroadcastMessage("is_ctrl_press", null, mIsMutiSelect);
            isFirst = true;
        }
        return false;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断backspace是回退还是删除操作
        if (keyCode == KeyEvent.KEYCODE_DEL && !et_search_view.hasFocus()) {
            onBackPressed();
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            L.e("KEYCODE_ENTER", "KEYCODE_ENTER");
            //TODO
        }
        if (event.isCtrlPressed()) {
            mIsMutiSelect = true;
        }
        if (mIsMutiSelect && isFirst) {
            sendBroadcastMessage("is_ctrl_press", null, mIsMutiSelect);
            isFirst = false;
        }
        if (event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_X) {
            sendBroadcastMessage("iv_menu", "pop_cut", false);
        }
        if (event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_C) {
            sendBroadcastMessage("iv_menu", "pop_copy", false);
        }
        if (event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_V) {
            sendBroadcastMessage("iv_menu", "pop_paste", false);
        }
        if (event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_Z) {
            sendBroadcastMessage("iv_menu", "pop_cacel", false);
        }
        if (event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_D) {
            sendBroadcastMessage("iv_menu", "pop_delete", false);
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_desk:   //桌面页面
                startAndSettingFragment(R.id.tv_desk, manager, deskFragment);
                break;
            case R.id.tv_music:  //音乐页面
                startAndSettingFragment(R.id.tv_music, manager, musicFragment);
                break;
            case R.id.tv_video:  //视频页面
                startAndSettingFragment(R.id.tv_video, manager, videoFragment);
                break;
            case R.id.tv_picture:   //图片页面
                startAndSettingFragment(R.id.tv_picture, manager, pictrueFragment);
                break;
            case R.id.tv_computer:  //计算机页面
                startAndSettingFragment(R.id.tv_computer, manager, sdStorageFragment);
                break;
            case R.id.tv_storage:    //USB页面
                setSelectedBackground(R.id.tv_storage);
                SystemSpaceFragment usbStorageFragment = new SystemSpaceFragment(USB_SPACE_FRAGMENT, usbs[0], null, null);
                manager.beginTransaction().replace(R.id.fl_mian, usbStorageFragment).commit();
                break;
            case R.id.tv_net_service:   //网上邻居页面
                startAndSettingFragment(R.id.tv_net_service, manager, onlineNeighborFragment);
                break;
//            case R.id.iv_menu:  //选项菜单popwindow
//                if (manager.getBackStackEntryCount() < 1) {
//                    T.showShort(MainActivity.this, "当前页面不支持此操作！");
//                } else {
//                    shownPopWidndow("iv_menu");
//                }
//                break;
            case R.id.iv_back:   //返回
                onBackPressed();
                break;
            case R.id.iv_setting:   //设置
                shownPopWidndow("iv_setting");
                break;
            case R.id.iv_grid_view:  //切换视图gridview
                //顶部切换视图按钮
                iv_grid_view.setSelected(true);
                iv_list_view.setSelected(false);
                if (!"grid".equals(LocalCache.getViewTag()) || "grid".equals(LocalCache.getViewTag())) {
                    LocalCache.setViewTag("grid");
                    T.showShort(MainActivity.this, "网格视图！");
                }
                //发送广播通知视图切换
                sendBroadcastMessage("iv_switch_view", "grid", false);
                break;
            case R.id.iv_list_view:  //切换视图listview
                //顶部切换视图按钮
                iv_grid_view.setSelected(false);
                iv_list_view.setSelected(true);
                if (!"list".equals(LocalCache.getViewTag()) || "list".equals(LocalCache.getViewTag())) {
                    LocalCache.setViewTag("list");
                    T.showShort(MainActivity.this, "列表视图！");
                }
                //发送广播通知视图切换
                sendBroadcastMessage("iv_switch_view", "list", false);
                break;
        }
    }

    //启动设置各个fragment页面
    private void startAndSettingFragment(int id, FragmentManager manager, Fragment fragment) {
        //设置选中文本背景
        setSelectedBackground(id);
        manager.beginTransaction().replace(R.id.fl_mian, fragment).commit();
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
    private void sendBroadcastMessage(String name, String tag, boolean isCtrl) {
        Intent intent = new Intent();
        switch (name) {
            case "iv_switch_view":
                intent.setAction("com.switchview");
                intent.putExtra("switch_view", tag);
                break;
            case "iv_fresh":
                intent.setAction("com.refreshview");
                break;
            case "iv_menu":
                intent.setAction("com.switchmenu");
                intent.putExtra("pop_menu", tag);
                break;
            case "is_ctrl_press":
                intent.setAction("com.isCtrlPress");
                intent.putExtra("is_ctrl_press", isCtrl);
                break;
        }
        sendBroadcast(intent);
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
//        if (menu_tag.equals("iv_menu")) {
//            popWinShare = new PopWinShare(MainActivity.this, paramOnClickListener,
//                    DisplayUtil.dip2px(MainActivity.this, 125), DisplayUtil.dip2px(MainActivity.this, 260), menu_tag);
//            //设置默认获取焦点
//            popWinShare.setFocusable(true);
//            //以某个控件的x和y的偏移量位置开始显示窗口
//            popWinShare.showAsDropDown(this.iv_menu, -60, 10);
//
//        } else
        if (menu_tag.equals("iv_setting")) {
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
    public void DismissPopwindow() {
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
            et_nivagation.setText("");
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
        receiver.unregisterReceiver();
    }

    @Override
    public void setNavigationBar(String displayPath) {
        if (displayPath != null) {
            et_nivagation.setText(displayPath);
        }
    }

}


