package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "mounted_photo_table")
public class MountedSpiPhotoEntity {
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