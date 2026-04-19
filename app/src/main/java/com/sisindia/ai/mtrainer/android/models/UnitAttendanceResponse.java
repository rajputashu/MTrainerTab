package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

// TODO : Need to add Score

@Parcel
public class UnitAttendanceResponse extends BaseApiResponse {
    @SerializedName("Data")
    public List<TrainingAttendanceResponse.AttendanceResponse> attendanceResponses;

    @SerializedName("Score")

    public List<TrainingAttendanceResponse.ScoreMetric> scoreMetric;
    @SerializedName("TotalEmployees")
    public List<TrainingAttendanceResponse.EmployeeCount> employeeCount;

    @SerializedName("LastAccessTime")
    public String accessedTime;

    public UnitAttendanceResponse() {

    }
}
