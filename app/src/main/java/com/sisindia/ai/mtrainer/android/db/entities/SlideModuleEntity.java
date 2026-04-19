package com.sisindia.ai.mtrainer.android.db.entities;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.List;


public class SlideModuleEntity extends BaseApiResponse {
    @SerializedName("Data")
    public List<SlideModuleEntity> slideModuleEntities;

    public SlideModuleEntity() {
    }

    @Parcel
    @Entity(tableName = "slide_module_table")
    public static class SlideModuleEntityList implements Serializable {
        @NonNull
        @PrimaryKey(autoGenerate = true)
        @SerializedName("id")
        public int id;

        @SerializedName("name")
        public String name;
        @SerializedName("sequence")
        public String sequence;

        @SerializedName("ModuleNo")
        public String moduleNo;

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

        @Ignore
        public SlideModuleEntityList() {

        }

        public SlideModuleEntityList(int id, String name) {
            this.name = name;
            this.id = id;
        }

        public SlideModuleEntityList(String ModuleNo, String SubModuleNo, String Path) {
            this.moduleNo = ModuleNo;
            this.SubModuleNo = SubModuleNo;
            this.Path = Path;
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
            return moduleNo;
        }

        public void setModuleNo(String moduleNo) {
            this.moduleNo = moduleNo;
        }

        public String getSubModuleNo() {
            return SubModuleNo;
        }

        public void setSubModuleNo(String subModuleNo) {
            this.SubModuleNo = subModuleNo;
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
            this.Path = path;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
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

}