package com.sisindia.ai.mtrainer.android.features.trainingprogram;

import com.sisindia.ai.mtrainer.android.db.entities.ProgramEntity;
import com.sisindia.ai.mtrainer.android.models.online.TrainingProgramTypeDataResponseMO;

import java.util.ArrayList;
import java.util.List;

public class ProgramEntityMapper {

    public static TrainingProgramTypeDataResponseMO toResponseMO(ProgramEntity entity) {
        return new TrainingProgramTypeDataResponseMO(
                0,               // courseTypeId  — not in ProgramEntity
                "",                          // courseType    — not in ProgramEntity
                entity.programId,
                entity.programName,
                entity.programThumbnail,
                0,               // totalCourseAccessed   — not in ProgramEntity
                0,               // totalCourseAssigned   — not in ProgramEntity
                null,            // totalAssessmenAttempted — nullable Int
                0,               // totalAssessmentAssigned — not in ProgramEntity
                entity.starProgramSeqNo,
                0                // isCertificateEnable   — not in ProgramEntity
        );
    }

    public static List<TrainingProgramTypeDataResponseMO> toResponseMOList(List<ProgramEntity> entities) {
        List<TrainingProgramTypeDataResponseMO> list = new ArrayList<>();
        for (ProgramEntity entity : entities) {
            list.add(toResponseMO(entity));
        }
        return list;
    }
}
