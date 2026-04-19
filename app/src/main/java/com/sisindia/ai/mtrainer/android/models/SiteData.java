package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.io.Serializable;

@Parcel
@Entity(tableName = "site_data")
public class SiteData implements Serializable {

    @SerializedName("SiteId")
    @PrimaryKey
    @NonNull
    public int siteId;

    @SerializedName("SiteCode")
    public String siteCode;

    @SerializedName("SiteName")
    public String siteName;

    @SerializedName("BranchId")
    public int branchId;
    @SerializedName("EmpCount")
    public int empCount;

    @SerializedName("Address")
    public String siteAddress;

    @Override
    public String toString() {
        return siteName + "("+ siteCode+ ")";
    }

}