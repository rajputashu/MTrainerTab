
package com.sisindia.ai.mtrainer.android.models;

        import androidx.annotation.NonNull;
        import androidx.room.ColumnInfo;
        import androidx.room.Entity;
        import androidx.room.PrimaryKey;

        import com.google.gson.annotations.SerializedName;

        import org.parceler.Parcel;

        import java.util.List;

public class SpiBasicInfoResponse extends  BaseApiResponse {


    @SerializedName("Data")
    public List<SpiBasicInfoDetailsData> spiBasicInfoDetailsData;

    public SpiBasicInfoResponse() {
    }


    @Parcel
    @Entity(tableName = "Spi_basic_info_table")
    public static class SpiBasicInfoDetailsData {
        @PrimaryKey(autoGenerate = true)
        @SerializedName("SNo")
        public int id;
        @SerializedName("KeyId")
        public int keyid;
        @SerializedName("PostId")
        public int postid;
       // public String uniqueId = keyid + "_" + postid;
        public String uniqueId;
        @SerializedName("PostName")
        public String postName;
        @SerializedName("StatusId")
        public int statusId;

        @ColumnInfo(defaultValue = "0")
        public int isDone = 0;

        public  void SpiBasicInfoDetailsData(){

        }

        @Override
        public String toString() {
            return "SpiBasicInfoDetailsData{" +
                    "id=" + id +
                    ", keyid=" + keyid +
                    ", postid=" + postid +
                    ", uniqueId='" + uniqueId + '\'' +
                    ", postName='" + postName + '\'' +
                    ", statusId=" + statusId +
                    ", isDone=" + isDone +
                    '}';
        }
    }

}


