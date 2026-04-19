package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.ReasonEntity;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface ReasonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertReason(ReasonEntity reasonEntity);
}

