package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.SiteEntity;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface SiteDao {
    @Query("SELECT * from site_table")
    LiveData<List<SiteEntity>> getSiteEntity();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertSite(SiteEntity siteEntity);
}
