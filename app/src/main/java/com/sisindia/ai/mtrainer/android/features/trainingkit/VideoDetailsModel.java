package com.sisindia.ai.mtrainer.android.features.trainingkit;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.List;

public class VideoDetailsModel extends BaseApiResponse {
    @SerializedName("Data")
    public List<VideoDetailsModellist> videoDetailsModellists;

    public VideoDetailsModel() {

    }

    @Parcel
    @Entity(tableName = "video_model_table")
    public static class VideoDetailsModellist implements Serializable {
        @NonNull
        @PrimaryKey(autoGenerate = true)
        @SerializedName("id")
        public int id;

        @SerializedName("SubModuleNo")
        public String SubModuleNo;

        @SerializedName("videoId")
        public String videoId;

        @SerializedName("name")
        public String name;

        @SerializedName("HindiName")
        public String HindiName;


        @SerializedName("sequence")
        public String sequence;

        @SerializedName("Path")
        public String Path;


        @SerializedName("Duration")
        public String Duration;


        @SerializedName("ModuleNo")
        public String ModuleNo;

        @SerializedName("VideoNo")
        public String VideoNo;

        @SerializedName("EnglishName")
        public String EnglishName;

        @SerializedName("PPTPath")
        public String PPTPath;

        public boolean isIssubmodule() {
            return issubmodule;
        }

        public void setIssubmodule(boolean issubmodule) {
            this.issubmodule = issubmodule;
        }

        private boolean issubmodule;

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }



        @Ignore
        public VideoDetailsModellist(String moduleNo, String name) {
            this.name = name;
            this.ModuleNo = moduleNo;
        }


        public VideoDetailsModellist() {

        }
/*

    public VideoDetailsModel(String ModuleNo,String SubModuleNo,String Path) {
        this.ModuleNo = ModuleNo;
        this.SubModuleNo= SubModuleNo;
        this.Path= Path;
    }
*/

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