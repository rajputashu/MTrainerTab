package com.sisindia.ai.mtrainer.android.features.feedback;

import static com.sisindia.ai.mtrainer.android.features.feedback.FeedbackActivity.selectedFeedbackDetails;
import static com.sisindia.ai.mtrainer.android.features.feedback.FeedbackActivity.selectedRating;
import static com.sisindia.ai.mtrainer.android.features.feedback.FeedbackActivity.selectedReasonList;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
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
import com.sisindia.ai.mtrainer.android.constants.FeedbackConstants;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.SavedTopic;
import com.sisindia.ai.mtrainer.android.db.entities.SavedFeedback;
import com.sisindia.ai.mtrainer.android.db.entities.SavedFeedbackReason;
import com.sisindia.ai.mtrainer.android.models.ContactListRequest;
import com.sisindia.ai.mtrainer.android.models.ContactListResponse;
import com.sisindia.ai.mtrainer.android.models.FeedBackOtpRequest;
import com.sisindia.ai.mtrainer.android.models.FeedBackOtpResponse;
import com.sisindia.ai.mtrainer.android.models.FeedBackVerifyOtpRequest;
import com.sisindia.ai.mtrainer.android.models.FeedBackVerifyOtpResponse;
import com.sisindia.ai.mtrainer.android.models.FeedbackReasonQuestionItem;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.RatingMasterRequest;
import com.sisindia.ai.mtrainer.android.models.RatingMasterResponse;
import com.sisindia.ai.mtrainer.android.models.UserRequest;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FeedbackActivityViewModel extends MTrainerViewModel {
    public ObservableField<String> feedbackOtpObs = new ObservableField<>();
    public ObservableField<String> feedbackOtp = new ObservableField<>();
    //    public ObservableField<String> feedbackname = new ObservableField<>();
//    public ObservableField<String> feedbackemailid = new ObservableField<>();
//    public ObservableField<String> feedbackphoneno = new ObservableField<>();
    private MtrainerDataBase dataBase;
    //    private final String gototp = "";
    public String companyID = Prefs.getString(PrefsConstants.COMPANY_ID);
    public ObservableField<String> totalEmployee = new ObservableField<>();
    public ObservableField<String> totalPost = new ObservableField<>();
    public ObservableField<String> totalTopic = new ObservableField<>();
    public ObservableField<String> selectedPost = new ObservableField<>();
    public ObservableField<String> selectedEmployee = new ObservableField<>();

    public ObservableInt selectedTopic = new ObservableInt();
    public ObservableInt selectedAdhocTopic = new ObservableInt();
    private String receivedFeedbackOTP = "";

    @Inject
    AuthApi authApi;

    @Inject
    public FeedbackActivityViewModel(@NonNull Application application) {
        super(application);
        initRetrofit();
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse1, this::onApiError));

        dataBase = MtrainerDataBase.getDatabase(application);
    }

    private void onPreAuthResponse1(PreAuthResponse response) {
        if (response != null && !TextUtils.isEmpty(response.accessToken)) {
            Prefs.putString(PrefsConstants.AUTH_TOKEN_KEY, response.tokenType.concat(" ").concat(response.accessToken));
            Prefs.putString(PrefsConstants.TOKEN_EXPIRE_DATE, response.expires);
        } else {
            showToast(R.string.something_went_wrong);
        }
    }

    @Inject
    DashBoardApi dashBoardApi;

    void getRatingMaster() {
        setIsLoading(true);

        RatingMasterRequest request = new RatingMasterRequest();
        request.companyId = Prefs.getString(PrefsConstants.COMPANY_ID);

        addDisposable(dashBoardApi.getRattingQuestionList(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRatingMasterSuccess, this::onApiError));
    }

    void getContactMaster() {
        setIsLoading(true);

        ContactListRequest request = new ContactListRequest();
        request.userId = Prefs.getString(PrefsConstants.EMPLOYEE_ID);
        request.updatedDate = "2020-11-04";

        addDisposable(dashBoardApi.getClientContactList(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onContactMasterSuccess, this::onApiError));
    }

    private void onRatingMasterSuccess(RatingMasterResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getRatingQuestionDao().insertRatingQuestion(response.ratingQuestionList)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
            dataBase.getRatingDataDao().insertRatingData(response.ratingDataList)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
    }

    private void onContactMasterSuccess(ContactListResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getClientListDao().insertClientData(response.clientDataList)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
    }

    void startFinalFeedback() {
        message.what = NavigationConstants.FINAL_FEEDBACK;
        liveData.postValue(message);
    }

    void nextFabPressed(int selectedOption) {
        if (selectedOption == FeedbackConstants.CLIENT_NOT_PRESENT) {
            message.what = NavigationConstants.CLIENT_NOT_PRESENT;
            liveData.postValue(message);
        } else if (selectedOption == FeedbackConstants.CLIENT_REPRESENTATIVE) {
            message.what = NavigationConstants.CLIENT_REPRESENTATIVE;
            liveData.postValue(message);
        } else {
            message.what = NavigationConstants.CLIENT_RATING;
            liveData.postValue(message);
        }
    }

    // TODO: Static Variable used just to get size
    void updateSummaryUI(String totalTopic, String totalEmp, String totalPost) {
        this.totalEmployee.set(totalEmp);
        this.totalPost.set(totalPost);
        this.totalTopic.set(totalTopic);
        selectedEmployee.set(String.valueOf(Prefs.getInt(PrefsConstants.SELECTED_EMP_COUNT, 0)));
        selectedPost.set(String.valueOf(Prefs.getInt(PrefsConstants.SELECTED_POST_COUNT, 0)));
        selectedTopic.set(Prefs.getInt(PrefsConstants.SELECTED_TOPIC_COUNT, 0));
        selectedAdhocTopic.set(Prefs.getInt(PrefsConstants.SELECTED_ADHOC_TOPIC_COUNT, 0));
        //selectedAdhocEmployee.set(Prefs.getInt(PrefsConstants.SELECTED_ADHOC_EMP_COUNT,0));
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

    public void onResendOtpClick(View view) {

    }

    @Deprecated(forRemoval = true)
    void feedbackOtp() {
        try {
            //logger.log("calling otpfeedbackapi");
            String number = FeedbackActivity.selectedFeedbackDetails.get(2);
            String email = FeedbackActivity.selectedFeedbackDetails.get(1);
//            showToast("OTP Sent to mobile no :"+number + Prefs.getInt(PrefsConstants.ROTA_ID));
            showToast("OTP Sent to mobile no :" + number);

            // Call the API and store the Otp for verification

            FeedBackOtpRequest request = new FeedBackOtpRequest();
            request.phoneNumber = number;
//            request.trainingId = FeedbackActivity.rotaid;
            request.clientEmailId = email;
            Prefs.putString(PrefsConstants.CLIENT_EMAIL, email);
            Prefs.putString(PrefsConstants.CLIENT_PHONE_NUMBER, number);
            addDisposable(dashBoardApi.getFeedBackOtp(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onFeedBackOtpSendSuccess, this::onApiError));
        } catch (Exception ignored) {
        }
    }

    void feedbackOtpV2() {
        setIsLoading(true);
        FeedBackOtpRequest request = new FeedBackOtpRequest();
        request.phoneNumber = Prefs.getString(PrefsConstants.CLIENT_PHONE_NUMBER);
        request.trainingId = Prefs.getInt(PrefsConstants.ROTA_ID);
        request.clientEmailId = Prefs.getString(PrefsConstants.CLIENT_EMAIL);
        addDisposable(dashBoardApi.getFeedBackOtp(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onFeedBackOtpSendSuccess, this::onApiError));
    }

    private void onFeedBackOtpSendSuccess(FeedBackOtpResponse response) {
        setIsLoading(false);
        if (response.feedBackOtpGeneratedResponse != null) {
            showToast(response.feedBackOtpGeneratedResponse.statusMessage);
            receivedFeedbackOTP = response.feedBackOtpGeneratedResponse.otp;
        } else {
            showToast(response.statusMessage);
        }
    }

    void verifyOtp(String otp) {

        if (TextUtils.isEmpty(otp)) {
            showToast("Please provide OTP");
            return;
        }
        if (otp.length() != 4) {
            showToast("Please Enter Valid OTP");
            return;
        }

//        String got = Prefs.getString(PrefsConstants.FEEDBACK_OTP);

        if (!receivedFeedbackOTP.equals(otp)) {
            showToast("Please enter correct OTP");
        } else {

            setIsLoading(true);
            feedbackOtp.set(otp);

            // Call the API and store the Otp for verification
            FeedBackVerifyOtpRequest request = new FeedBackVerifyOtpRequest();
            request.setOtp(Integer.parseInt(otp));
            request.setPhoneNumber(Prefs.getString(PrefsConstants.CLIENT_PHONE_NUMBER));
            request.setTrainerId(Prefs.getInt(PrefsConstants.ROTA_ID));
            request.setClientEmailId(Prefs.getString(PrefsConstants.CLIENT_EMAIL));
            addDisposable(dashBoardApi.getFeedBackverifyotp(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onFeedBackOtpverifySuccess, this::onApiError));
        }
    }

    private void onFeedBackOtpverifySuccess(FeedBackVerifyOtpResponse feedBackVerifyOtpResponse) {
        String userNumber = Prefs.getString(PrefsConstants.USER_PHONE_NUMBER);

        UserRequest request = new UserRequest();
        request.phoneNumber = userNumber;
        request.otp = feedbackOtp.get();
        message.what = NavigationConstants.SAVE_FEEDBACK_DATA;
        liveData.postValue(message);
    }

    void initLocalSaving() {
        try {
            addDisposable(dataBase.getSavedFeedbackReasonDao()
                    .deleteReason(String.valueOf(Prefs.getInt(PrefsConstants.ROTA_ID)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::saveToLocal, this::onApiError));
        } catch (Exception e) {
            logger.log("feedbackviewmodel initLocalSaving function exception", e.getCause());
        }
    }


    private void saveToLocal() {
        try {
            if (selectedRating != 0 && !selectedReasonList.isEmpty()) {
                ArrayList<SavedFeedbackReason> feedbackReasonArrayList = new ArrayList<>();
                for (FeedbackReasonQuestionItem reason : selectedReasonList) {
                    SavedFeedbackReason feedbackReason = new SavedFeedbackReason();
                    feedbackReason.rotaId = String.valueOf(Prefs.getInt(PrefsConstants.ROTA_ID));
                    feedbackReason.reason = reason.question;
                    feedbackReason.reasonId = reason.questionId;
                    feedbackReasonArrayList.add(feedbackReason);
                }
                dataBase.getSavedFeedbackReasonDao().insertReason(feedbackReasonArrayList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::saveMasterFeedback, this::onApiError);
            } else {
                saveMasterFeedback();
            }
        } catch (Exception e) {
            logger.log("feedbackactivityviewmodel saveToLocal function exception", e.getCause());
        }
    }

    private void onSavedFeedbackData() {
        message.what = NavigationConstants.CLOSE_FEEDBACK_ACTIVITY;
        liveData.postValue(message);
    }

    private void saveMasterFeedback() {
        try {
            SavedFeedback savedFeedback = new SavedFeedback();
            savedFeedback.rotaId = String.valueOf(Prefs.getInt(PrefsConstants.ROTA_ID));
            if (selectedRating == 0) {
                savedFeedback.clientNotAvailableReason = selectedReasonList.iterator().next().question;
            } else {
                savedFeedback.clientId = selectedFeedbackDetails.get(3);
                savedFeedback.name = selectedFeedbackDetails.get(0);
                savedFeedback.clientAvailable = "true";
                if (companyID.equals("2") || companyID.equals("7") || companyID.equals("9")) {
                    savedFeedback.clientMobNumber = selectedFeedbackDetails.get(2);
                    savedFeedback.clientOtpVerify = "true";
                } else {
                    savedFeedback.clientMobNumber = "NA";
                    savedFeedback.clientOtpVerify = "false";
                    savedFeedback.remarks = FeedbackActivity.feedbackRemarks;
                }
                savedFeedback.emailId = selectedFeedbackDetails.get(1);
                savedFeedback.rating = selectedRating;
            }
            dataBase.getSavedFeedbackDao().insertFeedback(savedFeedback)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onSavedFeedbackData, this::onApiError);
        } catch (Exception e) {
            logger.log("feedbackviewmodel saveMasterFeedback function exception", e.getCause());
        }
    }


    // this is getting topic count from table to show on the startactivity screen
    LiveData<List<SavedTopic>> getSavedList() {
        return dataBase.getSavedTopicDao().getSavedTopicList(Prefs.getInt(PrefsConstants.ROTA_ID));
    }

}
