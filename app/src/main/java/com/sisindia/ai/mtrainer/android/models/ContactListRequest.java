package com.sisindia.ai.mtrainer.android.models;


import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class ContactListRequest {

    @SerializedName("UpdatedDate")
    public String updatedDate;

    @SerializedName("UserId")
    public String userId;
}
