package com.sisindia.ai.mtrainer.android.features.trainingcourses;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.TRAINING_TOPICS;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.droidcommons.preference.Prefs;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.base.RequestHeaderInterceptor;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.CourseEntity;
import com.sisindia.ai.mtrainer.android.models.SasTokenResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingCoursesDataResponse;
import com.sisindia.ai.mtrainer.android.models.online.TrainingCourseDataResponseMO;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

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

public class TrainingCoursesViewmodel extends MTrainerViewModel {

//    ObservableField<List<TrainingCoursesDataResponse>> trainingcourses = new ObservableField<>();
//    public TrainingCoursesAdapter adapter = new TrainingCoursesAdapter();
//    ObservableField<List<TrainingCoursesDataResponse>> filtertrainingTopic = new ObservableField<>();
//    List<TrainingCoursesDataResponse> filteredList = new ArrayList<>();
//    public ObservableInt selectedProgramId = new ObservableInt();
//    public ObservableInt selectedCourseTypeId = new ObservableInt();

    public TrainingCoursesAdapterV2 adapterV2 = new TrainingCoursesAdapterV2();
    public ObservableField<String> employeeName = new ObservableField<>(Prefs.getString(PrefsConstants.EMPPLOYEE_NAME));
    private final List<TrainingCourseDataResponseMO> originalList = new ArrayList<>();
    private final List<TrainingCourseDataResponseMO> filteredListV2 = new ArrayList<>();

    @Inject
    DashBoardApi dashboardapi;

    MtrainerDataBase dataBase;

    @Inject
    public TrainingCoursesViewmodel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
        initRetrofit();
//        getTrainingCourses();
    }

    public void getTrainingCourses() {
        setIsLoading(true);
//        int programId = selectedProgramId.get();
//        int courseTypeId = selectedCourseTypeId.get();

        /*addDisposable(dashboardapi.getCourseslist(new TrainingCoursesRequest(
                        Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID)),
                        Prefs.getInt(PrefsConstants.BASE_TRAINER_ID)
                )).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTrainingCoursesList, this::onApiError));*/


        //New API Implementation
        /*TrainingCourseBodyMO body = new TrainingCourseBodyMO(
                Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID)),
                Prefs.getInt(PrefsConstants.BASE_TRAINER_ID),
                1,
                Prefs.getInt(PrefsConstants.SELECTED_PROGRAM_ID),
                Prefs.getInt(PrefsConstants.SELECTED_COURSE_TYPE_ID)
        );
        addDisposable(dashboardapi.getStarCourseList(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTrainingCoursesListV2, this::onApiError));*/

        addDisposable(dataBase.getTrainingMasterDao()
                .fetchAllCourses(
                        Prefs.getInt(PrefsConstants.SELECTED_PROGRAM_ID),
                        Prefs.getInt(PrefsConstants.SELECTED_LANGUAGE_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTrainingCoursesListV3, this::onApiError)
        );

    }

    /*private void onSasTokenResponse(SasTokenResponse sasTokenResponse) {
        Prefs.putString(PrefsConstants.SAS_TOKEN, sasTokenResponse.getData());
        adapter.clearAndSetItems(filteredList);
        adapter.setTopicsDataModels(filtertrainingTopic.get());
        adapter.notifyDataSetChanged();
        setIsLoading(false);
    }*/

    /*private void onTrainingCoursesList(TrainingCoursesResponse trainingCoursesResponse) {
        setIsLoading(false);
        if (trainingCoursesResponse.statusCode == 200 && !trainingCoursesResponse.getData().isEmpty()) {
            trainingcourses.set(trainingCoursesResponse.getData());
            filtertrainingTopic.set(trainingCoursesResponse.getData());
            for (TrainingCoursesDataResponse data : trainingCoursesResponse.getData()) {
                Integer isActive = data.isActive();
                if (isActive != null && isActive == 1)
                    filteredList.add(data);
            }
            addDisposable(dashboardapi
                    .getsastoken(trainingCoursesResponse.getData().get(0).getSegmentType())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onSasTokenResponse, this::onApiError));
        }
    }*/

   /* private void onTrainingCoursesListV2(TrainingCoursesResponseMO response) {
        setIsLoading(false);
        if (response.statusCode == 200 && !response.getData().isEmpty()) {
            originalList.clear();
            originalList.addAll(response.getData());

            filteredListV2.clear();
            filteredListV2.addAll(response.getData());

            adapterV2.clearAndSetItems(filteredListV2);

            addDisposable(dashboardapi
                    .getsastoken(response.getData().get(0).getSegmentType())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onSasTokenResponseV2, this::onApiError));
        }
    }*/

    private void onTrainingCoursesListV3(List<CourseEntity> courseEntities) {
        setIsLoading(false);
        if (courseEntities.isEmpty())
            return;

        List<TrainingCourseDataResponseMO> courseList = CourseEntityMapper.toResponseMOList(courseEntities);

        originalList.clear();
        originalList.addAll(courseList);

        filteredListV2.clear();
        filteredListV2.addAll(courseList);

        adapterV2.clearAndSetItems(filteredListV2);

        addDisposable(dashboardapi
                .getsastoken(courseList.get(0).getSegmentType())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSasTokenResponseV2, (error) -> {
                    showToast("SAS not refreshed");
                }));
    }

    private void onSasTokenResponseV2(SasTokenResponse sasTokenResponse) {
        Prefs.putString(PrefsConstants.SAS_TOKEN, sasTokenResponse.getData());
    }

    public void filterCourses(String query) {

        filteredListV2.clear();

        if (query == null || query.trim().isEmpty()) {
            filteredListV2.addAll(originalList);
        } else {
            String lowerCaseQuery = query.toLowerCase();

            for (TrainingCourseDataResponseMO item : originalList) {
                if (item.getCourseTitle().toLowerCase().contains(lowerCaseQuery)) {
                    filteredListV2.add(item);
                }
            }
        }

        adapterV2.clearAndSetItems(filteredListV2);
    }

    private void initRetrofit() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
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
        RequestHeaderInterceptor headerInterceptor = new RequestHeaderInterceptor();
        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(headerInterceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://mtrainer2-uat.azurewebsites.net/").client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        dashboardapi = retrofit.create(DashBoardApi.class);
    }

    public TrainingCourseViewListeners trainingCourseViewListeners = new TrainingCourseViewListeners() {
        @Override
        public void onCourseClick(TrainingCoursesDataResponse item) {
        }

        @Override
        public void onCourseClickV2(TrainingCourseDataResponseMO item) {
            Prefs.putInt(PrefsConstants.SELECTED_COURSE_ID, item.getCourseId());
            message.what = TRAINING_TOPICS;
            liveData.setValue(message);
        }
    };
}
