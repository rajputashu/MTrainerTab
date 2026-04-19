package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class SpiBranchRequest {

    @SerializedName("TrainerId")
    int trainerId;
    @SerializedName("CompanyId")
    int companyId;

    public  SpiBranchRequest(){

    }
    public SpiBranchRequest(int trainerid,int comaonyid){
        this.trainerId=trainerid;
        this.companyId=comaonyid;
    }

}
