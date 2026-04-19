package com.sisindia.ai.mtrainer.android.features.umbrellareport.db;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.droidcommons.preference.Prefs;
import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "umbrella_post_table")
public class UmbrellaPost {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @SerializedName("ID")
    public int postId = -1;

    @SerializedName("Name")
    public String postName;

    @SerializedName("SiteID")
    public int siteId;

    public String adHocPostId = "";

    public String umbrellaId = Prefs.getString(PrefsConstants.CURRENT_UMBRELLA_ID, "");
    public String ImageId = "";
    public String imagePath = "";
    public int canSync = 0; // 0 -> InProgress ;  1 -> Can Sync
    public int hasUrl = 0; // 0 -> has local url ; 1 -> Has Url
    public int isAdhoc = 0; // 0 -> Not Adhoc ; 1 -> Adhoc
    public int keyId = -1; // 0 -> Not Adhoc ; 1 -> Adhoc
    public int isCompress = 0;// 0 -> Not Adhoc ; 1 -> Adhoc
    @ColumnInfo(defaultValue = " ")
    public String waterMark="";
    @ColumnInfo(defaultValue = "0")
    public int isMark = 0;
}
