package com.sisindia.ai.mtrainer.android.features.choosetopics;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

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
import com.sisindia.ai.mtrainer.android.db.SavedTopic;
import com.sisindia.ai.mtrainer.android.db.entities.AdhocSavedTopics;
import com.sisindia.ai.mtrainer.android.features.pretraining.PreTrainingReviewActivity;
import com.sisindia.ai.mtrainer.android.models.AdhocTopicsResponse;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsRequest;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsResponse;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceResponse;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

public class ChooseTopicsViewModel extends MTrainerViewModel {
    public ChooseTopicsRecyclerAdapter choosetopicsAdapter;
    public ChooseTopicsRecyclerAdapter rotatopicsAdapter;
    public AdhocTopicRecyclerAdapter adhoctopicsAdapter;
    public final String selectedTopics = Prefs.getString(PrefsConstants.TOPIC_ID);
    public PublishSubject<String> stringObservable = PublishSubject.create();
    private List<ChooseTopicsResponse.TopicsResponse> masterTopicItems = new ArrayList<>();
    Set<Integer> selectedTopicSet = new HashSet<>();
    Set<Integer> selectedAdhocTopicSet = new HashSet<>();
    //below my code
   // public List<String> adhocTopicname =  new ArrayList<>();
    public ObservableField<String> adhocTopicname = new ObservableField<>();
    public ObservableInt topicCount =new ObservableInt();
    public ObservableInt adhocCount =new ObservableInt();
    @Inject
    AuthApi authApi;


    @Inject
    DashBoardApi dashBoardApi;

    MtrainerDataBase dataBase;


    @Inject
    public ChooseTopicsViewModel(@NonNull Application application) {
        super(application);
        initRetrofit();
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse1, this::onApiError));

        dataBase = MtrainerDataBase.getDatabase(application);
        choosetopicsAdapter = new ChooseTopicsRecyclerAdapter(dataBase);
        rotatopicsAdapter = new ChooseTopicsRecyclerAdapter(dataBase);
        adhoctopicsAdapter = new AdhocTopicRecyclerAdapter(dataBase);
        initListener();


    }

    void getChooseTopics() {
        setIsLoading(true);

        ChooseTopicsRequest request = new ChooseTopicsRequest(Prefs.getString(PrefsConstants.COMPANY_ID));
        addDisposable(dashBoardApi.getChooseTopics(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onChooseTopicsSuccess, this::onApiError));
    }

    private void onPreAuthResponse1(PreAuthResponse response) {
        if (response != null && !TextUtils.isEmpty(response.accessToken)) {
            Prefs.putString(PrefsConstants.AUTH_TOKEN_KEY, response.tokenType.concat(" ").concat(response.accessToken));
            Prefs.putString(PrefsConstants.TOKEN_EXPIRE_DATE, response.expires);
        } else {
            showToast(R.string.something_went_wrong);
        }
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


    private void onChooseTopicsSuccess(ChooseTopicsResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getTopicDao().insertMasterTopic(response.topicsResponses)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }

    LiveData<List<ChooseTopicsResponse.TopicsResponse>> getMasterList(){

        return dataBase.getTopicDao().getMasterTopicList(Prefs.getString(PrefsConstants.COMPANY_ID));
    }

    public void setData(List<ChooseTopicsResponse.TopicsResponse> items) {
        setIsLoading(false);
        masterTopicItems = items;
        choosetopicsAdapter.setSelectedTopicSet(selectedTopicSet);
        choosetopicsAdapter.clearAndSetItems(items);
    }

    public void setAdhocTopic(List<AdhocTopicsResponse.AdhocTopics> items){
        setIsLoading(false);
        adhoctopicsAdapter.setSelectedAdhocSet(selectedAdhocTopicSet);
        adhoctopicsAdapter.clearAndSetItems(items);

    }

    LiveData<List<AdhocTopicsResponse.AdhocTopics>> getAdhocTopics() {
        return dataBase.getAdhocTopicsDao().getAdhocTopics();
    }

    void setRotaData(List<ChooseTopicsResponse.TopicsResponse> items) {
        setIsLoading(false);
        rotatopicsAdapter.setSelectedTopicSet(selectedTopicSet);
        rotatopicsAdapter.clearAndSetItems(items);
    }

    LiveData<List<SavedTopic>> getSavedList(){
        return dataBase.getSavedTopicDao().getSavedTopicList(Prefs.getInt(PrefsConstants.ROTA_ID));
    }


    LiveData<List<AdhocSavedTopics>> getAdhocSavedList(){
        return dataBase.getAdhocSavedTopicsDao().getAdhocSavedTopicList(Prefs.getInt(PrefsConstants.ROTA_ID));
    }


    void recoverTopicsState() {
        dataBase.getSavedTopicDao().getSavedTopicIdList(Prefs.getInt(PrefsConstants.ROTA_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRestoreselectedTopicId);
    }

    private void onRestoreselectedTopicId(List<Integer> oldData) {
        selectedTopicSet = new HashSet<>(oldData);
        choosetopicsAdapter.setSelectedTopicSet(selectedTopicSet);
        rotatopicsAdapter.setSelectedTopicSet(selectedTopicSet);
        choosetopicsAdapter.notifyDataSetChanged();
        rotatopicsAdapter.notifyDataSetChanged();
    }



    void recoverAdhocTopicsState() {
        dataBase.getAdhocSavedTopicsDao().getOnlySavedTopicIdList(Prefs.getInt(PrefsConstants.ROTA_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRestoreselectedAdhocTopicId);
    }

    private void onRestoreselectedAdhocTopicId(List<Integer> oldData) {
        selectedAdhocTopicSet = new HashSet<>(oldData);
        adhoctopicsAdapter.setSelectedAdhocSet(selectedAdhocTopicSet);
        adhoctopicsAdapter.notifyDataSetChanged();
    }


    public void initListener() {
        addDisposable(stringObservable
                .subscribeOn(Schedulers.io())
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribe(data -> {
                    addDisposable(Observable.fromIterable(masterTopicItems)
                            .filter(item -> item.topicName.toLowerCase().startsWith(data.toLowerCase()))
                            .toList()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(list -> choosetopicsAdapter.clearAndSetItems(list), th -> {}));
                }, this::onApiError));
    }

    // writing below code for adhoc topic adding into topic list
    public void onAddAdhocTopic(View view){

            if(adhocTopicname==null || adhocTopicname.get().isEmpty()){
            showToast("Please enter topic name ");
        }
        else {

            AdhocTopicsResponse.AdhocTopics adhocTopics= new   AdhocTopicsResponse.AdhocTopics ();
            adhocTopics.topicName= adhocTopicname.toString();
            adhocTopics.rotaId=Prefs.getInt(PrefsConstants.ROTA_ID);


            dataBase.getAdhocTopicsDao().insertAdhocTopics(Collections.singletonList(adhocTopics))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }

    }


    public void saveManualTopic(int topicId, String topicName) {
        AdhocSavedTopics savedTopic = new AdhocSavedTopics();
        savedTopic.id =topicId;
        savedTopic.topicName = topicName;
        savedTopic.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
        selectedTopicSet.add(topicId);


        addDisposable( dataBase.getAdhocSavedTopicsDao().insertAdhocSavedTopic(savedTopic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    adhoctopicsAdapter.notifyDataSetChanged();
                   // message.what = NavigationConstants.UPDATE_ATTENDENCE_VIEW;
                   // liveData.postValue(message);
                }, Throwable::printStackTrace));
    }



}



