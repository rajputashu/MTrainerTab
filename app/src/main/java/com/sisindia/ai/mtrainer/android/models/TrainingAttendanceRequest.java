package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class TrainingAttendanceRequest {

    /*
       @SerializedName("EmployeeId")
        public String employeeId;

        @SerializedName("Status")
        public String status;
        @SerializedName("Latitude")
        public String latitude;
        @SerializedName("Longitude")
        public String longitude;
    */
    @SerializedName("TrainerId")
    public int trainerId;

    @SerializedName("PageNumber")
    public int currentPage;

    @SerializedName("Range")
    public int range;

    public TrainingAttendanceRequest() {
    }

   /* public TrainingAttendanceRequest(String empId, String status, String latitude, String longitude) {
        this.employeeId = empId;
        this.status=status;
        this.latitude=latitude;
        this.longitude=longitude;
    }*/
}
