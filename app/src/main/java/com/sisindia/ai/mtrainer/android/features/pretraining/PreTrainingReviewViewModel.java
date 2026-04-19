package com.sisindia.ai.mtrainer.android.features.pretraining;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;

import com.droidcommons.base.timer.CountUpTimer;
import com.droidcommons.preference.Prefs;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.base.RequestHeaderInterceptor;
import com.sisindia.ai.mtrainer.android.commons.TrainingTopicsRecyclerAdapter;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.MtrainerLogIntercepter;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PreTrainingConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsRequest;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsResponse;
import com.sisindia.ai.mtrainer.android.models.DashboardRequest;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_VIEW_PAGER;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_VIEW_PAGER1;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

public class PreTrainingReviewViewModel extends MTrainerViewModel {

    public TrainingTopicsRecyclerAdapter topicsRecyclerAdapter;
    public PreviousTrainingViewPagerAdapter previousTrainingAdapter = new PreviousTrainingViewPagerAdapter();
    public  ObservableField<String> unitName = new ObservableField<>();
    public  ObservableField<String> unitAddress = new ObservableField<>();
    public ObservableField<String> plannedTime= new ObservableField<>();
    MtrainerDataBase dataBase;
    @Inject
    AuthApi authApi;
    @Inject
    public CountUpTimer timer;

    public ObservableField<Integer> preTrainingStateObs = new ObservableField<>(Prefs.getInt(PrefsConstants.PRE_TRAINING_STATE, PreTrainingConstants.STARTED_FOR_SITE));


    @Inject
    DashBoardApi dashBoardApi;

    public PreTrainingViewListeners viewListeners = new PreTrainingViewListeners() {

        @Override
        public void onTopicItemClick() {
            message.what = NavigationConstants.ON_TRAINING_TOPIC_ITEM_CLICK;
            liveData.postValue(message);
        }

        @Override
        public void openTrainingDetail() {

        }
    };

    @Inject
    public PreTrainingReviewViewModel(@NonNull Application application) {
        super(application);
        initRetrofit();
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse1, this::onApiError));

        unitName.set(Prefs.getString(PrefsConstants.UNIT_NAME));
        unitAddress.set(Prefs.getString(PrefsConstants.UNIT_ADDRESS));
        SimpleDateFormat inputSdf = new SimpleDateFormat("hh:mm:ss aa", Locale.ENGLISH);
        SimpleDateFormat outputSdf = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String startTime = "";
        String endTime = "";
        try {
            Date startDate = inputSdf.parse(Prefs.getString(PrefsConstants.ESTIMATE_STATR_TIME).trim());
            if(startDate != null)
                startTime = outputSdf.format(startDate);
            Date endDate = inputSdf.parse(Prefs.getString(PrefsConstants.ESTIMATE_END_TIME).trim());
            if(endDate != null)
                endTime = outputSdf.format(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.plannedTime.set(startTime + " - " + endTime);
        dataBase = MtrainerDataBase.getDatabase(application);
        topicsRecyclerAdapter = new TrainingTopicsRecyclerAdapter(dataBase);

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

    void setPicasso() {
        previousTrainingAdapter.setPicasso(picasso);
        Log.d("PreTraining", "setPicasso: " + (picasso != null));
    }

    private void onPreAuthResponse1(PreAuthResponse response) {
        if (response != null && !TextUtils.isEmpty(response.accessToken)) {
            Prefs.putString(PrefsConstants.AUTH_TOKEN_KEY, response.tokenType.concat(" ").concat(response.accessToken));
            Prefs.putString(PrefsConstants.TOKEN_EXPIRE_DATE, response.expires);
        } else {
            showToast(R.string.something_went_wrong);
        }
    }


    public void onStartForSite(View view) {
        Integer currentState = preTrainingStateObs.get();
        if(currentState != null) {
            if (currentState == PreTrainingConstants.STARTED_FOR_SITE) {
                preTrainingStateObs.set(PreTrainingConstants.START_TRAINING);
                Date date = new Date();
                SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
                String currenttime = sdf2.format(date);
                // this code writing testing time for when come back again to pretrining show last site time
                if(Prefs.getString(PrefsConstants.PRE_TIME_SAVE)!=null && Prefs.getString(PrefsConstants.PRE_TIME_SAVE).length()>0){
                    Prefs.putString(PrefsConstants.PRE_TIME_SAVE, Prefs.getString(PrefsConstants.PRE_TIME_SAVE));
                }
                else {
                    Prefs.putString(PrefsConstants.PRE_TIME_SAVE, currenttime);
                    Log.v("","timer values"+currenttime);
                }
            //    Prefs.putString(PrefsConstants.PRE_TIME_SAVE, currenttime);

            }

            else {
                message.what = NavigationConstants.ON_START_TRAINING_CLICK;
                liveData.postValue(message);
                // this will remove started for site time
                Prefs.edit().remove(PrefsConstants.PRE_TIME_SAVE).apply();
                // this is removing in progress id
                Prefs.edit().remove(PrefsConstants.IS_PROGRESS_ROTAID).apply();


            }
        }

    }



    public void onCancelTraining(View view) {
        message.what = NavigationConstants.ON_CANCEL_TRAINING_CLICK;
        liveData.postValue(message);
    }

    public void initViewModel() {
       /* ArrayList<Object> list = new ArrayList<>();

        list.add("aaa");
        list.add("bbb");
        list.add("ccc");
        previousTrainingAdapter.clearAndSetItems(list);*/
    }
/*
    public enum PreTrainingState {
        START_FOR_SITE, START_TRAINING
    }*/

    public void tvArrowDownClick(View view) {
        message.what = OPEN_VIEW_PAGER;
        liveData.postValue(message);
    }
    public void tvArrowUpClick(View view) {
        message.what = OPEN_VIEW_PAGER1;
        liveData.postValue(message);
    }




    public void getChooseTopics() {
        setIsLoading(true);
        ChooseTopicsRequest request = new ChooseTopicsRequest(Prefs.getString(PrefsConstants.COMPANY_ID));

        addDisposable(dashBoardApi.getChooseTopics(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onChooseTopicsSuccess, this::onApiError));

    }

    private void onChooseTopicsSuccess(ChooseTopicsResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getTopicDao().insertMasterTopic(response.topicsResponses)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();

        }
    }


    public LiveData<List<ChooseTopicsResponse.TopicsResponse>> getMasterList(){
        return dataBase.getTopicDao().getMasterTopicList(Prefs.getString(PrefsConstants.COMPANY_ID));
    }

    public void setRotaData(List<ChooseTopicsResponse.TopicsResponse> items) {
        setIsLoading(false);
        topicsRecyclerAdapter.clearAndSetItems(items);
    }

    void getPreviousTraining(){
        DashboardRequest request = new DashboardRequest();
        request.employeeId = Prefs.getString(PrefsConstants.EMPLOYEE_ID);

        addDisposable(dashBoardApi.getPreviousTraining(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null && response.getStatusCode() == 200){
                            previousTrainingAdapter.clearAndSetItems(response.getData());
                        }
                    }, this::onApiError));
    }

}

