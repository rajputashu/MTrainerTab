package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.RatingMasterResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface RatingDataDao {
    @Query("SELECT * from master_rating_table WHERE ratingValue = :rating")
    Single<RatingMasterResponse.RatingData> getRatingData(int rating);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertRatingData(List<RatingMasterResponse.RatingData> ratingDataList);


    @Query("DELETE from master_rating_table")
    Completable flushRatingDataMaster();
}
