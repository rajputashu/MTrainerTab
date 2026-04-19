package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "location_details")
public class LocationDetailsEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String dateTime;
    public double latitude;
    public double longitude;
    public int status;
    public long pairingToken;
}