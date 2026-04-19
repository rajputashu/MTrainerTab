package com.sisindia.ai.mtrainer.android.models.sync;

import com.google.gson.annotations.SerializedName;

public class AssessmentReport {
    @SerializedName("videoID")
    private String id;
    @SerializedName("empId")
    private String empId;


    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpId() {
        return empId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}