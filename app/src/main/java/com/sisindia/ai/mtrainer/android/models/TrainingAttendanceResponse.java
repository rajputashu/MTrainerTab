package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

// TODO : Need to add Score

@Parcel
public class TrainingAttendanceResponse extends BaseApiResponse{


    @SerializedName("Data")
    public List<AttendanceResponse> attendanceResponses;

    @SerializedName("Score")
    public List<ScoreMetric> scoreMetric;

    @SerializedName("TotalEmployees")
    public List<EmployeeCount> employeeCount;

    public TrainingAttendanceResponse() {


    }

    @Entity(tableName = "master_attendance_name")
    @Parcel
    public static class AttendanceResponse {

        @SerializedName("ID")
        public int employeeId = -1;

        @SerializedName("Emptype")
        public String emptype;

        @PrimaryKey
        @NonNull
        @SerializedName("EmployeeCode")
        public String employeeCode;

        @SerializedName("EmployeeName")
        public String employeeName;

        @SerializedName("CurrSiteID")
        public String currSiteID;

        @SerializedName("SiteName")
        public String siteName;

        @SerializedName("CurrSitePostID")
        public String currSitePostID;

        @SerializedName("SiteCode")
        public String siteCode;

        @SerializedName("AttendanceTypeList")
        public String attendanceTypeList;

        @SerializedName("SitePostCode")
        public String sitePostCode;

        @SerializedName("SitePostName")
        public String sitePostName;

        @SerializedName("LastScore")
        public float score = -1f;

        @SerializedName("status")
        public int status;


        public int rotaId = -1;


        @Ignore
        public boolean isIsselected() {
            return isselected;
        }

        @Ignore
        public void setIsselected(boolean isselected) {
            this.isselected = isselected;
        }
        @Ignore
        public boolean isselected;

        @Ignore
        public String header;
    }

    @Parcel
    public static class ScoreMetric {
        @SerializedName("Score")
        public float score = -1f;
    }

    @Parcel
    public static class EmployeeCount {
        @SerializedName("TotalEmployee")
        public int employeeCount;

        @SerializedName("CurrentPageNumber")
        public int currentPageNumber;
    }
}