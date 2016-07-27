package com.emindsoft.filemanager.system;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.emindsoft.filemanager.R;
import com.emindsoft.filemanager.utils.LocalCache;

import java.util.List;

public class FileListAdapter extends ArrayAdapter<FileInfo> {
    private LayoutInflater mInflater;

    private FileViewInteractionHub mFileViewInteractionHub;

    private FileIconHelper mFileIcon;

    private Context mContext;
    private boolean isShowCheckbox = false;

    public FileListAdapter(Context context, int resource,
                           List<FileInfo> objects, FileViewInteractionHub f,
                           FileIconHelper fileIcon) {
        super(context, resource, objects);
        mInflater = LayoutInflater.from(context);
        mFileViewInteractionHub = f;
        mFileIcon = fileIcon;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            if ("list".equals(LocalCache.getViewTag())) {
                view = mInflater.inflate(R.layout.file_browser_item_list, parent, false);
            }
            else if ("grid".equals(LocalCache.getViewTag())) {
                view = mInflater.inflate(R.layout.file_browser_item_grid, parent, false);
            }
        }

        FileInfo lFileInfo = mFileViewInteractionHub.getItem(position);
        FileListItem.setupFileListItemInfo(mContext, view, lFileInfo,
                mFileIcon, mFileViewInteractionHub);
        assert view != null;
        view.findViewById(R.id.file_checkbox).setOnClickListener(
                new FileListItem.FileItemOnClickListener(
                        mFileViewInteractionHub));
        return view;
    }
}
