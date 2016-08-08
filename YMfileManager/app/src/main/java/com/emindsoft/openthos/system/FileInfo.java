package com.emindsoft.openthos.system;

/**
 * 文件信息对象
 * Created by zuojj on 16-5-12.
 */
public class FileInfo {

    public String fileName;

    public String filePath;

    public long fileSize;

    public boolean IsDir;

    public int Count;

    public long ModifiedDate;

    public boolean Selected;

    public boolean canRead;

    public boolean canWrite;

    public boolean isHidden;

    public long dbId; // id in the database, if is from database
}
