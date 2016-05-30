package com.ygcompany.zuojj.ymfilemanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ygcompany.zuojj.ymfilemanager.R;
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
    private View view;
//    public int[] icon = {R.mipmap.category_icon_music, R.mipmap.category_icon_video, R.mipmap.category_icon_picture,
//            R.mipmap.category_icon_theme, R.mipmap.category_icon_document, R.mipmap.category_icon_zip,
//            R.mipmap.category_icon_apk, R.mipmap.category_icon_favorite};
//    private String[] iconName = {"音乐", "视频", "图片", "主题", "文档", "压缩包", "安装包", "收藏"};

    @Bind(R.id.android_system)
    RelativeLayout android_system;
    @Bind(R.id.personal_space)
    RelativeLayout personal_space;
    @Bind(R.id.android_service)
    RelativeLayout android_service;
    @Bind(R.id.rl_dvd_space)
    RelativeLayout rl_dvd_space;
//    @Bind(R.id.gv_picture)
//    GridView gv_picture;

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
        return view;
    }

    private void initView() {

        android_system.setOnClickListener(this);
        personal_space.setOnClickListener(this);
        android_service.setOnClickListener(this);
        rl_dvd_space.setOnClickListener(this);
//        gv_picture.setAdapter(new AndroidAdapter(getContext(),icon,iconName));
//        gv_picture.setOnItemClickListener(this);
    }

//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        switch (i) {
//            case 0:
//                manager.popBackStack();
//                manager.beginTransaction().replace(R.id.fl_mian, new MusicFragment()).addToBackStack(null).commit();
//                break;
//            case 1:
//                manager.beginTransaction().replace(R.id.fl_mian, new VideoFragment()).addToBackStack(null).commit();
//                break;
//            case 2:
//                manager.beginTransaction().replace(R.id.fl_mian, new PictrueFragment()).addToBackStack(null).commit();
//                break;
//            case 3:
//                T.showShort(getContext(),"敬请期待");
//                break;
//            case 4:
//                T.showShort(getContext(),"敬请期待");
//                break;
//            case 5:
//                T.showShort(getContext(),"敬请期待");
//                break;
//            case 6:
//                T.showShort(getContext(),"敬请期待");
//                break;
//        }
//    }

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
