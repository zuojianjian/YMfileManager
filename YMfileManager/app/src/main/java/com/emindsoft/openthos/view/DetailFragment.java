package com.emindsoft.openthos.view;

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

import com.emindsoft.openthos.BaseFragment;
import com.emindsoft.openthos.R;
import com.emindsoft.openthos.adapter.ChildAdapter;
import com.emindsoft.openthos.bean.ImageBean;
import com.emindsoft.openthos.system.Constants;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * 显示全部图片页面
 * Created by zuojj on 16-5-18.
 */
public class DetailFragment extends BaseFragment {

    private GridView gv_detail_pictrue;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_detail_layout, container, false);
        gv_detail_pictrue = (GridView) view.findViewById(R.id.gv_detail_pictrue);
        initView();
        return view;
    }

    private void initView() {
        childPathList = mGruopMap.get(list.get(i).getFolderName());
        ChildAdapter adapter = new ChildAdapter(getActivity(), childPathList, gv_detail_pictrue);
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
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String type = Constants.getMIMEType(file);
                intent.setDataAndType(Uri.fromFile(file), type);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean canGoBack() {
        return false;
    }

    @Override
    public void goBack() {

    }
}

