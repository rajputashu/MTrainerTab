package com.sisindia.ai.mtrainer.android.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class CalendarResponse extends BaseApiResponse {

    @SerializedName("Data")
    public List<CalendarRotaResponse> calendarRotaResponses;

    public CalendarResponse() {

    }

    @Parcel
    @Entity(tableName = "calendar_rota_table")
    public static class CalendarRotaResponse {

        @PrimaryKey(autoGenerate = true)
        @SerializedName("SNo")
        public int sNo;

        @SerializedName("TrainingId")
        public int trainingId;

        @SerializedName("Branch")
        public String branch;

        @SerializedName("Site")
        public String site;

        @SerializedName("EstimatedStartDateTime")
        public String estimatedStartDate;

        @SerializedName("EstimatedEndDateTime")
        public String EstimatedEndDate;

        @SerializedName("ActualStartDateTime")
        public String actualStartTime;

        @SerializedName("ActualEndDateTime")
        public String actualEndtime;

        @SerializedName("Status")
        public String status;

        @SerializedName("MailSendAction")
        public String mailSendAction;

        /*public int getsNo() {
            return sNo;
        }

        public void setsNo(int sNo) {
            this.sNo = sNo;
        }*/

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public String getEstimatedStartDate() {
            return estimatedStartDate;
        }

        public void setEstimatedStartDate(String estimatedStartDate) {
            this.estimatedStartDate = estimatedStartDate;
        }

        public String getEstimatedEndDate() {
            return EstimatedEndDate;
        }

        public void setEstimatedEndDate(String estimatedEndDate) {
            EstimatedEndDate = estimatedEndDate;
        }

        public String getActualStartTime() {
            return actualStartTime;
        }

        public void setActualStartTime(String actualStartTime) {
            this.actualStartTime = actualStartTime;
        }

        public String getActualEndtime() {
            return actualEndtime;
        }

        public void setActualEndtime(String actualEndtime) {
            this.actualEndtime = actualEndtime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
