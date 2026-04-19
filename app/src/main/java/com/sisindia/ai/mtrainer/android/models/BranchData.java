package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;


@Parcel
@Entity(tableName = "branch_data")
public class BranchData {

    @SerializedName("BranchId")
    @PrimaryKey
    @NonNull
    public int branchId;

    @SerializedName("BranchCode")
    public String branchCode;

    @SerializedName("RegionId")
    public int regionId;

    @SerializedName("BranchName")
    public String branchName;


    public BranchData() {
    }

}








