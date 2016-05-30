package com.ygcompany.zuojj.ymfilemanager.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ygcompany.zuojj.ymfilemanager.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *个人空间页面
 * Created by zuojj on 16-5-20.
 */
public class PersonalSpaceFragment extends Fragment {
    private View view;
    @Bind(R.id.tv_personal)
    TextView tv_personal;
    @Bind(R.id.tv_service)
    TextView tv_service;
    @Bind(R.id.tv_dvd)
    TextView tv_dvd;
    private int flag;

    public PersonalSpaceFragment(int i) {
        this.flag = i;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.personal_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {

         if (flag == 0){
             tv_personal.setVisibility(View.VISIBLE);
         }else if (flag == 1){
             tv_service.setVisibility(View.VISIBLE);
         }else if (flag == 2){
             tv_dvd.setVisibility(View.VISIBLE);
         }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
