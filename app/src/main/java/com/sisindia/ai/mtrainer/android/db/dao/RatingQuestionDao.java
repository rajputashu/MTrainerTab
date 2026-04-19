package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.ChooseTopicsResponse;
import com.sisindia.ai.mtrainer.android.models.FeedbackReasonQuestionItem;
import com.sisindia.ai.mtrainer.android.models.RatingMasterResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface RatingQuestionDao {
    @Query("SELECT question, questionId from master_rating_question_table WHERE ratingValue = :rating")
    Single<List<FeedbackReasonQuestionItem>> getRatingQuestion(int rating);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertRatingQuestion(List<RatingMasterResponse.RatingQuestionData> ratingQuestionDataList);

    @Query("DELETE from master_rating_question_table")
    Completable flushRatingQuestionMaster();
}
