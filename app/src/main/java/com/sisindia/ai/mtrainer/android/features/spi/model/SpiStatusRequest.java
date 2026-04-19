package com.sisindia.ai.mtrainer.android.features.spi.model;

import com.google.gson.annotations.SerializedName;

public class SpiStatusRequest {
/*
    @SerializedName("CustomerId")
    int customerId;
    @SerializedName("TrainerId")
    int trainerId;
    @SerializedName("BranchId")
    int branchId;
    @SerializedName("CompanyId")
    String companyId;
    @SerializedName("TypeId")
    int typeId;
    @SerializedName("SiteId")
    int unitId;
    @SerializedName("status")
    String status;
    @SerializedName("statusId")
    int statusId;

    public SpiStatusRequest(int customerId,int trainerId,int branchId,String companyId,int typeId,int unitId,String status,int statusid){
        this.customerId=customerId;
        this.trainerId=trainerId;
        this.branchId=branchId;
        this.companyId=companyId;
        this.typeId=typeId;
        this.unitId=unitId;
        this.status=status;
        this.statusId=statusid;
    }*/
  @SerializedName("KeyId")
   int keyId;
    @SerializedName("StatusId")
    int statusId;
    public SpiStatusRequest(int keyid,int statusId){
        this.keyId=keyid;
        this.statusId=statusId;
    }

}
