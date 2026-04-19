package com.sisindia.ai.mtrainer.android.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ConveyanceDailyResponse : BaseApiResponse() {

    @SerializedName("Data")
    @Expose
    var data:List<ConveyanceDailyData>? = null

    @SerializedName("Data1")
    @Expose
    var data1:Any? = null

}

class ConveyanceDailyData : Serializable {

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

    @SerializedName("DayName")
    @Expose
    var dayName:String? = null

    @SerializedName("Date")
    @Expose
    var date:String? = null

    @SerializedName("TotalTask")
    @Expose
    var totalTask:Int? = null

    @SerializedName("TotalDistanceKM")
    @Expose
    var totalDistanceKM:Double? = null

    @SerializedName("VanTrainingKM")
    @Expose
    var vanTrainingKM:Double? = null

    @SerializedName("OtherDeductionKM")
    @Expose
    var otherDeductionKM:Double? = null

}