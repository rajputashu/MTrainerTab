package com.sisindia.ai.mtrainer.android.features.rota;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ON_ROTA_ITEM_CLICK_TMP;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;

import com.droidcommons.preference.Prefs;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.base.RequestHeaderInterceptor;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.MtrainerLogIntercepter;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.TimeLineEntity;
import com.sisindia.ai.mtrainer.android.features.dashboard.NotificationRequestReceive;
import com.sisindia.ai.mtrainer.android.models.DashboardRequest;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.TrainerPerformanceResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingCalendarResponse;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DashBoardRotaViewModel extends MTrainerViewModel {

    MtrainerDataBase dataBase;
    public TimeLineRecyclerAdapter recycleradapterTimeLine;
    public DashBoardRotaRecyclerAdapter recyclerAdapter = new DashBoardRotaRecyclerAdapter();
    public String companyId = Prefs.getString(PrefsConstants.COMPANY_ID);
    public String role = Prefs.getString(PrefsConstants.ROLE);
    public DashBoardPerformanceRecyclerAdapter performanceAdapter = new DashBoardPerformanceRecyclerAdapter();
    public ObservableField<String> regionalrank = new ObservableField<>();
    public ObservableArrayList<TrainerPerformanceResponse.PerformanceResponse> performancelist = new ObservableArrayList<>();
    public ObservableField<String> actualscore = new ObservableField<>();
    public ObservableField<String> trainername = new ObservableField<>();
    public ObservableField<String> status = new ObservableField<>();

    private AlarmManager alarmManager;
    private static final long ALARM_OFFSET = 15 * 60 * 1000;
    private static final String TAG = "DashBoardRotaViewModel";

    @Inject
    AuthApi authApi;

    public DashBoardRotaViewListeners viewListeners = new DashBoardRotaViewListeners() {

        @Override
        public void onPerformanceItemClick() {

        }

        @Override
        public void onRotaItemClick(TrainingCalendarResponse.TrainingCalendar item) {
            if ((Prefs.getInt(PrefsConstants.IS_PROGRESS_ROTAID, -1) == -1)
                    || (Prefs.getInt(PrefsConstants.IS_PROGRESS_ROTAID, -1) == item.rotaId)) {
//                validateRotaItemClick(item.traningStatusId);
                validateRotaItemClick(item);
            } else {
                showToast("Rota is in progress");
            }
        }
    };

    @Inject
    DashBoardApi dashBoardApi;

    @Inject
    public DashBoardRotaViewModel(@NonNull Application application) {
        super(application);
        initRetrofit();
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse1, this::onApiError));

        dataBase = MtrainerDataBase.getDatabase(application);
        recycleradapterTimeLine = new TimeLineRecyclerAdapter(dataBase);

        String employeeName = Prefs.getString(PrefsConstants.EMPPLOYEE_NAME).trim();
        if (employeeName.length() <= 12)
            trainername.set(employeeName);
        else {
            String[] tmp = employeeName.split(" ");
            trainername.set(tmp[0]);
        }
    }

    private void validateRotaItemClick(TrainingCalendarResponse.TrainingCalendar item) {

        recyclerAdapter.activateClick();
        int statusId = item.traningStatusId;
        Log.d("statusid: ",statusId+"");
        if (!Prefs.getBoolean(PrefsConstants.IS_ON_DUTY)) {
//            recyclerAdapter.activateClick();
            showToast("Please turn on your duty");
        } else if (statusId == 11) {
//            recyclerAdapter.activateClick();
            showToast("Rota already completed");
        } else if (statusId == 12) {
            //this is written for cancel training
//            recyclerAdapter.activateClick();
            showToast("Training cancelled");
        } else {
            setIsLoading(true);
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
            message.what = ON_ROTA_ITEM_CLICK_TMP;
            liveData.postValue(message);
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


    private void onPreAuthResponse1(PreAuthResponse response) {
        if (response != null && !TextUtils.isEmpty(response.accessToken)) {
            Prefs.putString(PrefsConstants.AUTH_TOKEN_KEY, response.tokenType.concat(" ").concat(response.accessToken));
            Prefs.putString(PrefsConstants.TOKEN_EXPIRE_DATE, response.expires);
        } else {
            showToast(R.string.something_went_wrong);
        }
    }


    public void getDashboardPerformance() {
        setIsLoading(true);

        String empId = Prefs.getString(PrefsConstants.EMPLOYEE_ID);
        DashboardRequest request = new DashboardRequest(empId);
        Log.d("employeeidno", empId);
        addDisposable(dashBoardApi.getTrainerPerformance(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTrainingPerformanceSuccess, this::onApiError));
    }

    public void getTrainingCalendar() {
        setIsLoading(true);
        String empId = Prefs.getString(PrefsConstants.EMPLOYEE_ID);
        DashboardRequest request = new DashboardRequest(empId);

        addDisposable(dashBoardApi.getTrainingCalendar(request)
                .toObservable()
                .filter(list -> !list.trainingCalendars.isEmpty())
                .flatMap(requestTest -> Observable.fromIterable(requestTest.trainingCalendars))
                .subscribeOn(Schedulers.io())
                .map(item -> {
                    Log.d(TAG, "getTrainingCalendar: " + item.estimatedStartDatetime.trim() + "_" + item.estimatedStartTime.trim());
                    setNotification(item.estimatedStartDatetime.trim() + " " + item.estimatedStartTime.trim(), item.rotaId, item.unitName);
                    return item;
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTrainingCalendarSuccess, this::onApiError));
               /* .subscribe(onTrainingCalendarSuccess -> {
                            Timber.e("");
                        },
                        throwable -> {
                            //Handle here
                        }));*/
    }

    private void onTrainingPerformanceSuccess(TrainerPerformanceResponse response) {
        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            //performanceAdapter.clearAndSetItems(response.performance);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
            String currentDate = dateFormat.format(new Date());
            TrainerPerformanceResponse.PerformanceResponse daily = new TrainerPerformanceResponse.PerformanceResponse();
            TrainerPerformanceResponse.PerformanceResponse weekly = new TrainerPerformanceResponse.PerformanceResponse();
            TrainerPerformanceResponse.PerformanceResponse monthly = new TrainerPerformanceResponse.PerformanceResponse();
            TrainerPerformanceResponse.PerformanceResponse previousmonth = new TrainerPerformanceResponse.PerformanceResponse();
            List<TrainerPerformanceResponse.PerformanceResponse> performanceResponseList = new ArrayList<>(4);
            switch (response.performance.size()) {
                case 0:
                    daily = new TrainerPerformanceResponse.PerformanceResponse();
                    weekly = new TrainerPerformanceResponse.PerformanceResponse();
                    monthly = new TrainerPerformanceResponse.PerformanceResponse();
                    previousmonth = new TrainerPerformanceResponse.PerformanceResponse();
                    daily.typeId = 1;
                    weekly.typeId = 2;
                    monthly.typeId = 3;
                    previousmonth.typeId = 4;

                    performanceResponseList.add(daily);
                    performanceResponseList.add(weekly);
                    performanceResponseList.add(monthly);
                    performanceResponseList.add(previousmonth);
                    dataBase.getPerformanceDao().insertPerformance(performanceResponseList)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    break;
                case 1:
                    daily = new TrainerPerformanceResponse.PerformanceResponse();
                    weekly = new TrainerPerformanceResponse.PerformanceResponse();
                    //monthly = new TrainerPerformanceResponse.PerformanceResponse();
                    daily.typeId = 1;
                    weekly.typeId = 2;
                    //monthly.typeId = 3;
                    daily.currentDate = currentDate;

                    performanceResponseList.add(daily);
                    performanceResponseList.add(weekly);
                    //performanceResponseList.add(monthly);
                    //performanceResponseList.add(response.performance.get(1));
                    performanceResponseList.add(response.performance.get(0));
                    dataBase.getPerformanceDao().insertPerformance(performanceResponseList)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    break;
                case 2:
                    daily = new TrainerPerformanceResponse.PerformanceResponse();
                    weekly = new TrainerPerformanceResponse.PerformanceResponse();
                    daily.typeId = 1;
                    weekly.typeId = 2;
                    daily.currentDate = currentDate;

                    performanceResponseList.add(daily);
                    performanceResponseList.add(weekly);
                    performanceResponseList.add(response.performance.get(1));
                    performanceResponseList.add(response.performance.get(0));
                    dataBase.getPerformanceDao().insertPerformance(performanceResponseList)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    break;
                case 3:
                case 4:
                    response.performance.get(0).currentDate = currentDate;
                    dataBase.getPerformanceDao().insertPerformance(response.performance)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    break;
            }
            try {
                performancelist.clear();
            } catch (Exception e) {

            }

            if (response.performance.size() > 0) {
                performancelist.addAll(response.performance);
                regionalrank.set("" + response.performance.get(0).panIndiaRank + "/" + performancelist.get(0).totalTrainer);
                //regionalrank.set(String.valueOf(Prefs.getInt(PrefsConstants.RANDOM_RANK)));
            } else {
                regionalrank.set("");
            }
            if (response.performance.size() > 0) {
                actualscore.set(response.performance.get(0).actualScore);
                //actualscore.set(String.valueOf(Prefs.getFloat(PrefsConstants.RANDOM_SCORE)));
            } else {
                actualscore.set("");
            }
        }
    }


    private void onTrainingCalendarSuccess(List<TrainingCalendarResponse.TrainingCalendar> response) {
        setIsLoading(false);
        //if (response.statusCode == SUCCESS_RESPONSE) {

        dataBase.getTrainingCalenderDao().insertTrainingCalender(response)
                .subscribeOn(Schedulers.io())
                .subscribe();

    }


    LiveData<List<TrainingCalendarResponse.TrainingCalendar>> getRotaDetails() {
        return dataBase.getTrainingCalenderDao().getTrainingCalenderList();
    }

    void refreshRecylerView(List<TrainingCalendarResponse.TrainingCalendar> data) {
        recyclerAdapter.clearAndSetItems(data);
    }

    void refreshPerfRecylerView1(List<TimeLineEntity> data) {
        recycleradapterTimeLine.clearAndSetItems(data);
    }

    LiveData<List<TrainerPerformanceResponse.PerformanceResponse>> getPerformanceDetails() {
        return dataBase.getPerformanceDao().getPerformanceList();
    }

    void refreshPerfRecylerView(List<TrainerPerformanceResponse.PerformanceResponse> data) {
        performanceAdapter.clearAndSetItems(data);
    }

    LiveData<List<TimeLineEntity>> getTimelineDetails1() {
        return dataBase.getTimeLineDao().getTimelineList();
    }


    void updatePerformance(String actualTrainingTime, String actualAvgRating, String actualFeedbackTaken, String actualTrainingCount, String actualGuardTrained, String actualUnitCoverd) {
        dataBase.getPerformanceDao().updatePerformance(actualTrainingTime, actualAvgRating, actualFeedbackTaken, actualTrainingCount, actualGuardTrained, actualUnitCoverd, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        /*float actualTrainingTimeValue = Float.valueOf(actualTrainingTime);
        int actualTrainingCountValue = Integer.parseInt(actualTrainingCount);
        int actualGuardTrainedValue = Integer.parseInt(actualGuardTrained);

        dataBase.getPerformanceDao().updatePerformance(String.valueOf(actualTrainingTimeValue * 5), actualAvgRating, actualFeedbackTaken, String.valueOf(actualTrainingCountValue * 4), String.valueOf(actualGuardTrainedValue * 3), actualUnitCoverd, 2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        dataBase.getPerformanceDao().updatePerformance(String.valueOf(actualTrainingTimeValue * 17), actualAvgRating, actualFeedbackTaken, String.valueOf(actualTrainingCountValue * 18), String.valueOf(actualGuardTrainedValue * 16), actualUnitCoverd, 3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();*/
    }

    LiveData<List<Integer>> canUpdatePerformance() {
        return dataBase.getTrainingFinalSubmitDao().getTrainingFinalSubmitResponse();
    }

    private void setNotification(String date, int rotaId, String siteName) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa", Locale.ENGLISH);
            long dateTime = Objects.requireNonNull(sdf.parse(date)).getTime();
            long notificationTime = dateTime - ALARM_OFFSET;
            long currentTime = System.currentTimeMillis();
            if (notificationTime < currentTime)
                return;
            Intent intent = new Intent(getApplication(), NotificationRequestReceive.class);
            intent.putExtra("SITE_NAME", siteName);
            intent.putExtra("ROTA_ID", rotaId);
            /*PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplication(), rotaId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dateTime - ALARM_OFFSET, pendingIntent);
            } else
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, dateTime - ALARM_OFFSET, pendingIntent);*/

            // Use FLAG_IMMUTABLE for API level 31 and above
            PendingIntent pendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getBroadcast(getApplication(), rotaId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getBroadcast(getApplication(), rotaId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent);
            }

        } catch (ParseException | NullPointerException e) {
            Log.e(TAG, "Notification parsing error", e);
        }
    }
}