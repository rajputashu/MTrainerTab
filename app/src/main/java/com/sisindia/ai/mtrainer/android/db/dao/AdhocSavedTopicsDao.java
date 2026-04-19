package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.AdhocSavedTopics;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface AdhocSavedTopicsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAdhocSavedTopic(AdhocSavedTopics savedTopic);

    @Query("DELETE from Adhoc_saved_topics_table WHERE id = :id AND rotaId = :rotaId")
    Completable deleteAdhocSavedTopic(int id, int rotaId);

    @Query("SELECT * from Adhoc_saved_topics_table WHERE rotaId = :rotaId")
    List<AdhocSavedTopics> getAdhocReportForSync(int rotaId);

    @Query("SELECT * from Adhoc_saved_topics_table WHERE rotaId = :rotaId")
    LiveData<List<AdhocSavedTopics>> getAdhocSavedTopicList(int rotaId);

    @Query("DELETE from Adhoc_saved_topics_table WHERE rotaId = :rotaId")
    Completable deleteAdhocSavedTopicByRotaId(int rotaId);

    @Query("SELECT topicName from Adhoc_saved_topics_table WHERE rotaId = :rotaId")
    List<String> getAdhocFinalTopicList(int rotaId);

    // To Recreate
    @Query("SELECT id from Adhoc_saved_topics_table WHERE rotaId = :rotaId")
    Single<List<Integer>> getOnlySavedTopicIdList(int rotaId);

}
