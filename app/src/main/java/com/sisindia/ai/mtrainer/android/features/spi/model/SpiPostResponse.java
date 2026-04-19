package com.sisindia.ai.mtrainer.android.features.spi.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;
import com.sisindia.ai.mtrainer.android.models.SpiBasicInfoResponse;

import org.parceler.Parcel;

import java.util.List;

public class SpiPostResponse extends BaseApiResponse {

    @SerializedName("Data")
    public List<SpiPostResponse.SpiPostdata> spiPostdata;

    public SpiPostResponse() {
    }


    @Parcel
    @Entity(tableName = "Spi_posts_table")
    public static class SpiPostdata {
        @PrimaryKey(autoGenerate = true)
        @SerializedName("SNo")
        public int id;
        @SerializedName("Id")
        public int postid;

        @SerializedName("PostName")
        public String postName;

        @ColumnInfo(defaultValue = "0")
        public int isCompleted = 0;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getPostid() {
            return postid;
        }

        public void setPostid(int postid) {
            this.postid = postid;
        }

        public String getPostName() {
            return postName;
        }

        public void setPostName(String postName) {
            this.postName = postName;
        }

        public  void SpiPostdata(){

        }



    }
}
