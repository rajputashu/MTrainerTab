package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class AssessmentModel {

    @SerializedName("CompanyId")
    String companyId;

    public AssessmentModel(String companyId) {
        this.companyId = companyId;
    }

    public AssessmentModel() {

    }

}
