package com.ygcompany.zuojj.ymfilemanager.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

import com.ygcompany.zuojj.ymfilemanager.BaseFragment;
import com.ygcompany.zuojj.ymfilemanager.R;
import com.ygcompany.zuojj.ymfilemanager.system.FileInfo;
import com.ygcompany.zuojj.ymfilemanager.system.FileViewInteractionHub;
import com.ygcompany.zuojj.ymfilemanager.system.Util;
import com.ygcompany.zuojj.ymfilemanager.utils.L;
import com.ygcompany.zuojj.ymfilemanager.utils.T;
import com.ygcompany.zuojj.ymfilemanager.view.SystemSpaceFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * SD卡存储页面
 * Created by zuojj on 16-5-18.
 */
public class SdStorageFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = SdStorageFragment.class.getSimpleName();
    //传入子页面字段标识（用于设置底部目录）
    private static final String SYSTEM_SPACE_FRAGMENT = "system_space_fragment";
    private static final String SD_SPACE_FRAGMENT = "sd_space_fragment";
    private static final String USB_SPACE_FRAGMENT = "usb_space_fragment";
    private static final String YUN_SPACE_FRAGMENT = "yun_space_fragment";

    // 启动各个fragment的标识
    private static final String SYSTEM_SPACE_FRAGMENT_TAG = "System_Space_Fragment_tag";
    private static final String PERSONAL_SPACE_FRAGMENT_TAG = "Personal_Space_Fragment_tag";
    private static final String USB_SPACE_FRAGMENT_TAG = "usb_space_fragment_tag";
    private static final String YUN_SPACE_FRAGMENT_TAG = "yun_space_fragment_tag";

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
    //usb存储
    @Bind(R.id.tv_usb_total)
    TextView tv_usb_total;
    @Bind(R.id.tv_usb_avail)
    TextView tv_usb_avail;
    @Bind(R.id.pb_usb)
    ProgressBar pb_usb;

    private View view;
    // 当前的主界面
    private BaseFragment curFragment;
    //sdStorageFragment页面标识
    private String sdStorageFragment;
    //fragament管理器
    FragmentManager manager = getFragmentManager();
    //设备列表集合
//    ArrayList<DeviceInfo> deviceInfos = new ArrayList<>();
    private String mountPath;

    public SdStorageFragment(FragmentManager manager, String sdStorageFragment) {
        this.manager = manager;
        this.sdStorageFragment = sdStorageFragment;
    }

//    //定时器功能
//    private final Timer timer = new Timer(true);
//    private TimerTask task;
//    public Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            // TODO Auto-generated method stub
//            //要做的事情，比如更新UI
//            switch (msg.what) {
//                case 1:
////                    T.showShort(getContext(),"更新UI");
//                    if (null != rl_mount_space_one) {
//                        rl_mount_space_one.setVisibility(View.VISIBLE);
//                        timer.cancel(); //退出计时器
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
//
//    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.android_fragment_layout, container, false);
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
//            L.e("tv_system_total",Util.convertStorage(systemInfo.romMemory).substring(0,3));
//            L.e("tv_system_avail",Util.convertStorage(systemInfo.avilMemory).substring(0,3));
//
//            pb_system.setMax((int) Double.parseDouble(Util.convertStorage(systemInfo.romMemory).substring(0,3))*10);
//            pb_system.setProgress(100);
//            pb_system.setSecondaryProgress((int) (Double.parseDouble(Util.convertStorage(systemInfo.romMemory-systemInfo.avilMemory).substring(0,3))*10));
        }
        //设置sd卡用量信息
        Util.SDCardInfo sdCardInfo = Util.getSDCardInfo();
        if (null != sdCardInfo) {
            tv_sd_total.setText(Util.convertStorage(sdCardInfo.total));
            tv_sd_avail.setText(Util.convertStorage(sdCardInfo.free));

            L.e("tv_sd_total",Util.convertStorage(sdCardInfo.total).substring(0,3));
            L.e("tv_sd_avail",Util.convertStorage(sdCardInfo.free).substring(0,3));

//            pb_sd.setMax((int) Double.parseDouble(Util.convertStorage(sdCardInfo.total).substring(0,3))*10);
//            pb_sd.setProgress((int) (Double.parseDouble(Util.convertStorage(sdCardInfo.total-sdCardInfo.free).substring(0,3))*10));
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
        rl_mount_space_one.setOnClickListener(this);

        //android作为host时,usbManager获取是否有usb连接的管理器
        UsbManager usbManager = (UsbManager) getContext().getSystemService(Context.USB_SERVICE);
        //获取设备列表返回HashMap
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        //遍历usb设备列表
        mountPath = "/storage/usb2";
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
//            DeviceInfo deviceInfo = new DeviceInfo();
            //设备名称
            String deviceName = device.getDeviceName();
            //当前内存不为空时赋值路径
//            deviceInfo.deviceName = deviceName;
//            deviceInfo.devicePath = mountPath;
//            deviceInfos.add(deviceInfo);
        }
        if (deviceList.size() > 0) {
//            task = new TimerTask() {
//                @Override
//                public void run() {
//                    // TODO Auto-generated method stub
//                    Message message = new Message();
//                    message.what = 1;
//                    handler.sendMessage(message);
//                }
//            };
//            timer.schedule(task, 1000, 3000); //延时1000ms后执行，3000ms执行一次
            rl_mount_space_one.setVisibility(View.VISIBLE);
        }else {
            rl_mount_space_one.setVisibility(View.GONE);
        }
    }

    class UsbReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            T.showLong(getContext(),"接收静态广播成功！！！！");
        }
    }

    @Override
    public void onClick(View view) {
        ArrayList<FileInfo> fileInfoArrayList = null;
        FileViewInteractionHub.CopyOrMove copyOrMove = null;
        switch (view.getId()) {
            case R.id.rl_android_system:   //安卓系统
                if (curFragment != null) {
                    fileInfoArrayList = ((SystemSpaceFragment) curFragment).getFileInfoList();
                    copyOrMove = ((SystemSpaceFragment) curFragment).getCurCopyOrMoveMode();
                }
                curFragment = new SystemSpaceFragment(SYSTEM_SPACE_FRAGMENT, null, fileInfoArrayList, copyOrMove);
                //开启事务替换当前fragment
                manager.beginTransaction().replace(R.id.fl_mian, curFragment, SYSTEM_SPACE_FRAGMENT_TAG)
                        .addToBackStack(null).commit();
                break;
            case R.id.rl_sd_space:     //磁盘空间
                if (curFragment != null) {
                    fileInfoArrayList = ((SystemSpaceFragment) curFragment).getFileInfoList();
                    copyOrMove = ((SystemSpaceFragment) curFragment).getCurCopyOrMoveMode();
                }
                curFragment = new SystemSpaceFragment(SD_SPACE_FRAGMENT,null, fileInfoArrayList, copyOrMove);
                T.showShort(getContext(), "磁盘空间");
                manager.beginTransaction().replace(R.id.fl_mian, curFragment, SYSTEM_SPACE_FRAGMENT_TAG)
                        .addToBackStack(null).commit();
                break;
            case R.id.rl_mount_space_one:   //移动磁盘
                T.showShort(getContext(), "可移动磁盘");
                if (curFragment != null) {
                    fileInfoArrayList = ((SystemSpaceFragment) curFragment).getFileInfoList();
                    copyOrMove = ((SystemSpaceFragment) curFragment).getCurCopyOrMoveMode();
                }
                curFragment = new SystemSpaceFragment(USB_SPACE_FRAGMENT, null, fileInfoArrayList, copyOrMove);
                manager.beginTransaction().replace(R.id.fl_mian, curFragment, SYSTEM_SPACE_FRAGMENT_TAG)
                        .addToBackStack(null).commit();
                break;
            case R.id.rl_android_service:
                T.showLong(getContext(), "敬请期待...");
                if (curFragment != null) {
                    fileInfoArrayList = ((SystemSpaceFragment) curFragment).getFileInfoList();
                    copyOrMove = ((SystemSpaceFragment) curFragment).getCurCopyOrMoveMode();
                }
                curFragment = new SystemSpaceFragment(YUN_SPACE_FRAGMENT,null , fileInfoArrayList, copyOrMove);
                manager.beginTransaction().replace(R.id.fl_mian, curFragment, SYSTEM_SPACE_FRAGMENT_TAG)
                        .addToBackStack(null).commit();
                break;
        }
    }

    /**
     * 当前fragment销毁时解绑view
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
