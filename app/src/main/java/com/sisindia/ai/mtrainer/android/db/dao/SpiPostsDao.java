package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.features.spi.model.SpiPostResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
@Dao
public interface SpiPostsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSpiPosts(List<SpiPostResponse.SpiPostdata> spiPostdata);

    @Query("SELECT * from spi_posts_table")
    LiveData<List<SpiPostResponse.SpiPostdata>> getSpiPosts();

    @Query("SELECT COUNT(id) from spi_posts_table WHERE isCompleted = 0")
    Single<Integer> getSpiPendingPosts();

    @Query("SELECT * from spi_posts_table WHERE isCompleted = 1")
    LiveData<List<SpiPostResponse.SpiPostdata>> getSpiCompletedPosts();

    @Query("UPDATE spi_posts_table SET isCompleted = 1 WHERE postid = :postId")
    void markPostCompleted(int postId);

    @Query("DELETE from spi_posts_table")
    Completable flushSpiPostsTable();
}
