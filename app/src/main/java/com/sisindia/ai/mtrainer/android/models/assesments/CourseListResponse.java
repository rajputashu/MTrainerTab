package com.sisindia.ai.mtrainer.android.models.assesments;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class CourseListResponse extends BaseApiResponse {

    @SerializedName("Data")
    public List<CourseListData> courseListData;

    @Parcel
    public static class CourseListData {

        @SerializedName("CourseId")
        public int courseId;

        @SerializedName("CourseName")
        public String courseName;
    }
}