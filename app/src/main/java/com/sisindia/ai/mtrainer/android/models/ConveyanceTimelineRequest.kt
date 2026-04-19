package com.sisindia.ai.mtrainer.android.models

import com.google.gson.annotations.SerializedName

data class ConveyanceTimelineRequest(
    @SerializedName("TrainerId")
    var trainerId:Int? = null,

    @SerializedName("ConDate")
var conDate:String? = null
) {



}