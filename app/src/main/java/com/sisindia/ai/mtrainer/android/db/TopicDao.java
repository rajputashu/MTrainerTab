package com.sisindia.ai.mtrainer.android.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Room;

import com.sisindia.ai.mtrainer.android.models.ChooseTopicsResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface TopicDao {
    @Query("SELECT * from master_topic_table WHERE companyId = :companyId ORDER BY topicName ASC")
    LiveData<List<ChooseTopicsResponse.TopicsResponse>> getMasterTopicList(String companyId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMasterTopic(List<ChooseTopicsResponse.TopicsResponse> topicList);

    @Query("DELETE from master_topic_table")
    Completable flushTopicMaster();

    /*@Query("SELECT * from master_topic_table WHERE topicId =topicId")
    Single<ChooseTopicsResponse.TopicsResponse> getTopic(int topicId);*/
}