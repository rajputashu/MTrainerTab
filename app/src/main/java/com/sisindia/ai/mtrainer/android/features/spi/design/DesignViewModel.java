package com.sisindia.ai.mtrainer.android.features.spi.design;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_CLIENT_APPROVAL_SCREEN;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
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
import com.sisindia.ai.mtrainer.android.features.spi.model.DesignStatusResponse;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiStatusRequest;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;
import com.sisindia.ai.mtrainer.android.models.DesignSpiRequest;
import com.sisindia.ai.mtrainer.android.models.DesignSpiResponse;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.SpiPrintingRequest;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;
import com.squareup.picasso.Picasso;

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

public class DesignViewModel extends MTrainerViewModel {

    public ObservableField<String> status = new ObservableField<>();

    public ObservableField<String> customer = new ObservableField<>();
    public ObservableField<String> customerid = new ObservableField<>();
    public ObservableField<String> type = new ObservableField<>();
    public ObservableField<String> designstatus = new ObservableField<>("pending");
    public String designstatus1 = "pending";

    public DesignRecyclerAdapter designRecyclerAdapter;
    @Inject
    DashBoardApi dashBoardApi;

    @Inject
    AuthApi authApi;
    MtrainerDataBase dataBase;
    @Inject
    Picasso picasso;

    @Inject
    public DesignViewModel(@NonNull Application application) {
        super(application);
        initRetrofit();
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse1, this::onApiError));

        dataBase = MtrainerDataBase.getDatabase(application);
        designRecyclerAdapter = new DesignRecyclerAdapter(dataBase, picasso, application);

        customer.set(Prefs.getString(PrefsConstants.SPI_CUSTOMER));
        customerid.set(Prefs.getString(PrefsConstants.SPI_UNIT_CODE));
        type.set(Prefs.getString(PrefsConstants.SPI_TYPE));
        status.set(Prefs.getString(PrefsConstants.SPI_STATUS));
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
        Retrofit retrofit1 = new Retrofit.Builder().baseUrl(BuildConfig.MTRAINER_HOST)
                .client(builder1.build())
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


    public void SpiDesignDetails() {

        dataBase.getDesignSpidao().flushDesignTable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        designRecyclerAdapter.clear();
        designRecyclerAdapter.notifyDataSetChanged();
//Prefs.getInt(PrefsConstants.SPI_ID)
        addDisposable(dashBoardApi.getSpiDesign(new DesignSpiRequest(Prefs.getInt(PrefsConstants.SPI_ID)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSpiDesignSuccess, this::onApiError));

    }

    private void onSpiDesignSuccess(DesignSpiResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {

            dataBase.getDesignSpidao().insertSpiDesignData(response.designSpiData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();

            if (response.designSpiData.isEmpty()) {
                showToast("Not found any rota ");
            } else {
                designstatus.set(response.designSpiData.get(0).status);
                designstatus1 = designstatus.get();
            }

        }
    }

    public void submitDesign(View view) {
        if (designstatus1.equals("Done")) {
            message.what = OPEN_CLIENT_APPROVAL_SCREEN;
            liveData.postValue(message);
        } else {
            showToast("design status pending");
        }
    }

    void designSpiDetails(List<DesignSpiResponse.DesignSpiData> data) {
        designRecyclerAdapter.clearAndSetItems(data);
        designRecyclerAdapter.notifyDataSetChanged();
    }

    LiveData<List<DesignSpiResponse.DesignSpiData>> getDesignSpiDetails() {
        return dataBase.getDesignSpidao().getDesignDetails(Prefs.getInt(PrefsConstants.SPI_ID));
    }

    public void flushDraftApproval() {
        dataBase.getDraftApprovalDao().flushDraftApprovalTable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }


    public void DesignStatus() {
//Prefs.getInt(PrefsConstants.SPI_ID)
        addDisposable(dashBoardApi.getDesignStatus(new SpiPrintingRequest(Prefs.getInt(PrefsConstants.SPI_ID)
                ))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onDesignStatussuccess, this::onApiError));
    }

    private void onDesignStatussuccess(DesignStatusResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {

            dataBase.getDesignStatusDao().insertDesignData(response.designStatuses)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
            dataBase.getMountedDao().flushMountedTable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();

            //  designstatus.set(response.designStatuses.get(0).status);

            if (response.designStatuses.isEmpty()) {

                showToast("Not found any rota ");
            } else {

            }

        }
    }


    void getDraftStatus(SpiStatusRequest request) {
        addDisposable(dashBoardApi.getSpiStatus(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSpistatusSuccess, this::onApiError));
    }

    private void onSpistatusSuccess(BaseApiResponse response) {
        if (response.statusCode == SUCCESS_RESPONSE) {

        }
    }


}
