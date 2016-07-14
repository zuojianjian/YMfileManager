package com.ygcompany.zuojj.ymfilemanager.bean;

/**
 * 设备图片信息
 * Created by zuojj on 16-5-10.
 */
public class ImageBean {
    /**
     * 文件夹的第一张图片路径
     */
    private String topImagePath;
    /**
     * 文件夹名
     */
    private String folderName;
    /**
     * 文件夹中的图片数
     */
    private int imageCounts;
    /**
     * 图片大小
     */
    private long iconSize;

    public long getIconSize() {
        return iconSize;
    }

    public void setIconSize(long iconSize) {
        this.iconSize = iconSize;
    }

    public String getTopImagePath() {
        return topImagePath;
    }
    public void setTopImagePath(String topImagePath) {
        this.topImagePath = topImagePath;
    }
    public String getFolderName() {
        return folderName;
    }
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    public int getImageCounts() {
        return imageCounts;
    }
    public void setImageCounts(int imageCounts) {
        this.imageCounts = imageCounts;
    }
}
