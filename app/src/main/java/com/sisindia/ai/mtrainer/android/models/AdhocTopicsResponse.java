package com.sisindia.ai.mtrainer.android.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;
@Parcel

public class AdhocTopicsResponse extends BaseApiResponse {

        @SerializedName("Data")
        public List<AdhocTopics> adhocTopicsResponses;

        public AdhocTopicsResponse() {

        }

        @Parcel
        @Entity(tableName = "adhoc_topics_table")
        public static class AdhocTopics {

            @PrimaryKey(autoGenerate = true)
            @SerializedName("Id")
            public int Id;

            @SerializedName("RotaId")
            public int rotaId;
            @SerializedName("Name")
            public String topicName;


            public int getId() {
                return Id;
            }

            public void setId(int id) {
                Id = id;
            }

            public int getRotaId() {
                return rotaId;
            }

            public void setRotaId(int rotaId) {
                this.rotaId = rotaId;
            }

            public String getTopicName() {
                return topicName;
            }

            public void setTopicName(String topicName) {
                this.topicName = topicName;
            }



           public boolean isselected;

            public boolean isIsselected() {
                return isselected;
            }

            public void setIsselected(boolean isselected) {
                this.isselected = isselected;
            }


        }
}
