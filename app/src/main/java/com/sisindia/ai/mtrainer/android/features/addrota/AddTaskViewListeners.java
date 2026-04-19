package com.sisindia.ai.mtrainer.android.features.addrota;

import com.sisindia.ai.mtrainer.android.db.entities.ReasonEntity;
import com.sisindia.ai.mtrainer.android.db.entities.SiteEntity;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

public interface AddTaskViewListeners {

    void onDateChanged(int viewId, LocalDate date);

    void onStartTimeSelected(LocalTime time);

    void onEndTimeSelected(LocalTime time);

    void onItemSpinnerSelected(int viewId, int position);

    default void onSendEmailClicked(int position) {
    }
}

