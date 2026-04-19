package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "cc_table")
public class SavedClientReportCc {
    // viewId + rotaId
    @PrimaryKey
    @NonNull
    public String id;
    public int rotaId;
    public String cc;
}
