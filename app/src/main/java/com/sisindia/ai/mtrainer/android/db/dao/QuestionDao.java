package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.models.AssesmentQustions;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceResponse;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface QuestionDao {
    @Query("SELECT question from assessment_question_master ")
    Single<List<String>> getQuestionList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMasterQuestionList(List<AssesmentQustions> masterQuestionList);
}
