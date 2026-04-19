package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "spi_branch_data")
public class SpiBranchData {

    @SerializedName("BranchId")
    @PrimaryKey
    @NonNull
    public int branchId;

    @SerializedName("TrainerId")
    public String trainerID;
    @SerializedName("RegionId")
    public int regionId;

    @SerializedName("BranchName")
    public String branchName;

     public SpiBranchData(){

     }


}
