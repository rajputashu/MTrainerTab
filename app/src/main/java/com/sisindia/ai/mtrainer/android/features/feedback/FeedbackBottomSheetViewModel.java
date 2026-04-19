package com.sisindia.ai.mtrainer.android.features.feedback;

import android.app.Application;

import androidx.annotation.NonNull;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;

import javax.inject.Inject;

public class FeedbackBottomSheetViewModel extends MTrainerViewModel {

    public String companyID = Prefs.getString(PrefsConstants.COMPANY_ID);
    public final String role = Prefs.getString(PrefsConstants.ROLE);


    @Inject
    public FeedbackBottomSheetViewModel(@NonNull Application application) {
        super(application);
    }

    void openRatingFragment() {
        message.what = NavigationConstants.CLIENT_RATING;
        liveData.postValue(message);
    }
}
