package com.sisindia.ai.mtrainer.android.features.takeassessment;

import android.view.View;
import android.widget.RadioButton;

import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;

public interface AssessmentItemListener {
    void onAssessmentItemClick(AttendanceEntity item, int position);
}
