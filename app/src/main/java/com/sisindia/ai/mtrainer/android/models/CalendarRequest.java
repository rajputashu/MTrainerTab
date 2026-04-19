package com.sisindia.ai.mtrainer.android.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class CalendarRequest {


    @SerializedName("TrainerId")
    public  String trainerId;

    @SerializedName("From")
    public  String from;

    @SerializedName("To")
    public  String to;


         public  CalendarRequest(){

                }

    public  CalendarRequest(String startTime, String endTime,String trainerId) {
        this.from=startTime;
        this.to=endTime;
        this.trainerId=trainerId;

    }
/*
    public CalendarRequest(org.threeten.bp.LocalDate sDate, org.threeten.bp.LocalDate eDate) {
        this.from=sDate;
        this.to=eDate;
    }*/
}
