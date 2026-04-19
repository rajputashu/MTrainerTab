package com.sisindia.ai.mtrainer.android.features.attendancemodule;

import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.app.Application;
import android.text.TextUtils;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
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
import com.sisindia.ai.mtrainer.android.constants.AttendanceConstants;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.db.entities.AttendanceEntity;
import com.sisindia.ai.mtrainer.android.models.EmployeeDetail;
import com.sisindia.ai.mtrainer.android.models.EmployeeSearchRequest;
import com.sisindia.ai.mtrainer.android.models.PostItem;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.SitePostRequest;
import com.sisindia.ai.mtrainer.android.models.SitePostResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingAttendanceResponse;
import com.sisindia.ai.mtrainer.android.models.UnitAttendanceRequest;
import com.sisindia.ai.mtrainer.android.models.UnitAttendanceResponse;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;
import com.sisindia.ai.mtrainer.android.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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


public class TrainingAttendanceViewModel extends MTrainerViewModel implements TrainingAttendanceRecyclerAdapter.OnAttendance, Filterable {
    public TrainingAttendanceRecyclerAdapter trainingattendanceAdapter;
    public ObservableField<String> total = new ObservableField<>();
    private MtrainerDataBase dataBase;
    public ObservableField<String> entryPost = new ObservableField<>();
    private List<TrainingAttendanceResponse.AttendanceResponse> attendanceResponseList = new ArrayList<>();

    String employeeName = "";
    public ObservableBoolean isAddVisual = new ObservableBoolean(false);
    public ObservableField<String> empName = new ObservableField<>("");
    public ObservableField<String> empRegNo = new ObservableField<>("");
    public ObservableField<Boolean> addEmpLoading = new ObservableField<>(false);
    //Set<String> selectedEmployeeSet = new HashSet<>();
    public HashSet<String> selectedEmployeeSet = new HashSet<>();
    public PostItem postItem;
    //Set<Integer> selectedPostSet = new HashSet<>();
    public HashSet<Integer> selectedPostSet = new HashSet<>();

    public ObservableBoolean canAddEmployeeName = new ObservableBoolean(false);

    @Inject
    AuthApi authApi;

    @Inject
    DashBoardApi dashBoardApi;


    @Inject
    public TrainingAttendanceViewModel(@NonNull Application application) {
        super(application);
        initRetrofit();
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse1, this::onApiError));

        trainingattendanceAdapter = new TrainingAttendanceRecyclerAdapter(MtrainerDataBase.getDatabase(application), this::onEmployeeSelected);
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


    public OnQuery onQueryListener = new OnQuery() {
        @Override
        public void query(String query) {
            getFilter().filter(query);
        }
    };

    void getPostDeatils() {
        String userid = Prefs.getString(PrefsConstants.EMPLOYEE_ID);
        SimpleDateFormat logDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String currentdate = logDateFormat.format(new Date());

        SitePostRequest request = new SitePostRequest(userid, currentdate);
        addDisposable(dashBoardApi.getSitePostName(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSitePostSuccess, this::onApiError));
    }

    private void onSitePostSuccess(SitePostResponse response) {

        setIsLoading(false);
        //showToast(response.statusMessage);
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getMasterPostDao().insertMasterPostList(response.postResponses)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }


    void getTrainingAttendance() {
        setIsLoading(true);
        String unitid = String.valueOf(Prefs.getInt(PrefsConstants.UNIT_ID));
        UnitAttendanceRequest request = new UnitAttendanceRequest(unitid);

        addDisposable(dashBoardApi.getUnitAttendance(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onUnitAttendanceSuccess, this::onApiError));
    }

    private void onTrainingAttendanceSuccess(TrainingAttendanceResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            Prefs.putString(PrefsConstants.SCORE_METRIC, String.valueOf(response.scoreMetric.get(0).score));
            dataBase.getMasterAttendanceDao().insertMasterEmployeeList(response.attendanceResponses)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }


    private void onUnitAttendanceSuccess(UnitAttendanceResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            Prefs.putString(PrefsConstants.SCORE_METRIC, String.valueOf(response.scoreMetric.get(0).score));
            dataBase.getMasterAttendanceDao().insertMasterEmployeeList(response.attendanceResponses)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }

    void setEmployee(List<TrainingAttendanceResponse.AttendanceResponse> attendanceResponses) {
        attendanceResponseList.clear();
        attendanceResponseList.addAll(getHeaderListLatter(attendanceResponses));
        trainingattendanceAdapter.setSelectedEmployeeSet(selectedEmployeeSet);
        trainingattendanceAdapter.clearAndSetItems(getHeaderListLatter(attendanceResponses));
    }

    private List<TrainingAttendanceResponse.AttendanceResponse> getHeaderListLatter(List<TrainingAttendanceResponse.AttendanceResponse> attendanceResponses) {
        List<TrainingAttendanceResponse.AttendanceResponse> attendanceResponsesModifid = new ArrayList<>();

        Collections.sort(attendanceResponses, new Comparator<TrainingAttendanceResponse.AttendanceResponse>() {
            @Override
            public int compare(TrainingAttendanceResponse.AttendanceResponse user1, TrainingAttendanceResponse.AttendanceResponse user2) {
                return String.valueOf(user1.employeeName.charAt(0)).toUpperCase().compareTo(String.valueOf(user2.employeeName.charAt(0)).toUpperCase());
            }
        });

        String lastHeader = "";

        int size = attendanceResponses.size();

        for (int i = 0; i < size; i++) {

            TrainingAttendanceResponse.AttendanceResponse user = attendanceResponses.get(i);
            String header = String.valueOf(user.employeeName.charAt(0)).toUpperCase();

            if (!TextUtils.equals(lastHeader, header)) {
                lastHeader = header;
                TrainingAttendanceResponse.AttendanceResponse attendanceResponseWithHeader = new TrainingAttendanceResponse.AttendanceResponse();
                attendanceResponseWithHeader.isselected = true;
                attendanceResponseWithHeader.header = header;
                attendanceResponsesModifid.add(attendanceResponseWithHeader);
            }
            attendanceResponsesModifid.add(user);
        }
        return attendanceResponsesModifid;
    }

    LiveData<Integer> getSavedattendanceList() {
        return dataBase.getAttendanceDao().getAttendanceList(Prefs.getInt(PrefsConstants.ROTA_ID));
    }

    LiveData<List<TrainingAttendanceResponse.AttendanceResponse>> getEmployeeList() {
        return dataBase.getMasterAttendanceDao().getEmployeeList(String.valueOf(Prefs.getInt(PrefsConstants.UNIT_ID)), Prefs.getInt(PrefsConstants.ROTA_ID));
    }

    LiveData<List<PostItem>> getPostList() {
        return dataBase.getMasterPostDao().getPostList(Prefs.getInt(PrefsConstants.UNIT_ID));
    }


    @Override
    public void onEmployeeSelected(TrainingAttendanceResponse.AttendanceResponse selectedEmp, int position) {
        if (selectedEmp != null) {
            EmployeeDetail employeeDetail = new EmployeeDetail();
            employeeDetail.id = selectedEmp.employeeId;
            employeeDetail.name = selectedEmp.employeeName;
            employeeDetail.empCode = selectedEmp.employeeCode;
            employeeDetail.score = selectedEmp.score;
            message.obj = employeeDetail;
            if (Prefs.getString(PrefsConstants.ATTENDANCE_TYPE_LIST).equals(String.valueOf(AttendanceConstants.PICTURE))) {
                message.what = NavigationConstants.TAKE_EMPLOYEE_IMAGE;
                liveData.postValue(message);
            } else if (Prefs.getString(PrefsConstants.ATTENDANCE_TYPE_LIST).equals(String.valueOf(AttendanceConstants.SIGNATURE))) {
                message.what = NavigationConstants.TAKE_EMPLOYEE_SIGH;
                liveData.postValue(message);
            } else if (Prefs.getString(PrefsConstants.ATTENDANCE_TYPE_LIST).equals(String.valueOf(AttendanceConstants.CLICK))) {
                saveData(selectedEmp.employeeName, selectedEmp.employeeId, selectedEmp.employeeCode, selectedEmp.score, position);
            } else {
                message.what = NavigationConstants.TAKE_BOTH;
                liveData.postValue(message);
            }
        } else {
            message.what = NavigationConstants.ATTENDENCE_ERROR;
            liveData.postValue(message);
        }
    }

    void refreshRecylerView() {
        trainingattendanceAdapter.setSelectedEmployeeSet(selectedEmployeeSet);
        trainingattendanceAdapter.notifyDataSetChanged();

    }

    private void saveData(String employeeName, int employeeID, String empCode, float score, int position) {
        AttendanceEntity attendanceEntity = new AttendanceEntity();
        attendanceEntity.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
        attendanceEntity.employeeId = employeeID;
        attendanceEntity.employeeName = employeeName;
        attendanceEntity.postName = postItem.postName;
        attendanceEntity.postId = postItem.postId;
        attendanceEntity.photoId = "NA";
        attendanceEntity.signatureId = "NA";
        attendanceEntity.empCode = empCode;
        attendanceEntity.score = score;
        selectedEmployeeSet.add(empCode);
        selectedPostSet.add(postItem.postId);

        trainingattendanceAdapter.notifyItemChanged(position);
        message.what = NavigationConstants.UPDATE_ATTENDENCE_VIEW;
        liveData.postValue(message);

        dataBase.getAttendanceDao().insertAttendance(attendanceEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private void saveManualData(String employeeName, int employeeID, String empCode, float score) {
        AttendanceEntity attendanceEntity = new AttendanceEntity();
        attendanceEntity.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
        attendanceEntity.employeeId = employeeID;
        attendanceEntity.employeeName = employeeName;
        attendanceEntity.postName = postItem.postName;
        attendanceEntity.postId = postItem.postId;
        attendanceEntity.photoId = "NA";
        attendanceEntity.signatureId = "NA";
        attendanceEntity.empCode = empCode;
        attendanceEntity.score = score;
        selectedEmployeeSet.add(empCode);
        selectedPostSet.add(postItem.postId);

        addDisposable(dataBase.getAttendanceDao().insertAttendance(attendanceEntity)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    trainingattendanceAdapter.notifyDataSetChanged();
                    message.what = NavigationConstants.UPDATE_ATTENDENCE_VIEW;
                    liveData.postValue(message);
                }, Throwable::printStackTrace));
    }

    @Override
    public Filter getFilter() {
        return employeeFilter;
    }

    private Filter employeeFilter = new Filter() {
        ArrayList<TrainingAttendanceResponse.AttendanceResponse> filterList = new ArrayList<>();

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint == null || constraint.length() == 0) {
                filterList = new ArrayList<>(attendanceResponseList);
            } else {
                String query = constraint.toString().trim();
                filterList.clear();
                for (TrainingAttendanceResponse.AttendanceResponse item : attendanceResponseList) {
                    if (TextUtils.isDigitsOnly(query)) {
                        if (item.employeeCode != null && item.employeeCode.startsWith(query))
                            filterList.add(item);
                    } else {
                        if (item.employeeName != null && item.employeeName.toLowerCase().startsWith(query.toLowerCase()))
                            filterList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null)
                trainingattendanceAdapter.clearAndSetItems((List<TrainingAttendanceResponse.AttendanceResponse>) results.values);
        }
    };

    void recoverAttendanceState() {
        dataBase.getAttendanceDao().getAttendanceIdList(Prefs.getInt(PrefsConstants.ROTA_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onRestoreEmpId, th -> {
                    showToast("Error fetching employee");
                });
    }

    // here need to change Integer List
    private void onRestoreEmpId(List<String> oldData) {
        selectedEmployeeSet = new HashSet<>(oldData);
        refreshRecylerView();
        message.what = NavigationConstants.UPDATE_ATTENDENCE_VIEW;
        liveData.postValue(message);
    }


    //0369697
    public void onAddClicked(View view) {
        String searchText = empRegNo.get().trim();
        Timber.e("Search Emp Text : " + searchText);
        if (isAddVisual.get()) {

            String empCodeToSearch = searchText.toUpperCase();
            addDisposable(dataBase.getAttendanceDao().checkDuplicateEmployee(empCodeToSearch)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(empCount -> {
                                if (empCount > 0) {
                                    showToast(empCodeToSearch + " already added");
                                } else {
                                    if (canAddEmployeeName.get()) {
                                        if (employeeName.isEmpty())
                                            showToast("Enter Employee Name");
                                        else {
                                            TrainingAttendanceResponse.AttendanceResponse adHocEmployee = new TrainingAttendanceResponse.AttendanceResponse();
                                            adHocEmployee.currSiteID = String.valueOf(Prefs.getInt(PrefsConstants.UNIT_ID));
                                            adHocEmployee.rotaId = Prefs.getInt(PrefsConstants.ROTA_ID);
                                            adHocEmployee.employeeCode = searchText + "_" + StringUtils.getTimeStamp();
                                            adHocEmployee.employeeName = employeeName;
                                            adHocEmployee.status = 1;
                                            addDisposable(dataBase.getMasterAttendanceDao().insertMasterEmployee(adHocEmployee)
                                                    .doOnComplete(() -> saveManualData(adHocEmployee.employeeName, adHocEmployee.employeeId, adHocEmployee.employeeCode, adHocEmployee.score))
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(this::employeeAdded, this::errorWhileAddingEmployee));
                                        }

                                    } else {
                                        addEmpLoading.set(true);
                                        addDisposable(dataBase.getMasterAttendanceDao().addEmployeeToCurrentSite(searchText.toUpperCase(), String.valueOf(Prefs.getInt(PrefsConstants.UNIT_ID)))
                                                .toSingleDefault(1)
                                                .flatMap(baseApiResponse -> dataBase.getMasterAttendanceDao().getEmployee(searchText.toUpperCase()))
                                                .map(data -> {
                                                    saveManualData(data.employeeName, data.employeeId, data.employeeCode, data.score);
                                                    return 1;
                                                })

                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(this::employeeAdded, this::errorWhileAddingEmployee));

                                    }
                                }
                            },
                            throwable -> {
                                Timber.e("Error in fetching count");
                                showToast("Unable to add " + empCodeToSearch);
                            }));

        } else {
            if (searchText.isEmpty())
                showToast("Enter Employee ID");
            else {
                addEmpLoading.set(true);
                dataBase.getMasterAttendanceDao().getName(searchText.toUpperCase())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(name -> {
                                    empName.set(name);
                                    addEmpLoading.set(false);
                                    isAddVisual.set(true);
                                },
                                throwable -> {
                                    searchOnline(searchText.toUpperCase());
                                });
            }
        }
    }

    private void employeeAdded() {
        showToast("Employee Added");
        addEmpLoading.set(false);
        isAddVisual.set(false);
        empName.set("");
        empRegNo.set("");
        canAddEmployeeName.set(false);
    }

    private void employeeAdded(int dummy) {
        showToast("Employee Added");
        addEmpLoading.set(false);
        isAddVisual.set(false);
        empName.set("");
        empRegNo.set("");
        canAddEmployeeName.set(false);
    }

    private void errorWhileAddingEmployee(Throwable th) {
        showToast("Employee already exists");
        addEmpLoading.set(false);
        isAddVisual.set(true);
    }

    private void searchOnline(String searchText) {
        EmployeeSearchRequest request = new EmployeeSearchRequest();
        request.setCompanyId(Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID)));
        request.setEmployeeCode(searchText);
        dashBoardApi.searchEmployee(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.attendanceResponse != null) {
                        dataBase.getMasterAttendanceDao().insertMasterEmployee(response.attendanceResponse)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                            empName.set(response.attendanceResponse.employeeName);
                                            addEmpLoading.set(false);
                                            isAddVisual.set(true);
                                        },
                                        th -> {
                                            logger.log("Error while inserting (insertMasterEmployee) - E1", th);
                                            showToast("Unexpected Error Occurred - E1");
                                        });
                    } else {
                        canAddEmployeeName.set(true);
                        //showToast("No Employee Found");
                        addEmpLoading.set(false);
                        isAddVisual.set(true);
                    }
                }, th -> {
                    logger.log("Error from API - E2", th);
                    showToast("Unexpected Error Occurred - E2");
                });
    }

    public void openBarcodeScreen(View v) {
        message.what = NavigationConstants.OPEN_BARCODE;
        liveData.postValue(message);
    }

    void updatePost(PostItem selectedPost) {
        trainingattendanceAdapter.setPostItem(selectedPost);
    }
}
