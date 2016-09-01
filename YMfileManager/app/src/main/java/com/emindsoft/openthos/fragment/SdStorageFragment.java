package com.emindsoft.openthos.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.emindsoft.openthos.BaseFragment;
import com.emindsoft.openthos.MainActivity;
import com.emindsoft.openthos.R;
import com.emindsoft.openthos.system.FileInfo;
import com.emindsoft.openthos.system.FileViewInteractionHub;
import com.emindsoft.openthos.system.Util;
import com.emindsoft.openthos.utils.L;
import com.emindsoft.openthos.utils.LocalCache;
import com.emindsoft.openthos.utils.T;
import com.emindsoft.openthos.view.SystemSpaceFragment;

import java.io.File;
import java.util.ArrayList;

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
    ArrayList<FileInfo> fileInfoArrayList = null;
    FileViewInteractionHub.CopyOrMove copyOrMove = null;

    //初始化控件
    private RelativeLayout rl_android_system;
    private RelativeLayout rl_sd_space;
    private RelativeLayout rl_android_service;
    private RelativeLayout rl_mount_space_one;
    private RelativeLayout rl_mount_space_two;
    //系统
    private TextView tv_system_total;
    private TextView tv_system_avail;
    private ProgressBar pb_system;
    //sd卡
    private TextView tv_sd_total;
    private TextView tv_sd_avail;
    private ProgressBar pb_sd;
    //可移动磁盘
    private TextView tv_usb_total;
    private TextView tv_usb_avail;
    private ProgressBar pb_usb;
    //云服务
    private ProgressBar pb_service;

    // 当前的主界面
    private BaseFragment curFragment;
    //fragament管理器
    FragmentManager manager = getFragmentManager();
    //上次按下返回键的系统时间
    private long lastBackTime = 0;
    //usb数据或者sd卡的路径文件
    private ArrayList<File> mountUsb = null;
    private String mountPath;
    private long currentBackTime;
    private String mountDiskPath = null;
    private Context context;

    /**
     * 构造器
     *  @param manager             fragment管理器
     * @param usbDeviceIsAttached usb是否连接的标识
     * @param context
     */
    @SuppressLint({"NewApi", "ValidFragment"})
    public SdStorageFragment(FragmentManager manager, String usbDeviceIsAttached, MainActivity context) {
        this.manager = manager;
        this.usbDeviceIsAttached = usbDeviceIsAttached;
        this.context = context;
    }
    @SuppressLint({"NewApi", "ValidFragment"})
    public SdStorageFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.android_fragment_layout, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        rl_android_system = (RelativeLayout) view.findViewById(R.id.rl_android_system);
        rl_sd_space = (RelativeLayout) view.findViewById(R.id.rl_sd_space);
        rl_android_service = (RelativeLayout) view.findViewById(R.id.rl_android_service);
        rl_mount_space_one = (RelativeLayout) view.findViewById(R.id.rl_mount_space_one);
        rl_mount_space_two = (RelativeLayout) view.findViewById(R.id.rl_mount_space_two);

        tv_system_total = (TextView) view.findViewById(R.id.tv_system_total);
        tv_system_avail = (TextView) view.findViewById(R.id.tv_system_avail);
        tv_sd_total = (TextView) view.findViewById(R.id.tv_sd_total);
        tv_sd_avail = (TextView) view.findViewById(R.id.tv_sd_avail);
        tv_usb_total = (TextView) view.findViewById(R.id.tv_usb_total);
        tv_usb_avail = (TextView) view.findViewById(R.id.tv_usb_avail);

        pb_system = (ProgressBar) view.findViewById(R.id.pb_system);
        pb_sd = (ProgressBar) view.findViewById(R.id.pb_sd);
        pb_usb = (ProgressBar) view.findViewById(R.id.pb_usb);
        pb_service = (ProgressBar) view.findViewById(R.id.pb_service);
    }

    private void setVolumSize() {
        //设置system ROM信息
        Util.SystemInfo systemInfo = Util.getRomMemory();
        if (null != systemInfo) {
            tv_system_total.setText(Util.convertStorage(systemInfo.romMemory));
            tv_system_avail.setText(Util.convertStorage(systemInfo.avilMemory));
//            L.e("tv_system_total", Util.convertStorage(systemInfo.romMemory).substring(0, 3));
//            L.e("tv_system_avail", Util.convertStorage(systemInfo.avilMemory).substring(0, 3));
            pb_system.setMax((int) Double.parseDouble(Util.convertStorage(systemInfo.romMemory).substring(0, 3)) * 10);
            pb_system.setSecondaryProgress((int) (Double.parseDouble(Util.convertStorage(systemInfo.romMemory - systemInfo.avilMemory).substring(0, 3)) * 10));
        }

        //设置本地磁盘用量信息
        String[] cmd = {"df"};
        String[] usbs = Util.execDisk(cmd);
        if (usbs != null && usbs.length > 0) {
            showDiskInfo(usbs);
        } else {
            //设置sd卡（个人空间）信息
            showSdcardInfo();
        }
    }

    private void showDiskInfo(String[] usbs) {
        mountDiskPath = usbs[0];
        tv_sd_total.setText(usbs[1]);
        tv_sd_avail.setText(usbs[3]);
        int max = (int) Double.parseDouble(usbs[1].substring(0, 3)) * 10;
        int avail = (int) Double.parseDouble(usbs[3].substring(0, 2)) * 10;
        pb_sd.setMax(max);
        pb_sd.setProgress(max - avail);
    }

    private void showSdcardInfo() {
        Util.SDCardInfo sdCardInfo = Util.getSDCardInfo();
        if (null != sdCardInfo) {
            tv_sd_total.setText(Util.convertStorage(sdCardInfo.total));
            tv_sd_avail.setText(Util.convertStorage(sdCardInfo.free));

            L.e("tv_sd_total", Util.convertStorage(sdCardInfo.total).substring(0, 3));
            L.e("tv_sd_avail", Util.convertStorage(sdCardInfo.free).substring(0, 3));

            pb_sd.setMax((int) Double.parseDouble(Util.convertStorage(sdCardInfo.total).substring(0, 3)) * 10);
            pb_sd.setProgress((int) (Double.parseDouble(Util.convertStorage(sdCardInfo.total - sdCardInfo.free).substring(0, 3)) * 10));
        }
    }

    private void initData() {
        //设置各个盘符的容量信息
        setVolumSize();
        rl_android_system.setOnClickListener(this);
        rl_sd_space.setOnClickListener(this);
        rl_android_service.setOnClickListener(this);
        //显示外接设备磁盘

        if (usbDeviceIsAttached != null && usbDeviceIsAttached.equals("usb_device_attached")) {
            //设置usb用量信息
            String[] cmd = {"df"};
            String[] usbs = Util.execUsb(cmd);
            if (usbs != null && usbs.length > 0) {
                showMountDevices(usbs);
                //设置移动磁盘的显示和隐藏
                rl_mount_space_one.setVisibility(View.VISIBLE);
                rl_mount_space_one.setOnClickListener(this);
            }
        } else if (usbDeviceIsAttached != null && usbDeviceIsAttached.equals("usb_device_detached")) {
            rl_mount_space_one.setVisibility(View.GONE);
        }
    }

    //显示外接设备磁盘
    private void showMountDevices(String[] usbs) {
        mountPath = usbs[0];
        tv_usb_total.setText(usbs[1]);
        tv_usb_avail.setText(usbs[3]);
        int max = (int) Double.parseDouble(usbs[1].substring(0, 3)) * 10;
        int avail = (int) Double.parseDouble(usbs[3].substring(0, 3)) * 10;
        pb_usb.setMax(max);
        pb_usb.setProgress(max - avail);
    }


    //computer页面各个盘符的点击事件集合
    @Override
    public void onClick(View view) {
        //获取当前系统时间的毫秒数
        currentBackTime = System.currentTimeMillis();
        switch (view.getId()) {
            case R.id.rl_android_system:   //安卓系统
                //比较上次按下返回键和当前按下返回键的时间差，如果大于2秒，则提示再按一次退出
                setDiskClickInfo(R.id.rl_android_system, SYSTEM_SPACE_FRAGMENT, null);
                break;
            case R.id.rl_sd_space:     //磁盘空间
                setDiskClickInfo(R.id.rl_sd_space, SD_SPACE_FRAGMENT,mountDiskPath);
                break;
            case R.id.rl_mount_space_one:   //移动磁盘
                setDiskClickInfo(R.id.rl_mount_space_one, USB_SPACE_FRAGMENT, mountPath);
                break;
            case R.id.rl_android_service:  //云服务
                setDiskClickInfo(R.id.rl_android_service, YUN_SPACE_FRAGMENT, null);
                break;
            default:
                break;
        }
    }

    private void setDiskClickInfo(int id, String tag, String path) {
        if (currentBackTime - lastBackTime > 800) {
            //设置选中背景
            setSelectedCardBg(id);
            lastBackTime = currentBackTime;
        } else { //如果两次按下的时间差小于0.8秒，则进入
            if (curFragment != null) {
                fileInfoArrayList = ((SystemSpaceFragment) curFragment).getFileInfoList();
                copyOrMove = ((SystemSpaceFragment) curFragment).getCurCopyOrMoveMode();
            }
            if (fileInfoArrayList != null && copyOrMove != null) {
                T.showShort(context, "系统目录只有读取权限！当前操作失败～");
            }
            curFragment = new SystemSpaceFragment(tag, path, fileInfoArrayList, copyOrMove);
            manager.beginTransaction().replace(R.id.fl_mian, curFragment, SYSTEM_SPACE_FRAGMENT_TAG)
                    .addToBackStack(null).commit();
        }
    }

    /*各个磁盘的背景选择*/
    private void setSelectedCardBg(int id) {
        switch (id) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalCache.setSearchText(null);
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

   /*执行回退操作*/
    public void goBack() {
        Fragment baseFragment = curFragment;
        if (baseFragment instanceof SystemSpaceFragment) {
            SystemSpaceFragment systemSpaceFragment = (SystemSpaceFragment) baseFragment;
            systemSpaceFragment.goBack();
        }
    }

}
