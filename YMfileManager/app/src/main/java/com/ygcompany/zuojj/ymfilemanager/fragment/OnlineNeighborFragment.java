package com.ygcompany.zuojj.ymfilemanager.fragment;

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
 * Created by zuojj on 16-6-8.
 */
public class OnlineNeighborFragment extends Fragment{
    private View view;

    @Bind(R.id.tv_internet)
    TextView tv_internet;
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
         tv_internet.setVisibility(View.VISIBLE);
    }
}
