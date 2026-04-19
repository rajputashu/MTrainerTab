package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class MyUnitsRequest {

    @SerializedName("From")
    public  String from;

    @SerializedName("To")
    public  String to;

    @SerializedName("TrainerId")
    public  String trainerId;

    @SerializedName("BranchId")
    public  String branchId;

    @SerializedName("SiteId")
    public  String siteId;
    public  MyUnitsRequest(){

       }

public  MyUnitsRequest(String from,String to,String trainerid,String branchid,String siteid){

    this.from=from;
    this.to=to;
    this.trainerId=trainerid;
    this.branchId=branchid;
    this.siteId=siteid;
}
}
