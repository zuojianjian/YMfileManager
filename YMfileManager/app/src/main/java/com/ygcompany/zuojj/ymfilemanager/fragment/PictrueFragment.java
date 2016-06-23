package com.ygcompany.zuojj.ymfilemanager.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.ygcompany.zuojj.ymfilemanager.BaseFragment;
import com.ygcompany.zuojj.ymfilemanager.R;
import com.ygcompany.zuojj.ymfilemanager.adapter.GroupAdapter;
import com.ygcompany.zuojj.ymfilemanager.bean.ImageBean;
import com.ygcompany.zuojj.ymfilemanager.system.Util;
import com.ygcompany.zuojj.ymfilemanager.view.GvDetailFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * 显示图片集合缩略图页面
 * Created by zuojj on 16-5-18.
 */
public class PictrueFragment extends BaseFragment {
    private static final int SCAN_OK = 1;
    private static final String GV_DETAIL = "gv_detail";

    private View view;
    @Bind(R.id.gv_pictrue)
    GridView gv_pictrue;
    @Bind(R.id.tv_no_pictrue)
    TextView tv_no_pictrue;

    private GroupAdapter adapter;
    private ProgressDialog mProgressDialog;
    //图片分类封装集合
    private List<ImageBean> list = new ArrayList<>();
    private HashMap<String, List<String>> mGruopMap = new HashMap<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    //关闭进度条
                    mProgressDialog.dismiss();
                    list = subGroupOfImage(mGruopMap);
                    if (null != list){
                        adapter = new GroupAdapter(getContext(), list, gv_pictrue);
                    }else {
                        tv_no_pictrue.setVisibility(View.VISIBLE);
                    }
                        gv_pictrue.setAdapter(adapter);
                    mHandler.removeCallbacksAndMessages(null);
                    break;
            }
        }

    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.pictrue_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        //清空上一次集合数据，防止数据重复加载
        if (null != list){
            list.clear();
        }
        //查询手机中的图片数据，并封装进list集合中
        getImages();
        //文件夹点击显示全部图片
        gv_pictrue.setOnItemClickListener(new FolderOnItemClickListener());
    }

    @Override
    public boolean canGoBack() {
        return false;
    }

    @Override
    public void goBack() {

    }

    private class FolderOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //将fragment添加到BackStack
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fl_mian, new GvDetailFragment(mGruopMap,list,i),GV_DETAIL);//主页面
            ft.addToBackStack(GV_DETAIL);
            ft.commit();
        }
    }
    /**
     * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中
     * 所以需要遍历HashMap将数据组装成List
     * @param mGruopMap
     * @return
     */
    private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap) {
        if (mGruopMap.size() == 0) {
            return null;
        }
        Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<String>> entry = it.next();
            ImageBean mImageBean = new ImageBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();

            mImageBean.setFolderName(key);
            mImageBean.setImageCounts(value.size());
            mImageBean.setTopImagePath(value.get(0));//获取该组的第一张图片
            list.add(mImageBean);
        }
        return list;
    }

    /**
     * 利用ContentProvider扫描手机中的图片数据，此方法在运行在子线程中
     */
    private void getImages() {
        if (!Util.isSDCardReady()) {
            Toast.makeText(getContext(), "暂无外部存储", LENGTH_SHORT).show();
            return;
        }
        //显示进度条
        mProgressDialog = ProgressDialog.show(getContext(), null, "正在加载...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = getContext().getContentResolver();
//                String[] projection = {
//                        MediaStore.Images.Media.DISPLAY_NAME,//图片名称
//                        MediaStore.Images.Media.SIZE//图片的大小
//                };
                //只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);

                while (mCursor.moveToNext()) {
//                    ImageBean mImageBean = new ImageBean();
                    //获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
//                    String iconName = mCursor.getString(mCursor
//                            .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
//                    //设置图片的名称
//                    mImageBean.setIconName(iconName);
//                    Long iconSize = mCursor.getLong(mCursor
//                            .getColumnIndex(MediaStore.Images.Media.SIZE));
//                    //设置图片的大小
//                    mImageBean.setIconSize(iconSize);
                    //将内容添加到集合中
//                    list.add(mImageBean);
                    //获取该图片的父路径名
                    String parentName = new File(path).getParentFile().getName();
                    //根据父路径名将图片放入到mGruopMap中
                    if (!mGruopMap.containsKey(parentName)) {
                        List<String> chileList = new ArrayList<String>();
                        chileList.add(path);
                        mGruopMap.put(parentName, chileList);
                    } else {
                        mGruopMap.get(parentName).add(path);
                    }
                }
                mCursor.close();
                //通知Handler扫描图片完成
                mHandler.sendEmptyMessage(SCAN_OK);
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
