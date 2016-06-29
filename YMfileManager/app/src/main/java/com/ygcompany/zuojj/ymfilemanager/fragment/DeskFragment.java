package com.ygcompany.zuojj.ymfilemanager.fragment;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ygcompany.zuojj.ymfilemanager.BaseFragment;
import com.ygcompany.zuojj.ymfilemanager.R;
import com.ygcompany.zuojj.ymfilemanager.adapter.DeskAdapter;
import com.ygcompany.zuojj.ymfilemanager.bean.AppInfo;
import com.ygcompany.zuojj.ymfilemanager.utils.L;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 桌面应用图标页面
 * Created by zuojj on 16-5-18.
 */
public class DeskFragment extends BaseFragment {
    //用来存储获取的应用信息数据
    private ArrayList<AppInfo> appInfos = new ArrayList<>();
    //应用包名
    private String packageName;
    private DeskAdapter deskAdapter;
    @Bind(R.id.gv_desk_icon)
    GridView gv_desk_icon;
    private PackageManager pm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.desk_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    //初始化数据
    private void initData() {
        //防止数据的重复加载每次加载先清空集合
        if (appInfos != null){
            appInfos.clear();
        }
        //获取应用的包信息并加入appInfos中去
        getInstallPackageInfo();
        //设置adapter
        deskAdapter = new DeskAdapter(appInfos, getContext());
        gv_desk_icon.setAdapter(deskAdapter);
        //设置鼠标的动作监听操作
        gv_desk_icon.setOnGenericMotionListener(new DeskOnGenericMotionListener());
    }

    //获取应用的包信息并加入appInfos中去
    private void getInstallPackageInfo() {
        //获取包管理器
        pm = getActivity().getPackageManager();
        //通过管理器获取已安装应用
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for(int i = 0; i< packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            AppInfo appInfo =new AppInfo();
            String appName = packageInfo.applicationInfo.loadLabel(getActivity().getPackageManager()).toString();
            appInfo.setAppName(appName);
            Drawable appIcon = packageInfo.applicationInfo.loadIcon(getActivity().getPackageManager());
            appInfo.setIcon(appIcon);
            String packageName = packageInfo.packageName;
            appInfo.setPackageName(packageName);
            //将应用信息加入appInfos
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==0){
                appInfos.add(appInfo);//如果非系统应用则添加进list
            }
        }
    }

    //设置鼠标的动作监听操作
    private class DeskOnGenericMotionListener implements View.OnGenericMotionListener {
        @Override
        public boolean onGenericMotion(View view, MotionEvent event) {
            switch (event.getButtonState()) {
                case MotionEvent.BUTTON_PRIMARY:   // BUTTON_PRIMARY鼠标左键点击
                    //item左键点击监听
                    IconItemClickListener();
                    break;
                case MotionEvent.BUTTON_SECONDARY:    //BUTTON_SECONDARY鼠标右键点击
                    //点击右键卸载应用
                    IconUninstallItemClickListener();
                    break;
                case MotionEvent.BUTTON_TERTIARY:   //BUTTON_TERTIARY鼠标滚轮点击
                    //item鼠标滚轮点击监听
                    IconItemClickListener();
                    break;
                case MotionEvent.ACTION_SCROLL:   //ACTION_SCROLL鼠标滚轮滑动
                    MouseScrollAction(event);
                    break;
            }
            return false;
        }

    }
    //应用图标的左键点击开启操作
    private void IconItemClickListener() {
        gv_desk_icon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                packageName = appInfos.get(i).getPackageName();
                //取到点击的包名
                Intent intent = pm.getLaunchIntentForPackage(packageName);
                //如果该程序不可启动（像系统自带的包，有很多是没有入口的）会返回NULL
                if (null != intent){
                    startActivity(intent);
                }
            }
        });
    }
    //应用图标的右键点击卸载操作
    private void IconUninstallItemClickListener() {
        gv_desk_icon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //获取包名
                packageName = appInfos.get(i).getPackageName();
                //卸载应用
                uninstallAPK(packageName);
            }
        });
    }

    //鼠标滚轮滑动
    private void MouseScrollAction(MotionEvent event) {
        if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f) {
            L.i("fortest::onGenericMotionEvent", "down");
//            T.showShort(getContext(), "向下滚动...");
        }
        //获得垂直坐标上的滚动方向,也就是滚轮向上滚
        else {
            L.i("fortest::onGenericMotionEvent", "up");
//            T.showShort(getContext(), "向上滚动...");
        }
    }

    //卸载应用
    private void uninstallAPK(String packageName){
        Uri uri=Uri.parse("package:"+packageName);
        Intent intent=new Intent(Intent.ACTION_DELETE,uri);
        startActivityForResult(intent,0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0){
            //通知更新UI
            deskAdapter.notifyDataSetChanged();
            initData();
        }
    }

    @Override
    public boolean canGoBack() {
        return false;
    }

    @Override
    public void goBack() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
