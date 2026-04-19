package com.sisindia.ai.mtrainer.android.models.assesments;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class CourseTypeResponseV2 extends BaseApiResponse {

    @SerializedName("Data")
    public List<CourseTypeDataV2> courseTypeList;

    @Parcel
    public static class CourseTypeDataV2 {

        @SerializedName("Id")
        public int id;

        @SerializedName("CourseType")
        public String courseTypeName;
    }
}