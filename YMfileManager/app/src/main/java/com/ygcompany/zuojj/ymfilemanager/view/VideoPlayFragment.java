package com.ygcompany.zuojj.ymfilemanager.view;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.ygcompany.zuojj.ymfilemanager.R;
import com.ygcompany.zuojj.ymfilemanager.adapter.VideoItem;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zuojj on 16-5-18.
 */
public class VideoPlayFragment extends Fragment {
    private VideoItem videoItem;
    private View view;
    @Bind(R.id.vv_media_player)
    VideoView vv_media_player;

    public VideoPlayFragment(VideoItem videoItem) {
        this.videoItem = videoItem;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.video_play_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        vv_media_player.setVideoPath(String.valueOf(Uri.parse(videoItem.getData())));
        vv_media_player.requestFocus();
        vv_media_player.pause();
        vv_media_player.start();
        vv_media_player.setOnErrorListener(videoErrorListener);
    }

    public MediaPlayer.OnErrorListener videoErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            // 对播放出错进行处理
            return true;
        }
//        Intent intent = new Intent();
//        intent.setDataAndType(Uri.parse(videoItem.getData()),"video/*");
//        activity.startActivity(intent);
    };
}
