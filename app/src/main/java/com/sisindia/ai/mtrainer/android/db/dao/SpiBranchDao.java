package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.BranchData;
import com.sisindia.ai.mtrainer.android.models.SpiBranchData;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
@Dao
public interface SpiBranchDao {
    @Query("SELECT * from Spi_branch_data WHERE regionId = :regionId")
    LiveData<List<SpiBranchData>> getSpiBranchList(int regionId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertSpiBranchList(List<SpiBranchData> branchDataList);

    @Query("DELETE from Spi_branch_data")
    Completable flushSpiBranchListMaster();
    @Query("SELECT count(*) from Spi_branch_data")
    Single<Integer> haveSpiBranchData();
}
