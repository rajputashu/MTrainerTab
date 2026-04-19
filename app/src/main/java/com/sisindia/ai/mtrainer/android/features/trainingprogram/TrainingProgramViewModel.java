package com.sisindia.ai.mtrainer.android.features.trainingprogram;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.LanguageEntity;
import com.sisindia.ai.mtrainer.android.db.entities.ProgramEntity;
import com.sisindia.ai.mtrainer.android.models.online.TrainingProgramTypeDataResponseMO;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class TrainingProgramViewModel extends MTrainerViewModel {

    public ObservableField<String> employeeName =
            new ObservableField<>(Prefs.getString(PrefsConstants.EMPPLOYEE_NAME));

    public TrainingProgramAdapter trainingProgramAdapter = new TrainingProgramAdapter();

    public TrainingProgramListener listener = item -> {
        message.what = NavigationConstants.OPEN_ONLINE_TRAINING_COURSES_SCREEN;

        Prefs.putInt(PrefsConstants.SELECTED_PROGRAM_ID, item.getProgramId());
//        Prefs.putInt(PrefsConstants.SELECTED_COURSE_TYPE_ID, item.getCourseTypeId());
        /*message.arg1 = item.getProgramId();
        message.arg2 = item.getCourseTypeId();*/
        liveData.postValue(message);
    };

    /*@Inject
    DashBoardApi dashboardapi;*/

    private final MtrainerDataBase database;
    public ObservableField<List<LanguageEntity>> languageList = new ObservableField<>();
    public ObservableInt savedLanguageId =
            new ObservableInt(Prefs.getInt(PrefsConstants.SELECTED_LANGUAGE_ID, -1));

    @Inject
    public TrainingProgramViewModel(@NonNull Application application) {
        super(application);
        database = MtrainerDataBase.getDatabase(application);
        /*initRetrofit();
        setIsLoading(true);
        addDisposable(dashboardapi.getStarProgramTypeList(
                                new TrainingProgramTypeBodyMO(
                                        Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID)),
                                        Prefs.getInt(PrefsConstants.BASE_TRAINER_ID), 1
                                )
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onTrainingProgramList, this::onApiError)
        );*/
        fetchLanguageListFromDB();
        fetchProgramListFromDB();
    }

    private void fetchLanguageListFromDB() {
        addDisposable(database.getTrainingMasterDao().fetchAllLanguages()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLanguageList, Timber::e)
        );
    }

    private void onLanguageList(List<LanguageEntity> languageEntities) {
        if (languageEntities.isEmpty()) return;
        languageList.set(languageEntities);
        // -------------------------------------------------------------------------
        // If no language has ever been saved (first launch), auto-select the first
        // item and persist it so downstream screens always have a valid language ID.
        // -------------------------------------------------------------------------
        if (Prefs.getInt(PrefsConstants.SELECTED_LANGUAGE_ID, -1) == -1) {
            int firstId = languageEntities.get(0).languageId;
            Prefs.putInt(PrefsConstants.SELECTED_LANGUAGE_ID, firstId);
            savedLanguageId.set(firstId);
        }
    }

    public LanguageSelectListener languageSelectListener = languageId -> {
        Timber.e("Selected language id: %s", languageId);
        Prefs.putInt(PrefsConstants.SELECTED_LANGUAGE_ID, languageId);
        savedLanguageId.set(languageId);
    };

    private void fetchProgramListFromDB() {
        setIsLoading(true);

        addDisposable(database.getTrainingMasterDao().fetchAllPrograms()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTrainingProgramList, this::onApiError)
        );
    }

    private void onTrainingProgramList(List<ProgramEntity> programEntities) {
        setIsLoading(false);
        if (programEntities.isEmpty())
            return;
        List<TrainingProgramTypeDataResponseMO> programs = ProgramEntityMapper.toResponseMOList(programEntities);
        trainingProgramAdapter.clearAndSetItems(programs);
    }

    /*private void onTrainingProgramList(TrainingProgramTypeResponseMO response) {
        setIsLoading(false);

        if (response != null) {
            response.getData();
            if (!response.getData().isEmpty()) {
                trainingProgramAdapter.clearAndSetItems(response.getData());
            }
        }
    }*/

    /*private void initRetrofit() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new RequestHeaderInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://mtrainer2-uat.azurewebsites.net/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        dashboardapi = retrofit.create(DashBoardApi.class);
    }*/
}