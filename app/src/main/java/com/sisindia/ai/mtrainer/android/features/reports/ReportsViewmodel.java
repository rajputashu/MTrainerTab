package com.sisindia.ai.mtrainer.android.features.reports;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.TRAINING_TOPICS;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

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
import com.sisindia.ai.mtrainer.android.features.myunits.BranchViewListeners;
import com.sisindia.ai.mtrainer.android.models.BranchDataModel;
import com.sisindia.ai.mtrainer.android.models.BranchDetails;
import com.sisindia.ai.mtrainer.android.models.BranchRequest;
import com.sisindia.ai.mtrainer.android.models.EmployeeReportsRequest;
import com.sisindia.ai.mtrainer.android.models.EmployeeReportsResponse;
import com.sisindia.ai.mtrainer.android.models.EmployeeReportsResponsedata;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.SasTokenResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingCoursesDataResponse;
import com.sisindia.ai.mtrainer.android.models.TrainingCoursesRequest;
import com.sisindia.ai.mtrainer.android.models.TrainingCoursesResponse;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ReportsViewmodel extends MTrainerViewModel {

    public ObservableField<String> selectedstartdate = new ObservableField<String>(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    @Inject
    AuthApi authApi;
    public ObservableField<String> selectedenddate = new ObservableField<String>(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    public ObservableList<BranchDataModel> branchList = new ObservableArrayList<>();
    public ObservableList<String> filterlist = new ObservableArrayList<>() {{
        add("NotStarted");
        add("Ongoing");
        add("Completed");
    }};
    public ObservableField<Integer> branchposition = new ObservableField<>(0);
    ObservableField<List<EmployeeReportsResponse>> trainingcourses= new ObservableField<>();

    public ReportsAdapter adapter = new ReportsAdapter();

    public  ObservableField<Integer> employeeName= new ObservableField<Integer>(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID));

    ObservableField<List<EmployeeReportsResponse>> filtertrainingTopic= new ObservableField<>();

   // List<EmployeeReportsResponse> filteredList = new ArrayList<>();

    @Inject
    DashBoardApi dashboardapi;

    MtrainerDataBase dataBase;

    @Inject
    public ReportsViewmodel(@NonNull Application application) {
        super(application);
        initRetrofit();
        setIsLoading(true);
        //Log.d("COMPANY ID",Prefs.getString(PrefsConstants.COMPANY_ID)+", "+Prefs.getInt(PrefsConstants.BASE_TRAINER_ID));

        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse1, this::onApiError));

        addDisposable(dashboardapi.getEmployeereport(new EmployeeReportsRequest(
                        Prefs.getInt(PrefsConstants.BASE_TRAINER_ID),
                        selectedstartdate.get(),
                        selectedenddate.get(),
                        0
                )).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTrainingCoursesList, this::onApiError));
        addDisposable(dashboardapi.getBranches(new BranchRequest(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onBranchListSuccess, this::onApiError));




    }


    public BranchViewListeners branchViewListeners = new BranchViewListeners() {
        @Override
        public void onItemSpinnerSelected(int viewId, int position) {
            branchposition.set(position);
        }
    };

    public InductionFilterViewListeners inductionFilterViewListeners = new InductionFilterViewListeners() {
        @Override
        public void onItemSpinnerSelected(int viewId, int position) {
            List<EmployeeReportsResponse> originalList = filtertrainingTopic.get();
            if (originalList == null) return;

            List<EmployeeReportsResponse> filteredList = originalList.stream()
                    .filter(item -> item.getCourseStatus() != null &&
                            item.getCourseStatus().toLowerCase().contains(filterlist.get(position).toLowerCase()))
                    .collect(Collectors.toList());
            adapter.clearAndSetItems(filteredList);
            adapter.notifyDataSetChanged();
        }
    };



    private void onPreAuthResponse1(PreAuthResponse response) {
        if (response != null && !TextUtils.isEmpty(response.accessToken)) {
            Prefs.putString(PrefsConstants.AUTH_TOKEN_KEY, response.tokenType.concat(" ").concat(response.accessToken));
            Prefs.putString(PrefsConstants.TOKEN_EXPIRE_DATE, response.expires);
        } else {
            showToast(R.string.something_went_wrong);
        }
    }


    private void onBranchListSuccess(BranchDetails response) {

            if (response.statusCode == SUCCESS_RESPONSE) {

                Gson gson = new Gson(); // Or use new GsonBuilder().create();
           /* Type listType = new TypeToken<ArrayList<BranchDataModel>>() {}.getType();
            ArrayList<BranchDataModel> users = gson.fromJson(response.getRegionDataList() , listType);*/
                BranchDataModel[] object = gson.fromJson(response.getRegionDataList(), BranchDataModel[].class);
                List<BranchDataModel> users = new ArrayList<>(Arrays.asList(object));
                branchList.addAll(users);
                branchposition.set(0);
                Log.d("branchpostion",""+branchList.get(branchposition.get()).branchId+" "+Prefs.getInt(PrefsConstants.BASE_TRAINER_ID));

            }

    }


    private void onTrainingCoursesList(EmployeeReportsResponsedata trainingCoursesResponse) {

        if(trainingCoursesResponse.getEmployeeDetails()!=null && !trainingCoursesResponse.getEmployeeDetails().isEmpty()){
            trainingcourses.set(trainingCoursesResponse.getEmployeeDetails());
            filtertrainingTopic.set(trainingCoursesResponse.getEmployeeDetails());

            /*for (EmployeeReportsResponse data : trainingcourses.get()) {
                    filteredList.add(data);
            }*/
            adapter.clearAndSetItems(trainingcourses.get());
            adapter.notifyDataSetChanged();
            setIsLoading(false);
        }else{
            setIsLoading(false);
        }
    }


    private void initRetrofit() {
        final int CONNECT_TIMEOUT_MILLIS = 1500 * 1000; // 15s
        final int READ_TIMEOUT_MILLIS = 2000 * 1000; // 20s

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

        //RequestHeaderInterceptor headerInterceptor = new RequestHeaderInterceptor();
        //MtrainerLogIntercepter mtrainerLogIntercepter = new MtrainerLogIntercepter();
        //builder.addInterceptor(headerInterceptor);
        //builder.addInterceptor(mtrainerLogIntercepter);
        RequestHeaderInterceptor headerInterceptor = new RequestHeaderInterceptor();
        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(headerInterceptor);
        /*builder.connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);*/

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

    public void applyclick(View view){
        if(selectedenddate.get()==null){
            showToast("Select End Date");
            return;
        }
        setIsLoading(true);
        addDisposable(dashboardapi.getEmployeereport(new EmployeeReportsRequest(
                        Prefs.getInt(PrefsConstants.BASE_TRAINER_ID),
                        //874610,
                        selectedstartdate.get(),
                        selectedenddate.get(),
                        branchList.get(branchposition.get()).branchId
                )).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTrainingCoursesList, this::onApiError));

    }


    public ReortsViewListeners trainingCourseViewListeners =  new ReortsViewListeners() {
        @Override
        public void onCourseClick(TrainingCoursesDataResponse item) {
  message.obj= item;
  message.what=TRAINING_TOPICS;
  liveData.setValue(message);
        }
    };
}
