package com.emindsoft.openthos.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.emindsoft.openthos.R;

import java.io.File;

/**
 * 用于显示text文本选择的dialog
 * Created by zuojj on 16-7-1.
 */
public class TextSelectDialog extends Dialog implements View.OnClickListener {
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
        initView();
    }

    private void initView() {
        TextView dialog_type_text = (TextView) findViewById(R.id.dialog_type_text);
        TextView dialog_type_audio = (TextView) findViewById(R.id.dialog_type_audio);
        TextView dialog_type_video = (TextView) findViewById(R.id.dialog_type_video);
        TextView dialog_type_image = (TextView) findViewById(R.id.dialog_type_image);
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

    public void showTextDialog(int x, int y, int height, int width) {
        show();
        /*
         * 获取对话框的窗口对象及参数对象以修改对话框的布局设置,
         * 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        /*
         * lp.x与lp.y表示相对于原始位置的偏移.
         * 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
         * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略.
         * 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
         * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略.
         * 当参数值包含Gravity.CENTER_HORIZONTAL时
         * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动.
         * 当参数值包含Gravity.CENTER_VERTICAL时
         * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向右移动,负值向左移动.
         * gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
         * Gravity.CENTER_VERTICAL.
         *
         * 本来setGravity的参数值为Gravity.LEFT | Gravity.TOP时对话框应出现在程序的左上角,但在
         * 我手机上测试时发现距左边与上边都有一小段距离,而且垂直坐标把程序标题栏也计算在内了,
         * Gravity.LEFT, Gravity.TOP, Gravity.BOTTOM与Gravity.RIGHT都是如此,据边界有一小段距离
         */
        // 新位置Y坐标
        lp.width = width; // 宽度
        lp.height = height; // 高度
        lp.x = x+80;
        lp.y = y;
//        lp.alpha = 0.9f; // 透明度
        dialogWindow.setAttributes(lp);
    }

}

