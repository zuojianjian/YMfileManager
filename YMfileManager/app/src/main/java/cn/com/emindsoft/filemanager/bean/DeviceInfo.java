package cn.com.emindsoft.filemanager.bean;

/**
 * Usb设备信息
 * Created by zuojj on 16-6-13.
 */
public class DeviceInfo {

    public String deviceName;
    public String devicePath;

    public String getDevicePath() {
        return devicePath;
    }

    public void setDevicePath(String devicePath) {
        this.devicePath = devicePath;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
