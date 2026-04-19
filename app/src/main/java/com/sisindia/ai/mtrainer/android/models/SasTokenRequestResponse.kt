package com.sisindia.ai.mtrainer.android.models

import com.google.gson.annotations.SerializedName

data class SasTokenResponse (

    @SerializedName("Data")
    var data:String,

    ):BaseApiResponse()


data class SasTokenRequest (

    @SerializedName("LoginCode")
    var loginCode:String,

    @SerializedName("Password")
    var password:String,

    @SerializedName("UserName")
    var userName:String,

    )