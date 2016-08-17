package com.emindsoft.openthos.system;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.emindsoft.openthos.R;
import com.emindsoft.openthos.utils.L;
import com.emindsoft.openthos.utils.LocalCache;
import com.emindsoft.openthos.utils.T;

import java.util.List;

public class FileListAdapter extends ArrayAdapter<FileInfo> {
    private LayoutInflater mInflater;

    private FileViewInteractionHub mFileViewInteractionHub;

    private FileIconHelper mFileIcon;

    private Context mContext;

    private boolean mIsCtrlPress;

    public FileListAdapter(Context context, int resource,
                           List<FileInfo> objects, FileViewInteractionHub f,
                           FileIconHelper fileIcon, boolean isCtrlPress) {
        super(context, resource, objects);
        mInflater = LayoutInflater.from(context);
        mFileViewInteractionHub = f;
        mFileIcon = fileIcon;
        mContext = context;
        mIsCtrlPress = isCtrlPress;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            if ("list".equals(LocalCache.getViewTag())) {
                view = mInflater.inflate(R.layout.file_browser_item_list, parent, false);
            } else if ("grid".equals(LocalCache.getViewTag())) {
                view = mInflater.inflate(R.layout.file_browser_item_grid, parent, false);
            }
        }
//   checkbox
        final FileInfo lFileInfo = mFileViewInteractionHub.getItem(position);
        FileListItem.setupFileListItemInfo(mContext, view, lFileInfo,
                mFileIcon, mFileViewInteractionHub);
        assert view != null;
        view.findViewById(R.id.file_checkbox).setOnClickListener(
                new FileListItem.FileItemOnClickListener(
                        mFileViewInteractionHub));

        final View finalView = view;
        L.e("mIsCtrlPress+++++++_____________+++++++++++++",mIsCtrlPress+"");
        if (mIsCtrlPress){
            view.findViewById(R.id.ll_grid_item_bg).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    T.showShort(mContext,"多选！");
                    mFileViewInteractionHub.setBackground(finalView, position, lFileInfo);
                    L.e("+++++++++++++", position + "");
                    if (!lFileInfo.Selected) {
                        v.setSelected(false);
                    } else {
                        v.setSelected(true);
                    }
                }
            });
        }else {

        }
        return view;
    }

}
