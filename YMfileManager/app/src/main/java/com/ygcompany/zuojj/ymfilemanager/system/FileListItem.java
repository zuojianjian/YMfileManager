package com.ygcompany.zuojj.ymfilemanager.system;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.ygcompany.zuojj.ymfilemanager.R;

public class FileListItem {
    public static void setupFileListItemInfo(Context context, View view,
            FileInfo fileInfo, FileIconHelper fileIcon,
            FileViewInteractionHub fileViewInteractionHub) {

        // if in moving mode, show selected file always
        if (fileViewInteractionHub.isMoveState()) {
            fileInfo.Selected = fileViewInteractionHub.isFileSelected(fileInfo.filePath);
        }

        Util.setText(view, R.id.file_name, fileInfo.fileName);
        Util.setText(view, R.id.file_count, fileInfo.IsDir ? "(" + fileInfo.Count + ")" : "");
        Util.setText(view, R.id.modified_time, Util.formatDateString(context, fileInfo.ModifiedDate));
        Util.setText(view, R.id.file_size, (fileInfo.IsDir ? "" : Util.convertStorage(fileInfo.fileSize)));

        ImageView lFileImage = (ImageView) view.findViewById(R.id.file_image);
        ImageView lFileImageFrame = (ImageView) view.findViewById(R.id.file_image_frame);

        if (fileInfo.IsDir) {
            lFileImageFrame.setVisibility(View.GONE);
            lFileImage.setImageResource(R.mipmap.folder);
        } else {
            fileIcon.setIcon(fileInfo, lFileImage, lFileImageFrame);
        }
    }
}
