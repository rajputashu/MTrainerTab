package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

public class DesignSpiResponse extends BaseApiResponse {
    @SerializedName("Data")

    public List<DesignSpiData> designSpiData;

    public DesignSpiResponse() {
    }

//indices = [Index(value = ["accountId","dvrId"], unique = true)]
    @Parcel
    @Entity(tableName = "design_spi_table")
    public static class DesignSpiData {

        @PrimaryKey(autoGenerate = true)
        @SerializedName("id")
        public int id;
        @SerializedName("SpiId")
        public int spiId;

        @SerializedName("PostId")
        public int postId;

        @SerializedName("PostName")
        public String postName;
        @SerializedName("ImagesList")
        public String imagesList;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @SerializedName("Status")
        public String status;

/*

        @SerializedName("Image1")
        public String image1;
        @SerializedName("Image2")
        public String image2;
        @SerializedName("Image2")
        public String image3;
*/


        public DesignSpiData(){

        }
    }
}
