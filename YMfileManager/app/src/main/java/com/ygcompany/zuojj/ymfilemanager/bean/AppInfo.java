package com.ygcompany.zuojj.ymfilemanager.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by zuojj on 16-5-11.
 */
public class AppInfo {
    String appName;
    Drawable appIcon;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return appIcon;
    }

    public void setIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }
}
