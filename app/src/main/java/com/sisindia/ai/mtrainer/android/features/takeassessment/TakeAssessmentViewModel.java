package com.sisindia.ai.mtrainer.android.features.takeassessment;

import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.app.Application;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.AssementEntity;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.features.pretraining.PreTrainingReviewActivity;
import com.sisindia.ai.mtrainer.android.models.PostItem;
import com.sisindia.ai.mtrainer.android.models.assesments.AssignTrainingCourseBody;
import com.sisindia.ai.mtrainer.android.models.assesments.CourseExecutionResultMO;
import com.sisindia.ai.mtrainer.android.models.assesments.CourseListResponse;
import com.sisindia.ai.mtrainer.android.models.assesments.CourseTypeResponseV2;
import com.sisindia.ai.mtrainer.android.models.assesments.ProgramResponseV2;
import com.sisindia.ai.mtrainer.android.models.assesments.TopicListResponse;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class TakeAssessmentViewModel extends MTrainerViewModel implements Filterable {

    public TakeAssessmentRecyclerAdapter takeAssessmentAdapter = new TakeAssessmentRecyclerAdapter();
    private MtrainerDataBase dataBase;
    public ObservableField<Integer> totalEmpSelected = new ObservableField<>(PreTrainingReviewActivity.selectedAssessmentEmpId.size());
    private final List<AttendanceEntity> attendanceResponseList = new ArrayList<>();
    //    Set<String> selectedEmployeeSet = new HashSet<>();
    public ObservableField<List<CourseTypeResponseV2.CourseTypeDataV2>> obsCourseTypeList = new ObservableField<>();
    public ObservableField<List<ProgramResponseV2.ProgramDataV2>> obsProgramList = new ObservableField<>();
    public ObservableField<List<CourseListResponse.CourseListData>> obsCourseList = new ObservableField<>();
    public ObservableField<List<TopicListResponse.TopicListData>> obsTopicList = new ObservableField<>();

    private int selectedCourseTypePos = 0;
    private int selectedProgramPos = 0;
    private int selectedCourseListPos = 0;
    private int selectedTopicListPos = 0;
    private int selectedCourseTypeId = 0;
    private int selectedProgramId = 0;
    private int selectedCourseId = 0;
    public ObservableBoolean isProgramVisible = new ObservableBoolean(false);
    public ObservableBoolean isCourseListVisible = new ObservableBoolean(false);
    public ObservableBoolean isCourseTopicVisible = new ObservableBoolean(false);
    public List<String> coursesIdList = new ArrayList<>();
    public List<String> topicIdList = new ArrayList<>();
    //    public List<String> attendanceEmpIdList = new ArrayList<>();
    Set<String> attendanceEmpIdList = new LinkedHashSet<>();
    public ObservableBoolean isCourseAlreadyAssigned = new ObservableBoolean(false);
    public ObservableBoolean isTimerRunning = new ObservableBoolean(false);
    public ObservableField<String> obsTimerValue = new ObservableField<>("");


    @Inject
    public TakeAssessmentViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
    }

    public AssessmentItemListener assessmentItemListener = new AssessmentItemListener() {
        @Override
        public void onAssessmentItemClick(AttendanceEntity item, int position) {
            /*if (PreTrainingReviewActivity.selectedAssessmentEmpId.add(item.empCode)) {
                // Added
                AssementEntity entity = new AssementEntity();
                entity.setEmpId(item.employeeId);
                entity.setEmpCode(item.empCode);
                entity.setEmpName(item.employeeName);
                entity.setRotaId(Prefs.getInt(PrefsConstants.ROTA_ID));
                entity.setId(entity.getRotaId() + "_" + entity.getEmpCode());
                dataBase.getAssessmentDao().insertAssessment(entity)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            } else {
                // Remove
                PreTrainingReviewActivity.selectedAssessmentEmpId.remove(item.empCode);
                String id = Prefs.getInt(PrefsConstants.ROTA_ID) + "_" + item.empCode;
                dataBase.getAssessmentDao().deleteAssessment(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }
            message.what = NavigationConstants.UPDATE_ASSESSMENT_VIEW;
            liveData.postValue(message);
            takeAssessmentAdapter.notifyItemChanged(position);
            totalEmpSelected.set(PreTrainingReviewActivity.selectedAssessmentEmpId.size());
            */
        }
    };

    public void onViewClicks(View view) {
        if (view.getId() == R.id.flButtonTraining) {
            /*if (!PreTrainingReviewActivity.selectedAssessmentEmpId.isEmpty()) {
                message.what = NavigationConstants.ON_OPEN_CHOOSE_ASSESSMENT;
                liveData.postValue(message);
            } else {
                showToast("Please Select Employee");
            }*/
            if (isAssessmentMapped)
                insertAllAssessmentsToDB();
            else
                showToast("Please assigned course to attendees");
        } else if (view.getId() == R.id.assignCourseButton) {
            if (isCourseAlreadyAssigned.get()) {
                if (isTimerRunning.get()) {
                    showToast("Please wait; the updated report will be available in 10 minutes");
                } else {
                    getCourseAssessmentReportList();
                }
            } else {
                validateCourseSelection();
            }
        }
    }

    public void initviewmodel() {
        Observable<List<AttendanceEntity>> canShowData =
                Observable.combineLatest(dataBase.getAttendanceDao()
                                .getAttendanceListForAssessment(Prefs.getInt(PrefsConstants.ROTA_ID))
                                .toObservable(),
                        dataBase.getAssessmentDao()
                                .getAttendanceIdListForAssessment(Prefs.getInt(PrefsConstants.ROTA_ID))
                                .toObservable(),
                        (master, saved) -> {
                            PreTrainingReviewActivity.selectedAssessmentEmpId = new HashSet<>(saved);
                            return master;
                        });

        addDisposable(canShowData.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onAssessmentListSuccess));
    }

    LiveData<List<PostItem>> getPostList() {
        return dataBase.getAttendanceDao().getAttendancePostList(Prefs.getInt(PrefsConstants.ROTA_ID));
    }

    private void onAssessmentListSuccess(List<AttendanceEntity> attendanceEntityList) {
        attendanceEmpIdList.clear();

        /*attendanceResponseList.clear();
        List<AttendanceEntity> modifiedList = getListWithLetter(attendanceEntityList);
        attendanceResponseList.addAll(modifiedList);
        takeAssessmentAdapter.setSelectedEmployeeSet(PreTrainingReviewActivity.selectedAssessmentEmpId);
        takeAssessmentAdapter.clearAndSetItems(modifiedList);*/

        // Keep ONLY real users here
        attendanceResponseList.clear();
        attendanceResponseList.addAll(attendanceEntityList);

        // UI list with headers
        List<AttendanceEntity> modifiedList = getListWithLetter(attendanceEntityList);

        takeAssessmentAdapter.setSelectedEmployeeSet(PreTrainingReviewActivity.selectedAssessmentEmpId);
        takeAssessmentAdapter.clearAndSetItems(modifiedList);

    }

    private List<AttendanceEntity> getListWithLetter(List<AttendanceEntity> attendanceEntities) {
        List<AttendanceEntity> modifiedList = new ArrayList<>();

        // Safe sorting
        attendanceEntities.sort((u1, u2) -> {
            String name1 = (u1.employeeName != null && !u1.employeeName.isEmpty()) ? u1.employeeName : u1.empName;
            String name2 = (u2.employeeName != null && !u2.employeeName.isEmpty()) ? u2.employeeName : u2.empName;

            if (name1 == null) name1 = "";
            if (name2 == null) name2 = "";

            return name1.compareToIgnoreCase(name2);
        });

        String lastHeader = "";

        for (AttendanceEntity user : attendanceEntities) {

            // Select all attendees by default
            PreTrainingReviewActivity.selectedAssessmentEmpId.add(user.empCode);
//            attendanceEmpIdList.add(String.valueOf(user.employeeId));
            if (user.employeeId != 0) {
                attendanceEmpIdList.add(String.valueOf(user.employeeId));
            }

            // -------- FIXED HEADER LOGIC ----------
            String name = (user.employeeName != null && !user.employeeName.isEmpty())
                    ? user.employeeName
                    : user.empName;

            if (name == null || name.isEmpty()) {
                name = "#"; // fallback for unnamed
            }

            String header = String.valueOf(name.charAt(0)).toUpperCase();

            if (!TextUtils.equals(lastHeader, header)) {
                lastHeader = header;
                AttendanceEntity headerEntity = new AttendanceEntity();
                headerEntity.isselected = true;
                headerEntity.header = header;
                modifiedList.add(headerEntity);
            }

            modifiedList.add(user);
        }

        return modifiedList;
    }


    public AssessmentQueryListener queryListener = query -> getFilter().filter(query);

    @Override
    public Filter getFilter() {
        return employeeFilter;
    }

    private Filter employeeFilter = new Filter() {
        ArrayList<AttendanceEntity> filterList = new ArrayList<>();

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint == null || constraint.length() == 0) {
                filterList = new ArrayList<>(attendanceResponseList);
            } else {
                String query = constraint.toString().trim();
                filterList.clear();
                for (AttendanceEntity item : attendanceResponseList) {
                    if (TextUtils.isDigitsOnly(query)) {
                        if (item.empCode != null && item.empCode.startsWith(query))
                            filterList.add(item);
                    } else {
                        /*if (item.employeeName != null && item.employeeName.toLowerCase().startsWith(query.toLowerCase()))
                            filterList.add(item);*/

                        String name = (item.employeeName != null && !item.employeeName.isEmpty())
                                ? item.employeeName
                                : item.empName;

                        if (name != null && name.toLowerCase().startsWith(query.toLowerCase())) {
                            filterList.add(item);
                        }
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null)
                takeAssessmentAdapter.clearAndSetItems((List<AttendanceEntity>) results.values);
        }
    };

    //------------------Course Type---------------
    @Inject
    DashBoardApi dashBoardApi;

    public CourseTypeListener courseTypeListener = position -> {

        selectedCourseTypePos = position;
        if (position > 0) {
            if (!Objects.requireNonNull(obsCourseTypeList.get()).isEmpty()) {
                selectedCourseTypeId = Objects.requireNonNull(obsCourseTypeList.get()).get(position - 1).id;
                fetchProgramList();
            }
        } else {
            Timber.e("Please select course type from spinner");
            isProgramVisible.set(false);
            isCourseListVisible.set(false);
            isCourseTopicVisible.set(false);
        }
        clearChips();
    };

    public ProgramListener programListener = position -> {
        selectedProgramPos = position;
        if (position > 0) {
            if (!Objects.requireNonNull(obsProgramList.get()).isEmpty()) {
                selectedProgramId = Objects.requireNonNull(obsProgramList.get()).get(position - 1).programId;
                fetchCourseList();
            }
        } else {
            Timber.e("Please select program from spinner");
            isCourseListVisible.set(false);
            isCourseTopicVisible.set(false);
        }
        clearChips();
    };

    public CourseListener courseListener = new CourseListener() {
        @Override
        public void onCourseListSelected(int position) {
            selectedCourseListPos = position;
            if (position > 0) {
                selectedCourseId = Objects.requireNonNull(obsCourseList.get()).get(position - 1).courseId;
                fetchCourseTopicList();
                clearChips();
            } else {
                isCourseTopicVisible.set(false);
            }
        }

        @Override
        public void onCourseListSelected(int position, CourseListResponse.CourseListData data) {
            /*selectedCourseListPos = position;
            message.what = NavigationConstants.ON_COURSE_CHIP_SELECTED;
            message.obj = data;
            liveData.postValue(message);*/
        }
    };

    public CourseTopicListener topicListener = new CourseTopicListener() {
        @Override
        public void onCourseTopicSelected(int position) {
            selectedTopicListPos = position;
            if (position > 0) {
                message.what = NavigationConstants.ON_COURSE_TOPIC_CHIP_SELECTED;
                message.obj = Objects.requireNonNull(obsTopicList.get()).get(position - 1);
                liveData.postValue(message);
            }
        }
    };

    private void clearChips() {
        message.what = NavigationConstants.ON_REMOVING_ALL_CHIPS;
        liveData.postValue(message);
    }

    void fetchCourseTypeList() {
        setIsLoading(true);
        addDisposable(dashBoardApi.getCourseTypeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onCourseTypeSuccess, this::onApiError));
    }

    private void onCourseTypeSuccess(CourseTypeResponseV2 response) {
        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            if (response.courseTypeList.isEmpty()) {
                showToast(response.statusMessage);
            } else {
                obsCourseTypeList.set(response.courseTypeList);
            }
        }
    }

    private void fetchProgramList() {
        setIsLoading(true);

        int companyId = Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID));
        addDisposable(dashBoardApi.getProgramList(selectedCourseTypeId, companyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onProgramSuccess, this::onApiError));
    }

    private void onProgramSuccess(ProgramResponseV2 response) {
        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            if (response.programDataList.isEmpty()) {
                showToast(response.statusMessage);
            } else {
                isProgramVisible.set(true);
                obsProgramList.set(response.programDataList);
            }
        }
    }

    private void fetchCourseList() {
        setIsLoading(true);

        int companyId = Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID));
        addDisposable(dashBoardApi.getCourseList(selectedCourseTypeId, companyId, selectedProgramId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onCourseListSuccess, this::onApiError));
    }

    private void onCourseListSuccess(CourseListResponse response) {
        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            if (response.courseListData.isEmpty()) {
                showToast(response.statusMessage);
            } else {
                isCourseListVisible.set(true);
                obsCourseList.set(response.courseListData);
            }
        }
    }

    private void fetchCourseTopicList() {
        setIsLoading(true);

        int companyId = Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID));
        addDisposable(dashBoardApi.getTopicsList(selectedCourseId, companyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onCourseTopicSuccess, this::onApiError));
    }

    private void onCourseTopicSuccess(TopicListResponse response) {
        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            if (response.topicListData.isEmpty()) {
                showToast(response.statusMessage);
            } else {
                isCourseTopicVisible.set(true);
                obsTopicList.set(response.topicListData);
            }
        }
    }

    private void validateCourseSelection() {
        if (selectedCourseTypePos == 0) {
            showToast("Please select valid course type");
        } else if (selectedProgramPos == 0) {
            showToast("Please select valid program");
        } else if (selectedCourseListPos == 0) {
            showToast("Please select valid course");
        } else if (topicIdList.isEmpty()) {
            showToast("Please select at least one topic for training");
        } else {
            isLoading.set(View.VISIBLE);
            int trainingId = Prefs.getInt(PrefsConstants.ROTA_ID);
            int trainerId = Prefs.getInt(PrefsConstants.BASE_TRAINER_ID);
            List<String> courseIdsList = Collections.singletonList(String.valueOf(selectedCourseId));

            List<String> finalUniqueTopicIdList = new ArrayList<>(new LinkedHashSet<>(attendanceEmpIdList));
            CourseExecutionResultMO model = new CourseExecutionResultMO(
                    courseIdsList,
                    topicIdList,
//                    attendanceEmpIdList,
                    finalUniqueTopicIdList,
                    trainingId,
                    trainerId
            );

            AssignTrainingCourseBody body = new AssignTrainingCourseBody(model);

            addDisposable(dashBoardApi.mapTrainingCourseTopic(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        isLoading.set(View.GONE);
                        if (response.statusCode == SUCCESS_RESPONSE) {
                            showToast("Topics assigned successfully");
                            isCourseAlreadyAssigned.set(true);
                            getCourseAssessmentReportList();
                        } else {
                            showToast(response.statusMessage);
                        }
                    }, this::onApiError));
        }
    }

    boolean isAssessmentMapped = false;

    private void getCourseAssessmentReportList() {
        setIsLoading(true);

        int trainingId = Prefs.getInt(PrefsConstants.ROTA_ID);
        addDisposable(dashBoardApi.getTopicsStatus(trainingId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    setIsLoading(false);
                    if (response.statusCode == SUCCESS_RESPONSE) {
                        if (response.attendanceList.isEmpty()) {
                            showToast("No data found");
                        } else {
                            startTimer();
//                            takeAssessmentAdapter.clearAndSetItems(response.attendanceList);
                            onAssessmentListSuccess(response.attendanceList);
                            isAssessmentMapped = true;
                        }
                    } else {
                        showToast(response.statusMessage);
                    }
                }, this::onApiError));
    }

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 5 * 60 * 1000;

    public void startTimer() {
        // avoid duplicate timers
        if (isTimerRunning.get()) return;

        isTimerRunning.set(true);

        timeLeftInMillis = 5 * 60 * 1000;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                long minutes = (timeLeftInMillis / 1000) / 60;
                long seconds = (timeLeftInMillis / 1000) % 60;

                obsTimerValue.set(String.format("Wait for %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                isTimerRunning.set(false);
            }
        }.start();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void insertAllAssessmentsToDB() {
        List<AssementEntity> assementEntityList = new ArrayList<>();

        for (int i = 0; i < attendanceResponseList.size(); i++) {
            AssementEntity entity = new AssementEntity();
            entity.setEmpId(attendanceResponseList.get(i).employeeId);
            entity.setEmpCode(attendanceResponseList.get(i).empCode);
            entity.setEmpName(attendanceResponseList.get(i).employeeName);
            entity.setRotaId(Prefs.getInt(PrefsConstants.ROTA_ID));
            entity.setId(entity.getRotaId() + "_" + entity.getEmpCode());
            assementEntityList.add(entity);
        }

        addDisposable(dataBase.getAssessmentDao().insertAllAssessment(assementEntityList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
//                    message.what = NavigationConstants.ON_OPEN_CHOOSE_ASSESSMENT;
                            message.what = NavigationConstants.ON_COMPLETING_ASSESSMENT;
                            liveData.postValue(message);
                            showToast("Assessment completed");
                        }, Throwable::printStackTrace)
        );
    }
}
