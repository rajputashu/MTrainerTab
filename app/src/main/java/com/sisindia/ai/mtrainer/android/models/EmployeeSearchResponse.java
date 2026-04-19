package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class EmployeeSearchResponse extends BaseApiResponse {
    @SerializedName("Data")
    public TrainingAttendanceResponse.AttendanceResponse attendanceResponse;
}
