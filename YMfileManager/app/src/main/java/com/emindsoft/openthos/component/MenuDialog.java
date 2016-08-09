package com.emindsoft.openthos.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.emindsoft.openthos.R;
import com.emindsoft.openthos.system.FileViewInteractionHub;
import com.emindsoft.openthos.utils.T;

/**
 * Created by zuojj on 16-8-9.
 */
public class MenuDialog extends Dialog implements View.OnClickListener {
    private boolean flag;
    //上下文
    private Context context;
    private FileViewInteractionHub mFileViewInteractionHub;

    private TextView newdir;
    private TextView newfile;
    private TextView refresh;
    private TextView paste;
    private TextView selectall;

    public void setEnablePaste(boolean can) {
        flag = can;
        if (can) {
            paste.setTextColor(Color.parseColor("#000000"));
        } else {
            paste.setTextColor(Color.parseColor("#b19898"));
        }
    }

    public MenuDialog(Context context, int menu_dialog, FileViewInteractionHub mFileViewInteractionHub) {
        super(context);
        this.context = context;
        this.mFileViewInteractionHub = mFileViewInteractionHub;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.right_click_menu2);

        initView();
        initData();
    }

    private void initData() {
        newdir.setOnClickListener(this);
        newfile.setOnClickListener(this);
        refresh.setOnClickListener(this);
        paste.setOnClickListener(this);
        selectall.setOnClickListener(this);
    }

    private void initView() {
        newdir = (TextView) findViewById(R.id.newdir);
        newfile = (TextView) findViewById(R.id.newfile);
        refresh = (TextView) findViewById(R.id.refresh);
        paste = (TextView) findViewById(R.id.paste);
        selectall = (TextView) findViewById(R.id.selectall);
        flag = true;
    }

    @Override
    public void onClick(View v) {
        T.showShort(context,"jsljfsljfs");
    }

    public void showDialog(int x, int y, int height, int width) {
        show();
        // setContentView可以设置为一个View也可以简单地指定资源ID
        // LayoutInflater
        // li=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        // View v=li.inflate(R.layout.dialog_layout, null);
        // dialog.setContentView(v);
        /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置,
         * 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);

        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
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
        if (x > (d.getWidth() - 200))
            lp.x = d.getWidth() - 200;
        else
            lp.x = x; // 新位置X坐标
        if (y > (d.getHeight() - 285))
            lp.y = d.getHeight() - 285;
        else
            lp.y = y - 10;
        // 新位置Y坐标
        lp.width = width; // 宽度
        lp.height = height; // 高度
        lp.alpha = 0.9f; // 透明度

        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        dialogWindow.setAttributes(lp);

        /*
         * 将对话框的大小按屏幕大小的百分比设置
         */
//        WindowManager m = getWindowManager();
//        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
//        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
//        p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
//        p.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.65
//        dialogWindow.setAttributes(p);


    }
}
