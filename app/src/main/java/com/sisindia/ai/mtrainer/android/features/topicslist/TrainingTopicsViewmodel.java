package com.sisindia.ai.mtrainer.android.features.topicslist;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.VIDEO_PLAY;

import android.app.Application;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

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
import com.sisindia.ai.mtrainer.android.db.entities.TopicEntity;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingCourseUpdateRequest;
import com.sisindia.ai.mtrainer.android.models.TrainingTopicsDataModel;
import com.sisindia.ai.mtrainer.android.models.online.TrainingTopicDataResponseMO;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;

import java.time.LocalDateTime;
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
import timber.log.Timber;

public class TrainingTopicsViewmodel extends MTrainerViewModel {

    //    ObservableField<List<TrainingTopicsDataModel>> trainingTopic = new ObservableField<>();
//    ObservableField<List<TrainingTopicsDataModel>> filterTrainingTopic = new ObservableField<>();
//    ObservableField<TrainingTopicsBodyMO> topicBodyMO = new ObservableField<>();
    //    public TrainingTopisAdapter adapter = new TrainingTopisAdapter();
    public TrainingTopicsAdapterV2 adapter = new TrainingTopicsAdapterV2();
    private final List<TrainingTopicDataResponseMO> originalList = new ArrayList<>();
    private final List<TrainingTopicDataResponseMO> filteredListV2 = new ArrayList<>();

    @Inject
    DashBoardApi dashboardapi;
    MtrainerDataBase dataBase;

    @Inject
    AuthApi authApi;

    @Inject
    public TrainingTopicsViewmodel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
        initRetrofit();
        addDisposable(authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES,
                        RestConstants.USER_NAME_VALUES,
                        RestConstants.PASSWORD_VALUES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onPreAuthResponse1, Timber::e));
    }

    private void initRetrofit() {
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

        RequestHeaderInterceptor headerInterceptor = new RequestHeaderInterceptor();
        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(headerInterceptor);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://mtrainer2-uat.azurewebsites.net/").client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        dashboardapi = retrofit.create(DashBoardApi.class);

        OkHttpClient.Builder builder1 = new OkHttpClient.Builder()
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

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

    void getTopicList() {
        addDisposable(dataBase.getMasterAttendanceDao()
                .getunsyncdata()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onUnSyncedTopicsList, Timber::e));
    }

    private void onUnSyncedTopicsList(List<TrainingTopicsDataModel> trainingTopicsDataModels) {
        if (!trainingTopicsDataModels.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                for (TrainingTopicsDataModel element : trainingTopicsDataModels) {
                    addDisposable(dashboardapi.sendcoursecontenttracker(new TrainingCourseUpdateRequest(
                                    1,//  Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID)),
                                    Prefs.getInt(PrefsConstants.BASE_TRAINER_ID),
                                    element.getSession(),
                                    element.getCourseId(),
                                    element.getCourseTopicId(),
                                    element.getCourseContentId(),
                                    1,
                                    element.getStartTime(),
                                    element.getLastseen(),
                                    LocalDateTime.now().toString()
                            )).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(trainingCourseUpdateResponse -> {
                                addDisposable(dataBase.getMasterAttendanceDao()
                                        .updatestatus(element.getCourseId(),
                                                element.getCourseTopicId(),
                                                element.getCourseContentId(),
                                                element.getSession())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                        }, Timber::e));
                            }, Timber::e));
                }
            }
        }

        setIsLoading(true);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {

            /*TrainingTopicsRequestModel trainingTopicsRequestModel = new TrainingTopicsRequestModel();
            trainingTopicsRequestModel.companyId = Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID));
            trainingTopicsRequestModel.userId = Prefs.getInt(PrefsConstants.BASE_TRAINER_ID);
            trainingTopicsRequestModel.courseId = trainingCourse.get().getCourseId();*/

            /*addDisposable(dashboardapi.getTopicslist(trainingTopicsRequestModel)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(TrainingTopicsViewmodel.this::onTrainingTopicsList,
                            TrainingTopicsViewmodel.this::onApiError));*/

            //NEW API implementation
            /*setIsLoading(true);
            TrainingTopicsBodyMO body = new TrainingTopicsBodyMO(
                    Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID)),
                    Prefs.getInt(PrefsConstants.BASE_TRAINER_ID),
                    1,
                    Prefs.getInt(PrefsConstants.SELECTED_PROGRAM_ID),
                    Prefs.getInt(PrefsConstants.SELECTED_COURSE_TYPE_ID),
                    Prefs.getInt(PrefsConstants.SELECTED_COURSE_ID)
            );

            addDisposable(dashboardapi.getCourseContentList(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(TrainingTopicsViewmodel.this::onTrainingTopicsList,
                            TrainingTopicsViewmodel.this::onApiError));*/

           /* addDisposable(dataBase.getTrainingMasterDao()
                    .fetchAllTopics(Prefs.getInt(PrefsConstants.SELECTED_COURSE_ID), 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onTrainingTopicsList, this::onApiError));*/

            addDisposable(dataBase.getTrainingMasterDao()
                    .fetchAllTopicsV2(Prefs.getInt(PrefsConstants.SELECTED_COURSE_ID),
                            Prefs.getInt(PrefsConstants.SELECTED_LANGUAGE_ID))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onTrainingTopicsListV2,
                            error -> Timber.e("Unable to fetch topics"))
            );

        }, 3000);
    }

    /*void sendtrackingdata(){
        TrainingTopicsRequestModel trainingTopicsRequestModel = new TrainingTopicsRequestModel();

        trainingTopicsRequestModel.companyId = 1;
        trainingTopicsRequestModel.userId = 101;
        trainingTopicsRequestModel.courseId = trainingCourse.get().getCourseId();
        addDisposable(dashboardapi.getTopicslist(trainingTopicsRequestModel).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTrainingTopicsList, this::onApiError));
    }*/

    /*private void onTrainingTopicsList(TrainingTopicsModel trainingTopicsModel) {
        if (trainingTopicsModel.statusCode == 200) {
            List<TrainingTopicsDataModel> filteredList = new ArrayList<>();

            trainingTopic.set(trainingTopicsModel.data);

            filterTrainingTopic.set(trainingTopicsModel.data);

            for (TrainingTopicsDataModel data : trainingTopic.get()) {
                if (data.isActive() == 1) {
                    filteredList.add(data);
                }
            }

            adapter.setTopicsDataModels(filterTrainingTopic.get());
            adapter.clearAndSetItems(filteredList);
            adapter.notifyDataSetChanged();
            setIsLoading(false);
        }
    }*/

    private void onTrainingTopicsList(List<TopicEntity> topicEntities) {
        setIsLoading(false);
        if (topicEntities.isEmpty())
            return;

        List<TrainingTopicDataResponseMO> topicList = TrainingTopicMapper.fromEntityList(topicEntities);
        originalList.clear();
        originalList.addAll(topicList);

        filteredListV2.clear();
        filteredListV2.addAll(topicList);

        adapter.clearAndSetItems(filteredListV2);
    }

    private void onTrainingTopicsListV2(List<TopicWithLastSeen> topicListWithLastSeen) {
        setIsLoading(false);
        if (topicListWithLastSeen.isEmpty())
            return;
        List<TrainingTopicDataResponseMO> topics =
                TrainingTopicMapper.fromTopicWithLastSeenList(topicListWithLastSeen);
        originalList.clear();
        originalList.addAll(topics);

        filteredListV2.clear();
        filteredListV2.addAll(topics);

        adapter.clearAndSetItems(filteredListV2);
    }

    public void filterTopics(String query) {

        filteredListV2.clear();

        if (query == null || query.trim().isEmpty()) {
            filteredListV2.addAll(originalList);
        } else {
            String lowerCaseQuery = query.toLowerCase();

            for (TrainingTopicDataResponseMO item : originalList) {
                if (item.getCourseTitle().toLowerCase().contains(lowerCaseQuery)) {
                    filteredListV2.add(item);
                }
            }
        }

        adapter.clearAndSetItems(filteredListV2);
    }

    public TrainingTopicsViewListeners topicListener = item -> {
        message.obj = item;
        message.what = VIDEO_PLAY;
        liveData.setValue(message);
    };
}
