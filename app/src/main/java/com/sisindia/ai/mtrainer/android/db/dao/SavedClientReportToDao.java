package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.db.entities.SavedClientReportTo;
import com.sisindia.ai.mtrainer.android.models.PostItem;
import com.sisindia.ai.mtrainer.android.models.Report.ReportDbResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;


@Dao
public interface SavedClientReportToDao {
    @Query("SELECT email from to_table WHERE rotaId = :rotaId")
    List<String> getClientEmailList(int rotaId);

    @Query("SELECT email,id from to_table WHERE rotaId = :rotaId")
    Single<List<ReportDbResponse>> getClientToList(int rotaId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertClientEmail(List<SavedClientReportTo> savedClientReportTo);

    @Query("DELETE from to_table WHERE id = :id")
    Completable deleteClientToEmail(String id);

    @Query("DELETE from to_table")
    Completable flushClientEmailList();

    @Query("DELETE from to_table WHERE rotaId = :rotaId")
    Completable deleteSyncedClientEmail(int rotaId);

    // To restore State
    @Query("SELECT id from to_table WHERE rotaId = :rotaId")
    Single<List<String>> getClientIDList(int rotaId);
}
