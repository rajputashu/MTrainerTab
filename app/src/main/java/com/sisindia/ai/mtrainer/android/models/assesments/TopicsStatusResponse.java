package com.sisindia.ai.mtrainer.android.models.assesments;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class TopicsStatusResponse extends BaseApiResponse {
    @SerializedName("Data")
    public List<AttendanceEntity> attendanceList;
}