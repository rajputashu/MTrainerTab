package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class SpiTableDetailsRequest {
    @SerializedName("TrainerId")
    int trainerId;
    @SerializedName("CompanyId")
    int companyId;
    @SerializedName("BranchId")
    int branchId;

    public SpiTableDetailsRequest(){

    }
    public SpiTableDetailsRequest(int trainerId, int companyId, int branchId ){
        this.trainerId=trainerId;
        this.companyId=companyId;
        this.branchId=branchId;
    }

}
