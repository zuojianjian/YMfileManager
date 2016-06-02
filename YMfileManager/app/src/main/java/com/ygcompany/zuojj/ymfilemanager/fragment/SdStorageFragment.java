package com.ygcompany.zuojj.ymfilemanager.fragment;

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

import com.ygcompany.zuojj.ymfilemanager.R;
import com.ygcompany.zuojj.ymfilemanager.system.Util;
import com.ygcompany.zuojj.ymfilemanager.utils.T;
import com.ygcompany.zuojj.ymfilemanager.view.PersonalSpaceFragment;
import com.ygcompany.zuojj.ymfilemanager.view.SystemSpaceFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * SD卡存储页面
 * Created by zuojj on 16-5-18.
 */
public class SdStorageFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = SdStorageFragment.class.getSimpleName();
    private static final String SYSTEM_SPACE_FRAGMENT = "system_space_fragment";
    FragmentManager manager = getFragmentManager();

    @Bind(R.id.android_system)
    RelativeLayout android_system;
    @Bind(R.id.personal_space)
    RelativeLayout personal_space;
    @Bind(R.id.android_service)
    RelativeLayout android_service;
    @Bind(R.id.rl_dvd_space)
    RelativeLayout rl_dvd_space;
    @Bind(R.id.tv_system_total)
    TextView tv_system_total;
    @Bind(R.id.tv_system_avail)
    TextView tv_system_avail;
    @Bind(R.id.pb_system_process)
    ProgressBar pb_system_process;

    private View view;

    public SdStorageFragment(FragmentManager manager) {
        this.manager = manager;
    }

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
        setSdInfo();
        return view;
    }

    //设置sd卡用量信息
    private void setSdInfo() {
        Util.SDCardInfo sdCardInfo = Util.getSDCardInfo();
        if (sdCardInfo != null) {
            tv_system_total.setText(Util.convertStorage(sdCardInfo.total));
            tv_system_avail.setText(Util.convertStorage(sdCardInfo.free));
            pb_system_process.setMax((int) sdCardInfo.total);
            pb_system_process.setProgress((int) (sdCardInfo.total - sdCardInfo.free));
        }

    }

    private void initView() {

        android_system.setOnClickListener(this);
        personal_space.setOnClickListener(this);
        android_service.setOnClickListener(this);
        rl_dvd_space.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.android_system:
                manager.beginTransaction().replace(R.id.fl_mian,new SystemSpaceFragment(),SYSTEM_SPACE_FRAGMENT)
                        .addToBackStack(null).commit();
                break;
            case R.id.personal_space:
                T.showShort(getContext(),"个人空间");
                manager.beginTransaction().replace(R.id.fl_mian,new PersonalSpaceFragment(0))
                        .addToBackStack(null).commit();
                break;
            case R.id.android_service:
                T.showShort(getContext(),"云盘");
                manager.beginTransaction().replace(R.id.fl_mian,new PersonalSpaceFragment(1))
                        .addToBackStack(null).commit();
                break;
            case R.id.rl_dvd_space:
                T.showShort(getContext(),"云盘");
                manager.beginTransaction().replace(R.id.fl_mian,new PersonalSpaceFragment(2))
                        .addToBackStack(null).commit();
                break;
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
