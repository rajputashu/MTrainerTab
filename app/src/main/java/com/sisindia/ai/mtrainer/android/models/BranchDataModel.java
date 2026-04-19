package com.sisindia.ai.mtrainer.android.models;

import androidx.room.Entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class BranchDataModel {

    @SerializedName("BranchId")
    public int branchId;

    @SerializedName("BranchName")
    public String branchName;

    public BranchDataModel() {
    }
}
