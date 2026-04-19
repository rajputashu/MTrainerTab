package com.sisindia.ai.mtrainer.android.features.trainingcourses;

import com.sisindia.ai.mtrainer.android.db.entities.CourseEntity;
import com.sisindia.ai.mtrainer.android.models.online.TrainingCourseDataResponseMO;

import java.util.ArrayList;
import java.util.List;

public class CourseEntityMapper {

    public static TrainingCourseDataResponseMO toResponseMO(CourseEntity entity) {
        return new TrainingCourseDataResponseMO(
                entity.courseId,
                entity.courseTitle,
                entity.courseSeqNo,
                entity.companyId,
                0.0,        // courseDuration       — not in CourseEntity
                0,          // isOffline             — not in CourseEntity
                entity.courseThumbnailURL,
                entity.segmentType,
                0,          // isActive              — not in CourseEntity
                0,          // contentTypeId         — not in CourseEntity
                "",         // contentType           — not in CourseEntity
                0,          // assessmentId          — not in CourseEntity
                "",         // assessmentName        — not in CourseEntity
                "",         // lastAccessDateTime    — not in CourseEntity
                0,          // totalQuestionCount    — not in CourseEntity
                0,          // attemptQuestionCount  — not in CourseEntity
                0,          // totalScore            — not in CourseEntity
                null,       // score                 — nullable, default null
                0,          // totalTopicCount       — not in CourseEntity
                0,          // attemptTopicCount     — not in CourseEntity
                0           // isReAttempt           — not in CourseEntity
        );
    }

    public static List<TrainingCourseDataResponseMO> toResponseMOList(List<CourseEntity> entities) {
        List<TrainingCourseDataResponseMO> list = new ArrayList<>();
        for (CourseEntity entity : entities) {
            list.add(toResponseMO(entity));
        }
        return list;
    }
}