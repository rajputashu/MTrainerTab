package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.sisindia.ai.mtrainer.android.models.ClientApprovalResponse;
import com.sisindia.ai.mtrainer.android.models.DesignSpiResponse;

import java.util.List;

import io.reactivex.Completable;
@Dao
public interface ClientApprovalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertClientApprovalData(List<ClientApprovalResponse.ClientApprovalStatus> clientApprovalStatuses);
}
