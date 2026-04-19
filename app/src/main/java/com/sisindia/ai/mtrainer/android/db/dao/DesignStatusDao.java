package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.sisindia.ai.mtrainer.android.features.spi.model.DesignStatusResponse;

import java.util.List;

import io.reactivex.Completable;
@Dao
public interface DesignStatusDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertDesignData(List<DesignStatusResponse.designStatus> designStatuses);
}
