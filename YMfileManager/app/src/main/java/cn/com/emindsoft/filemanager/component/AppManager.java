package cn.com.emindsoft.filemanager.component;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Stack;

/**
 * 这是一个管理Activity的类，可以对项目中Activity的生命周期进行管理，达到安全退出的目的；
 */
public class AppManager {
    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
    * 单一实例
    * @param
    * @return AppManager
    * created at 2016/1/25 10:12
    */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
    * 添加activity到堆栈
    * @param activity
    * @return 
    * created at 2016/1/25 10:13
    */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
    * 获取当前Activity（堆栈中最后一个压入的）
    * @param
    * @return Activity
    * created at 2016/1/25 10:14
    */

    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
    * 结束当前Activity（堆栈中最后一个压入的）
    * @param
    * @return
    * created at 2016/1/25 10:14
    */

    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
    * 结束指定的Activity
    * @param activity
    * @return
    * created at 2016/1/25 10:14
    */

    public void finishActivity(Activity activity) {
        if (activity != null) {
            if (activityStack != null) {
                activityStack.remove(activity);
            }
            activity.finish();
        }
    }

    /**
    * 结束指定类名的Activity
    * @param
    * @return Class<?>
    * created at 2016/1/25 10:19
    */

    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
    * 结束所有Activity
    * @param
    * @return
    * created at 2016/1/25 10:20
    */

    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
    * 退出应用程序
    * @param context
    * @return
    * created at 2016/1/25 10:21
    */

    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
        }
    }

    /**
    * 堆栈中压入的activity的数量
    * @param
    * @return 
    * created at 2016/1/25 13:11
    */
    public int ActivityStackSize() {
        return activityStack == null ? 0 : activityStack.size();
    }
}
