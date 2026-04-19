package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Adhoc_saved_topics_table")
public class AdhocSavedTopics {

        @PrimaryKey(autoGenerate = true)
        public int id;

        public String getTopicName() {
                return topicName;
        }

        public void setTopicName(String topicName) {
                this.topicName = topicName;
        }

        public int getRotaId() {
                return rotaId;
        }

        public void setRotaId(int rotaId) {
                this.rotaId = rotaId;
        }

        public String topicName;

        public int rotaId;
}
