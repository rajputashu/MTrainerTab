package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.DraftSpiResponse;

import java.util.List;

import io.reactivex.Completable;
@Dao
public interface DraftSpiDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertDraftSpi(List<DraftSpiResponse.DraftSpiDetailsData> draftSpiDetailsData);

    @Query("SELECT * from draft_spi_table")
    LiveData<List<DraftSpiResponse.DraftSpiDetailsData>> getDraftSpi();

    @Query("DELETE from draft_spi_table")
    Completable flushDraftSpiTable();
}
