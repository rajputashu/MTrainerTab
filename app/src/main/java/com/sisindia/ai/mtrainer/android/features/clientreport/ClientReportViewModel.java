package com.sisindia.ai.mtrainer.android.features.clientreport;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
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
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.db.entities.SavedClientReportCc;
import com.sisindia.ai.mtrainer.android.db.entities.SavedClientReportTo;
import com.sisindia.ai.mtrainer.android.features.attendancemodule.OnQuery;
import com.sisindia.ai.mtrainer.android.features.pretraining.PreTrainingReviewActivity;
import com.sisindia.ai.mtrainer.android.features.trainingimages.ToggleListener;
import com.sisindia.ai.mtrainer.android.models.ContactListRequest;
import com.sisindia.ai.mtrainer.android.models.ContactListResponse;
import com.sisindia.ai.mtrainer.android.models.PostItem;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.Report.ReportDbCcResponse;
import com.sisindia.ai.mtrainer.android.models.Report.ReportDbResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceRequest;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceResponse;
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

import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

public class ClientReportViewModel extends MTrainerViewModel implements ClientReportRecylerAdapter.ViewUpdate {
    public ClientReportRecylerAdapter clientReportRecylerAdapter;
    private String reportCc = "";
    public final ObservableField<String> numberOfClientHeader = new ObservableField<>("To");
    private MtrainerDataBase dataBase;
    public boolean canShow = Prefs.getBoolean(PrefsConstants.CAN_SHOW, false);
    private Observable.OnPropertyChangedCallback propertyChangedCallback;
    private List<SavedClientReportCc> clientReportCc;
    public ObservableBoolean isMultipleSession = new ObservableBoolean(false);
    @Inject
    DashBoardApi dashBoardApi;

    @Inject
    AuthApi authApi;

    @Inject
    public ClientReportViewModel(@NonNull Application application) {
        super(application);
        initRetrofit();
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse1, this::onApiError));

        clientReportRecylerAdapter = new ClientReportRecylerAdapter(MtrainerDataBase.getDatabase(application), this::updateView);
        dataBase = MtrainerDataBase.getDatabase(application);
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


    public ToggleListener toggleListener = new ToggleListener() {
        @Override
        public void onToggle(boolean isChecked) {
            isMultipleSession.set(isChecked);
            message.what = NavigationConstants.UPDATE_CLIENT_REPORT_VIEW;
            liveData.postValue(message);
        }
    };

    public TextChange onCcTextChangeListener = new TextChange() {
        @Override
        public void onCcTextChange(String text) {
            reportCc = text;
            if (text != null && text.contains("@")) {
                canShow = true;
                updateView();
            } else {
                canShow = false;
                updateView();
            }
        }
    };

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

    private void onContactMasterSuccess(ContactListResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getClientListDao().insertClientData(response.clientDataList)
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
    }

    LiveData<List<ContactListResponse.ClientData>> getClientList() {
        return dataBase.getClientListDao().getClientList(Prefs.getInt(PrefsConstants.UNIT_ID));
    }

    void setClientList(List<ContactListResponse.ClientData> clientDataList) {
        if(clientDataList != null && clientDataList.size() < 1) {
            numberOfClientHeader.set("Sorry no Client Found");

        } else {
            numberOfClientHeader.set("To");
            clientReportRecylerAdapter.clearAndSetItems(clientDataList);
        }
    }

    @Override
    public void updateView() {
        message.what = NavigationConstants.UPDATE_CLIENT_REPORT_VIEW;
        liveData.postValue(message);
    }

    void SaveReportData() {
        message.what = NavigationConstants.SAVE_CLIENT_REPORT;
        liveData.postValue(message);
    }


    void emailFinalSaveList(List<SavedClientReportTo> reportTo , List<SavedClientReportCc> clientReportCc) {
        if (reportTo.isEmpty() && clientReportCc.isEmpty()) {
            showToast("Need at least one email Id");
        } else {
            this.clientReportCc = clientReportCc;
            dataBase.getSavedClientReportToDao().insertClientEmail(reportTo)
                    .subscribeOn(Schedulers.io())
                    .subscribe(this::toEmailSaved);
        }
    }

    private void toEmailSaved() {
        Log.v("TO", "saved");
        dataBase.getSavedClientReportCcDao().insertClientCc(clientReportCc)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onDataSaved);
    }

     void onDataSaved() {
        Log.v("CC", "saved");
        canShow = true;
        message.what = NavigationConstants.CLOSE_CLIENT_REPORT_VIEW;
        liveData.postValue(message);
    }


    void getClientSavedData() {
        setIsLoading(true);
        dataBase.getSavedClientReportToDao().getClientToList(Prefs.getInt(PrefsConstants.ROTA_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onGettingToReportDataSucess);
    }

    private void onGettingToReportDataSucess(List<ReportDbResponse> dbResponses) {
        message.what = NavigationConstants.INFLATE_TO_CLIENT_REPORT_VIEW;
        message.obj = dbResponses;
        liveData.postValue(message);
        getCcData();
    }


    private void getCcData() {
        dataBase.getSavedClientReportCcDao().getClientSavedCcList(Prefs.getInt(PrefsConstants.ROTA_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onGettingCcReportDataSucess);
    }

    private void onGettingCcReportDataSucess(List<ReportDbCcResponse> dbResponses) {
        message.what = NavigationConstants.INFLATE_CC_CLIENT_REPORT_VIEW;
        message.obj = dbResponses;
        liveData.postValue(message);
    }

    void removeTo(String id) {
        dataBase.getSavedClientReportToDao().deleteClientToEmail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    void removeCc(String id) {
        dataBase.getSavedClientReportCcDao().deleteClientCcEmail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}