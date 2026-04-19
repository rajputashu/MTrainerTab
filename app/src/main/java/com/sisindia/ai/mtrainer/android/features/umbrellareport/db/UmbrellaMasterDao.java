package com.sisindia.ai.mtrainer.android.features.umbrellareport.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.features.umbrellareport.model.UmbrellaImageRvItem;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface UmbrellaMasterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertUmbrellaMasterData(UmbrellaMaster umbrellaMaster);

    @Query("DELETE from umbrella_master_table WHERE umbrellaId = :umbrellaId")
    void clearCurrentUmbrellaMaster(String umbrellaId);

    @Query("SELECT * from umbrella_master_table")
    List<UmbrellaMaster> getUmbrellaMasterData();
}