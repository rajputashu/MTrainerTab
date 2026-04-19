package com.sisindia.ai.mtrainer.android.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class MyUnitsResponse extends BaseApiResponse {

    @SerializedName("BodyData")
    public List<MyUnitsDetailList> BodyLists;

    @SerializedName("HeaderData")
    public List<MyUnitsDetailList> headerLists;
    public MyUnitsResponse(){

    }


    @Parcel
    @Entity(tableName = "my_units_table")
    public  static class MyUnitsDetailList {

       /* @SerializedName("id")
        @PrimaryKey
        @NonNull*/
       @PrimaryKey(autoGenerate = true)
       @SerializedName("Id")
        public int sNo;

        @SerializedName("ActualStartDateTime")
        public  String actualStartDateTime;
        @SerializedName("ActualEndDateTime")
        public  String actualEndDateTime;
        @SerializedName("GaurdTrained")
        public  int gaurdTrained;
        @SerializedName("AssesmentDone")
        public  int assesmentDone;
        @SerializedName("NoOfTopics")
        public  int noOfTopics;

        @SerializedName("SiteName")
        public  String siteName;
        @SerializedName("NoOfTrainingDone")
        public  int noOfTrainingDone;
        @SerializedName("NoOfGaurdTrained")
        public  int nnoOfGaurdTrained;


        public int getsNo() {
            return sNo;
        }

        public void setsNo(int sNo) {
            this.sNo = sNo;
        }

        public String getActualStartDateTime() {
            return actualStartDateTime;
        }

        public void setActualStartDateTime(String actualStartDateTime) {
            this.actualStartDateTime = actualStartDateTime;
        }

        public String getActualEndDateTime() {
            return actualEndDateTime;
        }

        public void setActualEndDateTime(String actualEndDateTime) {
            this.actualEndDateTime = actualEndDateTime;
        }

        public int getGaurdTrained() {
            return gaurdTrained;
        }

        public void setGaurdTrained(int gaurdTrained) {
            this.gaurdTrained = gaurdTrained;
        }

        public int getAssesmentDone() {
            return assesmentDone;
        }

        public void setAssesmentDone(int assesmentDone) {
            this.assesmentDone = assesmentDone;
        }

        public int getNoOfTopics() {
            return noOfTopics;
        }

        public void setNoOfTopics(int noOfTopics) {
            this.noOfTopics = noOfTopics;
        }

        public String getSiteName() {
            return siteName;
        }

        public void setSiteName(String siteName) {
            this.siteName = siteName;
        }

        public int getNnoOfGaurdTrained() {
            return nnoOfGaurdTrained;
        }

        public void setNnoOfGaurdTrained(int nnoOfGaurdTrained) {
            this.nnoOfGaurdTrained = nnoOfGaurdTrained;
        }

        public int getNoOfTrainingDone() {
            return noOfTrainingDone;
        }

        public void setNoOfTrainingDone(int noOfTrainingDone) {
            this.noOfTrainingDone = noOfTrainingDone;
        }



    }
}
