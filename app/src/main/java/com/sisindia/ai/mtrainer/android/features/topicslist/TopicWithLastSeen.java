package com.sisindia.ai.mtrainer.android.features.topicslist;

import androidx.room.ColumnInfo;

public class TopicWithLastSeen {

    public int id;
    public int courseId;
    public int courseTopicId;
    public String courseTopicTitle;
    public int topicSequence;
    public int courseContentId;
    public String fileViewName;
    public String fileURL;
    public String thumbnailURL;      // field name in TopicEntity = column name
    public int contentTypeId;
    public String contentType;
    public int contentLanguageId;
    public String contentLanguageType;

    // from LEFT JOIN
    @ColumnInfo(name = "lastseen")
    public String lastseen;
}