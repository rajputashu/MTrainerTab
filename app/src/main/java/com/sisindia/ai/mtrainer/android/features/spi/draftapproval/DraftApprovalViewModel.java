package com.sisindia.ai.mtrainer.android.features.spi.draftapproval;

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
import com.sisindia.ai.mtrainer.android.features.spi.model.ReuploadDraftSpiRequest;
import com.sisindia.ai.mtrainer.android.features.spi.model.ReuploadDraftSpiResponse;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiStatusRequest;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.SpiBasicInfoResponse;
import com.sisindia.ai.mtrainer.android.models.spi.DraftApprovalRequest;
import com.sisindia.ai.mtrainer.android.models.spi.DraftApprovalResponse;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;

import java.util.ArrayList;
import java.util.List;
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

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_DESIGN_SPI_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_DRAFT_SPI_SCREEN;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;


public class DraftApprovalViewModel extends MTrainerViewModel {

    public ObservableField<String> status= new ObservableField<>();
    public DraftApprovalRecyclerAdapter draftApprovalRecyclerAdapter;

    public ObservableField<String> customer= new ObservableField<>();
    public ObservableField<String> customerid= new ObservableField<>();
    public ObservableField<String> type= new ObservableField<>();
    private MtrainerDataBase dataBase;
    private List<DraftApprovalResponse.DraftApprovalTableDetailsData> approve;
    @Inject
    DashBoardApi dashBoardApi;
    @Inject
    AuthApi authApi;
    public String draftapproved="pending";
    Boolean canSubmit;
   // List<DraftApprovalResponse.DraftApprovalTableDetailsData> draftapproved;

    @Inject
    public DraftApprovalViewModel(@NonNull Application application)
    {
        super(application);
        initRetrofit();
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse1, this::onApiError));

        dataBase = MtrainerDataBase.getDatabase(application);
        draftApprovalRecyclerAdapter = new DraftApprovalRecyclerAdapter(dataBase);
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


    public void  flashDraftSpiPhotos(){
         dataBase.getDraftSpiPhotoDao().flushDraftSpiPhotoTable()
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe();

     }


/*
    public void submitdraftAppro(View view){

      //  message.what = OPEN_DESIGN_SPI_SCREEN;
     //   liveData.postValue(message);
        if(draftApprovalRecyclerAdapter.equals("")){
            showToast("draft not approved!! you can't go next step");
        }
        else {
            message.what = OPEN_DESIGN_SPI_SCREEN;
            liveData.postValue(message);

        }

    }*/

   public void getDraftAprrovalData(){
        //Prefs.getInt(PrefsConstants.SPI_ID)

       dataBase.getDraftApprovalDao().flushDraftApprovalTable()
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe();

        addDisposable(dashBoardApi.getDraftApproval(new DraftApprovalRequest(Prefs.getInt(PrefsConstants.SPI_ID)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onDraftApprovalSuccess, this::onApiError));
    }

    private void onDraftApprovalSuccess(DraftApprovalResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {

            dataBase.getDraftApprovalDao().insertDraftApproval(response.draftApprovalTableDetailsData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();

            if(!response.draftApprovalTableDetailsData.isEmpty()){

                 draftapproved=response.draftApprovalTableDetailsData.get(0).status;

            }

            else {
                showToast("no data found ");
            }

        }

    }


    void DraftApprovalDetails(List<DraftApprovalResponse.DraftApprovalTableDetailsData> data) {
        draftApprovalRecyclerAdapter.clearAndSetItems(data);
        draftApprovalRecyclerAdapter.notifyDataSetChanged();

    }

    LiveData<List<DraftApprovalResponse.DraftApprovalTableDetailsData>> getDraftApprovalDetails() {
        return dataBase.getDraftApprovalDao().getDraftApprovalDetails(Prefs.getInt(PrefsConstants.SPI_ID));

    }



    void getDraftStatus(SpiStatusRequest request) {
        addDisposable(dashBoardApi.getSpiStatus(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSpistatusSuccess, this::onApiError));
    }

    private void onSpistatusSuccess(BaseApiResponse response) {
        if(response.statusCode==SUCCESS_RESPONSE){

        }
    }
    //reuploading draft spi navigation
    public void getReuploadDraftSpiData(){

        flushReuploadData();
        addDisposable(dashBoardApi.getReuploadDraftSpi(new ReuploadDraftSpiRequest(Prefs.getInt(PrefsConstants.SPI_ID)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onReuploadDraftSpiSuccess, this::onApiError));
    }


    private void onReuploadDraftSpiSuccess(ReuploadDraftSpiResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {

            if(!response.reuploadDraftSpiData.isEmpty()){
                dataBase.getReuploadDraftSpiDao().insertReuploadDraftSpi(response.reuploadDraftSpiData)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }

            else {
                showToast("no data found ");
            }

        }

    }

    //reuploading draft spi navigation
    public void storeToBasicInfoTable(){

        // written flushing of basic info table
        dataBase.getSpiBasicInfoDao().flushSpiBasicTable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        addDisposable(dashBoardApi.getReuploadToBasic(new ReuploadDraftSpiRequest(Prefs.getInt(PrefsConstants.SPI_ID)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onStoreIntoBasicInfoTableSuccess, this::onApiError));
    }

    private void onStoreIntoBasicInfoTableSuccess(SpiBasicInfoResponse response) {

       if (response.statusCode == SUCCESS_RESPONSE) {
            if(response.spiBasicInfoDetailsData != null)
                Observable.fromIterable(response.spiBasicInfoDetailsData)
                        .map(data -> {
                            data.uniqueId = data.keyid + "_" + data.postid;
                            return data;
                        }).toList()
                        .map(data -> {
                            dataBase.getSpiBasicInfoDao().insertSpiBasicinfo(response.spiBasicInfoDetailsData);
                            return true;
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();

/*
            if(!response.spiBasicInfoDetailsData.isEmpty()){
                message.what = OPEN_DRAFT_SPI_SCREEN;
                liveData.postValue(message);

                Prefs.putInt(PrefsConstants.SPI_ID,response.spiBasicInfoDetailsData.get(0).keyid);
            }*/
        }

    }
    public  void flushReuploadData(){
        dataBase.getReuploadDraftSpiDao().flushReuploadDraftSpi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public  void draftAppovecondition(){

        addDisposable( dataBase.getDraftApprovalDao().getNotApprovedDraft(Prefs.getInt(PrefsConstants.SPI_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> {
                    if (count > 0) {
                        showToast("draft approval status pending!!");
                    }
                    else  if(draftApprovalRecyclerAdapter.isEmpty()){
                        showToast("draft approval not showing!!");

                    }
                    else {
                        message.what = OPEN_DESIGN_SPI_SCREEN;
                        liveData.postValue(message);
                       // openSpiDesignScreen();
                       // viewModel.flushReuploadData();
                    }

                }, throwable -> {
                    showToast("Something went wrong");

                }));
    }




}
