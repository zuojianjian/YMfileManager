package com.emindsoft.filemanager.fragment;

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

import com.emindsoft.filemanager.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.emindsoft.filemanager.BaseFragment;
import com.emindsoft.filemanager.bean.SearchInfo;
import com.emindsoft.filemanager.utils.L;
import com.emindsoft.filemanager.utils.LocalCache;
import com.emindsoft.filemanager.view.SystemSpaceFragment;

/**
 * 数据搜索页面
 * Created by zuojj on 16-6-12.
 */
public class SearchFragment extends BaseFragment{
    private String LOG_TAG = "SearchFragment";
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        L.e("initData"+LOG_TAG,mSearchList.size()+""); //  1
        SearchAdapter searchAdapter = new SearchAdapter();
        lv_mian_search.setAdapter(searchAdapter);
        lv_mian_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //获取点击item的路径
                String filePath = mSearchList.get(i).getFilePath();
                //根据路径截取所需路径
                String fileRealPath = filePath.substring(0, filePath.lastIndexOf("/") + 1);

                //开启事务替换当前fragment
                manager.popBackStack();
                manager.beginTransaction().replace(R.id.fl_mian,
                        new SystemSpaceFragment("search_fragment",fileRealPath,null,null)).commit();
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
//            L.e("mSearchList"+LOG_TAG,mSearchList.size()+"");
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
            view = View.inflate(getContext(), R.layout.search_file_item,null);
            TextView search_file_name = (TextView) view.findViewById(R.id.search_file_name);
            search_file_name.setText(mSearchList.get(i).fileName);
            return view;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalCache.setSearchText(null);
    }
}
