package com.emindsoft.filemanager.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.emindsoft.filemanager.BaseFragment;
import com.emindsoft.filemanager.R;
import com.emindsoft.filemanager.adapter.VideoAdapter;
import com.emindsoft.filemanager.adapter.VideoItem;
import com.emindsoft.filemanager.system.Constants;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 视频列表页面
 * Created by zuojj on 16-5-18.
 */
public class VideoFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ArrayList<VideoItem> videoItems;
    //播放视频专用
    private ProgressDialog mProgressDialog;

    @Bind(R.id.tv_no_video)
    TextView tv_no_video;
    @Bind(R.id.gv_video_pager)
    GridView gv_video_pager;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //关闭进度条
            mProgressDialog.dismiss();
            if (videoItems !=null && videoItems.size()>0){
                gv_video_pager.setAdapter(new VideoAdapter(getContext(), videoItems));
            }else {
                tv_no_video.setVisibility(View.VISIBLE);
            }
            handler.removeCallbacksAndMessages(null);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        getVideoList();
        //点击item播放视频
        gv_video_pager.setOnItemClickListener(this);
    }

    private void getVideoList() {
        mProgressDialog = ProgressDialog.show(getContext(), null, "正在加载...");
        //子线程准备数据
        new Thread(){
            public void run(){
                videoItems = new ArrayList<>();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = getContext().getContentResolver();
                String[] projection = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视屏名称
                        MediaStore.Video.Media.SIZE,//视频播放的大小
                        MediaStore.Video.Media.DATA//视频播放的绝对路径
                };
                Cursor cursor = contentResolver.query(uri,projection,null,null,null);
                if (cursor!=null){
                    while (cursor.moveToNext()){
                        //封装对象MediaItem
                        VideoItem item = new VideoItem();
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        item.setName(name);
                        Long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                        item.setSize(size);
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        item.setData(data);
                        //将对象加入到集合中去
                        videoItems.add(item);
                    }
                    cursor.close();
                    //发消息更新UI
                    handler.sendEmptyMessage(0);
                }
            }
        }.start();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //点击item播放视频
        VideoItem videoItem = videoItems.get(i);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        File f = new File(videoItem.getData());
        String type = Constants.getMIMEType(f);
        intent.setDataAndType(Uri.fromFile(f), type);
        startActivity(intent);
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
