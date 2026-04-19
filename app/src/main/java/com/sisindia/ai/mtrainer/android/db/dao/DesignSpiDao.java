package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.DesignSpiResponse;
import com.sisindia.ai.mtrainer.android.models.SpiTableDetailsResponse;

import java.util.List;

import io.reactivex.Completable;
@Dao
public interface DesignSpiDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertSpiDesignData(List<DesignSpiResponse.DesignSpiData> designSpiData);

    @Query("DELETE from design_spi_table")
    Completable flushDesignTable();

    @Query("SELECT * from design_spi_table WHERE spiId=:spiId")
    LiveData<List<DesignSpiResponse.DesignSpiData>> getDesignDetails(int spiId);
}
