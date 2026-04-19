package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.SpiTableDetailsResponse;
import com.sisindia.ai.mtrainer.android.models.spi.DraftApprovalResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface DraftApprovalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertDraftApproval(List<DraftApprovalResponse.DraftApprovalTableDetailsData> draftApprovalTableDetailsData);

    @Query("SELECT count(*) from spi_draft_approval_table WHERE statusid=10 AND  spiId=:spiId")
    Single<Integer> getNotApprovedDraft(int spiId);


    @Query("SELECT * from spi_draft_approval_table WHERE spiId=:spiId")
    LiveData<List<DraftApprovalResponse.DraftApprovalTableDetailsData>> getDraftApprovalDetails(int spiId);
    @Query("DELETE from spi_draft_approval_table")
    Completable flushDraftApprovalTable();

}
