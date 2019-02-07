package com.jediupc.helloandroid.model;

public class AudioModel {
    public String path;
    public long duration;

    public String getName() {
        return path.split("/")[1].split(".")[0];
    }

    public int getTime() {
        return Integer.valueOf(getName());
    }
}
