package com.ygcompany.zuojj.ymfilemanager.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ygcompany.zuojj.ymfilemanager.R;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zuojj on 16-5-18.
 */
public class FullScreenFragment extends Fragment {
    private View view;
    private List<String> childList;
    private int i;
    @Bind(R.id.iv_full_screen)
    ImageView iv_full_screen;
    private Bitmap bitmap;

    public FullScreenFragment(List<String> childList, int i) {
        this.childList = childList;
        this.i = i;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.full_screen_pic, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        String path = childList.get(i);
        final File file = new File(path);
        if (file.exists()) {
            //从本地取图片
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;//图片宽高都为原来的二分之一，即图片为原来的四分之一
            bitmap = BitmapFactory.decodeFile(path, options);
            iv_full_screen.setImageBitmap(bitmap);
        }
//        if(!bitmap.isRecycled()){
//            bitmap.recycle();   //回收图片所占的内存
//            System.gc() ; //提醒系统及时回收
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
