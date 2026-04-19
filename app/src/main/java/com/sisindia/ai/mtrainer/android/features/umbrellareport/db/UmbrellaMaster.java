package com.sisindia.ai.mtrainer.android.features.umbrellareport.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.droidcommons.preference.Prefs;
import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;

@Entity(tableName = "umbrella_master_table")
public class UmbrellaMaster {
    @PrimaryKey
    @NonNull
    public String umbrellaId;
    public int umbrellaCount;
    public int branchId;
    public int siteId;
}