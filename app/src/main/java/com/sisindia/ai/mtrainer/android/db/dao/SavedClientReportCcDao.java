package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.SavedClientReportCc;
import com.sisindia.ai.mtrainer.android.db.entities.SavedClientReportTo;
import com.sisindia.ai.mtrainer.android.models.Report.ReportDbCcResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface SavedClientReportCcDao {
    @Query("SELECT cc from cc_table WHERE rotaId = :rotaId")
    List<String> getClientCcList(int rotaId);

    @Query("SELECT cc,id from cc_table WHERE rotaId = :rotaId")
    Single<List<ReportDbCcResponse>> getClientSavedCcList(int rotaId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertClientCc(List<SavedClientReportCc> savedClientReportCc );

    @Query("DELETE from cc_table WHERE id = :id")
    Completable deleteClientCcEmail(String id);

    @Query("DELETE from cc_table")
    Completable flushClientCcList();

    @Query("DELETE from cc_table WHERE rotaId = :rotaId")
    Completable deleteSyncedClientCc(int rotaId);
}
