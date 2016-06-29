package com.ygcompany.zuojj.ymfilemanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 顶部菜单栏点击之后传入字符串标识
 * Created by zuojj on 16-6-15.
 */
public class LocalCache {

    // 单例模式
    private static LocalCache localCache;
    // 存储对象
    private static SharedPreferences sp;

    private LocalCache(Context context) {
        sp = context.getSharedPreferences(context.getPackageName(),
                Context.MODE_PRIVATE);
    }

    /**
     * 获取本地缓存实例F
     * @param context
     * @return 本地缓存
     */
    public static LocalCache getInstance(Context context) {
        if (localCache == null) {
            localCache = new LocalCache(context);
        }
        return localCache;
    }

    //保存切换视图的标识
    public static void setViewTag(String tag){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("viewTag", tag);
        editor.apply();
    }

    public static String getViewTag(){
        return  sp.getString("viewTag", null);
    }

    //保存切换视图的标识
    public static void setSearchText(String query){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("searchText", query);
        editor.apply();
    }

    public static String getSearchText(){
        return  sp.getString("searchText", null);
    }
}
