package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class RplFormUploadRequest {
    @SerializedName("RegistrationNo")
    public String registrationNo;
    @SerializedName("AadhaarFrontImageId")
    public String aadhaarfrontimageId;
    @SerializedName("AadhaarBackImageId")
    public String aadhaarbackimageId;
    @SerializedName("UnitId")
    public String unitId;
    @SerializedName("BranchId")
    public String branchid;

    @SerializedName("MobileNo")
    public String mobileno;

    @SerializedName("EducationQualification")
    public String educationqualification;

    @SerializedName("rotaid")
    public String rotaid;
    @SerializedName("Category")
    public String category;

    @SerializedName("SyncDateTime")
    public String syncdatetime;
    @SerializedName("Name")
    public String name;

}
