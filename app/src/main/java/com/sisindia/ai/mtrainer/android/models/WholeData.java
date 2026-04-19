package com.sisindia.ai.mtrainer.android.models;

public class WholeData {
    private String id,name,path,duration;
    private boolean issubmodule;
    private String videoId;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isIssubmodule() {
        return issubmodule;
    }

    public void setIssubmodule(boolean issubmodule) {
        this.issubmodule = issubmodule;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}