package com.ygcompany.zuojj.ymfilemanager;

import android.support.v4.app.Fragment;

/**
 * Created by zuojj on 16-6-5.
 *
 */
public abstract class BaseFragment extends Fragment {

    public abstract boolean canGoBack();
    /**
     * 执行回退操作
     */
    public abstract void goBack();
}
