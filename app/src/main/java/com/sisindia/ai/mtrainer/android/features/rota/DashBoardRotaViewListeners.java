package com.sisindia.ai.mtrainer.android.features.rota;

import com.sisindia.ai.mtrainer.android.models.TrainingCalendarResponse;

public interface DashBoardRotaViewListeners {
    void onPerformanceItemClick();
    void onRotaItemClick(TrainingCalendarResponse.TrainingCalendar item);
}
