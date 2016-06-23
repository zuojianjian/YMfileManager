package com.ygcompany.zuojj.ymfilemanager.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ygcompany.zuojj.ymfilemanager.R;
import com.ygcompany.zuojj.ymfilemanager.adapter.ChildAdapter;
import com.ygcompany.zuojj.ymfilemanager.bean.ImageBean;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 显示全部图片页面
 * Created by zuojj on 16-5-18.
 */
public class GvDetailFragment extends Fragment {
    private View view;

    @Bind(R.id.gv_detail_pictrue)
    GridView gv_detail_pictrue;

    private int i;
    private List<String> childList;
    private List<ImageBean> list;
    private ChildAdapter adapter;
    HashMap<String,List<String>> mGruopMap;

    public GvDetailFragment(HashMap<String, List<String>> mGruopMap, List<ImageBean> list, int i) {
        this.mGruopMap = mGruopMap;
        this.list = list;
        this.i = i;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.gv_detail_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        childList = mGruopMap.get(list.get(i).getFolderName());
        adapter = new ChildAdapter(getContext(), childList, gv_detail_pictrue);
        gv_detail_pictrue.setAdapter(adapter);
        gv_detail_pictrue.setOnItemClickListener(new DetailOnItemClickListener());
    }

    private class DetailOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String path = childList.get(i);
//Uri mUri = Uri.parse("file://" + picFile.getPath());Android3.0以后最好不要通过该方法，存在一些小Bug
            File file = new File(path);
            if (file != null && file.isFile() == true) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "image/*");
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

