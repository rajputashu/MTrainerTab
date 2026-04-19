package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class CancelTrainingRequest {


    @SerializedName("TrainingId")
    public String trainingId;

    @SerializedName("Status")
    public String status;
    @SerializedName("Reason")
    public String reason;

    @SerializedName("Latitude")
    public double latitude;

    @SerializedName("Longitude")
    public double longitude;
    @SerializedName("EmployeeId")
    public String employeeId;




    public CancelTrainingRequest() {
    }

    public CancelTrainingRequest(String trainingId,String empId, String status,String reason, double latitude, double longitude) {
        this.trainingId=trainingId;
        this.employeeId = empId;
        this.status=status;
        this.reason=reason;
        this.latitude=latitude;
        this.longitude=longitude;
    }


}
