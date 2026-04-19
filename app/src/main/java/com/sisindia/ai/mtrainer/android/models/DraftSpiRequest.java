package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class DraftSpiRequest {

    @SerializedName("CustomerId")
    String customerId;
    @SerializedName("TrainerId")
    int trainerId;
    @SerializedName("BranchId")
    String branchId;
    @SerializedName("CompanyId")
    String companyId;
    @SerializedName("TypeId")
    String typeId;
    @SerializedName("SiteId")
    String siteId;
}
