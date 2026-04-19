package com.sisindia.ai.mtrainer.android.base;

import android.content.Intent;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.droidcommons.base.BaseActivity;
import com.droidcommons.base.SingleLiveEvent;
import com.droidcommons.base.timer.SingleLiveTimerEvent;
import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.Logger;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.utils.LocationUtils;

import javax.inject.Inject;

import static com.sisindia.ai.mtrainer.android.constants.IntentRequestCodes.REQUEST_CODE_RESOLUTION_REQUIRED;
import static com.sisindia.ai.mtrainer.android.utils.LocationUtilsKt.LOCATION_PERMISSION_RESOLUTION_CODE;
import static com.sisindia.ai.mtrainer.android.utils.LocationUtilsKt.LOCATION_SETTING_RESOLUTION_CODE;

public abstract class MTrainerBaseActivity extends BaseActivity {

    final protected Logger logger = new Logger(this.getClass().getSimpleName());
    @Inject
    protected ViewModelProvider.Factory viewModelFactory;

    @Inject
    protected SingleLiveEvent<Message> liveData;

    @Inject
    protected SingleLiveTimerEvent<Message> liveTimerEvent;

    protected AndroidViewModel getAndroidViewModel(Class type) {

    return (AndroidViewModel) new ViewModelProvider(this, viewModelFactory).get(type);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationUtils.getLocationData(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOCATION_SETTING_RESOLUTION_CODE || requestCode == LOCATION_PERMISSION_RESOLUTION_CODE) {
            LocationUtils.getLocationData(this);
        }
    }
}
