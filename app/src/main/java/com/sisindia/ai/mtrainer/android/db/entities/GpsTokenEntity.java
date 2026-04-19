package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "gps_token_table")
public class GpsTokenEntity {
    @PrimaryKey
    public int id;
    public String pairingKey;
}
