package com.sisindia.ai.mtrainer.android.features.spi.mounted;

import com.sisindia.ai.mtrainer.android.models.SpiBasicInfoResponse;
import com.sisindia.ai.mtrainer.android.models.spi.MountedResponse;

public  interface  MountedViewListener {
    void onMountedItemClick(SpiBasicInfoResponse.SpiBasicInfoDetailsData item, int position);
}
