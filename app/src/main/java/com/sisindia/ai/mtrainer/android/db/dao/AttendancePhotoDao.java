package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.sisindia.ai.mtrainer.android.db.entities.AttendancePhotoEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface AttendancePhotoDao {

    @Query("SELECT * from attendance_photo_table")
    LiveData<List<AttendancePhotoEntity>> getAttendancePhotoList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAttendancePhoto(AttendancePhotoEntity AttendancePhotoEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAllAttendancePhoto(List<AttendancePhotoEntity> list);

    @Query("DELETE from attendance_photo_table WHERE id = :id")
    Completable deleteAttendancePhoto(String id);

    @Query("DELETE from attendance_photo_table WHERE rotaId = :rotaId")
    Completable flushAttendancePhoto(int rotaId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateAttendancePhoto(AttendancePhotoEntity attendancePhotoEntity);

    @Query("SELECT * from attendance_photo_table WHERE status = 8")
    List<AttendancePhotoEntity> getSavedAttendancePic();

    @Query("SELECT * from attendance_photo_table WHERE status = 8")
    Single<List<AttendancePhotoEntity>> getSavedAttendancePicForForced();

    @Query("UPDATE attendance_photo_table SET status = 8 WHERE rotaid = :rotaId ")
    void updateAttendanceImageStatus(int rotaId);

    /* @Query("UPDATE attendance_photo_table SET isMark = 1 WHERE rotaid = :rotaId ")
     void imageMarked(int rotaId);
 */
    @Query("UPDATE attendance_photo_table SET isMark = 1 WHERE attendancePhotoURI = :attendancePhotoURI")
    void imageMarked(String attendancePhotoURI);

    @Query("UPDATE attendance_photo_table SET isCompress = 1, attendancePhotoURI =:compressedImage WHERE attendancePhotoURI = :attendancePhotoURI ")
    void imageCompressed(String attendancePhotoURI, String compressedImage);

    @Query("SELECT count(id) from attendance_photo_table")
    Single<Integer> getPendingAttendancePicCount();
}
