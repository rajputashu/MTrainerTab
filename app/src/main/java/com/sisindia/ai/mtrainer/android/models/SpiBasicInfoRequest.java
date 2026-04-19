package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class SpiBasicInfoRequest {

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
    @SerializedName("AllPostsVisited")
    String allPostsVisited;
    @SerializedName("PostCount")
    String postCount;
    @SerializedName("AllPostsWereAvailableInIOPS")
    String allPostsWereAvailableInIOPS;
    @SerializedName("MissingPostsCreatedbyTrainer")
    String missingPostsCreatedbyTrainer;
    @SerializedName("VerifiedPostCountWithAreaOfficer")
    String verifiedPostCountWithAreaOfficer;
   /* @SerializedName("PostName")
    String postName;*/

    public SpiBasicInfoRequest(int customerId,int trainerId,int branchId,String companyId,int typeId,int unitId,
                               String allPostsVisited,String postCount,String allPostsWereAvailableInIOPS,String missingPostsCreatedbyTrainer,
                               String verifiedPostCountWithAreaOfficer)
    {
        this.customerId=customerId;
        this.trainerId=trainerId;
        this.branchId=branchId;
        this.companyId=companyId;
        this.typeId=typeId;
        this.unitId=unitId;
        this.allPostsVisited=allPostsVisited;
        this.postCount=postCount;
        this.allPostsWereAvailableInIOPS=allPostsWereAvailableInIOPS;
        this.missingPostsCreatedbyTrainer=missingPostsCreatedbyTrainer;
        this.verifiedPostCountWithAreaOfficer=verifiedPostCountWithAreaOfficer;
       // this.postName=postname;

    }


}
