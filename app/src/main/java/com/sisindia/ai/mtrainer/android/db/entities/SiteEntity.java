package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "site_table")
public class SiteEntity {

    @PrimaryKey
    @SerializedName("id")
    public int id;

    @SerializedName("siteName")
    public String siteName;

    @SerializedName("SiteAddress")
    public String siteAddress;

}