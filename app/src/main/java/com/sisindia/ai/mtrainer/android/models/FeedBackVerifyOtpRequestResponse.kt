package com.sisindia.ai.mtrainer.android.models

import com.google.gson.annotations.SerializedName

data class FeedBackVerifyOtpRequest(
    @SerializedName("TrainingId")
    var trainerId:Int? = 0,

    @SerializedName("OTP")
    var otp:Int? = 0,
    @SerializedName("PhoneNumber")
    var phoneNumber:String? = null,
    @SerializedName("ClientEmailId")
    var clientEmailId:String? = null
) {



}

data class FeedBackVerifyOtpResponse(


    @SerializedName("Data")
    var data:FeedBackVerifyOtpResponseData?,
) {



}
data class FeedBackVerifyOtpResponseData(


    @SerializedName("OTP")
    var otp:Int? = 0,
    @SerializedName("PhoneNumber")
    var phoneNumber:String? = null,
):BaseApiResponse()