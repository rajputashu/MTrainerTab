package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "video_click_table")
public class VideoClick {
    @NonNull
    @PrimaryKey
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("sequence")
    public String sequence;

    @SerializedName("ModuleNo")
    public String ModuleNo;

    @SerializedName("SubModuleNo")
    public String SubModuleNo;

    @SerializedName("VideoNo")
    public String VideoNo;

    @SerializedName("HindiName")
    public String HindiName;

    @SerializedName("EnglishName")
    public String EnglishName;

    @SerializedName("Path")
    public String Path;

    @SerializedName("PPTPath")
    public String PPTPath;


    @SerializedName("Duration")
    public String Duration;

    @SerializedName("videoId")
    public String videoId;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public VideoClick()
    {

    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getPPTPath() {
        return PPTPath;
    }

    public void setPPTPath(String PPTPath) {
        this.PPTPath = PPTPath;
    }

    public String getModuleNo() {
        return ModuleNo;
    }

    public void setModuleNo(String moduleNo) {
        ModuleNo = moduleNo;
    }

    public String getSubModuleNo() {
        return SubModuleNo;
    }

    public void setSubModuleNo(String subModuleNo) {
        SubModuleNo = subModuleNo;
    }

    public String getVideoNo() {
        return VideoNo;
    }

    public void setVideoNo(String videoNo) {
        VideoNo = videoNo;
    }

    public String getHindiName() {
        return HindiName;
    }

    public void setHindiName(String hindiName) {
        HindiName = hindiName;
    }

    public String getEnglishName() {
        return EnglishName;
    }

    public void setEnglishName(String englishName) {
        EnglishName = englishName;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
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

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
}
