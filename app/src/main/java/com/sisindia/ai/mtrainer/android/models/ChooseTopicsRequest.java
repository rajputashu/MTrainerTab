package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class ChooseTopicsRequest {


    @SerializedName("CompanyId")
    public String CompanyId;



    public ChooseTopicsRequest() {
    }

    public ChooseTopicsRequest(String companyId) {
        this.CompanyId = companyId;

    }
}
