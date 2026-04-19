package com.sisindia.ai.mtrainer.android.models.assesments;

import com.google.gson.annotations.SerializedName;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class TopicListResponse extends BaseApiResponse {

    @SerializedName("Data")
    public List<TopicListData> topicListData;

    @Parcel
    public static class TopicListData {

        @SerializedName("TopicId")
        public int topicId;

        @SerializedName("TopicName")
        public String topicName;
    }
}