package com.sisindia.ai.mtrainer.android.features.topicslist;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.TrainingTopicsDataModel;

import java.util.ArrayList;


public class TrainingTopicsModel{

    public ArrayList<TrainingTopicsDataModel> getData() {
        return this.data; }
    public void setData(ArrayList<TrainingTopicsDataModel> data) {
        this.data = data; }

    @SerializedName("Data")
    ArrayList<TrainingTopicsDataModel> data;

    public int getStatusCode() {
        return this.statusCode; }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode; }

    @SerializedName("statusCode")
    int statusCode;

    public String getStatusMessage() {
        return this.statusMessage; }
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage; }

    @SerializedName("statusMessage")
    String statusMessage;
}




