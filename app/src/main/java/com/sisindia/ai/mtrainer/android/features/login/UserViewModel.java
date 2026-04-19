package com.sisindia.ai.mtrainer.android.features.login;

import static android.view.View.GONE;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ON_UPDATING_LOADING_TIME;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_DASH_BOARD;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_LOGIN;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.app.Application;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.droidcommons.preference.Prefs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.base.RequestHeaderInterceptor;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.MtrainerLogIntercepter;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.dao.StarTrainingMasterDao;
import com.sisindia.ai.mtrainer.android.db.entities.CourseEntity;
import com.sisindia.ai.mtrainer.android.db.entities.LanguageEntity;
import com.sisindia.ai.mtrainer.android.db.entities.ProgramEntity;
import com.sisindia.ai.mtrainer.android.db.entities.TopicEntity;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.SaveTokenRequest;
import com.sisindia.ai.mtrainer.android.models.SaveTokenResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceRequest;
import com.sisindia.ai.mtrainer.android.models.UnitListResponse;
import com.sisindia.ai.mtrainer.android.models.UserRequest;
import com.sisindia.ai.mtrainer.android.models.UserResponse;
import com.sisindia.ai.mtrainer.android.models.VerifyOtpResponse;
import com.sisindia.ai.mtrainer.android.models.master.StarProgramMasterBodyMO;
import com.sisindia.ai.mtrainer.android.models.master.StarTrainingMasterResponseMO;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

public class UserViewModel extends MTrainerViewModel {
    private static final String TAG = "UserViewModel";
    public ObservableField<String> phoneNumberObs = new ObservableField<>();
    public ObservableField<String> otpTextObs = new ObservableField<>();
    public ObservableField<String> version = new ObservableField<>();
    public ObservableInt isotpLoading = new ObservableInt(GONE);
    public ObservableBoolean isDataSyncing = new ObservableBoolean(false);

    @Inject
    AuthApi authApi;
    String gotopt = "";
    AuthApi authApi1;

    private CountDownTimer cTimer = null;
    public ObservableInt percentageTime = new ObservableInt(0);
    private final MtrainerDataBase dataBase;

    @Inject
    DashBoardApi dashBoardApi;

    @Inject
    public UserViewModel(@NonNull Application application) {
        super(application);
        version.set(String.valueOf(BuildConfig.VERSION_CODE));
        dataBase = MtrainerDataBase.getDatabase(application);
        initRetrofit();
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse, this::onApiError));

    }

    private void onPreAuthResponse(PreAuthResponse response) {
        if (response != null && !TextUtils.isEmpty(response.accessToken)) {
            Prefs.putString(PrefsConstants.AUTH_TOKEN_KEY, response.tokenType.concat(" ").concat(response.accessToken));
            Prefs.putString(PrefsConstants.TOKEN_EXPIRE_DATE, response.expires);
        } else {
            showToast(R.string.something_went_wrong);
        }
    }


    private void initRetrofit() {
        Log.d(TAG, "initRetrofit: ");
        final int CONNECT_TIMEOUT_MILLIS = 1500 * 1000; // 15s
        final int READ_TIMEOUT_MILLIS = 2000 * 1000; // 20s

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(/*message -> Timber.d(message)*/);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        //RequestHeaderInterceptor headerInterceptor = new RequestHeaderInterceptor();
        //MtrainerLogIntercepter mtrainerLogIntercepter = new MtrainerLogIntercepter();
        //builder.addInterceptor(headerInterceptor);
        //builder.addInterceptor(mtrainerLogIntercepter);

        builder.addInterceptor(loggingInterceptor);
        /*builder.connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);*/

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://mtrainer.sisindia.com/").client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        authApi1 = retrofit.create(AuthApi.class);

        OkHttpClient.Builder builder1 = new OkHttpClient.Builder()
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

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


    public void onLoginSubmitMobileClick(View view) {
        String phoneNumber = phoneNumberObs.get();
        if (TextUtils.isEmpty(phoneNumber)) {
            showToast("Please provide mobile number");
            return;
        }

        if (phoneNumber.length() < 10) {
            showToast("Mobile number invalid");
            return;
        }
        //phone number is valid
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            showToast("Unexpected Error, please try again");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        setIsLoading(true);
                        UserRequest request = new UserRequest();
                        request.phoneNumber = phoneNumber;
                        request.firebaseToken = token;
                        request.version = BuildConfig.VERSION_CODE;
                        Log.i(TAG, "onComplete: Firebase Token -> " + token);
                        Prefs.putString(PrefsConstants.USER_PHONE_NUMBER, phoneNumber);
                        addDisposable(authApi.generateOtp(request)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(UserViewModel.this::onOtpSendSuccess, UserViewModel.this::onApiError));

                    }
                });
        /*setIsLoading(true);
        UserRequest request = new UserRequest();
        request.phoneNumber = phoneNumber;
        request.version = BuildConfig.VERSION_CODE;*/

        // store phone number in SP

        /*Prefs.putString(PrefsConstants.USER_PHONE_NUMBER, phoneNumber);

        addDisposable(authApi.generateOtp(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onOtpSendSuccess, this::onApiError));*/

    }

    public void setOtp(String otp) {
        otpTextObs.set(otp);
    }

    private void onOtpSendSuccess(UserResponse response) {
        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            Prefs.putString(PrefsConstants.EMPLOYEE_ID, response.otpResponse.employeeId);
            if (response.otpResponse.companyId != null)
                Prefs.putString(PrefsConstants.COMPANY_ID, response.otpResponse.companyId.trim());
            Prefs.putString(PrefsConstants.OTP, response.otpResponse.otp);
            gotopt = response.otpResponse.otp;
            message.what = NavigationConstants.ON_LOGIN_SUBMIT_MOBILE_NUMBER;
            liveData.postValue(message);
        } else {
            showToast(response.statusMessage);
        }
    }

    public void onOtpSubmitClick(View view) {

        String otp = otpTextObs.get();

        if (TextUtils.isEmpty(otp)) {
            showToast("Please provide OTP");
            return;
        }
        if (otp.length() != 6) {
            showToast("Please Enter Valid OTP");
            return;
        }

        String got = Prefs.getString(PrefsConstants.OTP);

        if (!got.equals(otp)) {
            showToast("Please Enter Correct Otp");
            otpTextObs.set("");

        } else {

            message.what = NavigationConstants.HIDE_KEYBOARD;
            liveData.postValue(message);

            isotpLoading.set(View.VISIBLE);
            String userNumber = Prefs.getString(PrefsConstants.USER_PHONE_NUMBER);

            UserRequest request = new UserRequest();
            request.phoneNumber = userNumber;
            request.otp = otp;

            addDisposable(authApi.verifyOtp(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onLoginResponse, this::onApiError));
        }

    }

    private void onLoginResponse(VerifyOtpResponse response) {
        //isotpLoading.set(GONE);
        if (response.statusCode == SUCCESS_RESPONSE) {
            Prefs.putBoolean(PrefsConstants.IS_LOGGED_IN, true);

            Prefs.putString(PrefsConstants.EMPPLOYEE_NAME, response.loginResponse.get(0).empName);
            Prefs.putString(PrefsConstants.ROLE, response.loginResponse.get(0).role);
            Prefs.putInt(PrefsConstants.BASE_UNIT_ID, response.loginResponse.get(0).unitId);
            Prefs.putString(PrefsConstants.BASE_UNIT_NAME, response.loginResponse.get(0).unitName);
            Prefs.putInt(PrefsConstants.BASE_TRAINER_ID, response.loginResponse.get(0).trainerId);
            Prefs.putString(PrefsConstants.EMPLOYEE_REG_NO, response.loginResponse.get(0).regdNo);
            Prefs.putInt(PrefsConstants.REGION_ID, response.loginResponse.get(0).regionId);
            Prefs.putString(PrefsConstants.SCORE_METRIC, "7");
           /* Random random = new Random();
            Prefs.putInt(PrefsConstants.RANDOM_RANK, random.nextInt(499) + 1);
            float score = ((float) (random.nextInt(3) + 1)) + (((float)(random.nextInt(8) + 1)) / (float) 10.0);
            Prefs.putFloat(PrefsConstants.RANDOM_SCORE, score);*/
            /*message.what = NavigationConstants.OPEN_DASH_BOARD;
            liveData.postValue(message);*/
            SaveTokenRequest saveTokenRequest = new SaveTokenRequest();
            saveTokenRequest.setLoginCode(response.loginResponse.get(0).regdNo);
            saveTokenRequest.setPassword("India@123");
            saveTokenRequest.setUserName("ApiAdmin");
            getgctoken(saveTokenRequest);

            /*message.what = NavigationConstants.OPEN_DASH_BOARD;
            liveData.postValue(message);      */
            openConfigurationScreen();
        } else {
            isotpLoading.set(GONE);
            Prefs.putBoolean(PrefsConstants.IS_LOGGED_IN, false);
            showToast(R.string.string_error_in_login);
        }
    }

    private void getgctoken(SaveTokenRequest saveTokenRequest) {

        authApi1.gettoken(saveTokenRequest).enqueue(new Callback<SaveTokenResponse>() {
            @Override
            public void onResponse(Call<SaveTokenResponse> call, Response<SaveTokenResponse> response) {
                try {
                    if (response.body().getStatus().equals("1")) {
                        Log.d("sxadsa", "Sucess");
                        Log.d("sxadsa", response.body().getLoginCodeToken());
                        setIsLoading(false);
                        Prefs.putString("LoginToken", response.body().getLoginCodeToken());
                        Date date = new Date();
                        Date date1 = new Date();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date1);
                        calendar.add(Calendar.HOUR, 8);
                        long formatdate = date.getTime();
                        Date date2 = calendar.getTime();
                        long expirydate = date2.getTime();
                        Prefs.putLong("TokenTime", formatdate);
                        Prefs.putLong("ExpiryTokenTime", expirydate);
                        isotpLoading.set(GONE);
                        message.what = OPEN_DASH_BOARD;
                        liveData.postValue(message);
                    }
                } catch (Exception e) {
                    isotpLoading.set(GONE);
                    message.what = OPEN_DASH_BOARD;
                    liveData.postValue(message);
                }
            }

            @Override
            public void onFailure(Call<SaveTokenResponse> call, Throwable t) {
                isotpLoading.set(GONE);
            }
        });


    }

    public void onEditNumberClick(View view) {
        message.what = OPEN_LOGIN;
        liveData.postValue(message);
    }

    public void onResendOtpClick(View view) {
        setIsLoading(true);
        String phoneNumber = Prefs.getString(PrefsConstants.USER_PHONE_NUMBER);


        UserRequest request = new UserRequest();
        request.phoneNumber = phoneNumber;
        request.version = BuildConfig.VERSION_CODE;


        addDisposable(authApi.generateOtp(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMobileNumberSubmitSuccess, this::onApiError));
    }

    private void onMobileNumberSubmitSuccess(UserResponse response) {
        setIsLoading(false);
        Timber.e("");
        if (response.statusCode == 200) {
            Toast.makeText(getApplication(), "Otp Sent Success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplication(), "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void openConfigurationScreen() {
        long delayTime = 32000;
        setIsLoading(false);
        isDataSyncing.set(true);
        calculateLoadingPercentage(delayTime);

        fetchMasterUnitList();
        loadTrainingMasterData();
    }

    private void calculateLoadingPercentage(long loaderTime) {
        long finalTime = loaderTime + 4000;
        cTimer = new CountDownTimer(finalTime, 2000) {
            public void onTick(long tickTime) {
                int percentage = (int) ((tickTime * 100) / finalTime);
                int updatedPercentage = 100 - percentage;
                percentageTime.set(updatedPercentage);
                message.what = ON_UPDATING_LOADING_TIME;
                message.arg1 = updatedPercentage;
                liveData.postValue(message);
            }

            public void onFinish() {
                message.what = OPEN_DASH_BOARD;
                liveData.postValue(message);
            }
        };
        cTimer.start();
    }

    public void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
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

    private void onMasterUnitSuccess(UnitListResponse response) {

        if (response.statusCode == SUCCESS_RESPONSE) {

            addDisposable(dataBase.getUnitListDao().insertUnitList(response.unitList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());

            int lastPage = (int) Math.ceil(response.totalCount / (double) 50);
            Prefs.putInt(PrefsConstants.UNIT_LAST_PAGE_NUMBER, lastPage);
            getUnitList(response.currPage, 50, lastPage);
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

    private void loadTrainingMasterData() {
        StarProgramMasterBodyMO body = new StarProgramMasterBodyMO(
                Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID)),
                Prefs.getInt(PrefsConstants.BASE_TRAINER_ID)
        );
        addDisposable(dashBoardApi.getTrainingMasterData(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFetchingTrainingMasterData, this::onApiError));
    }

    /*private void onFetchingTrainingMasterData(StarTrainingMasterResponseMO response) {
        if (response.statusCode == SUCCESS_RESPONSE) {

            if (!response.getData().isEmpty()) {

                StarTrainingMasterDao dao = dataBase.getTrainingMasterDao();

                addDisposable(dao.insertAllProgramList(response.getData())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe());

                for (ProgramEntity program : response.getData()) {
                    if (!program.languagesList.isEmpty()) {
                        addDisposable(dao.insertAllLanguageList(program.languagesList)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe());
                    }
                }

            }
        }
    }*/

    private void onFetchingTrainingMasterData(StarTrainingMasterResponseMO response) {
        if (response.statusCode != SUCCESS_RESPONSE) return;

        List<ProgramEntity> programs = response.getData();
        if (programs.isEmpty()) return;

        // Flat lists to collect all entities
        List<LanguageEntity> allLanguages = new ArrayList<>();
        List<CourseEntity> allCourses = new ArrayList<>();
        List<TopicEntity> allTopics = new ArrayList<>();

        for (ProgramEntity program : programs) {
            if (program.languagesList == null) continue;

            for (LanguageEntity language : program.languagesList) {
                allLanguages.add(language);
                if (language.coursesList == null) continue;

                for (CourseEntity course : language.coursesList) {
                    course.languageId = language.languageId;
                    allCourses.add(course);
                    if (course.topicsList == null) continue;

                    allTopics.addAll(course.topicsList);
                }
            }
        }

        StarTrainingMasterDao dao = dataBase.getTrainingMasterDao();

        addDisposable(
                dao.deleteAllMasterProgram()
                        .andThen(dao.deleteAllMasterLanguage())
                        .andThen(dao.deleteAllMasterCourse())
                        .andThen(dao.deleteAllMasterTopic())
                        .andThen(dao.insertAllProgramList(programs))
                        .andThen(dao.insertAllLanguageList(allLanguages))
                        .andThen(dao.insertAllCoursesList(allCourses))
                        .andThen(dao.insertAllTopicList(allTopics))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> Log.d(TAG, "Training data saved"),
                                err -> Log.e(TAG, "Insert failed", err)
                        )
        );
    }
}