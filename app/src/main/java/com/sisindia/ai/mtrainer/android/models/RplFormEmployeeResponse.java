package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class RplFormEmployeeResponse  {

    @SerializedName("EmployeeDetails")
    private RplFormEmpDetails employeeDetails;

    @SerializedName("statusMessage")
    private String statusMessage;

    @SerializedName("statusCode")
    private int statusCode;

    public RplFormEmpDetails getEmployeeDetails() {
        return employeeDetails;
    }

    public void setEmployeeDetails(RplFormEmpDetails employeeDetails) {
        this.employeeDetails = employeeDetails;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
