package com.sisindia.ai.mtrainer.android.models.spi;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;

import org.parceler.Parcel;

import java.util.List;

public class MountedResponse extends BaseApiResponse {

    @SerializedName("Data")
    public List<MountedData> mountedData;

    public MountedResponse() {
    }


    @Parcel
    @Entity(tableName = "mounted_table")
    public static class MountedData {

        @PrimaryKey(autoGenerate = true)
        @SerializedName("id")
        public int id;

        @SerializedName("PostId")
        public int postId;

        @SerializedName("PostName")
        public String postName;


        @SerializedName("KeyId")
        public int keyid;
        public String uniqueId;

        @SerializedName("Image1")
        public String image1;

        public MountedData(){

        }
    }
}
