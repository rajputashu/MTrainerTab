package com.sisindia.ai.mtrainer.android.uimodels

import org.parceler.Parcel

@Parcel
data class YearUIModel(
    var year:String,
    var number:Int
) {


    constructor():this("",0)

}