package com.sisindia.ai.mtrainer.android.features.takeassessment;

import android.app.Application;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.AssementEntity;
import com.sisindia.ai.mtrainer.android.models.AssessmentModel;
import com.sisindia.ai.mtrainer.android.models.AssessmentResponseModel;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AssesmentQuestionViewModel extends MTrainerViewModel {
    private final List<AssementEntity> employeeNameList = new ArrayList<>();
    private final List<String> questionList = new ArrayList<>();
    public ObservableField<String> participentName = new ObservableField<>();
    public ObservableField<String> totalparticipentNumber = new ObservableField<>("00");
    public ObservableField<String> currentparticipentNumber = new ObservableField<>("00");
    public ObservableField<String> question = new ObservableField<>();
    public ObservableField<String> options = new ObservableField<>();
    private final MtrainerDataBase dataBase;
    private boolean haveQuestion = false;
    private boolean haveEmp = false;
    private int count = 0;
    AssementEntity currentAssessmentEntity;
    private CountDownTimer timer;
    private final int MAX_DURATION = 60 * 1000;
    public ObservableField<Integer> timeTracker = new ObservableField<>(0);

    @Inject
    DashBoardApi api;

    @Inject
    public AssesmentQuestionViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
        initCountUpTimer();
    }

    void getAssessmentQuestions() {
        setIsLoading(true);
        addDisposable(api.getAssessmentQuestion(new AssessmentModel(Prefs.getString(PrefsConstants.COMPANY_ID)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getAssessmentQuestions, Timber::e));
        getEmployeeList();
    }

    private void getAssessmentQuestions(AssessmentResponseModel assessmentResponseModel) {
        if (assessmentResponseModel != null && !assessmentResponseModel.assesmentQustionsList.isEmpty()) {
            addDisposable(dataBase.getQuestionDao()
                    .insertMasterQuestionList(assessmentResponseModel.assesmentQustionsList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::getQuestion));
        } else {
            getQuestion();
            showToast(R.string.assessment_q_null);
        }
    }

    void getQuestion() {
        addDisposable(dataBase.getQuestionDao()
                .getQuestionList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setQuestionList));
    }

    void setQuestionList(List<String> questionList) {
        this.questionList.addAll(questionList);
        if (!questionList.isEmpty())
            haveQuestion = true;
        getNext();
    }

    void getNext() {
        if (haveEmp && haveQuestion) {
            setIsLoading(false);
            List<String> questionList = new ArrayList<>(this.questionList);
            Random random = new Random();
            if (!questionList.isEmpty())
                question.set(questionList.get(random.nextInt(questionList.size())));
            if (!employeeNameList.isEmpty()) {
                currentAssessmentEntity = employeeNameList.get(count);
                participentName.set(employeeNameList.get(count).getEmpName());
            }
            count = count + 1;
            if (count < 10)
                currentparticipentNumber.set("0" + count);
            else
                currentparticipentNumber.set(String.valueOf(count));
            if (currentparticipentNumber.get().equals(totalparticipentNumber.get())) {
                message.what = NavigationConstants.UPDATE_ASSESSMENT_VIDEO_VIEW;
                liveData.postValue(message);
            }
        }
    }

    private void getEmployeeList() {
        addDisposable(dataBase.getAssessmentDao()
                .getAttendanceListForAssessment(Prefs.getInt(PrefsConstants.ROTA_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getEmployeeSuccess));
    }

    private void getEmployeeSuccess(List<AssementEntity> assementEntityList) {
        employeeNameList.clear();
        employeeNameList.addAll(assementEntityList);
        if (assementEntityList.size() < 10)
            totalparticipentNumber.set("0" + employeeNameList.size());
        else
            totalparticipentNumber.set(String.valueOf(employeeNameList.size()));
        haveEmp = true;
        getNext();
    }

    private void initCountUpTimer() {
        timer = new CountDownTimer(MAX_DURATION, 1000) {

            public void onTick(long millisUntilFinished) {
                timeTracker.set((int) (MAX_DURATION - millisUntilFinished) / 1000);
            }

            public void onFinish() {
                message.what = NavigationConstants.STOP_ASSESSMENT_VIDEO;
                liveData.postValue(message);
                showToast("Video max limit reached");
            }
        };
    }

    void saveDataToDb(String fileName) {
        addDisposable(dataBase.getAssessmentDao()
                .updateAssessmentDetail(fileName,
                        question.get(),
                        Prefs.getInt(PrefsConstants.ROTA_ID),
                        currentAssessmentEntity.getEmpCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    void startTimer() {
        timer.start();
    }

    void stopTimer() {
        timer.cancel();
    }
}

