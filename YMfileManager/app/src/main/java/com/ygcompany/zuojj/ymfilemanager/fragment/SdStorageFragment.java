package com.ygcompany.zuojj.ymfilemanager.fragment;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ygcompany.zuojj.ymfilemanager.BaseFragment;
import com.ygcompany.zuojj.ymfilemanager.R;
import com.ygcompany.zuojj.ymfilemanager.system.Util;
import com.ygcompany.zuojj.ymfilemanager.utils.T;
import com.ygcompany.zuojj.ymfilemanager.view.PersonalSpaceFragment;
import com.ygcompany.zuojj.ymfilemanager.view.SystemSpaceFragment;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * SD卡存储页面
 * Created by zuojj on 16-5-18.
 */
public class SdStorageFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = SdStorageFragment.class.getSimpleName();
    //当前fragment标识
    private static final String SYSTEM_SPACE_FRAGMENT = "system_space_fragment";
    private static final String SD_SPACE_FRAGMENT = "sd_space_fragment";

    // 启动系统空间的标识
    private static final String SYSTEM_SPACE_FRAGMENT_TAG = "System_Space_Fragment_tag";
    // 启动个人中心的标识
    private static final String PERSONAL_SPACE_FRAGMENT_TAG = "Personal_Space_Fragment_tag";

    //初始化控件
    @Bind(R.id.rl_android_system)
    RelativeLayout rl_android_system;
    @Bind(R.id.rl_sd_space)
    RelativeLayout rl_sd_space;
    @Bind(R.id.rl_android_service)
    RelativeLayout rl_android_service;
    @Bind(R.id.rl_mount_space)
    RelativeLayout rl_mount_space;
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

    private View view;

    private String sdStorageFragment;
    FragmentManager manager = getFragmentManager();
    public SdStorageFragment(FragmentManager manager, String sdStorageFragment) {
        this.manager = manager;
        this.sdStorageFragment = sdStorageFragment;
    }

    //定时器功能
    private final Timer timer = new Timer(true);
    private TimerTask task;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            //要做的事情，比如更新UI
            switch (msg.what) {
                case 1:
//                    T.showShort(getContext(),"更新UI");
                    if (null != rl_mount_space){
                        rl_mount_space.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    break;
            }
        }

    };

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
        //设置各个盘符的容量信息
        setVolumSize();
        return view;
    }


    private void setVolumSize() {
        //设置system ROM信息
        Util.SystemInfo systemInfo = Util.getRomMemory();
        if (null != systemInfo){
            tv_system_total.setText(Util.convertStorage(systemInfo.romMemory));
            tv_system_avail.setText(Util.convertStorage(systemInfo.avilMemory));
            pb_system.setMax((int) systemInfo.romMemory);
            pb_system.setProgress((int) (systemInfo.romMemory - systemInfo.avilMemory));
        }
        //设置sd卡用量信息
        Util.SDCardInfo sdCardInfo = Util.getSDCardInfo();
        if (null != sdCardInfo) {
            tv_sd_total.setText(Util.convertStorage(sdCardInfo.total));
            tv_sd_avail.setText(Util.convertStorage(sdCardInfo.free));
            pb_sd.setMax((int) sdCardInfo.total);
            pb_sd.setProgress((int) (sdCardInfo.total - sdCardInfo.free));
        }
    }

    private void initView() {
        rl_android_system.setOnClickListener(this);
        rl_sd_space.setOnClickListener(this);
        rl_android_service.setOnClickListener(this);
        rl_mount_space.setOnClickListener(this);

        //android作为host时,usbManager获取是否有usb连接的管理器
        UsbManager usbManager = (UsbManager) getContext().getSystemService(Context.USB_SERVICE);
        //获取设备列表返回HashMap
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        if (deviceList.size() > 0){
            task = new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Message message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            };
            timer.schedule(task,1000, 3000); //延时1000ms后执行，1000ms执行一次
            //timer.cancel(); //退出计时器
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_android_system:
                //开启事务替换当前fragment
                manager.beginTransaction().replace(R.id.fl_mian, new SystemSpaceFragment(SYSTEM_SPACE_FRAGMENT), SYSTEM_SPACE_FRAGMENT_TAG)
                        .addToBackStack(null).commit();
                break;
            case R.id.rl_sd_space:
                T.showShort(getContext(), "磁盘空间");
                manager.beginTransaction().replace(R.id.fl_mian, new SystemSpaceFragment(SD_SPACE_FRAGMENT), SYSTEM_SPACE_FRAGMENT_TAG)
                        .addToBackStack(null).commit();
                break;
            case R.id.rl_android_service:
                T.showShort(getContext(), "云盘");
                manager.beginTransaction().replace(R.id.fl_mian, new PersonalSpaceFragment(1), SYSTEM_SPACE_FRAGMENT_TAG)
                        .addToBackStack(null).commit();
                break;
            case R.id.rl_mount_space:
                T.showShort(getContext(), "可移动磁盘");
                manager.beginTransaction().replace(R.id.fl_mian, new PersonalSpaceFragment(2), SYSTEM_SPACE_FRAGMENT_TAG)
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

//    /**
//     * 当前是否可以发生回退操作
//     * @return
//     */
//    public boolean canGoBack() {
//        boolean canGoBack = false;
//        Fragment baseFragment = manager.getFragment(null,SYSTEM_SPACE_FRAGMENT_TAG);
//        if(baseFragment instanceof  PersonalSpaceFragment)
//        {
//            PersonalSpaceFragment personalSpaceFragment = (PersonalSpaceFragment) baseFragment;
//            canGoBack = personalSpaceFragment.canGoBack();
//        }
//        else if (baseFragment instanceof  SystemSpaceFragment)
//        {
//            SystemSpaceFragment systemSpaceFragment = (SystemSpaceFragment) baseFragment;
//            canGoBack = systemSpaceFragment.canGoBack();
//        }
//        return canGoBack;
//    }

//    /**
//     * 执行回退操作
//     */
//    public void goBack() {
//        Fragment baseFragment = manager.getFragment(null,SYSTEM_SPACE_FRAGMENT_TAG);
//        if(baseFragment instanceof  PersonalSpaceFragment)
//        {
//            PersonalSpaceFragment personalSpaceFragment = (PersonalSpaceFragment) baseFragment;
//            personalSpaceFragment.goBack();
//        }
//        else if (baseFragment instanceof  SystemSpaceFragment)
//        {
//            SystemSpaceFragment systemSpaceFragment = (SystemSpaceFragment) baseFragment;
//            systemSpaceFragment.goBack();
//        }
//    }

}
