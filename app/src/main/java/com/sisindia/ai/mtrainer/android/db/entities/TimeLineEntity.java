package com.sisindia.ai.mtrainer.android.db.entities;

import androidx.lifecycle.LiveData;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
@Entity(tableName = "timeline_table")

public class TimeLineEntity  {

        @PrimaryKey(autoGenerate =true)
        @SerializedName("ID")
        public int id;

        @SerializedName("Time")
        public String time;

        @SerializedName("DutyOn")
        public String dutyon;

        @SerializedName("KmMeter")
        public String kmmeter;

        public int getEmployeeId() {
            return id;
        }

        public void setEmployeeId(int employeeId) {
            this.id = employeeId;
        }

        public String getDutyon() {
            return dutyon;
        }

        public void setDutyon(String dutyon) {
            this.dutyon = dutyon;
        }

        public String getKmmeter() {
            return kmmeter;
        }

        public void setKmmeter(String kmmeter) {
            this.kmmeter = kmmeter;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }


}