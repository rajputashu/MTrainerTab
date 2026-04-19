package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.models.EmployeeListData;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingTopicsDataModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface MasterAttendanceDao {
    // TODO: Use light weight class
    @Query("SELECT * from master_attendance_name WHERE currSiteID = :siteId AND status = 1 AND (rotaId = -1 OR rotaId = :rotaId) ORDER BY employeeName ASC")
    LiveData<List<TrainingAttendanceResponse.AttendanceResponse>> getEmployeeList(String siteId, int rotaId);

    @Query("SELECT employeeName,employeeCode from master_attendance_name WHERE currSiteID = :siteId AND status = 1 AND rotaId = -1 ORDER BY employeeName ASC")
    Single<List<EmployeeListData>> getEmployeeListToShow(String siteId);


    @Query("SELECT Count(*) from master_attendance_name WHERE currSiteID = :siteId AND status = 1 AND (rotaId = -1 OR rotaId = :rotaId)")
    LiveData<Integer> getTotalEmployeeList(String siteId, int rotaId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertMasterEmployeeList(List<TrainingAttendanceResponse.AttendanceResponse> masterEmployeeList);

    @Query("DELETE from master_attendance_name")
    Completable flushEmployeeData();


    @Query("SELECT employeeName from master_attendance_name WHERE employeeCode = :empCode")
    Single<String> getName(String empCode);

    @Query("SELECT * from master_attendance_name WHERE employeeCode = :empCode AND status = 1")
    Single<TrainingAttendanceResponse.AttendanceResponse> getEmployee(String empCode);

    @Transaction
    @Query("UPDATE master_attendance_name SET currSiteID = :siteId WHERE employeeCode = :empCode")
    Completable addEmployeeToCurrentSite(String empCode, String siteId);

    /*
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertMasterEmployee(TrainingAttendanceResponse.AttendanceResponse masterEmployee);
*/
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMasterEmployee(TrainingAttendanceResponse.AttendanceResponse masterEmployee);

    @Query("DELETE from master_attendance_name WHERE rotaId = :rotaId")
    void deleteAdhocAttendance(int rotaId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUnitEmployeeList(List<TrainingAttendanceResponse.AttendanceResponse> masterEmployeeList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertCoursesList(TrainingTopicsDataModel courses);

    @Query("SELECT * from training_topics_courses WHERE CourseId =:courseid AND courseTopicId =:topicid AND courseContentId=:contentid")
    Single<TrainingTopicsDataModel> getCourseLastSeenById(int courseid, int topicid, int contentid);

    @Query("SELECT * from training_topics_courses WHERE SyncStatus=0")
    Single<List<TrainingTopicsDataModel>> getunsyncdata();

    @Query("UPDATE training_topics_courses SET SyncStatus=1 WHERE CourseId =:courseid AND courseTopicId =:topicid AND courseContentId=:contentid AND session=:sessionid")
    Completable updatestatus(int courseid, int topicid, int contentid,String sessionid);

    @Query("UPDATE training_topics_courses SET lastseen=:lastseen,startTime=:starttime,SyncStatus=0 WHERE CourseId =:courseid AND courseTopicId =:topicid AND courseContentId=:contentid AND session=:sessionid")
    Completable updatelastseen(int courseid, int topicid, int contentid, String starttime, String lastseen, String sessionid);


}
