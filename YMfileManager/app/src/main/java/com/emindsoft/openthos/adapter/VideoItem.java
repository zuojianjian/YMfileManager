package com.emindsoft.openthos.adapter;

/**
 * 视频实体类集合item
 * Created by zuojj on 16-5-18.
 */
public class VideoItem {
    private String name;
    private long size;
    private String data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "VideoItem{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", data='" + data + '\'' +
                '}';
    }
}

