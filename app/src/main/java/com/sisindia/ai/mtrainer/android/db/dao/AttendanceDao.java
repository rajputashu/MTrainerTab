package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.models.PostItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface AttendanceDao {

    @Query("SELECT Count(*) from attendance_table WHERE rotaId = :rotaId ORDER BY employeeName ASC")
    LiveData<Integer> getAttendanceList(int rotaId);

    @Query("SELECT count(*) from attendance_table WHERE empCode = :employeeCode")
    Single<Integer> checkDuplicateEmployee(String employeeCode);

    @Query("SELECT DISTINCT postId, postName from attendance_table WHERE rotaId = :rotaId ORDER BY postName ASC")
    LiveData<List<PostItem>> getAttendancePostList(int rotaId);

    @Query("SELECT * from attendance_table WHERE rotaId = :rotaId ORDER BY employeeName ASC")
    Single<List<AttendanceEntity>> getAttendanceListForAssessment(int rotaId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAttendance(AttendanceEntity attendanceEntity);

    @Query("DELETE from attendance_table WHERE empCode = :empCode AND rotaId = :rotaId")
    Completable deleteAttendance(String empCode, int rotaId);

    @Query("DELETE from attendance_table")
    Completable flushAttendance();

    @Query("SELECT * from attendance_table WHERE rotaId = :rotaId AND employeeId != -1")
    List<AttendanceEntity> getAttendanceSubList(int rotaId);

    @Query("SELECT * from attendance_table WHERE rotaId = :rotaId AND employeeId = -1")
    List<AttendanceEntity> getAdhocAttendanceList(int rotaId);

    @Query("DELETE from attendance_table WHERE rotaId = :rotaId")
    Completable deleteSyncedAttendance(int rotaId);

    //Used to restore saved state
    @Query("SELECT empCode from attendance_table WHERE rotaId = :rotaId")
    Single<List<String>> getAttendanceIdList(int rotaId);


}
