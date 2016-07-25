package com.emindsoft.filemanager.system;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 这个文件夹里面存储的内容是app2sd产生的文件夹，也就是是你手机上所有安装到SD卡的应用程序的缓存文件夹。
 * androidsecure文件夹可以删除吗？
 * 如果删除之后，软件不能正常使用，和系统没有关系。
 * 删的话除了会可能导致移动至sd卡的程序损坏，数据丢失，并不会造成什么严重后果。
 * 只要把移动到sd卡的损坏程序卸载，重装，手机就完全没有损伤，文件夹也会在再次app2sd时自动重建的。
 */
public class Util {

    private static final String LOG_TAG = "Util";

    //获得SD卡的存储状态，“mounted”表示已经就绪
    public static boolean isSDCardReady() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // if path1 contains path2
    public static boolean containsPath(String path1, String path2) {
        String path = path2;
        while (path != null) {
            if (path.equalsIgnoreCase(path1))
                return true;

            if (path.equals(Constants.ROOT_PATH))
                break;
            path = new File(path).getParent();
        }

        return false;
    }

    //2个路径相加的时候，是否需要加上文件分隔符
    public static String makePath(String path1, String path2) {
        if (path1.endsWith(File.separator))
            return path1 + path2;

        return path1 + File.separator + path2;
    }

    //获得SD卡的存储目录
    public static String getSdDirectory() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    //判断1个文件是否为“普通文件”，ANDROID_SECURE下的文件都不是普通的
    public static boolean isNormalFile(String fullName) {
        String ANDROID_SECURE = "/mnt/sdcard/.android_secure";
        return !fullName.equals(ANDROID_SECURE);
    }

    //根据文件路径，获得Java文件File，再包装成FileInfo
    public static FileInfo GetFileInfo(String filePath) {
        File lFile = new File(filePath);
        if (!lFile.exists())
            return null;

        FileInfo lFileInfo = new FileInfo();
        lFileInfo.canRead = lFile.canRead();
        lFileInfo.canWrite = lFile.canWrite();
        lFileInfo.isHidden = lFile.isHidden();
        lFileInfo.fileName = Util.getNameFromFilepath(filePath);
        lFileInfo.ModifiedDate = lFile.lastModified();
        lFileInfo.IsDir = lFile.isDirectory();
        lFileInfo.filePath = filePath;
        lFileInfo.fileSize = lFile.length();
        return lFileInfo;
    }

    //根据File对象，和FilenameFilter等选项，获得包装的FileInfo
    //需要注意多少，如果File是个目录，Count就是当前目录下的文件的个数。如果是普通文件，就计算文件大小。
    //这个时候，我们知道Count字段的含义了
    public static FileInfo GetFileInfo(File f, FilenameFilter filter, boolean showHidden) {
        FileInfo lFileInfo = new FileInfo();
        String filePath = f.getPath();
        File lFile = new File(filePath);
        lFileInfo.canRead = lFile.canRead();
        lFileInfo.canWrite = lFile.canWrite();
        lFileInfo.isHidden = lFile.isHidden();
        lFileInfo.fileName = f.getName();
        lFileInfo.ModifiedDate = lFile.lastModified();
        lFileInfo.IsDir = lFile.isDirectory();
        lFileInfo.filePath = filePath;
        if (lFileInfo.IsDir) {
            int lCount = 0;
            File[] files = lFile.listFiles(filter);

            // null means we cannot access this dir
            if (files == null) {
                return null;
            }

            for (File child : files) {
                if ((!child.isHidden() || showHidden)
                        && Util.isNormalFile(child.getAbsolutePath())) {
                    lCount++;
                }
            }
            lFileInfo.Count = lCount;

        } else {

            lFileInfo.fileSize = lFile.length();

        }
        return lFileInfo;
    }

    /*
     * 采用了新的办法获取APK图标，之前的失败是因为android中存在的一个BUG,通过
     * appInfo.publicSourceDir = apkPath;来修正这个问题，详情参见:
     * http://code.google.com/p/android/issues/detail?id=9151
     */
    public static Drawable getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
                Log.e(LOG_TAG, e.toString());
            }
        }
        return null;
    }

    //获得文件的扩展名
    public static String getExtFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(dotPosition + 1, filename.length());
        }
        return "";
    }

    //获得去掉“文件后缀”的文件名字，比如“C:/a/b/c.png”，输出“C:/a/b/c”
    public static String getNameFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(0, dotPosition);
        }
        return "";
    }

    //从文件路径中，获得路径
    public static String getPathFromFilepath(String filepath) {
        int pos = filepath.lastIndexOf('/');
        if (pos != -1) {
            return filepath.substring(0, pos);
        }
        return "";
    }

    //从文件路径中，获得文件名（带后缀，如果有）
    public static String getNameFromFilepath(String filepath) {
        int pos = filepath.lastIndexOf('/');
        if (pos != -1) {
            return filepath.substring(pos + 1);
        }
        return "";
    }

    // return new file path if successful, or return null
    public static String copyFile(String src, String dest) {
        File file = new File(src);
        if (!file.exists() || file.isDirectory()) {
            Log.v(LOG_TAG, "copyFile: file not exist or is directory, " + src);
            return null;
        }
        FileInputStream fi = null;
        FileOutputStream fo = null;
        try {
            fi = new FileInputStream(file);
            File destPlace = new File(dest);
            if (!destPlace.exists()) {
                if (!destPlace.mkdirs())
                    return null;
            }

            String destPath = Util.makePath(dest, file.getName());
            File destFile = new File(destPath);
            int i = 1;
            while (destFile.exists()) {
                String destName = Util.getNameFromFilename(file.getName()) + " " + i++ + "."
                        + Util.getExtFromFilename(file.getName());
                destPath = Util.makePath(dest, destName);
                destFile = new File(destPath);
            }

            if (!destFile.createNewFile())
                return null;

            fo = new FileOutputStream(destFile);
            int count = 102400;
            byte[] buffer = new byte[count];
            int read;
            while ((read = fi.read(buffer, 0, count)) != -1) {
                fo.write(buffer, 0, read);
            }

            // TODO: set access privilege

            return destPath;
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "copyFile: file not found, " + src);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, "copyFile: " + e.toString());
        } finally {
            try {
                if (fi != null)
                    fi.close();
                if (fo != null)
                    fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    // does not include sd card folder
    private static String[] SysFileDirs = new String[]{
            "miren_browser/imagecaches"
    };

    //判断一个文件是否需要显示，根据Setting中的设置。特别说明：某个系统文件目录，不显示。
    public static boolean shouldShowFile(String path) {
        return shouldShowFile(new File(path));
    }

    //判断一个文件是否需要显示，根据Setting中的设置。特别说明：某个系统文件目录，不显示。
    public static boolean shouldShowFile(File file) {
        boolean show = Settings.instance().getShowDotAndHiddenFiles();
        if (show)
            return true;

        if (file.isHidden())
            return false;

        if (file.getName().startsWith("."))
            return false;

        String sdFolder = getSdDirectory();
        for (String s : SysFileDirs) {
            if (file.getPath().startsWith(makePath(sdFolder, s)))
                return false;
        }

        return true;
    }

    //向View中的某个TextView设置文本
    public static boolean setText(View view, int id, String text) {
        TextView textView = (TextView) view.findViewById(id);
        if (textView == null)
            return false;

        textView.setText(text);
        return true;
    }

    //向View中的某个TextView设置文本
    public static boolean setText(View view, int id, int text) {
        TextView textView = (TextView) view.findViewById(id);
        if (textView == null)
            return false;

        textView.setText(text);
        return true;
    }

    // comma separated number
    public static String convertNumber(long number) {
        return String.format("%,d", number);
    }

    // storage, G M K B
    public static String convertStorage(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    //SD卡常量信息
    public static class SDCardInfo {
        public long total;

        public long free;
    }

    //外接usb常量信息
    public static class UsbMemoryInfo {
        public long usbTotal;

        public long usbFree;
    }

    //System常量信息
    public static class SystemInfo {
        public long romMemory;

        public long avilMemory;
    }

    //获得SD卡的各种信息，总容量大小和剩余容量大小等
    public static SDCardInfo getSDCardInfo() {
//        String sDcString = Environment.getExternalStorageState();
//
//        if (sDcString.equals(Environment.MEDIA_MOUNTED)) {
            File pathFile = Environment.getExternalStorageDirectory();
            try {
                android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());

                // 获取SDCard上BLOCK总数
                long nTotalBlocks = statfs.getBlockCount();

                // 获取SDCard上每个block的SIZE
                long nBlocSize = statfs.getBlockSize();

                // 获取可供程序使用的Block的数量
                long nAvailaBlock = statfs.getAvailableBlocks();

                // 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)
                long nFreeBlock = statfs.getFreeBlocks();

                SDCardInfo info = new SDCardInfo();
                // 计算SDCard 总容量大小MB
                info.total = nTotalBlocks * nBlocSize;

                // 计算 SDCard 剩余大小MB
                info.free = nAvailaBlock * nBlocSize;

                return info;
            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, e.toString());
            }
//        }
        return null;
    }

    //获得system ROM的各种信息，总容量大小和剩余容量大小等
    public static SystemInfo getRomMemory() {
        try {
            SystemInfo systemInfo = new SystemInfo();
            //Total rom memory
            systemInfo.romMemory = getTotalInternalMemorySize();

            //Available rom memory
            File path = Environment.getRootDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            systemInfo.avilMemory = blockSize * availableBlocks;
            return systemInfo;
        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, e.toString());
        }
        return null;
    }

    public static long getTotalInternalMemorySize() {
        File path = Environment.getRootDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    //获得USB的各种信息，总容量大小和剩余容量大小等
    public static UsbMemoryInfo getUsbMemoryInfo() {
        Context context = null;
        //android作为host时,usbManager获取是否有usb连接的管理器
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        //获取设备列表返回HashMap
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        if (deviceList.size() > 0) {
            String pathFile = "";
            for (int i = 0; i < deviceList.size(); i++){
                //usb设备路径
                pathFile = "/storage/usb"+i;
            }
            try {
                android.os.StatFs statfs = new android.os.StatFs(pathFile);

                // 获取SDCard上BLOCK总数
                long nTotalBlocks = statfs.getBlockCount();
                // 获取SDCard上每个block的SIZE
                long nBlocSize = statfs.getBlockSize();
                // 获取可供程序使用的Block的数量
                long nAvailaBlock = statfs.getAvailableBlocks();
                // 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)
                long nFreeBlock = statfs.getFreeBlocks();

                UsbMemoryInfo usbInfo = new UsbMemoryInfo();
                // 计算SDCard 总容量大小MB
                usbInfo.usbTotal = nTotalBlocks * nBlocSize;
                // 计算 SDCard 剩余大小MB
                usbInfo.usbFree = nAvailaBlock * nBlocSize;

                return usbInfo;
            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG, e.toString());
            }
        }
        return null;
    }

    //格式化毫秒格式的时间
    public static String formatDateString(Context context, long time) {
        DateFormat dateFormat = android.text.format.DateFormat
                .getDateFormat(context);
        DateFormat timeFormat = android.text.format.DateFormat
                .getTimeFormat(context);
        Date date = new Date(time);
        return dateFormat.format(date) + " " + timeFormat.format(date);
    }

    //對sDocMimeTypesSet查詢拼接條件是
    public static HashSet<String> sDocMimeTypesSet = new HashSet<String>() {
        {
            add("text/plain");
            add("text/plain");
            add("application/pdf");
            add("application/msword");
            add("application/vnd.ms-excel");
            add("application/vnd.ms-excel");
        }
    };
    //对于压缩文件（Zip）的查詢條件是：
    public static String sZipFileMimeType = "application/zip";

    public static int CATEGORY_TAB_INDEX = 0;
    public static int SDCARD_TAB_INDEX = 1;
}
