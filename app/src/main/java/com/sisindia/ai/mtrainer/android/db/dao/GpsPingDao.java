package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.LocationDetailsEntity;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface GpsPingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insertLocationDetail(LocationDetailsEntity locationDetailsEntity);

    @Query("SELECT * from location_details WHERE status = 1 ORDER BY status ASC")
    Single<List<LocationDetailsEntity>> getDutyOnDetails();

    @Query("SELECT * from location_details WHERE status = 2 ORDER BY status ASC")
    Single<List<LocationDetailsEntity>> getDutyOffDetails();

    @Query("DELETE from location_details WHERE dateTime =:dateTime")
    void removeSyncedLocationDetail(String dateTime);
}
