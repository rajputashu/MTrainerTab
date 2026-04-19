package com.sisindia.ai.mtrainer.android.features.myconveyance;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_CONVEYANCETIMELINE;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

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
import com.sisindia.ai.mtrainer.android.models.ConveyanceDailyData;
import com.sisindia.ai.mtrainer.android.models.ConveyanceDailyResponse;
import com.sisindia.ai.mtrainer.android.models.ConveyanceMonthlyData;
import com.sisindia.ai.mtrainer.android.models.ConveyanceMonthlyRequest;
import com.sisindia.ai.mtrainer.android.models.ConveyanceMonthlyResponse;
import com.sisindia.ai.mtrainer.android.models.ConveyanceTimeLineData;
import com.sisindia.ai.mtrainer.android.models.ConveyanceTimelineRequest;
import com.sisindia.ai.mtrainer.android.models.ConveyanceTimelineResponse;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;
import com.sisindia.ai.mtrainer.android.uimodels.YearUIModel;

import org.jetbrains.annotations.NotNull;
import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MyConveyanceViewModel extends MTrainerViewModel {

    MtrainerDataBase dataBase;

    public MyConveyanceMonthlyRecyclerAdapter adapter;
    public MyConveyanceDialyRecyclerAdapter dialyRecyclerAdapter;
    public MyConveyanceTimelineRecyclerAdapter timelineRecyclerAdapter;
    public ObservableField<Integer> selectedmonth = new ObservableField<>();
    public ObservableField<Integer> selectedyear = new ObservableField<>();
    public ObservableField<String> selecteddate = new ObservableField<>();

    @Inject
    @Named("Year")
    public ArrayList<YearUIModel> yearList;

    @Inject
    @Named("Month")
    public ArrayList<YearUIModel> monthList;

    @Inject
    DashBoardApi dashBoardApi;
    @Inject
    AuthApi authApi;
    List<ConveyanceMonthlyData> conveyanceMonthlyData;
    List<ConveyanceDailyData> conveyancedailyData;
    List<ConveyanceTimeLineData> conveyancetimelineData;

    @Inject
    public MyConveyanceViewModel(@NonNull Application application) {
        super(application);
        initRetrofit();
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse1, this::onApiError));

        dataBase = MtrainerDataBase.getDatabase(application);
        adapter = new MyConveyanceMonthlyRecyclerAdapter(conveyanceMonthlyData);
        dialyRecyclerAdapter = new MyConveyanceDialyRecyclerAdapter(conveyancedailyData);
        timelineRecyclerAdapter = new MyConveyanceTimelineRecyclerAdapter(conveyancetimelineData);
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


    public MyconvenceListeners listeners = new MyconvenceListeners() {
        @Override
        public void onconveyanceitemclick(@NotNull ConveyanceMonthlyData data, int position) {
            Log.d("sxdfsf", data.getMonthName());
            message.obj = data;
            message.what = NavigationConstants.OPEN_CONVEYANCEDAILY;
            liveData.postValue(message);
        }
    };

    public MyconvencedailyListeners dailylisteners = new MyconvencedailyListeners() {
        @Override
        public void onconveyanceitemclick(@NotNull ConveyanceDailyData data, int position) {
            message.arg1 = position;
            message.obj = data;
            message.what = OPEN_CONVEYANCETIMELINE;
            liveData.postValue(message);
        }
    };

    public MyconvencetimelineListeners timelienlisteners = new MyconvencetimelineListeners() {
        @Override
        public void onconveyanceitemclick(@NotNull ConveyanceTimeLineData data, int position) {

        }
    };

    public void fetchconveyencemonthlydata(boolean isComingFirstTime) {
        if (isComingFirstTime) {
            selectedmonth.set(LocalDate.now().getMonthValue());
        }

        setIsLoading(true);
        addDisposable(dashBoardApi
                .getconveyancemonthdata(new ConveyanceMonthlyRequest(
                        Prefs.getInt(PrefsConstants.BASE_TRAINER_ID),
                        selectedmonth.get(), selectedyear.get()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::conveyancemonthlysucess, this::onApiError));

    }

    public YearListeners yearListeners = new YearListeners() {
        @Override
        public void onyearitemclick(int position) {
            selectedyear.set(Integer.parseInt(yearList.get(position).getYear()));
        }
    };

    public MonthListeners monthListeners = new MonthListeners() {
        @Override
        public void onmonthitemclick(int position) {
            selectedmonth.set(monthList.get(position).getNumber());
        }
    };

    public void applyclick(View view) {
        fetchconveyencemonthlydata(false);
    }

    public void fetchconveyencedialydata() {

        setIsLoading(true);
        addDisposable(dashBoardApi.getconveyancedailydata(new ConveyanceMonthlyRequest(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID), selectedmonth.get(), selectedyear.get()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::conveyancedailysucess, this::onApiError));

    }

    public void fetchconveyencetimelinedata() {

        setIsLoading(true);
        addDisposable(dashBoardApi.getconveyancetimelinedata(new ConveyanceTimelineRequest(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID), selecteddate.get()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::conveyancetimelinesucess, this::onApiError));

    }

    private void conveyancetimelinesucess(ConveyanceTimelineResponse conveyanceTimelineResponse) {
        setIsLoading(false);
        if (conveyanceTimelineResponse.statusCode == 200 && conveyanceTimelineResponse.statusMessage.equals("Success")) {
            conveyancetimelineData = new ArrayList<>();
            conveyancetimelineData.addAll(conveyanceTimelineResponse.getData());
            timelineRecyclerAdapter.setitems(conveyancetimelineData);
            timelineRecyclerAdapter.clearAndSetItems(conveyancetimelineData);
            timelineRecyclerAdapter.notifyDataSetChanged();
        }
    }


    private void conveyancedailysucess(ConveyanceDailyResponse conveyanceMonthlyResponse) {
        setIsLoading(false);
        if (conveyanceMonthlyResponse.statusCode == 200 && conveyanceMonthlyResponse.statusMessage.equals("Success")) {
            conveyancedailyData = new ArrayList<>();
            conveyancedailyData.addAll(conveyanceMonthlyResponse.getData());
            dialyRecyclerAdapter.setitems(conveyancedailyData);
            dialyRecyclerAdapter.clearAndSetItems(conveyancedailyData);
            dialyRecyclerAdapter.notifyDataSetChanged();
        } else if (conveyanceMonthlyResponse.statusCode == 404 && conveyanceMonthlyResponse.statusMessage.equals("Data NOT Found")) {
            conveyancedailyData = new ArrayList<>();
            dialyRecyclerAdapter.setitems(conveyancedailyData);
            dialyRecyclerAdapter.clearAndSetItems(conveyancedailyData);
            dialyRecyclerAdapter.notifyDataSetChanged();
        }
    }


    private void conveyancemonthlysucess(ConveyanceMonthlyResponse conveyanceMonthlyResponse) {
        setIsLoading(false);
        if (conveyanceMonthlyResponse.statusCode == 200 && conveyanceMonthlyResponse.statusMessage.equals("Success")) {
            conveyanceMonthlyData = new ArrayList<>();
            conveyanceMonthlyData.add(conveyanceMonthlyResponse.getData());
            adapter.setitems(conveyanceMonthlyData);
            adapter.clearAndSetItems(conveyanceMonthlyData);
            adapter.notifyDataSetChanged();
        } else if (conveyanceMonthlyResponse.statusCode == 404 && conveyanceMonthlyResponse.statusMessage.equals("Data NOT Found")) {
            conveyanceMonthlyData = new ArrayList<>();
            adapter.setitems(conveyanceMonthlyData);
            adapter.clearAndSetItems(conveyanceMonthlyData);
            adapter.notifyDataSetChanged();
        }
    }

}
