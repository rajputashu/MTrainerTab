package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public
class BranchRegionRequest {

    @SerializedName("QType")
    String QType;
    @SerializedName("CompanyId")
    int companyId;
    @SerializedName("RegionId")
    int RegionId;
    @SerializedName("BranchId")
    int BranchId;
    @SerializedName("TrainerId")
    int trainerId;


    public BranchRegionRequest(String QType,int companyId,int RegionId,int BranchId, int trainerId) {
        this.QType = QType;
        this.companyId = companyId;
        this.RegionId = RegionId;
        this.BranchId = BranchId;
        this.trainerId = trainerId;
    }

    public BranchRegionRequest() {

    }
}


