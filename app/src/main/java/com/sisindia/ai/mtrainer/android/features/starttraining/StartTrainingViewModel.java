package com.sisindia.ai.mtrainer.android.features.starttraining;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.CLOSE_REMARKS_DIALOG;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_ONLINE_TRAINING_COURSES;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.LiveData;
import androidx.room.EmptyResultSetException;

import com.droidcommons.base.timer.CountUpTimer;
import com.droidcommons.preference.Prefs;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.base.RequestHeaderInterceptor;
import com.sisindia.ai.mtrainer.android.commons.NavigationUiRecyclerAdapter;
import com.sisindia.ai.mtrainer.android.commons.NavigationViewListeners;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.MtrainerLogIntercepter;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.SavedTopic;
import com.sisindia.ai.mtrainer.android.db.entities.EmployeeApiEntity;
import com.sisindia.ai.mtrainer.android.db.entities.TimeLineEntity;
import com.sisindia.ai.mtrainer.android.features.addrota.AddRotaViewmodel;
import com.sisindia.ai.mtrainer.android.features.pretraining.PreTrainingReviewActivity;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsRequest;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsResponse;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.SitePostRequest;
import com.sisindia.ai.mtrainer.android.models.SitePostResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceRequest;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceResponse;
import com.sisindia.ai.mtrainer.android.models.UnitAttendanceRequest;
import com.sisindia.ai.mtrainer.android.models.UnitAttendanceResponse;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;
import com.sisindia.ai.mtrainer.android.uimodels.NavigationUIModel;
import com.sisindia.ai.mtrainer.android.utils.ApiUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class StartTrainingViewModel extends MTrainerViewModel {

    @Inject
    public CountUpTimer timer;
    private MtrainerDataBase dataBase;

    public float rating = Prefs.getFloat(PrefsConstants.RATING, -1);
    public boolean canDeleteRota = Prefs.getBoolean(PrefsConstants.CAN_DELETE_ROTA, false);
    boolean canShow = Prefs.getBoolean(PrefsConstants.CAN_SHOW, false);

    private Observable<Boolean> dataChain;

    @Inject
    DashBoardApi dashBoardApi;

    @Inject
    AuthApi authApi;

    public String companyID = Prefs.getString(PrefsConstants.COMPANY_ID);
    public final String role = Prefs.getString(PrefsConstants.ROLE);

    public NavigationUiRecyclerAdapter navigationRecyclerAdapter = new NavigationUiRecyclerAdapter();
    public ObservableField<String> unitName = new ObservableField<>();
    public ObservableField<String> plannedTime = new ObservableField<>();
    public ObservableField<String> totalEmployee = new ObservableField<>();
    public ObservableField<String> totalPost = new ObservableField<>();
    public ObservableField<String> totalTopic = new ObservableField<>();
    public ObservableField<String> remarkstext = new ObservableField<>("");

    public ObservableField<String> selectedPost = new ObservableField<>();
    public ObservableField<String> selectedEmployee = new ObservableField<>();
    public ObservableInt selectedTopic = new ObservableInt();
    public ObservableInt selectedAdhocTopic = new ObservableInt();


    public NavigationViewListeners viewListeners = this::onStartTrainingNavigationClick;

    @Inject
    @Named("StartTraining")
    ArrayList<NavigationUIModel> navigationList;

    @Inject
    public StartTrainingViewModel(@NonNull Application application) {
        super(application);
        initRetrofit();
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse1, this::onApiError));

        dataBase = MtrainerDataBase.getDatabase(application);
        this.unitName.set(Prefs.getString(PrefsConstants.UNIT_NAME));
        this.plannedTime.set(Prefs.getString(PrefsConstants.ESTIMATE_STATR_TIME) + " - " + Prefs.getString(PrefsConstants.ESTIMATE_END_TIME));
        String rotaTopicId = Prefs.getString(PrefsConstants.TOPIC_ID);
        if (rotaTopicId.length() != 1) {
            StringTokenizer tokenizer = new StringTokenizer(rotaTopicId, ",");
            tokenizer.countTokens();
            totalTopic.set(String.valueOf(tokenizer.countTokens()));
        } else if (!rotaTopicId.trim().isEmpty()) {
            totalTopic.set("01");
        } else
            totalTopic.set("00");
        if (Prefs.getString(PrefsConstants.ROLE).equals("Training Champ"))
            canShow = true;
    }

    LiveData<Integer> getEmployeeCount() {
        return dataBase.getMasterAttendanceDao().getTotalEmployeeList(String.valueOf(Prefs.getInt(PrefsConstants.UNIT_ID)), Prefs.getInt(PrefsConstants.ROTA_ID));
    }


    void updateEmpCount(Integer employeeCount) {
        if (employeeCount != null)
            totalEmployee.set(String.valueOf(employeeCount));
        else
            totalEmployee.set("0");
    }

    private void onStartTrainingNavigationClick(NavigationUIModel model) {

        switch (model.getViewType()) {
            case TAKE_ATTENDANCE:
                message.what = NavigationConstants.ON_TAKE_ATTENDANCE_CLICK;
                break;

            case TAKE_TRAINING_PHOTOS:
                message.what = NavigationConstants.ON_TAKE_TRAINING_PHOTO;
                break;
            case CLIENT_REPORT:
                message.what = NavigationConstants.CLIENT_REPORT;
                break;

            case CHOOSE_TOPICS_TRAINED:
                message.what = NavigationConstants.ON_OPEN_CHOOSE_TOPICS_TRAINED;
                break;

            case TAKE_ASSESSMENT:
                message.what = NavigationConstants.ON_OPEN_TAKE_ASSESSMENT;
                break;

            case ASSESSMENT_REPORTS:
                message.what = NavigationConstants.ON_OPEN_ASSESSMENT_REPORT;
                break;

            case TAKE_RPL:
                message.what = NavigationConstants.ON_OPEN_RPL_FORM;
                break;

            case REMARKS:
                message.what = NavigationConstants.REMARKS;
                break;
        }

        liveData.postValue(message);
    }

    public void initViewModel() {
        navigationRecyclerAdapter.clearAndSetItems(navigationList);
    }

    ArrayList<SavedTopic> savedTopic = new ArrayList<SavedTopic>();

    //below method one for training done button clicked
    public void onClickSubmitReport(View view) {


        message.what = NavigationConstants.GET_END_LOCATION;
        liveData.postValue(message);
    }

    private void onSucessDBSaveTest() {
        clearTempData();
        Date date = new Date();
        String duty = "Training Completed";
        //  String km="kft";
        String km = Prefs.getString(PrefsConstants.UNIT_NAME);

        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String currenttime = sdf2.format(date);

        TimeLineEntity timeLineResponse = new TimeLineEntity();
        timeLineResponse.dutyon = duty;
        timeLineResponse.time = currenttime;
        timeLineResponse.kmmeter = km;

        dataBase.getTimeLineDao().insertTimeline(timeLineResponse)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        message.what = NavigationConstants.ON_OPEN_AGAIN_DASHBOARD_SCREEN;
        liveData.postValue(message);
    }

    private void onSitePostSuccess(SitePostResponse response) {

       /* setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            totalPost.set(String.valueOf(response.postResponses.size()));
            dataBase.getMasterPostDao().insertMasterPostList(response.postResponses)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();*/
        setIsLoading(false);

        if (response.statusCode == SUCCESS_RESPONSE) {
            Set<Integer> uniquePost = new HashSet<>();
            for (SitePostResponse.PostResponse post : response.postResponses) {
                if (post.siteID == Prefs.getInt(PrefsConstants.UNIT_ID))
                    uniquePost.add(post.siteID);
            }
            totalPost.set(String.valueOf(uniquePost.size()));

            dataBase.getMasterPostDao().insertMasterPostList(response.postResponses)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }

    private void onTrainingAttendanceSuccess(UnitAttendanceResponse response) {

        if (response.statusCode == SUCCESS_RESPONSE) {
            if (response.scoreMetric != null && !response.scoreMetric.isEmpty())
                Prefs.putString(PrefsConstants.SCORE_METRIC, String.valueOf(response.scoreMetric.get(0).score));
            dataBase.getMasterAttendanceDao().insertMasterEmployeeList(response.attendanceResponses)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }

    private void onMasterAttendanceSuccess(TrainingAttendanceResponse response) {

        if (response.statusCode == SUCCESS_RESPONSE) {
            //totalEmployee.set(String.valueOf(response.attendanceResponses.size()));
            if (response.scoreMetric != null && !response.scoreMetric.isEmpty())
                Prefs.putString(PrefsConstants.SCORE_METRIC, String.valueOf(response.scoreMetric.get(0).score));

            addDisposable(dataBase.getMasterAttendanceDao().insertMasterEmployeeList(response.attendanceResponses)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
            if (response.employeeCount != null && !response.employeeCount.isEmpty()) {
                int lastPage = (int) Math.ceil(response.employeeCount.get(0).employeeCount / (double) 50);
                Prefs.putInt(PrefsConstants.LAST_PAGE_NUMBER, lastPage);
                getEmployeeList(response.employeeCount.get(0).currentPageNumber,
                        50, lastPage);
            }
        }
    }

    /*void updateTotalEmployee (int count) {
        totalEmployee.set(String.valueOf(count));
    }*/


    private void onChooseTopicsSuccess(ChooseTopicsResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getTopicDao().insertMasterTopic(response.topicsResponses)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }

    void updateSummaryUI() {
        selectedEmployee.set(String.valueOf(Prefs.getInt(PrefsConstants.SELECTED_EMP_COUNT, 0)));
        selectedPost.set(String.valueOf(Prefs.getInt(PrefsConstants.SELECTED_POST_COUNT, 0)));
        //selectedTopic.set((Prefs.getInt(PrefsConstants.SELECTED_TOPIC_COUNT, 0)));
        selectedAdhocTopic.set(Prefs.getInt(PrefsConstants.SELECTED_ADHOC_TOPIC_COUNT, 0));

    }

    void flushData() {
        int rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);

        if (canDeleteRota) {
            addDisposable(dashBoardApi.deleteRota(rotaId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::deleteRotaFromMaster, this::onApiError));
        }

        // Attendance Deleted
        dataBase.getAttendanceDao().deleteSyncedAttendance(rotaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        // Feedback Deleted
        dataBase.getSavedFeedbackDao().deleteFeedback(String.valueOf(rotaId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        // Feedback Reason Deleted
        dataBase.getSavedFeedbackReasonDao().deleteReason(String.valueOf(rotaId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        // Topics Deleted
        dataBase.getSavedTopicDao().deleteSavedTopicByRotaId(rotaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        // Delete Pic
        dataBase.getAttendancePhotoDao().flushAttendancePhoto(rotaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        dataBase.getTrainingCalenderDao().updateInProgressStatus(rotaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();


        dataBase.getPhotoAttachmentDao().flushPhotoAttachment(rotaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        // Delete Assessment Data
        dataBase.getAssessmentDao().deleteSyncedAssessment(rotaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        dataBase.getSavedClientReportCcDao().deleteSyncedClientCc(rotaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        dataBase.getSavedClientReportToDao().deleteSyncedClientEmail(rotaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        // Rota Deleted
        if (!canDeleteRota)
            dataBase.getTrainingFinalSubmitDao().deleteSyncedRota(rotaId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::canGoBack);


        // saved adhoc topic deleted
        dataBase.getAdhocSavedTopicsDao().deleteAdhocSavedTopicByRotaId(rotaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        //remove client employee count
        Prefs.edit().remove(PrefsConstants.CLIENT_EMPLOYEE_COUNT).apply();


    }

    private void deleteRotaFromMaster(BaseApiResponse response) {
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getTrainingCalenderDao().deleteRotaFromMaster(Prefs.getInt(PrefsConstants.ROTA_ID))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::deleteSyncRota);
        }
    }

    private void deleteSyncRota() {
        dataBase.getTrainingFinalSubmitDao().deleteSyncedRota(Prefs.getInt(PrefsConstants.ROTA_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::canGoBack);
    }

    private void canGoBack() {
        clearTempData();
        message.what = NavigationConstants.ON_OPEN_AGAIN_DASHBOARD_SCREEN;
        liveData.postValue(message);
    }

    void startRestoreSequence() {
        setIsLoading(true);
        dataBase.getAssessmentDao().getAttendanceIdListForAssessment(Prefs.getInt(PrefsConstants.ROTA_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRestoreAssessmentEmpId);
    }

    //  PreTrainingReviewActivity.selectedAssessmentEmpId this made string
    private void onRestoreAssessmentEmpId(List<String> oldData) {
        PreTrainingReviewActivity.selectedAssessmentEmpId = new HashSet<>(oldData);
        setIsLoading(false);
    }

    public void onRemarkClick(View view) {

        message.what = CLOSE_REMARKS_DIALOG;
    }

    public void onViewClick(View view) {
        if (view.getId() == R.id.onlineTrainingCourseButton) {
            message.what = OPEN_ONLINE_TRAINING_COURSES;
            liveData.postValue(message);
        }
    }

    void saveFinalData() {
        if (isLoading.get() == View.GONE) {
            setIsLoading(true);

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            String endTime = sdf.format(date);
            String totalTrained = String.valueOf(Prefs.getInt(PrefsConstants.SELECTED_EMP_COUNT, 0));
            String givenRating = String.valueOf(rating);

            SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
            String time1 = sdf2.format(date);


            dataBase.getTrainingCalenderDao().updateData(time1, givenRating, totalTrained, 11, Prefs.getInt(PrefsConstants.ROTA_ID))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();

            dataBase.getTrainingFinalSubmitDao().updateEndTime(endTime, String.valueOf(Prefs.getDouble(PrefsConstants.LAT)), String.valueOf(Prefs.getDouble(PrefsConstants.LONGI)), Prefs.getInt(PrefsConstants.ROTA_ID), remarkstext.get())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onSucessDBSaveTest);
        }
    }


    void getEmployeeList(int pageNumber, int range, int lastPage) {
        if (pageNumber >= lastPage)
            return;

        TrainingAttendanceRequest request = new TrainingAttendanceRequest();
        request.currentPage = pageNumber + 1;
        request.range = range;
        request.trainerId = Prefs.getInt(PrefsConstants.BASE_TRAINER_ID);
        Prefs.putInt(PrefsConstants.LAST_FETCH_PAGE, pageNumber + 1);

        addDisposable(dashBoardApi.getAttendance(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMasterAttendanceSuccess, this::onApiError));
    }

    void initDataCollection() {


        setIsLoading(true);
        String unitid = String.valueOf(Prefs.getInt(PrefsConstants.UNIT_ID));
        UnitAttendanceRequest attendanceRequest = new UnitAttendanceRequest(unitid);
        attendanceRequest.range = 50;
        attendanceRequest.currentPage = 1;

        ChooseTopicsRequest topicsRequest = new ChooseTopicsRequest(Prefs.getString(PrefsConstants.COMPANY_ID));

        String userid = Prefs.getString(PrefsConstants.EMPLOYEE_ID);
        SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String currentdate = logDateFormat.format(new Date());

        SitePostRequest postRequest = new SitePostRequest(userid, currentdate);

        addDisposable(dataBase.getEmployeeApiDao().getLastAccessTime(Prefs.getInt(PrefsConstants.UNIT_ID))
                .subscribeOn(Schedulers.io())
                .map(datetime -> ApiUtils.canCallApi(datetime.split(" ")[0]) ? datetime : "-1")
                .onErrorReturn(error -> {
                    if (error instanceof EmptyResultSetException)
                        return "0";
                    else
                        throw new IllegalArgumentException(error.getMessage());
                })
                .flatMap(lastAccessTime -> {
                    if (!lastAccessTime.equals("-1")) {
                        attendanceRequest.lastAccessTime = lastAccessTime;
                        return dashBoardApi.getUnitAttendance(attendanceRequest);
                    } else
                        throw new AddRotaViewmodel.NoDataException();
                })
                .map(response -> {
                    int lastPage = 0;
                    if (response.statusCode == SUCCESS_RESPONSE) {
                        EmployeeApiEntity apiEntity = new EmployeeApiEntity();
                        apiEntity.lastAccessedTime = response.accessedTime;
                        dataBase.getEmployeeApiDao().insertLastAccessedTime(apiEntity);
                        // dataBase.getMasterAttendanceDao().insertUnitEmployeeList(response.attendanceResponses);
                        if (response.employeeCount != null && !response.employeeCount.isEmpty()) {
                            lastPage = (int) Math.ceil(response.employeeCount.get(0).employeeCount / (double) 50);
                        }
                    }
                    return lastPage;
                }).toObservable()
                .flatMap(lastPage -> {
                    if (lastPage <= 1)
                        throw new AddRotaViewmodel.NoDataException();
                    else
                        return Observable.range(2, lastPage - 1);
                })
                .flatMap(currentPage -> {
                    attendanceRequest.currentPage = currentPage;
                    Log.v("Rxjava", "Site API : Current Page : " + currentPage);
                    return dashBoardApi.getUnitAttendance(attendanceRequest).toObservable();
                })
                .map(response -> {
                    //dataBase.getMasterAttendanceDao().insertUnitEmployeeList(response.attendanceResponses);
                    return true;
                })
                .onErrorReturn(error -> (error instanceof AddRotaViewmodel.NoDataException))
                .observeOn(AndroidSchedulers.mainThread())
                .map(bool -> {
                    if (!bool)
                        showToast("Error fetching some employee info");
                    return bool;
                })
                .observeOn(Schedulers.io())
                .lastOrError()
                .flatMap(bool -> dashBoardApi.getSitePostName(postRequest))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sitePostResponse -> {
                    setIsLoading(false);
                    if (sitePostResponse != null)
                        onSitePostSuccess(sitePostResponse);
                }, throwable -> {
                    showToast("Something went wrong");
                    setIsLoading(false);
                }));

      /*  dataChain = Observable.combineLatest(dashBoardApi.getSitePostName(postRequest).toObservable(),
                dashBoardApi.getChooseTopics(topicsRequest).toObservable(),
                dashBoardApi.getUnitAttendance(attendanceRequest).toObservable(),
                new Function3<SitePostResponse, ChooseTopicsResponse, UnitAttendanceResponse, Boolean>() {
                    @Override
                    public Boolean apply(SitePostResponse sitePostResponse, ChooseTopicsResponse chooseTopicsResponse, UnitAttendanceResponse unitAttendanceResponse) throws Exception {
                        if(chooseTopicsResponse != null)
                            onChooseTopicsSuccess(chooseTopicsResponse);
                        if(sitePostResponse != null)
                            onSitePostSuccess(sitePostResponse);
                        if(unitAttendanceResponse != null)
                            onTrainingAttendanceSuccess(unitAttendanceResponse);
                        return sitePostResponse != null && unitAttendanceResponse != null && chooseTopicsResponse != null;
                    }
                });
        addDisposable( dataChain.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respose -> {if(!respose)
                    showToast("Unable to fetch some details");}, this::onApiError));*/
    }

    private void initRetrofit() {
        OkHttpClient.Builder builder1 = new OkHttpClient.Builder()
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        RequestHeaderInterceptor headerInterceptor = new RequestHeaderInterceptor();
        MtrainerLogIntercepter mtrainerLogIntercepter = new MtrainerLogIntercepter();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        builder1.addInterceptor(headerInterceptor);
        builder1.addInterceptor(mtrainerLogIntercepter);

        builder1.addInterceptor(httpLoggingInterceptor);
        Retrofit retrofit1 = new Retrofit.Builder().baseUrl(BuildConfig.MTRAINER_HOST).client(builder1.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        authApi = retrofit1.create(AuthApi.class);
    }

    private void onPreAuthResponse1(PreAuthResponse response) {
        if (response != null && !TextUtils.isEmpty(response.accessToken)) {
            Prefs.putString(PrefsConstants.AUTH_TOKEN_KEY, response.tokenType.concat(" ").concat(response.accessToken));
            Prefs.putString(PrefsConstants.TOKEN_EXPIRE_DATE, response.expires);
        } else {
            showToast(R.string.something_went_wrong);
        }
    }


    void fetchMasterEmployee() {
        int lastPageNumber = Prefs.getInt(PrefsConstants.LAST_PAGE_NUMBER, -1);
        int lastAccessPage = Prefs.getInt(PrefsConstants.LAST_FETCH_PAGE, -1);
        TrainingAttendanceRequest attendanceRequest = new TrainingAttendanceRequest();
        if (lastPageNumber == -1 || lastAccessPage == -1) {
            attendanceRequest.currentPage = 1;
        } else if (lastAccessPage == lastPageNumber)
            return;
        else {
            attendanceRequest.currentPage = lastAccessPage;
        }
        attendanceRequest.range = 50;
        attendanceRequest.trainerId = Prefs.getInt(PrefsConstants.BASE_TRAINER_ID);
        addDisposable(dashBoardApi.getAttendance(attendanceRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMasterAttendanceSuccess, this::onApiError));
    }

    private void clearTempData() {
        setIsLoading(true);
        Prefs.putStringOnMainThread(PrefsConstants.STARTED_TIME, "-1");
        Prefs.putStringOnMainThread(PrefsConstants.STARTED_TIME_FOR_SERVER, "-1");
        Prefs.putIntOnMainThread(PrefsConstants.SELECTED_TOPIC_COUNT, 0);
        Prefs.putIntOnMainThread(PrefsConstants.SELECTED_ADHOC_TOPIC_COUNT, 0);
        Prefs.putIntOnMainThread(PrefsConstants.SELECTED_POST_COUNT, 0);
        Prefs.putBooleanOnMainThread(PrefsConstants.CAN_DELETE_ROTA, false);
        Prefs.putBooleanOnMainThread(PrefsConstants.CAN_SHOW, false);
        Prefs.putFloatOnMainThread(PrefsConstants.RATING, -1);
        Prefs.putIntOnMainThread(PrefsConstants.VIEW_COUNT, 0);
        Prefs.putIntOnMainThread(PrefsConstants.TRAINING_IMAGE_COUNT, 0);
        Prefs.putIntOnMainThread(PrefsConstants.SELECTED_EMP_COUNT, 0);
        // Prefs.putIntOnMainThread(PrefsConstants.SELECTED_ADHOC_EMP_COUNT, 0);
        setIsLoading(false);
    }


    // this is getting topic count from table to show on the startactivity screen
    LiveData<List<SavedTopic>> getSavedList() {
        return dataBase.getSavedTopicDao().getSavedTopicList(Prefs.getInt(PrefsConstants.ROTA_ID));
    }

}


