package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.MyUnitsResponse;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface MyUnitsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMyUnits(List<MyUnitsResponse.MyUnitsDetailList> myUnitsDetailLists);

    @Query("SELECT * from my_units_table")
    LiveData<List<MyUnitsResponse.MyUnitsDetailList>> getMyUnitsList();

    @Query("DELETE from my_units_table")
    Completable flushMyUnits();
}
