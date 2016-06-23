package com.ygcompany.zuojj.ymfilemanager.system;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.ygcompany.zuojj.ymfilemanager.R;

import java.io.File;

/**
 * @author ShunLi
 */
public class FileManagerPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    private static final String PRIMARY_FOLDER = "pref_key_primary_folder";
    private static final String READ_ROOT = "pref_key_read_root";
    private static final String SHOW_REAL_PATH = "pref_key_show_real_path";
    private static final String SYSTEM_SEPARATOR = File.separator;
    private static String sdOrSystem = "";

    private EditTextPreference mEditTextPreference;
    private static String primaryFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mEditTextPreference = (EditTextPreference) findPreference(PRIMARY_FOLDER);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Setup the initial values
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        mEditTextPreference.setSummary(this.getString(
                R.string.pref_primary_folder_summary,
                sharedPreferences.getString(PRIMARY_FOLDER, Constants.ROOT_PATH)));

        // Set up a listener whenever a key changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    //sp存储了程序启动时的根路径“/“
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedpreferences, String key) {
        if (PRIMARY_FOLDER.equals(key)) {
            mEditTextPreference.setSummary(this.getString(
                    R.string.pref_primary_folder_summary,
                    sharedpreferences.getString(PRIMARY_FOLDER, Constants.ROOT_PATH)));
        }
    }

    //获取文件的根目录（根据各个页面传入字段的不同选择不同的路径）
    public static String getPrimaryFolder(Context context, String sdOrSystem, String directorPath) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        if ("system_space_fragment".equals(sdOrSystem)){
            primaryFolder = settings.getString(PRIMARY_FOLDER, context.getString(R.string.default_system_primary_folder, Constants.ROOT_PATH));
        }else if ("sd_space_fragment".equals(sdOrSystem)){
            primaryFolder = settings.getString(PRIMARY_FOLDER, context.getString(R.string.default_sd_primary_folder, Constants.ROOT_PATH));
        }else if ("usb_space_fragment".equals(sdOrSystem)){
            primaryFolder = settings.getString(PRIMARY_FOLDER, context.getString(R.string.default_usb_primary_folder, Constants.ROOT_PATH));
        }else if ("yun_space_fragment".equals(sdOrSystem)){
            primaryFolder = settings.getString(PRIMARY_FOLDER, context.getString(R.string.default_yun_primary_folder, Constants.ROOT_PATH));
        }else if ("search_fragment".equals(sdOrSystem)){
            primaryFolder = settings.getString(PRIMARY_FOLDER, directorPath);
        }

        if (TextUtils.isEmpty(primaryFolder)) { // setting primary folder = empty("")
            primaryFolder = Constants.ROOT_PATH;
        }

        // it's remove the end char of the home folder setting when it with the '/' at the end.
        // if has the backslash at end of the home folder, it's has minor bug at "UpLevel" function.
        int length = primaryFolder.length();
        if (length > 1 && SYSTEM_SEPARATOR.equals(primaryFolder.substring(length - 1))) { // length = 1, ROOT_PATH
            return primaryFolder.substring(0, length - 1);
        } else {
            return primaryFolder;
        }
    }

    public static boolean isReadRoot(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        boolean isReadRootFromSetting = settings.getBoolean(READ_ROOT, false);
        String directorPath = "";
        boolean isReadRootWhenSettingPrimaryFolderWithoutSdCardPrefix = !getPrimaryFolder(context, sdOrSystem, directorPath).startsWith(Util.getSdDirectory());

        return isReadRootFromSetting || isReadRootWhenSettingPrimaryFolderWithoutSdCardPrefix;
    }
    
    public static boolean showRealPath(Context context) {
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
    	return settings.getBoolean(SHOW_REAL_PATH, false);
    }
}
