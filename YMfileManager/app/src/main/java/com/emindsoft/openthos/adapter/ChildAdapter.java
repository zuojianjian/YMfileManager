package com.emindsoft.openthos.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.emindsoft.openthos.R;

import java.util.List;

import com.emindsoft.openthos.component.MyImageView;
import com.emindsoft.openthos.component.NativeImageLoader;
import com.emindsoft.openthos.utils.L;

/**
 * 图片显示页面数据
 * Created by zuojj on 16-5-10.
 */
public class ChildAdapter extends BaseAdapter {
    private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
    /**
     * 用来存储图片的选中情况
     */
//    private HashMap<Integer, Boolean> mSelectMap = new HashMap<>();
    private GridView mGridView;
    private List<String> childPathList;
    protected LayoutInflater mInflater;

    public ChildAdapter(Context context, List<String> childPathList, GridView mGridView) {
        this.childPathList = childPathList;
        this.mGridView = mGridView;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return childPathList.size();
    }

    @Override
    public Object getItem(int position) {
        return childPathList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        //打开文件图片的路径
        String path = childPathList.get(position);
        L.e("path",path);
        //截取路径中文件名
        String iconName = path.substring(path.lastIndexOf("/")+1);
        L.e("iconName",iconName);
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.grid_child_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.tv_icon_name);

            //用来监听ImageView的宽和高
            viewHolder.mImageView.setOnMeasureListener(new MyImageView.OnMeasureListener() {
                @Override
                public void onMeasureSize(int width, int height) {
                    mPoint.set(width, height);
                }
            });

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mImageView.setImageResource(R.mipmap.pictures_no);
        }
        viewHolder.mImageView.setTag(path);

        //利用NativeImageLoader类加载本地图片
        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageLoader.NativeImageCallBack() {
            @Override
            public void onImageLoader(Bitmap bitmap, String path) {
                ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
                if(bitmap != null && mImageView != null){
                    mImageView.setImageBitmap(bitmap);
                }
            }
        });

        if(bitmap != null){
            viewHolder.mImageView.setImageBitmap(bitmap);
        }else{
            viewHolder.mImageView.setImageResource(R.mipmap.pictures_no);
        }
        viewHolder.mTextView.setText(iconName);
        return convertView;
    }

//    /**
//     * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画
//     * @param view
//     */
//    private void addAnimation(View view){
//        float [] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
//        AnimatorSet set = new AnimatorSet();
//        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
//                ObjectAnimator.ofFloat(view, "scaleY", vaules));
//        set.setDuration(150);
//        set.start();
//    }


//    /**
//     * 获取选中的Item的position
//     * @return
//     */
//    public List<Integer> getSelectItems(){
//        List<Integer> childPathList = new ArrayList<Integer>();
//        for(Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();){
//            Map.Entry<Integer, Boolean> entry = it.next();
//            if(entry.getValue()){
//                childPathList.add(entry.getKey());
//            }
//        }
//
//        return childPathList;
//    }


    class ViewHolder{
        //要显示的图片
        public MyImageView mImageView;
//        //图片名字
        public TextView mTextView;
    }

}
