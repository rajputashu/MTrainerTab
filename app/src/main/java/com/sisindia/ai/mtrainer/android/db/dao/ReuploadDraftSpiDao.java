package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.features.spi.model.ReuploadDraftSpiResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
@Dao
public interface ReuploadDraftSpiDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertReuploadDraftSpi(List<ReuploadDraftSpiResponse.ReuploadDraftSpiData> reuploadDraftSpiData);

    @Query("SELECT * from reupload_draft_spi_table WHERE keyid =:spiId")
    Single<List<ReuploadDraftSpiResponse.ReuploadDraftSpiData>> getReuploadData(int spiId);

    @Query("DELETE from reupload_draft_spi_table")
    Completable flushReuploadDraftSpi();
}
