package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_feedback_table")
public class SavedFeedback {

    @PrimaryKey
    @NonNull
    public String rotaId;

    public String name = "NA";
    public float rating = 0.0f;

    public String clientId = "NA";

    public String emailId = "NA";

    public String clientAvailable = "false";

    public String clientNotAvailableReason = "NA";

    public String clientMobNumber = "NA";

    public String clientOtpVerify = "false";
    public String remarks = "NA";
}
