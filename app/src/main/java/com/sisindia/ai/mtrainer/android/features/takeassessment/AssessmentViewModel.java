package com.sisindia.ai.mtrainer.android.features.takeassessment;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;

import com.droidcommons.base.timer.CountUpTimer;
import com.droidcommons.preference.Prefs;
import com.google.gson.Gson;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.commons.NavigationUiRecyclerAdapter;
import com.sisindia.ai.mtrainer.android.commons.NavigationViewListeners;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.models.AssessmentModel;
import com.sisindia.ai.mtrainer.android.models.AssessmentResponseModel;
import com.sisindia.ai.mtrainer.android.models.PostItem;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceResponse;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.uimodels.NavigationUIModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AssessmentViewModel extends MTrainerViewModel {

    public ObservableField<String> participentName = new ObservableField<>();
    public ObservableField<String> participentNumber = new ObservableField<>();
    public ObservableField<String> question = new ObservableField<>();
    public ObservableField<String> options = new ObservableField<>();
    private MtrainerDataBase dataBase;
    public static PostItem selectedPost = null;

    @Inject
    DashBoardApi api;

    public NavigationUiRecyclerAdapter navigationRecyclerAdapter = new NavigationUiRecyclerAdapter();

    public NavigationViewListeners viewListeners = new NavigationViewListeners() {
        @Override
        public void onNavigationItemClick(NavigationUIModel model) {

        }
    };

    @Inject
    @Named("StartTraining")
    ArrayList<NavigationUIModel> navigationList;

    @Inject
    public AssessmentViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);

    }

    public void initViewModel() {
        navigationRecyclerAdapter.clearAndSetItems(navigationList);
    }

    public void getAssesmentQuestions() {
        //get Attendace name list
        //api call for getting assessment questions
        addDisposable(api.getAssessmentQuestion(new AssessmentModel(Prefs.getString(PrefsConstants.COMPANY_ID))).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getAssesmentQuestions, Timber::e));
    }

    public void getAssesmentQuestions(AssessmentResponseModel assessmentResponseModel) {
        if (assessmentResponseModel != null && !assessmentResponseModel.assesmentQustionsList.isEmpty()) {
            Log.d("Response == ", "" + new Gson().toJson(assessmentResponseModel));
            question.set(assessmentResponseModel.assesmentQustionsList.get(0).question);
        } else {
            showToast(R.string.assessment_q_null);
        }

    }

   /* LiveData<List<AttendanceEntity>> getEmployeeList() {
        return dataBase.getAttendanceDao().getAttendanceList(Prefs.getInt(PrefsConstants.ROTA_ID));
    }*/
}
