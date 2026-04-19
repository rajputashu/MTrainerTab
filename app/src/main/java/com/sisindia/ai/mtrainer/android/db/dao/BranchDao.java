package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.BranchData;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface BranchDao {
    @Query("SELECT * from branch_data WHERE regionId = :regionId")
    LiveData<List<BranchData>> getBranchList(int regionId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertBranchList(List<BranchData> branchDataList);

    @Query("DELETE from branch_data")
    Completable flushBranchListMaster();

    @Query("SELECT count(*) from branch_data WHERE regionId = :regionId")
    Single<Integer> haveBranchData(int regionId);
}

