package com.ygcompany.zuojj.ymfilemanager.fragment;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.ygcompany.zuojj.ymfilemanager.BaseFragment;
import com.ygcompany.zuojj.ymfilemanager.R;
import com.ygcompany.zuojj.ymfilemanager.adapter.AudioAdapter;
import com.ygcompany.zuojj.ymfilemanager.bean.AudioItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zuojj on 16-5-18.
 */
public class MusicFragment  extends BaseFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = MusicFragment.class.getSimpleName();
    private View view;
    private static final int MUSIC_OK = 0;
    private ArrayList<AudioItem> audioItems;
    private String currentPath;
    private ContentResolver contentResolver;
    private ProgressDialog mProgressDialog;

    @Bind(R.id.gv_audio_pager)
    GridView gv_audio_pager;
    @Bind(R.id.tv_no_audio)
    TextView tv_no_audio;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case MUSIC_OK:
                    //关闭进度条
                    mProgressDialog.dismiss();
                    if (audioItems != null && audioItems.size() > 0) {
                        gv_audio_pager.setAdapter(new AudioAdapter(getContext(), audioItems));
                    } else {
                        tv_no_audio.setVisibility(View.VISIBLE);
                    }
                    handler.removeCallbacksAndMessages(null);
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
        view = inflater.inflate(R.layout.music_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        getAudioList();
        gv_audio_pager.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        MediaPlayer mediaPlayer = new MediaPlayer();
        AudioItem audioItem = audioItems.get(i);
//            mediaPlayer.release();
//            mediaPlayer = null;
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        try {
//            mediaPlayer.setDataSource(getContext(), Uri.parse(currentPath));
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mediaPlayer.start();
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.intent.action.MUSIC_PLAYER");
        startActivity(intent);
    }

    private void getAudioList() {
        mProgressDialog = ProgressDialog.show(getContext(), null, "正在加载...");
        //子线程准备数据
        new Thread() {
            public void run() {
                audioItems = new ArrayList<AudioItem>();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                contentResolver = getContext().getContentResolver();
                String[] projection = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//音乐名称
                        MediaStore.Audio.Media.SIZE,//音乐播放的时长
                        MediaStore.Audio.Media.DATA//音乐播放的绝对路径
                };
                Cursor cursor = contentResolver.query(uri, projection, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        //封装对象MediaItem
                        AudioItem item = new AudioItem();
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        item.setName(name);
                        Long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                        item.setSize(size);
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        Log.e(TAG, data);
                        item.setData(data);
                        //将对象加入到集合中去
                        audioItems.add(item);

                    }
                    cursor.close();
                    //发消息更新UI
                    handler.sendEmptyMessage(MUSIC_OK);
                }
            }
        }.start();
    }
}
