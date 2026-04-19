package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.CalendarResponse;

import java.io.SequenceInputStream;
import java.util.List;

import io.reactivex.Completable;

@Dao
public interface CalendrRotaCompletdDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertCalendarRota(List<CalendarResponse.CalendarRotaResponse> topicList);

    @Query("SELECT * from calendar_rota_table")
    LiveData<List<CalendarResponse.CalendarRotaResponse>> getCalendarList();

    @Query("DELETE from calendar_rota_table")
    Completable flushTrainingCalendar();
}
