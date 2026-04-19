package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

public class SitePostRequest {
    @SerializedName("UserId")
    public String userId;
    @SerializedName("UpdatedDate")
    public String upDatedDate;

    public SitePostRequest(String userid, String currentdate) {
        this.userId=userid;
        this.upDatedDate=currentdate;
    }
}
