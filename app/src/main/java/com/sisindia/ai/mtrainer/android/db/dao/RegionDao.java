package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.RegionData;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface RegionDao {

    @Query("SELECT * from region_data")
    LiveData<List<RegionData>> getRegionList();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertRegionList(List<RegionData> regionDataList);

    @Query("DELETE from region_data")
    Completable flushRegionListMaster();
}
