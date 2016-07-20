package cn.com.emindsoft.filemanager.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.com.emindsoft.filemanager.R;

/**
 * 用于显示text文本选择的dialog
 * Created by zuojj on 16-7-1.
 */
public class TextSelectDialog extends AlertDialog implements View.OnClickListener {
    //用于打开显示文本的选择
    @Bind(R.id.dialog_type_text)
    TextView dialog_type_text;
    @Bind(R.id.dialog_type_audio)
    TextView dialog_type_audio;
    @Bind(R.id.dialog_type_video)
    TextView dialog_type_video;
    @Bind(R.id.dialog_type_image)
    TextView dialog_type_image;
    //上下文
    private Context context;
    //文本打开选择的路径
    private String filePath;

    public TextSelectDialog(Context mContext, int dialog, String filePath) {
        super(mContext, dialog);
        this.context = mContext;
        this.filePath = filePath;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_select_dialog);
        ButterKnife.bind(TextSelectDialog.this);
        initView();
    }

    private void initView() {
        //用于打开显示文本的选择
        dialog_type_text.setOnClickListener(this);
        dialog_type_audio.setOnClickListener(this);
        dialog_type_video.setOnClickListener(this);
        dialog_type_image.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        String selectType = "*/*";
        switch (view.getId()) {

            //用于打开显示文本的选择
            case R.id.dialog_type_text:  //文本
                selectType = "text/plain";
                TextSelectDialog.this.dismiss();
                break;
            case R.id.dialog_type_audio:  //音频
                selectType = "audio/*";
                TextSelectDialog.this.dismiss();
                break;
            case R.id.dialog_type_video:  //视频
                selectType = "video/*";
                TextSelectDialog.this.dismiss();
                break;
            case R.id.dialog_type_image:  //图片
                selectType = "image/*";
                TextSelectDialog.this.dismiss();
                break;
            default:
                break;

        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(filePath)), selectType);
        context.startActivity(intent);
    }

}

