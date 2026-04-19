package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class CsatOycCountRequest {
    @SerializedName("BranchId")
    int branchId;
    @SerializedName("CompanyId")
    int companyId;
    public  CsatOycCountRequest(){

    }
    public CsatOycCountRequest(int branchId,int companyId){
        this.branchId=branchId;
        this.companyId=companyId;
    }

}
