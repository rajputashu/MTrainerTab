package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "draft_spi_photos_table")
public class DraftSpiPhotoEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int postId;
    public int spiId;
    public String imageUrl;
    public int position;
    public int branchid;
    public int customerid;
    public int unitId;
    public int status;
    public String uniqueId ;
    public int viewposition ;
    public int isSynced = 0;
   // public String uniqueId = spiId + "_" + postId;
    public int isCompress = 0;
}
