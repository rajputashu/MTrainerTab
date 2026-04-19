package com.sisindia.ai.mtrainer.android.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class TrainingCalendarResponse extends BaseApiResponse {

    @SerializedName("Data")
    public List<TrainingCalendar> trainingCalendars;

    public TrainingCalendarResponse() {

    }

    @Parcel
    @Entity(tableName = "training_calender_table")
    public static class TrainingCalendar {


        @SerializedName("UnitCode")
        public String unitCode;

        @SerializedName("Id")
        public int id;

        @SerializedName("UnitName")
        public String unitName;

        @SerializedName("UnitAddress")
        public String unitAddress;

        @PrimaryKey
        @SerializedName("RotaId")
        public int rotaId;

        @SerializedName("UnitId")
        public int unitId;

        @SerializedName("TrainerId")
        public int trainerId;

        @SerializedName("TrainerName")
        public String trainerName;


        @SerializedName("TrainingTypeId")
        public int trainingTypeId;

        @SerializedName("TopicId")
        public String topicId;

        @SerializedName("NFCId")
        public String nfcId;

        @SerializedName("IsAdhoc")
        public String isAdhoc;

        @SerializedName("TrainingType")
        public String trainingType;

        @SerializedName("UnitLat")
        public String lattitude;

        @SerializedName("UnitLong")
        public String longitude;

        @SerializedName("EstimatedStartDate")
        public String estimatedStartDatetime;

        @SerializedName("EstimatedEndDate")
        public String estimatedEndDatetime;

        @SerializedName("EstimatedStartTime")
        public String estimatedStartTime;
        @SerializedName("EstimatedEndTime")
        public String estimatedEndTime;

        @SerializedName("TaskStatusId")
        public int taskStatusId;

        @SerializedName("ActualStartDatetime")
        public String actualStartDatetime;

        @SerializedName("ActualEndDatetime")
        public String actualEndDatetime;

        @SerializedName("AttendanceTypeList")
        public String attendanceTypeList;

        @SerializedName("TraningStatusId")
        public int traningStatusId;

        @SerializedName("TraningStatus")
        public String traningStatus;

        @SerializedName("AvgRating")
        public String givenRating = "NA";

        @SerializedName("TotalEmpTrained")
        public String totalTrained = "NA";

        @SerializedName("ActualStartTime")
        public String savedStartTime = "NA";

        @SerializedName("ActualEndTime")
        public String savedEndTime = "NA";
        @SerializedName("Reason")
        public String reason = "NA";
        @SerializedName("RegionName")
        public String regionName;
        @SerializedName("LocalStatus")
        public int isInProgress = 0;

        public int getTraningStatusId() {
            return traningStatusId;
        }

        public void setTraningStatusId(int traningStatusId) {
            this.traningStatusId = traningStatusId;
        }

      /*  public String getUnitCode() {
            return unitCode;
        }

        public void setUnitCode(String unitCode) {
            this.unitCode = unitCode;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSiteName() {
            return siteName;
        }

        public void setSiteName(String siteName) {
            this.siteName = siteName;
        }

        public String getUnitAddress() {
            return unitAddress;
        }

        public void setUnitAddress(String unitAddress) {
            this.unitAddress = unitAddress;
        }

        public int getRotaId() {
            return rotaId;
        }

        public void setRotaId(int rotaId) {
            this.rotaId = rotaId;
        }

        public int getUnitId() {
            return unitId;
        }

        public void setUnitId(int unitId) {
            this.unitId = unitId;
        }

        public int getTrainerId() {
            return trainerId;
        }

        public void setTrainerId(int trainerId) {
            this.trainerId = trainerId;
        }

        public String getTrainerName() {
            return trainerName;
        }

        public void setTrainerName(String trainerName) {
            this.trainerName = trainerName;
        }

        public int getTrainingTypeId() {
            return trainingTypeId;
        }

        public void setTrainingTypeId(int trainingTypeId) {
            this.trainingTypeId = trainingTypeId;
        }

        public int getTopicId() {
            return topicId;
        }

        public void setTopicId(int topicId) {
            this.topicId = topicId;
        }

        public String getNfcId() {
            return nfcId;
        }

        public void setNfcId(String nfcId) {
            this.nfcId = nfcId;
        }

        public String getIsAdhoc() {
            return isAdhoc;
        }

        public void setIsAdhoc(String isAdhoc) {
            this.isAdhoc = isAdhoc;
        }

        public String getTrainingType() {
            return trainingType;
        }

        public void setTrainingType(String trainingType) {
            this.trainingType = trainingType;
        }

        public double getLattitude() {
            return lattitude;
        }

        public void setLattitude(double lattitude) {
            this.lattitude = lattitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getEstimatedStartDatetime() {
            return estimatedStartDatetime;
        }

        public void setEstimatedStartDatetime(String estimatedStartDatetime) {
            this.estimatedStartDatetime = estimatedStartDatetime;
        }

        public String getEstimatedEndDatetime() {
            return estimatedEndDatetime;
        }

        public void setEstimatedEndDatetime(String estimatedEndDatetime) {
            this.estimatedEndDatetime = estimatedEndDatetime;
        }

        public int getTaskStatusId() {
            return taskStatusId;
        }

        public void setTaskStatusId(int taskStatusId) {
            this.taskStatusId = taskStatusId;
        }

        public String getActualStartDatetime() {
            return actualStartDatetime;
        }

        public void setActualStartDatetime(String actualStartDatetime) {
            this.actualStartDatetime = actualStartDatetime;
        }

        public String getActualEndDatetime() {
            return actualEndDatetime;
        }

        public void setActualEndDatetime(String actualEndDatetime) {
            this.actualEndDatetime = actualEndDatetime;
        }

        public int getExpectedGuards() {
            return expectedGuards;
        }

        public void setExpectedGuards(int expectedGuards) {
            this.expectedGuards = expectedGuards;
        }

        public int getBranchId() {
            return branchId;
        }

        public void setBranchId(int branchId) {
            this.branchId = branchId;
        }

        public String getBranchName() {
            return branchName;
        }

        public void setBranchName(String branchName) {
            this.branchName = branchName;
        }

        public int getRegionId() {
            return regionId;
        }

        public void setRegionId(int regionId) {
            this.regionId = regionId;
        }

        public String getRegionName() {
            return regionName;
        }

        public void setRegionName(String regionName) {
            this.regionName = regionName;
        }

        */


    }
}
