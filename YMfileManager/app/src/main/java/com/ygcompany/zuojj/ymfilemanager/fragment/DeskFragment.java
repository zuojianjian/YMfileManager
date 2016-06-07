package com.ygcompany.zuojj.ymfilemanager.fragment;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.ygcompany.zuojj.ymfilemanager.BaseFragment;
import com.ygcompany.zuojj.ymfilemanager.R;
import com.ygcompany.zuojj.ymfilemanager.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zuojj on 16-5-18.
 */
public class DeskFragment extends BaseFragment {
    private View view;
    //用来存储获取的应用信息数据
    private ArrayList<AppInfo> appInfos = new ArrayList<>();
    private List<PackageInfo> packages;

    @Bind(R.id.gv_desk_icon)
    GridView gv_desk_icon;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.desk_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        packages = getActivity().getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packages.size();i++) {
            PackageInfo packageInfo = packages.get(i);
            AppInfo appInfo =new AppInfo();
            String appName = packageInfo.applicationInfo.loadLabel(getActivity().getPackageManager()).toString();
            appInfo.setAppName(appName);
            Drawable appIcon = packageInfo.applicationInfo.loadIcon(getActivity().getPackageManager());
            appInfo.setIcon(appIcon);
            //将应用信息加入appInfos
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==0){
                appInfos.add(appInfo);//如果非系统应用则添加进list
            }
        }
        gv_desk_icon.setAdapter(new DeskAdapter());
    }

    private class DeskAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return appInfos.size();
        }

        @Override
        public Object getItem(int i) {
            return appInfos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            Hodler hodler = null;
            if (convertView==null){
                hodler = new Hodler();
                convertView = convertView.inflate(getContext(),R.layout.desk_icon_item,null);
                hodler.iv_desk_icon = (ImageView) convertView.findViewById(R.id.iv_desk_icon);
//                hodler.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                convertView.setTag(hodler);
            }else {
                hodler = (Hodler) convertView.getTag();
            }
            AppInfo appInfo = appInfos.get(i);
            hodler.iv_desk_icon.setImageDrawable(appInfo.getIcon());
//            hodler.tv_app_name.setText(appInfo.getAppName());
            return convertView  ;
        }
    }

    static class Hodler {
        //        TextView tv_app_name;
        ImageView iv_desk_icon;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
