package com.sisindia.ai.mtrainer.android.features.takeassessment;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.AssementEntity;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChooseAssessmentViewModel extends MTrainerViewModel {
    public final ObservableField<Integer> totalpar = new ObservableField<>(0);
    private final MtrainerDataBase dataBase;
//    private boolean haveLatestData = false;

    @Inject
    public ChooseAssessmentViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
    }

    void getAttendanceCount() {
        setIsLoading(true);
        addDisposable(dataBase.getAssessmentDao()
                .getAttendanceListForAssessment(Prefs.getInt(PrefsConstants.ROTA_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getEmployeeSucess));
    }

    public void onFabClick(View view) {
        if (isLoading.get() == View.GONE) {
            if (totalpar.get() != null && totalpar.get() == 0)
                showToast("No pending assessment");
            else {
                message.what = NavigationConstants.OPEN_ASSESSMENT_VIDEO_ACTIVITY;
                liveData.postValue(message);
            }
        } else
            showToast("Please wait loading data...");
    }

    private void getEmployeeSucess(List<AssementEntity> assementEntityList) {
        totalpar.set(assementEntityList.size());
        setIsLoading(false);
    }
}
