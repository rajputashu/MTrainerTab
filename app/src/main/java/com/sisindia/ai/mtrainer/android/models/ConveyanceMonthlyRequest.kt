package com.sisindia.ai.mtrainer.android.models

import com.google.gson.annotations.SerializedName

data class ConveyanceMonthlyRequest(
    @SerializedName("TrainerId")
    var trainerId:Int? = null,

    @SerializedName("Month")
var month:Int? = null,

@SerializedName("Year")
var year:Int? = null
) {



}