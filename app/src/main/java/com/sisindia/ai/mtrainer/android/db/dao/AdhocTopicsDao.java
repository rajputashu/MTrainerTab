package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.AdhocTopicsResponse;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface AdhocTopicsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAdhocTopics(List<AdhocTopicsResponse.AdhocTopics> adhocTopics);

    @Query("SELECT * from adhoc_topics_table")
    LiveData<List<AdhocTopicsResponse.AdhocTopics>> getAdhocTopics();

    @Query("DELETE from adhoc_topics_table WHERE rotaId = :rotaId")
    Completable deleteadhocTopicByRotaId(int rotaId);

}
