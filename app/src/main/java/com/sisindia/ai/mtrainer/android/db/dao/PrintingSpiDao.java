package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.sisindia.ai.mtrainer.android.models.SpiPrintingResponse;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface PrintingSpiDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertPrintingSpilData(List<SpiPrintingResponse.SpiPrintingStatus> spiPrintingStatuses);
}

