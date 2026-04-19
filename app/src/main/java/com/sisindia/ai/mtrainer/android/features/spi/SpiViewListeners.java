package com.sisindia.ai.mtrainer.android.features.spi;

import com.sisindia.ai.mtrainer.android.models.SpiTableDetailsResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingCalendarResponse;

public interface SpiViewListeners {
    void onSpiItemClick(SpiTableDetailsResponse.SpiTableDetailsData item);
}
