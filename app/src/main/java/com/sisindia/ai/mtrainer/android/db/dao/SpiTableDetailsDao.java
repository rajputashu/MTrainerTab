package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.MyUnitsResponse;
import com.sisindia.ai.mtrainer.android.models.SpiTableDetailsResponse;

import java.util.List;

import io.reactivex.Completable;
@Dao
public interface SpiTableDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertSpiTableDetails(List<SpiTableDetailsResponse.SpiTableDetailsData> spiTableDetailsData);

    @Query("SELECT * from Spi_table")
    LiveData<List<SpiTableDetailsResponse.SpiTableDetailsData>> getSpiTableDetails();

    @Query("DELETE from Spi_table")
    Completable flushSpiTable();



}
