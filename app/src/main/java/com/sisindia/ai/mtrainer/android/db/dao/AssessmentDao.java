package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sisindia.ai.mtrainer.android.db.entities.AssementEntity;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.db.entities.TrainingPhotoAttachmentEntity;
import com.sisindia.ai.mtrainer.android.models.PostItem;
import com.sisindia.ai.mtrainer.android.models.sync.AssessmentReport;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface AssessmentDao {
    @Query("SELECT * from assessment_table WHERE rotaId = :rotaId AND videoPath = 'NA' ORDER BY empName")
    Single<List<AssementEntity>> getAttendanceListForAssessment(int rotaId);

    @Query("SELECT empId from assessment_table WHERE rotaId = :rotaId AND videoPath = 'NA'")
    Single<List<Integer>> getAttendanceCountListForAssessment(int rotaId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAssessment(AssementEntity assementEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAllAssessment(List<AssementEntity> list);

    @Query("DELETE from assessment_table WHERE id = :id")
    Completable deleteAssessment(String id);

    @Query("DELETE from assessment_table WHERE rotaId = :rotaId")
    Completable deleteSyncedAssessment(int rotaId);

    @Query("UPDATE assessment_table SET videoPath = :videoPath, question = :question WHERE rotaid = :rotaId AND empCode = :empCode")
    Completable updateAssessmentDetail(String videoPath, String question, int rotaId, String empCode);


    @Query("SELECT * from assessment_table WHERE status = 8")
    List<AssementEntity> getAssessmentVideoForSync();

    @Query("SELECT * from assessment_table WHERE status = 8")
    Single<List<AssementEntity>> getAssessmentVideoForForceSync();

/*
    @Query("SELECT * from assessment_table")
    List<AssessmentReport> getAssessmentReportForSync();
    */

    @Query("SELECT * from assessment_table WHERE rotaId = :rotaId AND empId != -1")
    List<AssessmentReport> getAssessmentReportForSync(int rotaId);

    @Query("SELECT * from assessment_table WHERE rotaId = :rotaId AND empId = -1")
    List<AssementEntity> getAdHocAssessmentReportForSync(int rotaId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAssessmentVideo(AssementEntity assementEntity);

    @Query("DELETE from assessment_table WHERE id = :id")
    void deleteSingleAssessment(String id);

    @Query("DELETE from assessment_table")
    Completable deleteAll();

    // To Restore
    @Query("SELECT empCode from assessment_table WHERE rotaId = :rotaId")
    Single<List<String>> getAttendanceIdListForAssessment(int rotaId);

   /* @Query("SELECT empId from assessment_table WHERE rotaId = :rotaId")
   Single<List<Integer>> getAttendanceIdListForAssessment(int rotaId);
*/

    @Query("UPDATE assessment_table SET status = 8 WHERE rotaid = :rotaId ")
    void updateVideoStatus(int rotaId);
    @Query("SELECT count(id) from assessment_table")
    Single<Integer> getPendingAssessmentVideoCount();
}
