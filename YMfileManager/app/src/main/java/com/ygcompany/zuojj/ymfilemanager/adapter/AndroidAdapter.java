package com.ygcompany.zuojj.ymfilemanager.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ygcompany.zuojj.ymfilemanager.R;

/**
 * Created by zuojj on 16-5-19.
 */
public class AndroidAdapter extends BaseAdapter {
    private int[] icon;

    private Context context;
    private String[] iconName;

    public AndroidAdapter(Context context, int[] icon, String[] iconName) {
        this.icon = icon;
        this.context = context;
        this.iconName = iconName;
    }

    @Override
    public int getCount() {
        return icon.length;
    }

    @Override
    public Object getItem(int i) {
        return icon[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHodler hodler;
        if (convertView == null) {
            hodler = new ViewHodler();
            convertView = convertView.inflate(context, R.layout.icon_item, null);
            hodler.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            hodler.tv_icon = (TextView) convertView.findViewById(R.id.tv_icon);
            convertView.setTag(hodler);
        } else {
            hodler = (ViewHodler) convertView.getTag();
        }
        hodler.iv_icon.setImageResource(icon[i]);
        hodler.tv_icon.setText(iconName[i]);
        return convertView;
    }
    class ViewHodler {
        private ImageView iv_icon;
        private TextView tv_icon;
    }
}

