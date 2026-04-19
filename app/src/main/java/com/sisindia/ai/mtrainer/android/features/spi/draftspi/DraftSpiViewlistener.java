package com.sisindia.ai.mtrainer.android.features.spi.draftspi;

import com.sisindia.ai.mtrainer.android.models.SpiBasicInfoResponse;

public interface DraftSpiViewlistener {
    void onSpiDraftItemClick(SpiBasicInfoResponse.SpiBasicInfoDetailsData item, int position);
}
