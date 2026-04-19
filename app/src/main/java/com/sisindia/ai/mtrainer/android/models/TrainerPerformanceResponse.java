package com.sisindia.ai.mtrainer.android.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel

public class TrainerPerformanceResponse extends BaseApiResponse {

    @SerializedName("Data")
    public List<PerformanceResponse> performance;

    public TrainerPerformanceResponse() {

    }

    @Parcel
    @Entity(tableName = "performance_table")
    public static class PerformanceResponse {

        @PrimaryKey
        @SerializedName("TypeId")
        public int typeId;

        @SerializedName("EmpName")
        public String empName;

        @SerializedName("RegNo")
        public String regNo;

        @SerializedName("TrainerId")
        public String trainerId;

        @SerializedName("NationalRank")
        public String nationalRank;

        @SerializedName("RegionalRank")
        public String regionalRank;

        @SerializedName("ActualScore")
        public String actualScore;

        @SerializedName("TargetScore")
        public String targetScore;

        @SerializedName("ActualUnitCoverd")
        public String actualUnitCoverd = "0";

        @SerializedName("TargetUnitCoverd")
        public String targetUnitCoverd = "0";

        @SerializedName("currentDate")
        public String currentDate = "NA";


        public int getTypeId() {
            return typeId;
        }

        public void setTypeId(int typeId) {
            this.typeId = typeId;
        }

        public String getEmpName() {
            return empName;
        }

        public void setEmpName(String empName) {
            this.empName = empName;
        }

        public String getRegNo() {
            return regNo;
        }

        public void setRegNo(String regNo) {
            this.regNo = regNo;
        }

        public String getTrainerId() {
            return trainerId;
        }

        public void setTrainerId(String trainerId) {
            this.trainerId = trainerId;
        }

        public String getNationalRank() {
            return nationalRank;
        }

        public void setNationalRank(String nationalRank) {
            this.nationalRank = nationalRank;
        }

        public String getRegionalRank() {
            return regionalRank;
        }

        public void setRegionalRank(String regionalRank) {
            this.regionalRank = regionalRank;
        }

        public String getActualScore() {
            return actualScore;
        }

        public void setActualScore(String actualScore) {
            this.actualScore = actualScore;
        }

        public String getTargetScore() {
            return targetScore;
        }

        public void setTargetScore(String targetScore) {
            this.targetScore = targetScore;
        }

        public String getActualUnitCoverd() {
            return actualUnitCoverd;
        }

        public void setActualUnitCoverd(String actualUnitCoverd) {
            this.actualUnitCoverd = actualUnitCoverd;
        }

        public String getTargetUnitCoverd() {
            return targetUnitCoverd;
        }

        public void setTargetUnitCoverd(String targetUnitCoverd) {
            this.targetUnitCoverd = targetUnitCoverd;
        }

        public String getActualGuardTrained() {
            return actualGuardTrained;
        }

        public void setActualGuardTrained(String actualGuardTrained) {
            this.actualGuardTrained = actualGuardTrained;
        }

        public String getTargetGuardTraind() {
            return targetGuardTraind;
        }

        public void setTargetGuardTraind(String targetGuardTraind) {
            this.targetGuardTraind = targetGuardTraind;
        }

        public String getActualTrainingCount() {
            return actualTrainingCount;
        }

        public void setActualTrainingCount(String actualTrainingCount) {
            this.actualTrainingCount = actualTrainingCount;
        }

        public String getTargetTrainingCount() {
            return targetTrainingCount;
        }

        public void setTargetTrainingCount(String targetTrainingCount) {
            this.targetTrainingCount = targetTrainingCount;
        }

        public String getActualFeedbackTaken() {
            return actualFeedbackTaken;
        }

        public void setActualFeedbackTaken(String actualFeedbackTaken) {
            this.actualFeedbackTaken = actualFeedbackTaken;
        }

        public String getTargetFeedbackTaken() {
            return targetFeedbackTaken;
        }

        public void setTargetFeedbackTaken(String targetFeedbackTaken) {
            this.targetFeedbackTaken = targetFeedbackTaken;
        }

        public String getActualAvgRating() {
            return actualAvgRating;
        }

        public void setActualAvgRating(String actualAvgRating) {
            this.actualAvgRating = actualAvgRating;
        }

        public String getActualTrainingTime() {
            return actualTrainingTime;
        }

        public void setActualTrainingTime(String actualTrainingTime) {
            this.actualTrainingTime = actualTrainingTime;
        }

        public String getTargetTrainingTime() {
            return targetTrainingTime;
        }

        public void setTargetTrainingTime(String targetTrainingTime) {
            this.targetTrainingTime = targetTrainingTime;
        }

        public int getPanIndiaRank() {
            return panIndiaRank;
        }

        public void setPanIndiaRank(int panIndiaRank) {
            this.panIndiaRank = panIndiaRank;
        }

        public int getcOMarks() {
            return cOMarks;
        }

        public void setcOMarks(int cOMarks) {
            this.cOMarks = cOMarks;
        }

        public String getCurrentDate() {
            return currentDate;
        }

        public void setCurrentDate(String currentDate) {
            this.currentDate = currentDate;
        }

        public int getTotalTrainer() {
            return totalTrainer;
        }

        public void setTotalTrainer(int totalTrainer) {
            this.totalTrainer = totalTrainer;
        }

        @SerializedName("ActualGuardTrained")
        public String actualGuardTrained = "0";

        @SerializedName("TargetGuardTraind")
        public String targetGuardTraind = "0";

        @SerializedName("ActualTrainingCount")
        public String actualTrainingCount = "0";

        @SerializedName("TargetTrainingCount")
        public String targetTrainingCount = "0";

        @SerializedName("ActualFeedbackTaken")
        public String actualFeedbackTaken = "0";

        @SerializedName("TargetFeedbackTaken")
        public String targetFeedbackTaken = "0";

        @SerializedName("ActualAvgRating")
        public String actualAvgRating = "0";

        @SerializedName("ActualTrainingTime")
        public String actualTrainingTime = "0";

        @SerializedName("TargetTrainingTime")
        public String targetTrainingTime = "0";

        @SerializedName("PanIndiaRank")
        public int panIndiaRank;

        @SerializedName("COMarks")
        public int cOMarks;

        @SerializedName("TotalTrainer")
        public int totalTrainer;

        public PerformanceResponse() {

        }

    }

}
