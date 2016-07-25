package com.emindsoft.filemanager.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.emindsoft.filemanager.R;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.emindsoft.filemanager.BaseFragment;
import com.emindsoft.filemanager.adapter.ChildAdapter;
import com.emindsoft.filemanager.bean.ImageBean;

/**
 * 显示全部图片页面
 * Created by zuojj on 16-5-18.
 */
public class DetailFragment extends BaseFragment {

    @Bind(R.id.gv_detail_pictrue)
    GridView gv_detail_pictrue;

    private int i;
    private List<String> childPathList;
    private List<ImageBean> list;
    HashMap<String, List<String>> mGruopMap;

    @SuppressLint("ValidFragment")
    public DetailFragment(HashMap<String, List<String>> mGruopMap, List<ImageBean> list, int i) {
        this.mGruopMap = mGruopMap;
        this.list = list;
        this.i = i;
    }

    public DetailFragment() {
    }

//    public static DetailFragment getInstance(HashMap<String, List<String>> mGruopMap, ArrayList<ImageBean> list, int i){
//
//        DetailFragment instance = new DetailFragment();
//        Bundle args = new Bundle();
//        args.putSerializable("groupmap", mGruopMap);
//        args.putSerializable("list", list);
//        args.putSerializable("int", i);
//        return instance;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_detail_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        childPathList = mGruopMap.get(list.get(i).getFolderName());
        ChildAdapter adapter = new ChildAdapter(getContext(), childPathList, gv_detail_pictrue);
        gv_detail_pictrue.setAdapter(adapter);
        gv_detail_pictrue.setOnItemClickListener(new DetailOnItemClickListener());
    }

    private class DetailOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String path = childPathList.get(i);
            //Uri mUri = Uri.parse("file://" + picFile.getPath());Android3.0以后最好不要通过该方法，存在一些小Bug
            File file = new File(path);
            if (file.isFile()) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                //intent.addCategory(Intent.CATEGORY_OPENABLE);
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

    @Override
    public boolean canGoBack() {
        return false;
    }

    @Override
    public void goBack() {

    }
}

