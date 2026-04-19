package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class DashboardRequest {

    @SerializedName("EmployeeId")
    public String employeeId;

    public DashboardRequest() {
    }

    public DashboardRequest(String empId) {
        this.employeeId = empId;
    }
}
