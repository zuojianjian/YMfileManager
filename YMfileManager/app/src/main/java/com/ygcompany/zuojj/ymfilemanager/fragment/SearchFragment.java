package com.ygcompany.zuojj.ymfilemanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ygcompany.zuojj.ymfilemanager.BaseFragment;
import com.ygcompany.zuojj.ymfilemanager.R;
import com.ygcompany.zuojj.ymfilemanager.bean.SearchInfo;
import com.ygcompany.zuojj.ymfilemanager.view.SystemSpaceFragment;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zuojj on 16-6-12.
 */
public class SearchFragment extends BaseFragment{
    private View view;
    //文件list集合
    private ArrayList<SearchInfo> mSearchList = new ArrayList<>();
    FragmentManager manager = getFragmentManager();

    @Bind(R.id.lv_mian_search)
    ListView lv_mian_search;

    public SearchFragment(FragmentManager manager, ArrayList<SearchInfo> mFileList) {
        this.mSearchList = mFileList;
        this.manager = manager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        lv_mian_search.setAdapter(new SearchAdapter());
        lv_mian_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //获取点击item的路径
                String filePath = mSearchList.get(i).getFilePath();
                //根据路径截取所需路径
                String fileRealPath = filePath.substring(0, filePath.lastIndexOf("/") + 1);

                //开启事务替换当前fragment
                manager.beginTransaction().replace(R.id.fl_mian, new SystemSpaceFragment("search_fragment",fileRealPath,null,null))
                        .addToBackStack(null).commit();
            }
        });
    }

    @Override
    public boolean canGoBack() {
        return false;
    }

    @Override
    public void goBack() {

    }

    private class SearchAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSearchList.size();
        }

        @Override
        public Object getItem(int i) {
            return mSearchList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = View.inflate(getContext(),R.layout.search_file_item,null);
            TextView search_file_name = (TextView) view.findViewById(R.id.search_file_name);
            search_file_name.setText(mSearchList.get(i).fileName);
            return view;
        }
    }
}
