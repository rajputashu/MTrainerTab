package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.List;

public class MLCVideoDetailsModel extends BaseApiResponse {
    @SerializedName("Data")
    public List<MlcVideoDetailsModellist> mlcvideoDetailsModellists;

    public MLCVideoDetailsModel() {

    }

    @Parcel
    @Entity(tableName = "mlc_video_model_table")
    public static class MlcVideoDetailsModellist implements Serializable {
        @NonNull
        @PrimaryKey(autoGenerate = true)
        @SerializedName("id")
        public int id;

        @SerializedName("videoId")
        public String videoId;

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


        public MlcVideoDetailsModellist() {

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




        public String getModuleNo() {
            return ModuleNo;
        }

        public void setModuleNo(String moduleNo) {
            ModuleNo = moduleNo;
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



        public String getSequence() {
            return sequence;
        }

        public void setSequence(String sequence) {
            this.sequence = sequence;
        }
    }
}