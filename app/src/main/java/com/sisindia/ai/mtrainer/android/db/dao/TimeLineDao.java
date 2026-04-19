package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.TimeLineEntity;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface TimeLineDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertTimeline(TimeLineEntity timeLineEntities);

    @Query("SELECT * from timeline_table")
    LiveData<List<TimeLineEntity>> getTimelineList();

    @Query("DELETE from timeline_table")
    Completable flushTimelinet();

}
