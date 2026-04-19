package com.sisindia.ai.mtrainer.android.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class TrainingFinalSubmitResponse extends  BaseApiResponse {


    @SerializedName("Data")
    public TrainingSubmitResponse trainingSubmitResponses;

    public TrainingFinalSubmitResponse() {

    }

    @Parcel
    @Entity(tableName = "training_final_submit_table")
    public static class TrainingSubmitResponse {

        @PrimaryKey
        @SerializedName("RotaId")
        public int rotaid;

        @SerializedName("ActualEndTime")
        public String taskEndTime = "NA";

        public String remarks = "";

        public String startTime;

        public String startLat = "NA";
        public String startLong = "NA";
        public String endLong = "NA";
        public String endLat = "NA";

        public String getTaskEndTime() {
            return taskEndTime;
        }

        public void setTaskEndTime(String taskEndTime) {
            this.taskEndTime = taskEndTime;
        }

        public int getRotaid() {
            return rotaid;
        }

        public void setRotaid(int rotaid) {
            this.rotaid = rotaid;
        }
    }
}
