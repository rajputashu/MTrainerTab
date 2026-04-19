package com.sisindia.ai.mtrainer.android.features.feedback;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.FeedbackConstants;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.models.CancelTrainingRequest;
import com.sisindia.ai.mtrainer.android.models.CancelTrainingResponse;
import com.sisindia.ai.mtrainer.android.models.ContactListResponse;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

public class MainFeedbackViewModel extends MTrainerViewModel implements MainFeedbackFragmentRecylerAdapter.ClearActivityView {

    public MainFeedbackFragmentRecylerAdapter feedbackActivityRecylerAdapter = new MainFeedbackFragmentRecylerAdapter(this::clearActivityView);
    private MtrainerDataBase dataBase;

    @Inject
    public MainFeedbackViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
    }

    @Inject
    DashBoardApi dashBoardApi;

    public void getContactList() {
        setIsLoading(true);

        /*String empId = Prefs.getString(PrefsConstants.EMPLOYEE_ID);
        String status = Prefs.getString(PrefsConstants.STATUS);
        String lat = String.valueOf(Prefs.getDouble(PrefsConstants.LAT));
        String longi = String.valueOf(Prefs.getDouble(PrefsConstants.LONGI));
*/
        CancelTrainingRequest request = new CancelTrainingRequest();

        /*addDisposable(dashBoardApi.getRattingQuestionList(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTrainingPerformanceSuccess, this::onApiError));*/
    }

    private void onTrainingPerformanceSuccess(CancelTrainingResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            /*feedbackActivityRecylerAdapter.clearAndSetItems(response.statusCode);*/
        }
    }

    LiveData<List<ContactListResponse.ClientData>> getClientList() {
        return dataBase.getClientListDao().getClientList(Prefs.getInt(PrefsConstants.UNIT_ID));
    }

    void onClientListDbSuccess(List<ContactListResponse.ClientData> clientData) {
        ContactListResponse.ClientData data = new ContactListResponse.ClientData();
        data.clientName = " Other Client's Representative";
        data.clientId = FeedbackConstants.CLIENT_REPRESENTATIVE;
        clientData.add(data);
        feedbackActivityRecylerAdapter.clearAndSetItems(clientData);
    }

    @Override
    public void clearActivityView() {
        message.what = NavigationConstants.CLEAR_VIEW;
        liveData.postValue(message);
    }
}