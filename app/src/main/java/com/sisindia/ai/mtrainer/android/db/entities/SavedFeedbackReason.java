package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "feedback_reason_table")
public class SavedFeedbackReason {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String rotaId;
    public int reasonId;
    public String reason;
}
