package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "region_data")
public class RegionData {

    @SerializedName("RegionId")
    @PrimaryKey
    @NonNull
    public int regionId;

    @SerializedName("RegionCode")
    public String regionCode;

    @SerializedName("RegionName")
    public String regionName;


    public RegionData() {
    }

}
