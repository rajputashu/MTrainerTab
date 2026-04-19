package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.spi.MountedResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface MountedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMountedData(List<MountedResponse.MountedData> designSpiData);

    @Query("DELETE from mounted_table")
    Completable flushMountedTable();

    @Query("SELECT * from mounted_table")
    Single<List<MountedResponse.MountedData>> getMountedDetails();
}
