package com.emindsoft.filemanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emindsoft.filemanager.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 网络邻居页面
 * Created by zuojj on 16-6-8.
 */
public class OnlineNeighborFragment extends Fragment{

    @Bind(R.id.tv_internet)
    TextView tv_internet;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.personal_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
         tv_internet.setVisibility(View.VISIBLE);
    }
}
