package com.sisindia.ai.mtrainer.android.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_topics_table")
public class SavedTopic {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int topicId;

    public String topicName;

    public int rotaId;
}
