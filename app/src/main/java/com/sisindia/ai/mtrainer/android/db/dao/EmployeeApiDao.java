package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.EmployeeApiEntity;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface EmployeeApiDao {
    @Query("SELECT lastAccessedTime from employee_api WHERE siteId = :siteId")
    Single<String> getLastAccessTime(int siteId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLastAccessedTime(EmployeeApiEntity apiEntity);

    @Query("DELETE from employee_api")
    Completable flushLastAccessTime();
}