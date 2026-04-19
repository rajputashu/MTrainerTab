package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.PostItem;
import com.sisindia.ai.mtrainer.android.models.SitePostResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceResponse;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface MasterPostDao {
    @Query("SELECT postId,postName from post_master_table WHERE siteID = :siteId")
    LiveData<List<PostItem>> getPostList(int siteId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMasterPostList(List<SitePostResponse.PostResponse> masterPostList);

    @Query("DELETE from post_master_table")
    Completable flushPostListMaster();
}
