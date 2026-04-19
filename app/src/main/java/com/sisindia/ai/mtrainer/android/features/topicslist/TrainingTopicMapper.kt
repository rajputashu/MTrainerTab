package com.sisindia.ai.mtrainer.android.features.topicslist

import com.sisindia.ai.mtrainer.android.db.entities.TopicEntity
import com.sisindia.ai.mtrainer.android.models.TrainingTopicsDataModel
import com.sisindia.ai.mtrainer.android.models.online.TrainingTopicDataResponseMO

object TrainingTopicMapper {
    @JvmStatic
    fun mapToDbModel(api: TrainingTopicDataResponseMO): TrainingTopicsDataModel {
        return TrainingTopicsDataModel(
            courseId = api.courseId,
            courseTitle = api.courseTitle,
            companyId = api.companyId,
            lastseen = api.lastSeen,
            session = null, // will be set later in VideoScorm
            startTime = null,
            courseDuration = api.courseDuration,
            isOffline = api.isOffline,
            segmentType = api.segmentType,
            courseTopicId = api.courseTopicId,
            courseTopicTitle = api.courseTopicTitle,
            topicDuration = api.topicDuration,
            topicSequence = api.topicSequence,
            courseTopicType = api.courseTopicType,
            courseContentId = api.courseContentId,
            fileDownloadName = api.fileDownloadName,
            fileViewName = api.fileViewName,
            fileURL = api.fileURL,
            contentSize = api.contentSize,
            contentVersion = api.contentVersion,
            courseContentDuration = api.courseContentDuration,
            courseContentType = api.courseContentType,
            languageType = api.languageType,
            courseContentThumbnailURL = api.courseContentThumbnailURL,
            isActive = api.isActive,
            SyncStatus = 0
        )
    }

    @JvmStatic
    fun fromEntity(entity: TopicEntity): TrainingTopicDataResponseMO {
        return TrainingTopicDataResponseMO(
            courseId = entity.courseId,
            courseTitle = "",       // not in TopicEntity
            courseSequenceNo = 0,        // not in TopicEntity
            companyId = 0,        // not in TopicEntity
            courseDuration = 0.0,      // not in TopicEntity
            isOffline = 0,        // not in TopicEntity
            segmentType = "",       // not in TopicEntity
            courseTopicId = entity.courseTopicId,
            courseTopicTitle = entity.courseTopicTitle,
            topicDuration = 0.0,      // not in TopicEntity
            topicSequence = entity.topicSequence,
            courseTopicType = "",       // not in TopicEntity
            courseContentId = entity.courseContentId,
            fileDownloadName = "",       // not in TopicEntity
            fileViewName = entity.fileViewName,
            fileURL = entity.fileURL,
            contentSize = 0,        // not in TopicEntity
            contentVersion = 0,        // not in TopicEntity
            courseContentDuration = 0.0,      // not in TopicEntity
            courseContentType = entity.contentType,
            courseContentThumbnailURL = entity.thumbnailURL,
            languageType = entity.contentLanguageType,
            lastSeen = null,     // not in TopicEntity
            isActive = 0,        // not in TopicEntity
            assessmentId = 0,        // not in TopicEntity
            assessmentName = "",       // not in TopicEntity
            lastAccessDateTime = "",       // not in TopicEntity
            totalQuestionCount = 0,        // not in TopicEntity
            attemptQuestionCount = 0,        // not in TopicEntity
            totalScore = 0,        // not in TopicEntity
            score = null,     // nullable, default null
            isReAttempt = 0,        // not in TopicEntity
            totalViews = 0         // not in TopicEntity
        )
    }

    @JvmStatic
    fun fromEntityList(entities: List<TopicEntity>): List<TrainingTopicDataResponseMO> {
        return entities.map { fromEntity(it) }
    }

    @JvmStatic
    fun fromTopicWithLastSeen(entity: TopicWithLastSeen): TrainingTopicDataResponseMO {
        return TrainingTopicDataResponseMO(
            courseId                  = entity.courseId,
            courseTitle               = "",        // not in topic_table
            courseSequenceNo          = 0,         // not in topic_table
            companyId                 = 0,         // not in topic_table
            courseDuration            = 0.0,       // not in topic_table
            isOffline                 = 0,         // not in topic_table
            segmentType               = "",        // not in topic_table
            courseTopicId             = entity.courseTopicId,
            courseTopicTitle          = entity.courseTopicTitle,
            topicDuration             = 0.0,       // not in topic_table
            topicSequence             = entity.topicSequence,
            courseTopicType           = "",        // not in topic_table
            courseContentId           = entity.courseContentId,
            fileDownloadName          = "",        // not in topic_table
            fileViewName              = entity.fileViewName,
            fileURL                   = entity.fileURL,
            contentSize               = 0,         // not in topic_table
            contentVersion            = 0,         // not in topic_table
            courseContentDuration     = 0.0,       // not in topic_table
            courseContentType         = entity.contentType,
            courseContentThumbnailURL = entity.thumbnailURL,
            languageType              = entity.contentLanguageType,
            lastSeen                  = entity.lastseen,   // ← from JOIN
            isActive                  = 0,         // not in topic_table
            assessmentId              = 0,         // not in topic_table
            assessmentName            = "",        // not in topic_table
            lastAccessDateTime        = "",        // not in topic_table
            totalQuestionCount        = 0,         // not in topic_table
            attemptQuestionCount      = 0,         // not in topic_table
            totalScore                = 0,         // not in topic_table
            score                     = null,
            isReAttempt               = 0,         // not in topic_table
            totalViews                = 0          // not in topic_table
        )
    }

    @JvmStatic
    fun fromTopicWithLastSeenList(entities: List<TopicWithLastSeen>): List<TrainingTopicDataResponseMO> {
        return entities.map { fromTopicWithLastSeen(it) }
    }
}