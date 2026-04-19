package com.sisindia.ai.mtrainer.android.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface SavedTopicDao {
    @Query("SELECT * from saved_topics_table WHERE rotaId = :rotaId")
    LiveData<List<SavedTopic>>getSavedTopicList(int rotaId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertSavedTopic(SavedTopic savedTopic);

    @Query("DELETE from saved_topics_table WHERE topicId = :id AND rotaId = :rotaId")
    Completable deleteSavedTopic(int id, int rotaId);

    @Query("DELETE from saved_topics_table")
    Completable flushSavedTopic();

    @Query("DELETE from saved_topics_table WHERE rotaId = :rotaId")
    Completable deleteSavedTopicByRotaId(int rotaId);

    @Query("SELECT topicId from saved_topics_table WHERE rotaId = :rotaId")
    List<Integer> getSavedTopicSubList(int rotaId);

    @Query("DELETE from saved_topics_table")
    Completable deleteAll();
    // To Recreate
    @Query("SELECT topicId from saved_topics_table WHERE rotaId = :rotaId")
    Single<List<Integer>> getSavedTopicIdList(int rotaId);
}
