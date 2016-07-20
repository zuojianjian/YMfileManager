package cn.com.emindsoft.filemanager.fragment;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.com.emindsoft.filemanager.R;
import cn.com.emindsoft.filemanager.BaseFragment;
import cn.com.emindsoft.filemanager.system.FileInfo;
import cn.com.emindsoft.filemanager.system.FileViewInteractionHub;
import cn.com.emindsoft.filemanager.system.Util;
import cn.com.emindsoft.filemanager.utils.L;
import cn.com.emindsoft.filemanager.utils.LocalCache;
import cn.com.emindsoft.filemanager.utils.T;
import cn.com.emindsoft.filemanager.view.SystemSpaceFragment;

/**
 * SD卡存储页面
 * Created by zuojj on 16-5-18.
 */
public class SdStorageFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = SdStorageFragment.class.getSimpleName();
    //usb是否连接的标识
    private String usbDeviceIsAttached;

    //传入子页面字段标识（用于设置底部目录）
    private static final String SYSTEM_SPACE_FRAGMENT = "system_space_fragment";
    private static final String SD_SPACE_FRAGMENT = "sd_space_fragment";
    private static final String USB_SPACE_FRAGMENT = "usb_space_fragment";
    private static final String YUN_SPACE_FRAGMENT = "yun_space_fragment";

    // 启动各个fragment的标识
    private static final String SYSTEM_SPACE_FRAGMENT_TAG = "System_Space_Fragment_tag";
//    private static final String PERSONAL_SPACE_FRAGMENT_TAG = "Personal_Space_Fragment_tag";
//    private static final String USB_SPACE_FRAGMENT_TAG = "usb_space_fragment_tag";
//    private static final String YUN_SPACE_FRAGMENT_TAG = "yun_space_fragment_tag";

    //初始化控件
    @Bind(R.id.rl_android_system)
    RelativeLayout rl_android_system;
    @Bind(R.id.rl_sd_space)
    RelativeLayout rl_sd_space;
    @Bind(R.id.rl_android_service)
    RelativeLayout rl_android_service;
    @Bind(R.id.rl_mount_space_one)
    RelativeLayout rl_mount_space_one;
    @Bind(R.id.rl_mount_space_two)
    RelativeLayout rl_mount_space_two;
    //系统
    @Bind(R.id.tv_system_total)
    TextView tv_system_total;
    @Bind(R.id.tv_system_avail)
    TextView tv_system_avail;
    @Bind(R.id.pb_system)
    ProgressBar pb_system;
    //sd卡
    @Bind(R.id.tv_sd_total)
    TextView tv_sd_total;
    @Bind(R.id.tv_sd_avail)
    TextView tv_sd_avail;
    @Bind(R.id.pb_sd)
    ProgressBar pb_sd;
    //可移动磁盘
    @Bind(R.id.tv_usb_total)
    TextView tv_usb_total;
    @Bind(R.id.tv_usb_avail)
    TextView tv_usb_avail;
    @Bind(R.id.pb_usb)
    ProgressBar pb_usb;
    //云服务
    @Bind(R.id.pb_service)
    ProgressBar pb_service;

    // 当前的主界面
    private BaseFragment curFragment;
    //fragament管理器
    FragmentManager manager = getFragmentManager();
    //是否为第一次点击
    private boolean isFrist = true;
    //上一次点击位置
    private int prePosition;
    //上次按下返回键的系统时间
    private long lastBackTime = 0;
    //当前按下返回键的系统时间
    private long currentBackTime = 0;

    /**
     * 构造器
     * @param manager  fragment管理器
     * @param usbDeviceIsAttached  usb是否连接的标识
     */
    public SdStorageFragment(FragmentManager manager, String usbDeviceIsAttached) {
        this.manager = manager;
        this.usbDeviceIsAttached = usbDeviceIsAttached;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.android_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void setVolumSize() {
        //设置system ROM信息
        Util.SystemInfo systemInfo = Util.getRomMemory();
        if (null != systemInfo) {
            tv_system_total.setText(Util.convertStorage(systemInfo.romMemory));
            tv_system_avail.setText(Util.convertStorage(systemInfo.avilMemory));
            L.e("tv_system_total", Util.convertStorage(systemInfo.romMemory).substring(0, 3));
            L.e("tv_system_avail", Util.convertStorage(systemInfo.avilMemory).substring(0, 3));
//
            pb_system.setMax((int) Double.parseDouble(Util.convertStorage(systemInfo.romMemory).substring(0, 3)) * 10);
//            pb_system.setProgress(100);
            pb_system.setSecondaryProgress((int) (Double.parseDouble(Util.convertStorage(systemInfo.romMemory - systemInfo.avilMemory).substring(0, 3)) * 10));
        }
        //设置sd卡用量信息
        Util.SDCardInfo sdCardInfo = Util.getSDCardInfo();
        if (null != sdCardInfo) {
            tv_sd_total.setText(Util.convertStorage(sdCardInfo.total));
            tv_sd_avail.setText(Util.convertStorage(sdCardInfo.free));

            L.e("tv_sd_total", Util.convertStorage(sdCardInfo.total).substring(0, 3));
            L.e("tv_sd_avail", Util.convertStorage(sdCardInfo.free).substring(0, 3));

            pb_sd.setMax((int) Double.parseDouble(Util.convertStorage(sdCardInfo.total).substring(0, 3)) * 10);
            pb_sd.setProgress((int) (Double.parseDouble(Util.convertStorage(sdCardInfo.total - sdCardInfo.free).substring(0, 3)) * 10));
        }
//        //设置usb用量信息
//        Util.UsbMemoryInfo usbMemoryInfo = Util.getUsbMemoryInfo();
//        if (null != usbMemoryInfo) {
//            tv_usb_total.setText(Util.convertStorage(usbMemoryInfo.usbTotal));
//            tv_usb_avail.setText(Util.convertStorage(usbMemoryInfo.usbFree));
//            pb_usb.setMax((int) usbMemoryInfo.usbTotal);
//            pb_usb.setProgress((int) (usbMemoryInfo.usbTotal - usbMemoryInfo.usbFree));
//        }
    }

    private void initView() {
        //设置各个盘符的容量信息
        setVolumSize();
        rl_android_system.setOnClickListener(this);
        rl_sd_space.setOnClickListener(this);
        rl_android_service.setOnClickListener(this);

        UsbManager usbManager = (UsbManager) getContext().getSystemService(Context.USB_SERVICE);
        //获取设备列表返回HashMap
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        //设置移动磁盘的显示和隐藏
        if (null != usbDeviceIsAttached && usbDeviceIsAttached.equals("usb_device_attached") || deviceList.size() > 2) {
            rl_mount_space_one.setVisibility(View.VISIBLE);
            rl_mount_space_one.setOnClickListener(this);

        } else if (null != usbDeviceIsAttached && usbDeviceIsAttached.equals("usb_device_detached")) {
            rl_mount_space_one.setVisibility(View.GONE);
        }


//        //android作为host时,usbManager获取是否有usb连接的管理器
        //遍历usb设备列表
//        for (UsbDevice device : deviceList.values()) {
//                        DeviceInfo deviceInfo = new DeviceInfo();
//            //设备名称
//            String deviceName = device.getDeviceName();
//            String mountPath = device.getDeviceName();
//
//            L.e(TAG,deviceName+"====================="+mountPath+"++++++++++++++++"+deviceList.size());
//            //当前内存不为空时赋值路径
////            deviceInfo.deviceName = deviceName;
////            deviceInfo.devicePath = mountPath;
////            deviceInfos.add(deviceInfo);
//        }
//        if (deviceList.size() > 0) {
////            task = new TimerTask() {
////                @Override
////                public void run() {
////                    // TODO Auto-generated method stub
////                    Message message = new Message();
////                    message.what = 1;
////                    handler.sendMessage(message);
////                }
////            };
////            timer.schedule(task, 1000, 3000); //延时1000ms后执行，3000ms执行一次
//            rl_mount_space_one.setVisibility(View.VISIBLE);
//        }else {
//            rl_mount_space_one.setVisibility(View.GONE);
//        }
    }

    //computer页面各个盘符的点击事件集合
    @Override
    public void onClick(View view) {
        ArrayList<FileInfo> fileInfoArrayList = null;
        FileViewInteractionHub.CopyOrMove copyOrMove = null;
        //获取当前系统时间的毫秒数
        currentBackTime = System.currentTimeMillis();
        switch (view.getId()) {
            case R.id.rl_android_system:   //安卓系统
                //比较上次按下返回键和当前按下返回键的时间差，如果大于2秒，则提示再按一次退出
                if (currentBackTime - lastBackTime > 800) {
                    //设置选中背景
                    setSelectedCardBg(R.id.rl_android_system);
                    lastBackTime = currentBackTime;
                } else { //如果两次按下的时间差小于1秒，则退出程序
                    if (curFragment != null) {
                        fileInfoArrayList = ((SystemSpaceFragment) curFragment).getFileInfoList();
                        copyOrMove = ((SystemSpaceFragment) curFragment).getCurCopyOrMoveMode();
                    }
                    if (fileInfoArrayList != null && copyOrMove != null){
                        T.showShort(getContext(),"系统目录只有读取权限！当前操作失败～");
                    }
                    curFragment = new SystemSpaceFragment(SYSTEM_SPACE_FRAGMENT, null, fileInfoArrayList, copyOrMove);
                    //开启事务替换当前fragment
                    manager.beginTransaction().replace(R.id.fl_mian, curFragment, SYSTEM_SPACE_FRAGMENT_TAG)
                            .addToBackStack(null).commit();
                }
                break;
            case R.id.rl_sd_space:     //磁盘空间
                //比较上次按下返回键和当前按下返回键的时间差，如果大于2秒，则提示再按一次退出
                if (currentBackTime - lastBackTime > 800) {
                    //设置选中背景
                    setSelectedCardBg(R.id.rl_sd_space);
                    lastBackTime = currentBackTime;
                } else { //如果两次按下的时间差小于1秒，则进入
                    if (curFragment != null) {
                        fileInfoArrayList = ((SystemSpaceFragment) curFragment).getFileInfoList();
                        copyOrMove = ((SystemSpaceFragment) curFragment).getCurCopyOrMoveMode();
                    }
                    curFragment = new SystemSpaceFragment(SD_SPACE_FRAGMENT, null, fileInfoArrayList, copyOrMove);
                    manager.beginTransaction().replace(R.id.fl_mian, curFragment, SYSTEM_SPACE_FRAGMENT_TAG)
                            .addToBackStack(null).commit();
                }
                break;
            case R.id.rl_mount_space_one:   //移动磁盘
                //比较上次按下返回键和当前按下返回键的时间差，如果大于0.8秒，则选中
                if (currentBackTime - lastBackTime > 800) {
                    //设置选中背景
                    setSelectedCardBg(R.id.rl_mount_space_one);
                    lastBackTime = currentBackTime;
                } else { //如果两次按下的时间差小于1秒，则进入
                    if (curFragment != null) {
                        fileInfoArrayList = ((SystemSpaceFragment) curFragment).getFileInfoList();
                        copyOrMove = ((SystemSpaceFragment) curFragment).getCurCopyOrMoveMode();
                    }
                    curFragment = new SystemSpaceFragment(USB_SPACE_FRAGMENT, null, fileInfoArrayList, copyOrMove);
                    manager.beginTransaction().replace(R.id.fl_mian, curFragment, SYSTEM_SPACE_FRAGMENT_TAG)
                            .addToBackStack(null).commit();
                }
                break;
            case R.id.rl_android_service:
                //比较上次按下返回键和当前按下返回键的时间差，如果大于0.8秒，则选中
                if (currentBackTime - lastBackTime > 800) {
                    //设置选中背景
                    setSelectedCardBg(R.id.rl_android_service);
                    lastBackTime = currentBackTime;
                } else { //如果两次按下的时间差小于0.8秒，则退出程序
                    if (curFragment != null) {
                        fileInfoArrayList = ((SystemSpaceFragment) curFragment).getFileInfoList();
                        copyOrMove = ((SystemSpaceFragment) curFragment).getCurCopyOrMoveMode();
                    }
                    curFragment = new SystemSpaceFragment(YUN_SPACE_FRAGMENT, null, fileInfoArrayList, copyOrMove);
                    manager.beginTransaction().replace(R.id.fl_mian, curFragment, SYSTEM_SPACE_FRAGMENT_TAG)
                            .addToBackStack(null).commit();
                }
                break;
        }
    }

    //各个磁盘的背景选择
    private void setSelectedCardBg(int id) {
        switch (id){
            case R.id.rl_android_system:
                rl_android_system.setSelected(true);
                rl_sd_space.setSelected(false);
                rl_mount_space_one.setSelected(false);
                rl_android_service.setSelected(false);
                break;
            case R.id.rl_sd_space:
                rl_android_system.setSelected(false);
                rl_sd_space.setSelected(true);
                rl_mount_space_one.setSelected(false);
                rl_android_service.setSelected(false);
                break;
            case R.id.rl_mount_space_one:
                rl_android_system.setSelected(false);
                rl_sd_space.setSelected(false);
                rl_mount_space_one.setSelected(true);
                rl_android_service.setSelected(false);
                break;
            case R.id.rl_android_service:
                rl_android_system.setSelected(false);
                rl_sd_space.setSelected(false);
                rl_mount_space_one.setSelected(false);
                rl_android_service.setSelected(true);
                break;
        }
    }

    /**
     * 当前fragment销毁时解绑view
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalCache.setSearchText(null);
        ButterKnife.unbind(this);
    }

    /**
     * 当前是否可以发生回退操作
     *
     * @return
     */
    public boolean canGoBack() {
        boolean canGoBack = false;
        Fragment baseFragment = curFragment;
        if (baseFragment instanceof SystemSpaceFragment) {
            SystemSpaceFragment systemSpaceFragment = (SystemSpaceFragment) baseFragment;
            canGoBack = systemSpaceFragment.canGoBack();
        }
        return canGoBack;
    }

    /**
     * 执行回退操作
     */
    public void goBack() {
        Fragment baseFragment = curFragment;
        if (baseFragment instanceof SystemSpaceFragment) {
            SystemSpaceFragment systemSpaceFragment = (SystemSpaceFragment) baseFragment;
            systemSpaceFragment.goBack();
        }
    }
}
