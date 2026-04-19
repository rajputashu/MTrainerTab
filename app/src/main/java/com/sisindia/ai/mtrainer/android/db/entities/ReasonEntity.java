package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "reason_table")
public class ReasonEntity {

    @PrimaryKey
    @SerializedName("id")
    public int id;
    @SerializedName("ReasonName")
    public String reasonName;

    @SerializedName("displayName")
    public String displayName;
    public ReasonEntity(){

    }


}
