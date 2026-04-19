package com.sisindia.ai.mtrainer.android.features.dashboard;

import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.app.Application;
import android.app.Dialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableList;
import androidx.lifecycle.LiveData;

import com.droidcommons.preference.Prefs;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.base.RequestHeaderInterceptor;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.MtrainerLogIntercepter;
import com.sisindia.ai.mtrainer.android.constants.Constant;
import com.sisindia.ai.mtrainer.android.constants.DutyStatus;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.TimeLineEntity;
import com.sisindia.ai.mtrainer.android.features.addrota.AddTaskViewListeners;
import com.sisindia.ai.mtrainer.android.models.AddRotaMainRequest;
import com.sisindia.ai.mtrainer.android.models.AddRotaRequest;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.RefreshRequest;
import com.sisindia.ai.mtrainer.android.models.RefreshResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceRequest;
import com.sisindia.ai.mtrainer.android.models.TrainingCalendarResponse;
import com.sisindia.ai.mtrainer.android.models.UnitListResponse;
import com.sisindia.ai.mtrainer.android.models.VanResponse;
import com.sisindia.ai.mtrainer.android.models.VanRunningStatusRequest;
import com.sisindia.ai.mtrainer.android.models.gps.GpsRequest;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DashBoardViewModel extends MTrainerViewModel implements NavigationView.OnNavigationItemSelectedListener {

    @Inject
    DashBoardApi dashBoardApi;

    @Inject
    AuthApi authApi;

    public ObservableBoolean isOnDuty = new ObservableBoolean(Prefs.getBoolean(PrefsConstants.IS_ON_DUTY));
    public ObservableField<String> companyId = new ObservableField<>(Prefs.getString(PrefsConstants.COMPANY_ID));
    public final String role = Prefs.getString(PrefsConstants.ROLE);
    public boolean canDelete = false;
    private final MtrainerDataBase dataBase;
    public ObservableField<String> dutyStatusText = new ObservableField<>();
    public ObservableField<String> appVersion = new ObservableField<>(BuildConfig.VERSION_NAME);
    public ObservableField<String> selectedSiteAddress = new ObservableField<>("");
    public ObservableList<UnitListResponse.Unit> unitList = new ObservableArrayList<>();
    public ObservableField<Boolean> cantShowSpinner = new ObservableField<>(true);
    public ObservableField<Boolean> cantShowSpinner1 = new ObservableField<>(true);
    public ObservableField<Boolean> reasonShowSpinner = new ObservableField<>(true);
    public ObservableField<Boolean> checkBase = new ObservableField<>(true);
    public ObservableBoolean isSyncDataLoading = new ObservableBoolean(false);
    private int selectedUnitID = -1;
    public ObservableField<String> baseUnitName = new ObservableField<>(Prefs.getString(PrefsConstants.BASE_UNIT_NAME));
    public ObservableField<Boolean> addRotaLoading = new ObservableField<>(false);
    private final int OJT = 0;
    int trainingConductType = 0;
    public ObservableInt empCount = new ObservableInt(-1);
    Dialog dialog;
    DashBoardActivity dashBoardActivity;
    //    LayoutDrawerHeaderBinding binding1;
    public ObservableBoolean canSync = new ObservableBoolean(true);
    public ObservableInt pendingRota = new ObservableInt(0);
    public ObservableInt pendingImages = new ObservableInt(0);
    private static final String TAG = "DashBoardViewModel";
    public ObservableBoolean showSyncButton = new ObservableBoolean(true);

//    private String dutyTime = "";

    @Inject
    public DashBoardViewModel(@NonNull Application application) {
        super(application);
        initRetrofit();
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse1, this::onApiError));

        dutyStatusText.set(isOnDuty.get() ? "Duty On (" + Prefs.getString(PrefsConstants.DUTY_ON_UI_TIME, "") + ")" : "Duty Off (" + Prefs.getString(PrefsConstants.DUTY_ON_UI_TIME, "") + ")");
        dataBase = MtrainerDataBase.getDatabase(application);
        if (Prefs.getString(PrefsConstants.ROLE).equals("Training Champ") || Prefs.getString(PrefsConstants.ROLE).equals("Unit Commander")) {
            isOnDuty.set(true);
            Prefs.putBoolean(PrefsConstants.IS_ON_DUTY, true);
            dashBoardActivity = new DashBoardActivity();

        }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.report_webpage:
                message.what = NavigationConstants.OPEN_REPORT_PAGE;
                break;

            case R.id.actionCourses:
                message.what = NavigationConstants.TRAINING_COURSES;
                break;

            case R.id.raising_webpage:
                message.what = NavigationConstants.OPEN_RAISING_PAGE;
                break;

            case R.id.online_report:
                message.what = NavigationConstants.OPEN_ONLINE_REPORT_WEB_VIEW;
                break;

            case R.id.actionRefresh:
                setIsLoading(true);
                message.what = NavigationConstants.REFRESH_SITE_DATA;
                break;

            case R.id.actionDashboardRota:
                message.what = NavigationConstants.ON_MENU_ITEM_DASHBOARD_ROTA_CLICK;
                break;

            case R.id.actionMyUnits:
                message.what = NavigationConstants.ON_MENU_ITEM_MY_UNITS_CLICK;
                break;

            case R.id.actionTrainingCalendar:
                message.what = NavigationConstants.ON_MENU_ITEM_TRAINING_CALENDAR;
                break;

            /*case R.id.actionNotifications:
                message.what = NavigationConstants.ON_MENU_ITEM_NOTIFICATIONS_CLICK;
                break;

            case R.id.actionTodayTimeline:
                message.what = NavigationConstants.ON_MENU_ITEM_TODAY_TIMELINE;
                break;*/

            case R.id.actionCheckIn:
                message.what = NavigationConstants.ON_MENU_ITEM_CHECK_IN;
                break;

            case R.id.actionMyConveyance:
                message.what = NavigationConstants.ON_MENU_ITEM_MY_CONVEYANCE;
                break;

            case R.id.report_employee:
                message.what = NavigationConstants.ON_MENU_ITEM_REPORT_EMPLOYEE;
                break;

            case R.id.actionTrainingKit:
                message.what = NavigationConstants.ON_MENU_ITEM_TRAINING_KIT;
                break;

            /*case R.id.actionMyAttendance:
                message.what = NavigationConstants.ON_MENU_ITEM_MY_ATTENDANCE;
                break;*/

            case R.id.actionRplForm:
                message.what = NavigationConstants.ON_MENU_ITEM_RPL_FORM;
                break;

            case R.id.actionUmbrella:
                message.what = NavigationConstants.START_UMBRELLA_REPORT;
                break;

            case R.id.actionTickets:
                message.what = NavigationConstants.ON_MENU_ITEM_TICKETS;
                break;

            case R.id.actionForceSync:
                message.what = NavigationConstants.ACTION_FORCE_SYNC;
                break;

            case R.id.actionSpi:
                message.what = NavigationConstants.ON_MENU_ITEM_SPI;
                break;
        }
        liveData.postValue(message);
        return false;
    }

    public AddTaskViewListeners viewListeners = new AddTaskViewListeners() {

        @Override
        public void onDateChanged(int viewId, LocalDate date) {
        }

        @Override
        public void onStartTimeSelected(LocalTime time) {
        }

        @Override
        public void onEndTimeSelected(LocalTime time) {
        }

        @Override
        public void onItemSpinnerSelected(int viewId, int position) {
            switch (viewId) {
                case R.id.sp_unit_name:
                    selectedUnitID = unitList.get(position).unitId;
                    empCount.set(unitList.get(position).empCount);
                    if (unitList.get(position).SiteAddress == null || unitList.get(position).SiteAddress.equals(""))
                        selectedSiteAddress.set("");
                    else
                        selectedSiteAddress.set(unitList.get(position).SiteAddress);
                    break;

                case R.id.sp_training_counduct_type:
                    if (position == OJT)
                        trainingConductType = Constant.OJT;
                    else
                        trainingConductType = Constant.CLASSROOM;
                    break;

            }
        }
    };

    public void onDutyChanged(View view) {
        setIsLoading(true);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 3);
        c.set(Calendar.MINUTE, 30);
        c.set(Calendar.SECOND, 0);
        Calendar c1 = Calendar.getInstance();
        int dutytime = c1.get(Calendar.HOUR_OF_DAY);
        Log.d("asfsdfs", dutytime + "," + c.getTime() + "," + c1.getTime());
        if (isOnDuty.get() || (System.currentTimeMillis() >= c.getTimeInMillis() && dutytime < 10)) {
            //if(isOnDuty.get() || (dutytime >= 5 && dutytime <23)) {
            message.what = NavigationConstants.DUTY_STATUS_CHANGED;
            liveData.setValue(message);
        } else {
            setIsLoading(false);
            showToast("Can't turn on duty now, you crossed time limit");
        }
    }

    public String getUserName() {
        return Prefs.getString(PrefsConstants.EMPPLOYEE_NAME);
    }

    void createImmediateRota() {
        setIsLoading(true);
        if (Prefs.getString(PrefsConstants.ROLE).equals("Training Champ")) {
            addRotaLoading.set(true);
        }

        Date date = new Date();
        long taskStartDateTime = date.getTime();
        long taskEndDateTime = taskStartDateTime + 15 * 60 * 1000;
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);

        AddRotaRequest request = new AddRotaRequest();
        request.setStartTime(sdf.format(new Date(taskStartDateTime)));
        request.setEndTime(sdf.format(new Date(taskEndDateTime)));
        request.setExpectedTrainee("0");
        request.setSelectedTopicsID(new ArrayList<Integer>());
        if (Prefs.getString(PrefsConstants.ROLE).equals("Training Champ") && !checkBase.get()) {
            if (selectedUnitID == -1) {
                showToast("Please select Site");
                addRotaLoading.set(false);
                return;
            } else
                request.setUnitId(String.valueOf(selectedUnitID));
        } else {
            request.setUnitId(String.valueOf(Prefs.getInt(PrefsConstants.BASE_UNIT_ID)));
        }

        if (Prefs.getString(PrefsConstants.COMPANY_ID).equals("1") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("4") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("8")) {
            if (trainingConductType == 0) {
                addRotaLoading.set(false);
                Toast.makeText(getApplication(), "Please select Training Type.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        request.setTrainerId(Prefs.getString(PrefsConstants.EMPLOYEE_ID));
        request.setTrainingTypeId(String.valueOf(trainingConductType));

        AddRotaMainRequest rotaMainRequest = new AddRotaMainRequest();
        rotaMainRequest.taskExecutionResult = request;
        addDisposable(dashBoardApi.requestRota(rotaMainRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onImmediateRotaRequestSuccess, this::onApiError));
    }

    private void onImmediateRotaRequestSuccess(TrainingCalendarResponse response) {
        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {

            TrainingCalendarResponse.TrainingCalendar item = response.trainingCalendars.get(0);
            Prefs.putInt(PrefsConstants.ROTA_ID, item.rotaId);
            Prefs.putInt(PrefsConstants.STATUS, item.traningStatusId);
            Prefs.putInt(PrefsConstants.TRAINING_TYPE_ID, item.trainingTypeId);
            Prefs.putString(PrefsConstants.UNIT_NAME, item.unitName);
            Prefs.putString(PrefsConstants.UNIT_ADDRESS, item.unitAddress);
            Prefs.putString(PrefsConstants.ESTIMATE_STATR_TIME, item.estimatedStartTime);
            Prefs.putString(PrefsConstants.ESTIMATE_END_TIME, item.estimatedEndTime);
            Prefs.putInt(PrefsConstants.UNIT_ID, item.unitId);
            Prefs.putString(PrefsConstants.ATTENDANCE_TYPE_LIST, item.attendanceTypeList);
            Prefs.putInt(PrefsConstants.TRAINING_ID, item.trainerId);
            Prefs.putString(PrefsConstants.TOPIC_ID, item.topicId);
            Prefs.putInt(PrefsConstants.TRAINING_STATUS_ID, item.traningStatusId);

            addDisposable(dataBase.getTrainingCalenderDao()
                    .insertTrainingCalender(response.trainingCalendars)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::canOpenTrainingScreen));
        }
    }

    private void canOpenTrainingScreen() {
        if (Prefs.getString(PrefsConstants.ROLE).equals("Training Champ")) {
            message.what = NavigationConstants.CLOSE_DIALOG;
            liveData.setValue(message);
            addRotaLoading.set(false);
        }

        message.what = NavigationConstants.TRAINING_CHAMP_START_TRAINING_SCREEN;
        liveData.postValue(message);
        canDelete = true;
    }

    public void onHeaderClick(View view) {
        if (!checkBase.get()) {
            checkBase.set(true);
            cantShowSpinner.set(true);
        } else {
            checkBase.set(false);
            cantShowSpinner.set(false);
        }
    }

    public void onNextClick(View view) {
        createImmediateRota();
    }

    void fetchUnitList() {
        addDisposable(dashBoardApi.getUnitList(Prefs.getString(PrefsConstants.EMPLOYEE_REG_NO))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onUnitListSuccess, this::onApiError));
    }

    private void onUnitListSuccess(UnitListResponse response) {
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getUnitListDao().insertUnitList(response.unitList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }

    LiveData<List<UnitListResponse.Unit>> getUnitListFromDb() {
        return dataBase.getUnitListDao().getUnitList();
    }

    void setUnitList(List<UnitListResponse.Unit> unitList) {
        this.unitList.clear();
        this.unitList.addAll(unitList);
    }

    /*private void getReportUrl() {

    }*/

    private void onRefreshSuccess(RefreshResponse response) {

        if (response.statusCode == SUCCESS_RESPONSE) {
            Prefs.putInt(PrefsConstants.BASE_UNIT_ID, response.siteResponses.get(0).unitId);
            Prefs.putString(PrefsConstants.BASE_UNIT_NAME, response.siteResponses.get(0).unitName);
            //Prefs.putBooleanOnMainThread(PrefsConstants.IS_DEBUG, response.siteResponses.get(0).isDebug == 1);
            clearAllTable();
        } else {
            showToast("Error Refreshing");
        }
    }

    void refreshSiteData() {
        RefreshRequest refreshRequest = new RefreshRequest();
        refreshRequest.RegNo = Prefs.getString(PrefsConstants.EMPLOYEE_REG_NO);
        addDisposable(dashBoardApi.refresh(refreshRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRefreshSuccess, this::onApiError));
        if (picasso == null)
            Log.d("Picasso Bhaar", "refreshSiteData: null");
        else
            Log.d("Picasso Bhaar", "refreshSiteData: NOT null");
    }

    void clearAllTable() {
        addDisposable(Completable.mergeArray(dataBase.getMasterAttendanceDao().flushEmployeeData(),
                        dataBase.getTopicDao().flushTopicMaster(),
                        dataBase.getRatingQuestionDao().flushRatingQuestionMaster(),
                        dataBase.getRatingDataDao().flushRatingDataMaster(),
                        dataBase.getClientListDao().flushClientListMaster(),
                        dataBase.getUnitListDao().flushUnitListMaster(),
                        dataBase.getRegionDao().flushRegionListMaster(),
                        dataBase.getBranchDao().flushBranchListMaster(),
                        dataBase.getSite1Dao().flushSiteListMaster())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    setIsLoading(false);
                }, th -> {
                    showToast("Error Refreshing");
                    setIsLoading(false);
                }));
        ;
    }

    void fetchMasterUnitList() {

        int lastPageNumber = Prefs.getInt(PrefsConstants.UNIT_LAST_PAGE_NUMBER, -1);
        int lastAccessPage = Prefs.getInt(PrefsConstants.UNIT_LAST_FETCH_PAGE, -1);
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
        addDisposable(dashBoardApi.getMasterUnitList(attendanceRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMasterUnitSuccess, this::onApiError));
    }

    private void onPreAuthResponse1(PreAuthResponse response) {
        if (response != null && !TextUtils.isEmpty(response.accessToken)) {
            Prefs.putString(PrefsConstants.AUTH_TOKEN_KEY, response.tokenType.concat(" ").concat(response.accessToken));
            Prefs.putString(PrefsConstants.TOKEN_EXPIRE_DATE, response.expires);
        } else {
            showToast(R.string.something_went_wrong);
        }
    }

    private void onMasterUnitSuccess(UnitListResponse response) {

        if (response.statusCode == SUCCESS_RESPONSE) {
            //totalEmployee.set(String.valueOf(response.attendanceResponses.size()));

            dataBase.getUnitListDao().insertUnitList(response.unitList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();

            int lastPage = (int) Math.ceil(response.totalCount / (double) 50);
            Prefs.putInt(PrefsConstants.UNIT_LAST_PAGE_NUMBER, lastPage);
            getUnitList(response.currPage,
                    50, lastPage);
        }
    }

    void getUnitList(int pageNumber, int range, int lastPage) {
        if (pageNumber >= lastPage)
            return;

        TrainingAttendanceRequest request = new TrainingAttendanceRequest();
        request.currentPage = pageNumber + 1;
        request.range = range;
        request.trainerId = Prefs.getInt(PrefsConstants.BASE_TRAINER_ID);
        Prefs.putInt(PrefsConstants.UNIT_LAST_FETCH_PAGE, pageNumber + 1);

        addDisposable(dashBoardApi.getMasterUnitList(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMasterUnitSuccess, this::onApiError));
    }

    public void startForceSync(View view) {
        try {
            message.what = NavigationConstants.START_FORCE_SYNC;
            liveData.postValue(message);
        } catch (Exception e) {
            logger.log("startForceSync", e);
        }
    }

    void getPendingDataCount() {
        isSyncDataLoading.set(true);
        final int[] result = new int[2];
        addDisposable(dataBase.getTrainingFinalSubmitDao().getPendingRotaCount()
                .subscribeOn(Schedulers.io())
                .onErrorReturnItem(0)
                .flatMap(rotaCount -> {
                    result[0] = rotaCount;
                    return dataBase.getPhotoAttachmentDao().getPendingTrainingPhotoCount();
                }).onErrorReturnItem(0)
                .flatMap(trainingImgCount -> {
                    result[1] = trainingImgCount;
                    return dataBase.getAssessmentDao().getPendingAssessmentVideoCount();
                }).onErrorReturnItem(0)
                .flatMap(videoCount -> {
                    result[1] = result[1] + videoCount;
                    return dataBase.getAttendancePhotoDao().getPendingAttendancePicCount();
                }).onErrorReturnItem(0)
                .map(attendanceImgCount -> {
                    result[1] = result[1] + attendanceImgCount;
                    return result;
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    isSyncDataLoading.set(true);
                    if (result[0] == 0 && result[1] == 0)
                        canSync.set(false);
                    else
                        canSync.set(true);
                    pendingRota.set(result[0]);
                    pendingImages.set(result[1]);
                }, th -> {
                    isSyncDataLoading.set(true);
                    canSync.set(false);
                    showToast("Something went wrong");
                    logger.log("Rxjava Exception Thrown", th);
                }));
    }

    void updateDutyStatus() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat sdfUi = new SimpleDateFormat("dd MMM HH:mm a", Locale.ENGLISH);

        GpsRequest request = new GpsRequest();
        request.setDateTime(sdf.format(date));
        request.setDutyOnOffId((!isOnDuty.get()) ? "" : Prefs.getString(PrefsConstants.LAST_GPS_PAIRING_KEY, ""));
        request.setLatitude(String.valueOf(Prefs.getDouble(PrefsConstants.LAT)));
        request.setLongitude(String.valueOf(Prefs.getDouble(PrefsConstants.LONGI)));
        request.setTrainerId(String.valueOf(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID)));
        request.setStatusId(String.valueOf((!isOnDuty.get()) ? DutyStatus.DUTY_ON : DutyStatus.DUTY_OFF));
        //changeDutyStatus();

        addDisposable(dashBoardApi.sendGpsPing(request)
                .map(response -> {
                    if (response != null && response.getStatusCode() == 200) {
                        Prefs.putStringOnMainThread(PrefsConstants.DUTY_ON_UI_TIME, sdfUi.format(date));
                        if (response.getStatusId() == 1) {
                            Log.d(TAG, "doWork: update Duty On  Key : " + response.getKey());
                            Prefs.putStringOnMainThread(PrefsConstants.LAST_GPS_PAIRING_KEY, String.valueOf(response.getKey()));
                        }
                        return true;
                    } else
                        return false;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if (data) {
                        // Success
                        Log.d(TAG, "sendDutyDataToServer: Success");
                        // Trying to turn on duty
                        if (!isOnDuty.get()) {
                            message.what = NavigationConstants.SHOW_SIS_MAIN_DIALOG;
                            liveData.postValue(message);
                        } else
                            changeDutyStatus();
                    } else {
                        // Failure
                        setIsLoading(false);
                        showToast("Unexpected Error Occurred, Please retry");

                    }
                }, th -> {
                    setIsLoading(false);
                    showToast("Error Occurred, Please check Your Internet");
                }));


        /*addDisposable(dataBase.getGpsPingDao().getDutyOnDetails()
                .subscribeOn(Schedulers.io())
                .flatMap(dutyOnPings -> {
                    sendDataToServer(dutyOnPings);
                    return dataBase.getGpsPingDao().getDutyOffDetails();
                }).map(this::sendDataToServer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if(data) {
                        // Success
                        Log.d(TAG, "sendDutyDataToServer: Success");
                        if(!isOnDuty.get()) {
                            message.what = NavigationConstants.SHOW_SIS_MAIN_DIALOG;
                            message.obj = isOnDuty.get();
                            liveData.postValue(message);
                        } else
                            changeDutyStatus();
                    }
                    else {
                        // Failure
                        setIsLoading(false);
                        showToast("Error Occurred, Please check Your Internet");
                    }
                }, th -> {
                    setIsLoading(false);
                    showToast("Unexpected Error Occurred, Please retry");
                })
        );*/
    }

    /*void updateDutyStatusOld() {
        //setIsLoading(true);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat sdfUi = new SimpleDateFormat("dd MMM HH:mm a", Locale.ENGLISH);
        LocationDetailsEntity ping = new LocationDetailsEntity();
        ping.dateTime = sdf.format(date);
        ping.latitude = Prefs.getDouble(PrefsConstants.LAT);
        ping.longitude = Prefs.getDouble(PrefsConstants.LONGI);
        ping.status = isOnDuty.get() ? DutyStatus.DUTY_ON : DutyStatus.DUTY_OFF;
        Prefs.putStringOnMainThread(PrefsConstants.DUTY_ON_UI_TIME, sdfUi.format(date));
        // If user turning on duty then we will get new pairing token and we don't have any token hence value is -1
        // If User is turning off duty then we have to set a pairing key
        // User might turn off/on duty multiple time and user don't have internet we are using rowId just to map pairing key with the right value
        ping.pairingToken = isOnDuty.get() ? -1 : Prefs.getLong(PrefsConstants.LAST_DUTY_ON_id, -1);

        // No Need of work manager
        addDisposable(dataBase.getGpsPingDao().insertLocationDetail(ping)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((rowId) -> {
                    if (ping.status == DutyStatus.DUTY_ON)
                        Prefs.putLong(PrefsConstants.LAST_DUTY_ON_id, rowId);
                    setIsLoading(false);
                    sendDutyDataToServer();
                }, throwable -> {
                    setIsLoading(false);
                    showToast("Unexpected Error Occurred");
                }));
    }*/

    /*private void sendDutyDataToServer() {
        addDisposable(dataBase.getGpsPingDao().getDutyOnDetails()
                .subscribeOn(Schedulers.io())
                .flatMap(dutyOnPings -> {
                    sendDataToServer(dutyOnPings);
                    return dataBase.getGpsPingDao().getDutyOffDetails();
                }).map(this::sendDataToServer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if(data) {
                        // Success
                        Log.d(TAG, "sendDutyDataToServer: Success");
                        if(!isOnDuty.get()) {
                            message.what = NavigationConstants.SHOW_SIS_MAIN_DIALOG;
                            message.obj = isOnDuty.get();
                            liveData.postValue(message);
                        } else
                            changeDutyStatus();
                    }
                    else {
                        // Failure
                        setIsLoading(false);
                        showToast("Error Occurred, Please check Your Internet");
                    }
                }, th -> {
                        setIsLoading(false);
                        showToast("Unexpected Error Occurred, Please retry");
                })
        );
    }*/

    void sendSisDialogDataToServer(VanRunningStatusRequest request) {
        addDisposable(dashBoardApi.getVanRunningStatus(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onVanRunningSuccess, th -> {
                    setIsLoading(false);
                    showToast("Error Occurred, Please check Your Internet");
                }));
    }

    private void onVanRunningSuccess(VanResponse response) {
        if (response.statusCode == SUCCESS_RESPONSE) {
            showToast(response.data);
            message.what = NavigationConstants.CLOSE_SIS_MAIN_DIALOG;
            liveData.postValue(message);
            changeDutyStatus();
        } else {
            setIsLoading(false);
            showToast("Unexpected Error Occurred, Please retry");
        }
    }

    private void changeDutyStatus() {
        //Log.d(TAG, "changeDutyStatus: P " + isOnDuty.get());
        //message.what = NavigationConstants.CHANGE_LOCATION_SERVICE_STATE;
        //message.arg1 = 0;
        //liveData.postValue(message);
        //Log.d(TAG, "changeDutyStatus: A " + isOnDuty.get());
        //Log.d(TAG, "changeDutyStatus: P " + isOnDuty.get());
        //message.what = NavigationConstants.CHANGE_LOCATION_SERVICE_STATE;
        //message.arg1 = 1;
        //liveData.postValue(message);
        //Log.d(TAG, "changeDutyStatus: A " + isOnDuty.get());
        isOnDuty.set(!isOnDuty.get());
        dutyStatusText.set(isOnDuty.get() ? "Duty On (" + Prefs.getString(PrefsConstants.DUTY_ON_UI_TIME, "") + ")" : "Duty Off (" + Prefs.getString(PrefsConstants.DUTY_ON_UI_TIME, "") + ")");
        Prefs.putBoolean(PrefsConstants.IS_ON_DUTY, isOnDuty.get());

        //  if (isOnDuty.get()) {
        Date date = new Date();
        String duty = isOnDuty.get() ? "DutyOn" : "DutyOff";
        // String km="1.2 km from home";
        String km = "";
        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String currenttime = sdf2.format(date);

        TimeLineEntity timeLineResponse = new TimeLineEntity();
        timeLineResponse.dutyon = duty;
        timeLineResponse.time = currenttime;
        timeLineResponse.kmmeter = km;

        addDisposable(dataBase.getTimeLineDao().insertTimeline(timeLineResponse)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> setIsLoading(false), throwable -> {
                    setIsLoading(false);
                    showToast("Unexpected Error Occurred, Please retry");
                }));

       /* }else
            setIsLoading(false);*/
    }

    /*private void startWorkManager() {
        // Sending ON pings first to get pairing keys
        *//*List<LocationDetailsEntity> dutyOnPings = dataBase.getGpsPingDao().getDutyOnDetails();
        sendDataToServer(dutyOnPings);
        List<LocationDetailsEntity> dutyOffPings = dataBase.getGpsPingDao().getDutyOffDetails();
        sendDataToServer(dutyOffPings);*//*


        //////////////////////////////////////////////
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        WorkRequest immediateSync = new OneTimeWorkRequest.Builder(GpsWorker.class)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(getApplication()).enqueue(immediateSync);
    }*/

    /*  *//**
     * Method will be call on the calling thread
     * *//*
    private synchronized GpsRequest prepareRequest(LocationDetailsEntity ping) {
        String token = dataBase.getGpsTokenDao().getPairingKey(ping.pairingToken);
        // STATUS = 2 (DutyOff)
        if(ping.status == 2 && token == null)
            return null;
        GpsRequest request = new GpsRequest();
        request.setDateTime(ping.dateTime);
        request.setDutyOnOffId(ping.status == 1? "" : token);
        request.setLatitude(String.valueOf(ping.latitude));
        request.setLongitude(String.valueOf(ping.longitude));
        request.setTrainerId(String.valueOf(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID)));
        request.setStatusId(String.valueOf(ping.status));
        return request;
    }

    *//**
     * Method will be call on the calling thread
     * *//*
    private synchronized GpsRequest prepareOnlineRequest(LocationDetailsEntity ping) {
        String token = dataBase.getGpsTokenDao().getPairingKey(ping.pairingToken);
        // STATUS = 2 (DutyOff)
        *//*if(ping.status == 2 && token == null)
            return null;*//*
        GpsRequest request = new GpsRequest();
        request.setDateTime(ping.dateTime);
        request.setDutyOnOffId(ping.status == 1? "" : Prefs.getString(PrefsConstants.LAST_GPS_PAIRING_KEY, ""));
        request.setLatitude(String.valueOf(ping.latitude));
        request.setLongitude(String.valueOf(ping.longitude));
        request.setTrainerId(String.valueOf(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID)));
        request.setStatusId(String.valueOf(ping.status));
        return request;
    }

    */

    /**
     * Method will be call on the calling thread
     * This method will return true if its successfully pushed data to server or have no data
     *//*
    private synchronized boolean sendDataToServer(List<LocationDetailsEntity> pings) {
        for(LocationDetailsEntity ping : pings) {
            // Preparing request
            GpsRequest request  = prepareRequest(ping);
            if(request == null)
                continue;
            // Doing Network Call
            GpsResponse response = dashBoardApi.sendGpsPing(request).blockingGet();
            Log.d(TAG, "doWork: " + response);
            if(response!=null && response.getStatusCode() == 200) {
                // For Turning on Duty we will get pairing key
                if(response.getStatusId() == 1) {
                    Log.d(TAG, "doWork: update " + ping.id + "   Key : " + response.getKey() );
                    GpsTokenEntity gpsTokenEntity = new GpsTokenEntity();
                    gpsTokenEntity.id = ping.id;
                    gpsTokenEntity.pairingKey = String.valueOf(response.getKey());
                    dataBase.getGpsTokenDao().insertGpsToken(gpsTokenEntity);
                } else if(response.getStatusId() == 2)
                    dataBase.getGpsTokenDao().removeGpsToken((int)ping.pairingToken);
                // Removing GPS ping
                dataBase.getGpsPingDao().removeSyncedLocationDetail(response.getDataTime());
                return true;
            } else
                return false;
        }
        return true;
    }*/
    public void updateTaskStartDateTime(String time1) {
        addDisposable(dataBase.getTrainingCalenderDao()
                .updateStartTime(time1, Prefs.getInt(PrefsConstants.ROTA_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }
}







