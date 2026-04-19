package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.TrainerPerformanceResponse;
import com.sisindia.ai.mtrainer.android.models.UnitListResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface UnitListDao {
    @Query("SELECT * from master_unit_table ORDER BY unitName ASC")
    LiveData<List<UnitListResponse.Unit>> getUnitList();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertUnitList(List<UnitListResponse.Unit> unitList);

    @Query("DELETE from master_unit_table")
    Completable flushUnitListMaster();
    @Query("SELECT count(*) from master_unit_table")
    Single<Integer> haveSiteData();
}
