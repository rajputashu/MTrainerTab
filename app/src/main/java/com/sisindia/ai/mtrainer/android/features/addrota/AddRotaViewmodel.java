package com.sisindia.ai.mtrainer.android.features.addrota;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableList;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.droidcommons.preference.Prefs;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sisindia.ai.mtrainer.android.BuildConfig;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.base.RequestHeaderInterceptor;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.MtrainerLogIntercepter;
import com.sisindia.ai.mtrainer.android.constants.Constant;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.models.AddRotaMainRequest;
import com.sisindia.ai.mtrainer.android.models.AddRotaRequest;
import com.sisindia.ai.mtrainer.android.models.AddRotaResponse;
import com.sisindia.ai.mtrainer.android.models.BranchData;
import com.sisindia.ai.mtrainer.android.models.BranchRegionRequest;
import com.sisindia.ai.mtrainer.android.models.BranchResponse;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsRequest;
import com.sisindia.ai.mtrainer.android.models.ChooseTopicsResponse;
import com.sisindia.ai.mtrainer.android.models.CourseTypeData;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.RegionData;
import com.sisindia.ai.mtrainer.android.models.RegionResponse;
import com.sisindia.ai.mtrainer.android.models.SiteData;
import com.sisindia.ai.mtrainer.android.models.SiteResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingCalendarResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingType;
import com.sisindia.ai.mtrainer.android.models.UnitListResponse;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

public class AddRotaViewmodel extends MTrainerViewModel implements AddRotaTopicAdapter.TopicSelected {

    public ObservableInt taskEndTimeErrorVisibility = new ObservableInt(GONE);
    public ObservableInt taskStartTimeErrorVisibility = new ObservableInt(GONE);
    public ObservableInt isReasonAvailable = new ObservableInt(GONE);
    public final String role = Prefs.getString(PrefsConstants.ROLE);
    public final String companyId = Prefs.getString(PrefsConstants.COMPANY_ID);
    public ObservableInt empCount = new ObservableInt(-1);
    public ObservableList<UnitListResponse.Unit> unitList = new ObservableArrayList<>();
    public ObservableList<RegionData> regionList = new ObservableArrayList<>();
    public ObservableList<BranchData> branchList = new ObservableArrayList<>();
    public ObservableList<SiteData> siteList = new ObservableArrayList<>();
    public List<TrainingType> typeList = new ArrayList<>();
    public List<CourseTypeData> coursetypeList = new ArrayList<>();
    private MtrainerDataBase dataBase;
    private int selectedTrainingTypeID;
    private int selectedCourseTypeID;
    private int selectedUnitID;
    public static HashSet<Integer> selectedTopic = new HashSet<>();
    public AddRotaTopicAdapter topicsAdapter;
   // private String numberOfTrainee = "5";
    private volatile boolean canAddRota = true;
    public ObservableField<String> selectedSiteAddress = new ObservableField<>("");
    public int regionId,branchId,siteId;
    public PublishSubject<String> stringObservable = PublishSubject.create();

    private MutableLiveData<Integer> branchRequestLiveData = new MutableLiveData<>();
    private LiveData<List<BranchData>> branchListLiveData;
    private MutableLiveData<Integer> siteRequestLiveData = new MutableLiveData<>();
    private LiveData<List<SiteData>> siteListLiveData;
    private List<ChooseTopicsResponse.TopicsResponse> masterTopicItems = new ArrayList<>();
    private final int OJT = 0;
    private int trainingConductType = 0;

    @Inject
    AuthApi authApi;

    @Inject
    DashBoardApi dashBoardApi;


    public ObservableField<LocalDate> startDate = new ObservableField<>(LocalDate.now());
    public ObservableField<LocalDate> endDate = new ObservableField<>(LocalDate.now());
    public ObservableField<LocalTime> taskStartTime = new ObservableField<>(LocalTime.now());
    public final ObservableInt topicCount = new ObservableInt();
    public ObservableField<LocalTime> taskEndTime = new ObservableField<>(LocalTime.now().plusMinutes(15));

    public AddTaskViewListeners viewListeners = new AddTaskViewListeners() {

        @Override
        public void onDateChanged(int viewId ,LocalDate date) {
            switch (viewId) {
                case R.id.add_rota_start_date:
                    startDate.set(date);
                    endDate.set(date);
                    break;
            }
        }

        @Override
        public void onStartTimeSelected(LocalTime time) {
            taskStartTime.set(time);
            validateTime();
        }

        @Override
        public void onEndTimeSelected(LocalTime time) {
            taskEndTime.set(time);
            validateTime();
        }

        @Override
        public void onItemSpinnerSelected(int viewId, int position) {
            switch (viewId) {
                case R.id.sp_training_type:
                    selectedTrainingTypeID = typeList.get(position).id;
                    break;
                case R.id.sp_unit_name:
                    selectedUnitID = unitList.get(position).unitId;
                    empCount.set(unitList.get(position).empCount);
                    if(unitList.get(position).SiteAddress == null || unitList.get(position).SiteAddress.equals(""))
                        selectedSiteAddress.set("");
                    else
                        selectedSiteAddress.set(unitList.get(position).SiteAddress);
                    break;
                case R.id.sp_region_name:
                    regionId = regionList.get(position).regionId;
                    branchRequestLiveData.setValue(regionId);
                    siteRequestLiveData.setValue(-1);
                    siteId = 0;
                    empCount.set(-1);
                    fetchBranchList();
                    break;
                /*case R.id.sp_course_type:
                    selectedCourseTypeID = coursetypeList.get(position).Id;
                    break;*/
                case R.id.sp_branch_name:
                    branchId = branchList.get(position).branchId;
                    siteRequestLiveData.setValue(branchId);
                    siteId = 0;
                    empCount.set(-1);
                    fetchSiteList();
                    break;
                case R.id.sp_unit_name3:
                    if (siteList.size() != 0) {
                        siteId = siteList.get(position).siteId;
                        empCount.set(siteList.get(position).empCount);
                        if (siteList.get(position).siteAddress == null || siteList.get(position).siteAddress.equals(""))
                            selectedSiteAddress.set("");
                        else
                            selectedSiteAddress.set(siteList.get(position).siteAddress);
                    }
                    break;
                case R.id.sp_training_counduct_type:
                    if (position == OJT)
                        trainingConductType = Constant.OJT;
                    else
                        trainingConductType = Constant.CLASSROOM;
                    break;

            }
        }
    };

    @Inject
    public AddRotaViewmodel(@NonNull Application application) {
        super(application);
        initRetrofit();
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse1, this::onApiError));

        dataBase = MtrainerDataBase.getDatabase(application);
        topicsAdapter = new AddRotaTopicAdapter(this::topicSelected);
        branchListLiveData = Transformations.switchMap(branchRequestLiveData, regionId -> dataBase.getBranchDao().getBranchList(regionId));
        siteListLiveData = Transformations.switchMap(siteRequestLiveData, branchId ->dataBase.getSite1Dao().getSiteList(branchId));
    }

    public void saveTaskClick(View view) {
        validateTime();
        LocalDate sDate = startDate.get();
        LocalDate eDate = endDate.get();
        LocalTime sTime = taskStartTime.get();
        LocalTime eTime = taskEndTime.get();

        /*if (site == null || site.id == 0) {
            Toast.makeText(getApplication(), "Please select Unit.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (reason == null || reason.id == 0) {
            Toast.makeText(getApplication(), "Please select Reason.", Toast.LENGTH_SHORT).show();
            return;
        }*/

        if(taskEndTimeErrorVisibility.get() == VISIBLE || taskStartTimeErrorVisibility.get() == VISIBLE) {
            Toast.makeText(getApplication(), "Please select valid time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedUnitID == 0 && siteId == 0) {
            Toast.makeText(getApplication(), "Please select Site name.", Toast.LENGTH_SHORT).show();
            return;
        }

        /*if (numberOfTrainee == null || numberOfTrainee.isEmpty()) {
            Toast.makeText(getApplication(), "Please add expected traninee.", Toast.LENGTH_SHORT).show();
            return;
        }*/

        if (sDate == null) {
            Toast.makeText(getApplication(), "Please reselect date.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (sTime == null) {
            Toast.makeText(getApplication(), "Please reselect Stat Time.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (eTime == null) {
            Toast.makeText(getApplication(), "Please reselect End Time.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (topicCount.get() == 0) {
            Toast.makeText(getApplication(), "Please select topic.", Toast.LENGTH_SHORT).show();
            return;
        }


        if (Prefs.getString(PrefsConstants.COMPANY_ID).equals("1") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("4") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("8")) {
            if(trainingConductType == 0) {
                Toast.makeText(getApplication(), "Please select Training Type.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (canAddRota) {
            canAddRota = false;
            setIsLoading(true);
            LocalDateTime taskStartDateTime = LocalDateTime.of(sDate, sTime);
            LocalDateTime taskEndDateTime = LocalDateTime.of(eDate, eTime);
            String format = "yyyy-MM-dd HH:mm:ss";

            AddRotaRequest request = new AddRotaRequest();
            request.setEndTime(taskEndDateTime.format(DateTimeFormatter.ofPattern(format)));
            request.setStartTime(taskStartDateTime.format(DateTimeFormatter.ofPattern(format)));
            request.setSelectedTopicsID(new ArrayList<Integer>(selectedTopic));
            //request.setUnitId(String.valueOf(selectedUnitID));
            request.setExpectedTrainee("4");
            if(role.equals("Training Champ") || role.equals("Unit Commander"))
                request.setUnitId(String.valueOf(selectedUnitID));
            else
                request.setUnitId(String.valueOf(siteId));
            request.setTrainerId(Prefs.getString(PrefsConstants.EMPLOYEE_ID));
            if(Prefs.getString(PrefsConstants.COMPANY_ID).equals("1") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("4") || Prefs.getString(PrefsConstants.COMPANY_ID).equals("8"))
            request.setTrainingTypeId(String.valueOf(trainingConductType));
            else
                request.setTrainingTypeId(String.valueOf(selectedTrainingTypeID));

            AddRotaMainRequest rotaMainRequest = new AddRotaMainRequest();
            rotaMainRequest.taskExecutionResult = request;
            addDisposable(dashBoardApi.requestRota(rotaMainRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onRotaRequestSuccess, this::onAddingRotaError));
        }
    }

    private void onTaskCreated() {
        message.what = NavigationConstants.ON_TASK_CREATED_SUCCESS;
        liveData.postValue(message);
    }

    private void onAddingRotaError(Throwable throwable) {
        showToast("Unable to add Rota, Please try again");
        throwable.printStackTrace();
        canAddRota = true;
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


    private void validateTime() {
        LocalTime selectedStartTime = taskStartTime.get();
        LocalTime selectedEndTime = taskEndTime.get();
        LocalDate startDate = this.startDate.get();
        LocalDate endDate = this.endDate.get();

        if (startDate != null && endDate != null && selectedStartTime != null && selectedEndTime != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sDateTime = LocalDateTime.of(startDate, selectedStartTime);
            LocalDateTime eDateTime = LocalDateTime.of(endDate, selectedEndTime);
            taskStartTimeErrorVisibility.set(sDateTime.isAfter(now) ? GONE : VISIBLE);
            taskEndTimeErrorVisibility.set(eDateTime.isAfter(sDateTime) ? GONE : VISIBLE);
        }
    }

    void fetchUnitList() {
        setIsLoading(true);
        addDisposable(dashBoardApi.getUnitList(Prefs.getString(PrefsConstants.EMPLOYEE_REG_NO))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onUnitListSuccess, this::onApiError));
    }


    void fetchRegionList() {
        if(!role.equals("Manager") && !role.equals("Trainer")) {
            regionId = Prefs.getInt(PrefsConstants.REGION_ID);
            branchRequestLiveData.setValue(regionId);
            siteRequestLiveData.setValue(-1);
            siteId = 0;
            fetchBranchList();
        } else if (role.equals("Trainer")){
            setIsLoading(true);
            addDisposable(dashBoardApi.getRegion(new BranchRegionRequest("GetRegion",
                   // Prefs.getString(PrefsConstants.COMPANY_ID), "", ""))
                    Integer.valueOf(Prefs.getString(PrefsConstants.COMPANY_ID)), 0, 0, Prefs.getInt(PrefsConstants.BASE_TRAINER_ID)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onRegionListSuccess, this::onApiError));
        } else {
            setIsLoading(true);
            addDisposable(dashBoardApi.getRegion(new BranchRegionRequest("GetRegion",
                    // Prefs.getString(PrefsConstants.COMPANY_ID), "", ""))
                    Integer.valueOf(Prefs.getString(PrefsConstants.COMPANY_ID)), 0, 0, 0))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onRegionListSuccess, this::onApiError));
        }
    }

    private void fetchBranchList() {
        setIsLoading(true);
        addDisposable(dataBase.getBranchDao().haveBranchData(regionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> {
                    if (count <= 0) {
                        addDisposable(dashBoardApi.getBranch(new BranchRegionRequest("GetBranch",
                                Integer.valueOf(Prefs.getString(PrefsConstants.COMPANY_ID)), regionId, 0, Prefs.getInt(PrefsConstants.BASE_TRAINER_ID)))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(this::onBranchListSuccess, this::onApiError));
                    } else
                        setIsLoading(false);
                }, throwable -> {
                    showToast("Something went wrong");
                    setIsLoading(false);
                }));
    }

    private void fetchSiteList() {
        setIsLoading(true);
        dataBase.getSite1Dao().haveSiteData(branchId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> {
                    if (count <= 0) {
                        Log.d("getsitedetails",""+Prefs.getString(PrefsConstants.COMPANY_ID)+","+regionId+","+branchId+","+Prefs.getInt(PrefsConstants.BASE_TRAINER_ID));
                        addDisposable(dashBoardApi.getSite(new BranchRegionRequest("GetSite",
                                Integer.valueOf(Prefs.getString(PrefsConstants.COMPANY_ID)), regionId, branchId, Prefs.getInt(PrefsConstants.BASE_TRAINER_ID)))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(this::onSiteListSuccess, this::onApiError));
                    } else
                        setIsLoading(false);
                }, throwable -> {
                    showToast("Something went wrong");
                    setIsLoading(false);
                });
    }
    LiveData<List<ChooseTopicsResponse.TopicsResponse>> getMasterList(){
        return dataBase.getTopicDao().getMasterTopicList(Prefs.getString(PrefsConstants.COMPANY_ID));
    }

    LiveData<List<RegionData>> getRegionList(){
        return dataBase.getRegionDao().getRegionList();
    }

    LiveData<List<BranchData>> getBranchList(){
        return branchListLiveData;
    }

    LiveData<List<SiteData>> getSiteList(){
        return siteListLiveData;
    }

    public void setData(List<ChooseTopicsResponse.TopicsResponse> items) {
        setIsLoading(false);
        masterTopicItems = items;
        topicsAdapter.clearAndSetItems(items);
    }

    void getTopics() {
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



    private void onChooseTopicsSuccess(ChooseTopicsResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getTopicDao().insertMasterTopic(response.topicsResponses)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }


    void getTypeList() {
        typeList.clear();
        TrainingType trainingType = new TrainingType();
        trainingType.name = "VAN";
        trainingType.id = 2;
        typeList.add(trainingType);
        trainingType = new TrainingType();
        trainingType.name = "MOBILE";
        trainingType.id = 1;
        typeList.add(trainingType);
    }



    @Override
    public void topicSelected() {
        topicCount.set(selectedTopic.size());
    }

    private void onUnitListSuccess(UnitListResponse response) {
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getUnitListDao().insertUnitList(response.unitList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }
    private void onRegionListSuccess(RegionResponse response) {
        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getRegionDao().insertRegionList(response.regionDataList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }
    private void onBranchListSuccess(BranchResponse response) {
        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getBranchDao().insertBranchList(response.regionDataList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }

    private void onSiteListSuccess(SiteResponse response) {
        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            Log.d("Siteresponse",""+response.siteDataList.size());
            dataBase.getSite1Dao().insertSiteList(response.siteDataList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }
    LiveData<List<UnitListResponse.Unit>> getUnitListFromDb() {
        return dataBase.getUnitListDao().getUnitList();
    }
    private void onRotaRequestSuccess(TrainingCalendarResponse rotaResponse) {
        if(rotaResponse.statusCode == SUCCESS_RESPONSE) {
            setIsLoading(false);
            onTaskCreated();
        }
    }
    void setUnitList(List<UnitListResponse.Unit> unitList) {
        this.unitList.clear();
        this.unitList.addAll(unitList);
    }

    void setRegionList(List<RegionData> regionList) {
        this.regionList.clear();
        this.regionList.addAll(regionList);
    }

    void setBranchList(List<BranchData> regionList) {
        this.branchList.clear();
        this.branchList.addAll(regionList);
    }

    void setSiteList(List<SiteData> regionList) {
        this.siteList.clear();
        this.siteList.addAll(regionList);
    }
    void clearRegion(){
        dataBase.getRegionDao().flushRegionListMaster();
    }

   /* void setNumberOfTrainee(String numberOfTrainee) {
        this.numberOfTrainee = numberOfTrainee;
    }*/

   public void initListener() {
        addDisposable(stringObservable
                .subscribeOn(Schedulers.io())
                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribe(data -> {
                    addDisposable(Observable.fromIterable(masterTopicItems)
                            .filter(item -> item.topicName.toLowerCase().startsWith(data.toLowerCase()))
                            .toList()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(list -> topicsAdapter.clearAndSetItems(list), th -> {
                            }));
                }, this::onApiError));
    }


    public static class NoDataException extends Exception {}
}