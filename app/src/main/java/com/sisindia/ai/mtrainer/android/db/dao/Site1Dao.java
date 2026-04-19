package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.sisindia.ai.mtrainer.android.models.SiteData;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface Site1Dao {

    @Query("SELECT * from site_data WHERE branchId = :branchId OR branchId=273 OR branchId=587")
    LiveData<List<SiteData>> getSiteList(int branchId);
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertSiteList(List<SiteData> siteDataList);

    @Query("DELETE from site_data")
    Completable flushSiteListMaster();

    @Query("SELECT count(*) from site_data WHERE branchId = :branchId")
    Single<Integer> haveSiteData(int branchId);

}