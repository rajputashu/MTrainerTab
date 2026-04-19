package com.sisindia.ai.mtrainer.android.features.splash;

import android.app.Application;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SplashViewModel extends MTrainerViewModel {

    private static final int DELAY_TIME = 2000;
    private MtrainerDataBase dataBase;
    @Inject
    AuthApi authApi;
    @Inject
    DashBoardApi dashBoardApi;

    private final Handler splashHandler = new Handler();

    private final Runnable loginRunnable = () -> {
        setIsLoading(false);
        addDisposable(dataBase.getTrainingCalenderDao()
                .flushTrainingCalender()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::openLogin, this::onApiError));
    };

    private final Runnable dashBoardRunnable = () -> {
        setIsLoading(false);
        addDisposable(dataBase.getTrainingCalenderDao()
                .flushTrainingCalender()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::openDashboard, this::onApiError));
    };

    @Inject
    public SplashViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
    }

    void checkUserState() {

        if (!Prefs.getBoolean(PrefsConstants.OPENED_FIRST_TIME, false)) {
            Prefs.putBooleanOnMainThread(PrefsConstants.OPENED_FIRST_TIME, true);
            getPreAuthToken();
        } else if (Prefs.getBoolean(PrefsConstants.IS_LOGGED_IN)) {
            getPreAuthDashboardToken();
        } else {
            getPreAuthToken();
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        splashHandler.removeCallbacks(loginRunnable);
        splashHandler.removeCallbacks(dashBoardRunnable);
    }

    void getPreAuthToken() {
        setIsLoading(true);
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse, this::onApiError));
    }

    void getPreAuthDashboardToken() {
        setIsLoading(true);
        addDisposable(authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES,
                        RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onPreAuthResponse1, this::onApiError));
    }

    private void onPreAuthResponse(PreAuthResponse response) {
        setIsLoading(false);
        if (response != null && !TextUtils.isEmpty(response.accessToken)) {
            Prefs.putString(PrefsConstants.AUTH_TOKEN_KEY, response.tokenType.concat(" ").concat(response.accessToken));
            Prefs.putString(PrefsConstants.TOKEN_EXPIRE_DATE, response.expires);
            splashHandler.postDelayed(loginRunnable, DELAY_TIME);
        } else {
            showToast(R.string.something_went_wrong);
        }
    }

    private void onPreAuthResponse1(PreAuthResponse response) {
        setIsLoading(false);
        if (response != null && !TextUtils.isEmpty(response.accessToken)) {
            Prefs.putString(PrefsConstants.AUTH_TOKEN_KEY, response.tokenType.concat(" ").concat(response.accessToken));
            Prefs.putString(PrefsConstants.TOKEN_EXPIRE_DATE, response.expires);
            splashHandler.postDelayed(dashBoardRunnable, DELAY_TIME);
            // splashHandler.postDelayed(dashBoardRunnable, DELAY_TIME);
        } else {
            showToast(R.string.something_went_wrong);
        }
    }

    private void openLogin() {
        message.what = NavigationConstants.OPEN_LOGIN;
        liveData.postValue(message);
    }

    private void openDashboard() {
        message.what = NavigationConstants.OPEN_DASH_BOARD;
        liveData.postValue(message);
    }
}