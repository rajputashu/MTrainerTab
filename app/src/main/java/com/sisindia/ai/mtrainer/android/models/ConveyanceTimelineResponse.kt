package com.sisindia.ai.mtrainer.android.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ConveyanceTimelineResponse : BaseApiResponse() {

    @SerializedName("Data")
    @Expose
    var data:List<ConveyanceTimeLineData>? = null

    @SerializedName("Data1")
    @Expose
    var data1:Any? = null

}

class ConveyanceTimeLineData{

    @SerializedName("TrainerId")
    @Expose
    var trainerId:Int? = null

    @SerializedName("TrainerName")
    @Expose
    var trainerName:String? = null

    @SerializedName("EmployeeNo")
    @Expose
    var employeeNo:String? = null

    @SerializedName("Year")
    @Expose
    var year:Int? = null

    @SerializedName("Month")
    @Expose
    var month:Int? = null

    @SerializedName("MonthName")
    @Expose
    var monthName:String? = null

    @SerializedName("SiteId")
    @Expose
    var siteId:Int? = null

    @SerializedName("SiteName")
    @Expose
    var siteName:String? = null

    @SerializedName("SiteCode")
    @Expose
    var siteCode:String? = null

    @SerializedName("SiteLat")
    @Expose
    var siteLat:String? = null

    @SerializedName("SiteLong")
    @Expose
    var siteLong:String? = null

    @SerializedName("TaskDate")
    @Expose
    var taskDate:String? = null

    @SerializedName("TaskStartTime")
    @Expose
    var taskStartTime:String? = null


    @SerializedName("TaskEndTime")
    @Expose
    var taskEndTime:String? = null

    @SerializedName("Task")
    @Expose
    var task:String? = null

    @SerializedName("TrainingType")
    @Expose
    var trainingType:String? = null


    @SerializedName("TrainingId")
    @Expose
    var trainingId:Int? = null


    @SerializedName("TaskLat")
    @Expose
    var taskLat:String? = null

    @SerializedName("TaskLong")
    @Expose
    var taskLong:String? = null


    @SerializedName("HomeLat")
    @Expose
    var homeLat:String? = null

    @SerializedName("HomeLong")
    @Expose
    var homeLong:String? = null

    @SerializedName("Address")
    @Expose
    var address:String? = null

    @SerializedName("DistanceKM")
    @Expose
    var distanceKM:Double? = null


    @SerializedName("DeductReason")
    @Expose
    var deductReason:String? = null

    @SerializedName("VanTrainingKM")
    @Expose
    var vanTrainingKM:Double? = null

    @SerializedName("OtherDeductionKM")
    @Expose
    var otherDeductionKM:Double? = null

}