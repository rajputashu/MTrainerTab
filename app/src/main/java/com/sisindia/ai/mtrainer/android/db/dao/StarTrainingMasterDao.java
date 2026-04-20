package com.sisindia.ai.mtrainer.android.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sisindia.ai.mtrainer.android.db.entities.CourseEntity;
import com.sisindia.ai.mtrainer.android.db.entities.LanguageEntity;
import com.sisindia.ai.mtrainer.android.db.entities.ProgramEntity;
import com.sisindia.ai.mtrainer.android.db.entities.TopicEntity;
import com.sisindia.ai.mtrainer.android.features.topicslist.TopicWithLastSeen;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface StarTrainingMasterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAllProgramList(List<ProgramEntity> programEntities);

    @Query("DELETE from program_table")
    Completable deleteAllMasterProgram();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAllLanguageList(List<LanguageEntity> languageEntities);

    @Query("DELETE from language_table")
    Completable deleteAllMasterLanguage();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAllCoursesList(List<CourseEntity> courseEntities);

    @Query("DELETE from course_table")
    Completable deleteAllMasterCourse();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAllTopicList(List<TopicEntity> topicEntities);

    @Query("DELETE from topic_table")
    Completable deleteAllMasterTopic();

    @Query("SELECT * from program_table")
    Single<List<ProgramEntity>> fetchAllPrograms();

    @Query("SELECT * from course_table where programId=:programId and languageId=:languageId")
    Single<List<CourseEntity>> fetchAllCourses(int programId, int languageId);

    @Query("SELECT * from topic_table where courseId=:courseId and contentLanguageId=:languageId")
    Single<List<TopicEntity>> fetchAllTopics(int courseId, int languageId);

    /*@Query("select tt.* , ttc.lastseen from topic_table as tt left join training_topics_courses as ttc on tt.courseId=ttc.courseId where tt.courseId=:courseId and tt.contentLanguageId=:languageId")
    Single<List<TopicWithLastSeen>> fetchAllTopicsV2(int courseId, int languageId);*/

    @Query("SELECT tt.id, tt.courseId, tt.courseTopicId, tt.courseTopicTitle, tt.topicSequence, tt.courseContentId, tt.fileViewName, tt.fileURL, tt.thumbnailURL, tt.contentTypeId, tt.contentType, tt.contentLanguageId, tt.contentLanguageType, tt.isDownloaded, ttc.lastseen FROM topic_table AS tt LEFT JOIN training_topics_courses AS ttc ON tt.courseId = ttc.courseId AND tt.courseContentId = ttc.courseContentId WHERE tt.courseId = :courseId AND tt.contentLanguageId = :languageId")
    Single<List<TopicWithLastSeen>> fetchAllTopicsV2(int courseId, int languageId);

    @Query("SELECT * from language_table")
    Single<List<LanguageEntity>> fetchAllLanguages();

    @Query("Update topic_table set isDownloaded =1 where courseId=:courseId and courseTopicId=:courseTopicId and contentLanguageId=:languageId")
    Single<Integer> updateDownloadStatus(int courseId, int courseTopicId, int languageId);
}

