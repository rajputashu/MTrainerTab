package com.sisindia.ai.mtrainer.android.features.myunits;

import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableList;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
import com.sisindia.ai.mtrainer.android.models.BranchDataModel;
import com.sisindia.ai.mtrainer.android.models.BranchDetails;
import com.sisindia.ai.mtrainer.android.models.BranchRequest;
import com.sisindia.ai.mtrainer.android.models.BranchWiseSummaryResponse;
import com.sisindia.ai.mtrainer.android.models.PreAuthResponse;
import com.sisindia.ai.mtrainer.android.models.RegionData;
import com.sisindia.ai.mtrainer.android.models.SectorDataResponse;
import com.sisindia.ai.mtrainer.android.models.SectorRequest;
import com.sisindia.ai.mtrainer.android.models.SectorTypeResponse;
import com.sisindia.ai.mtrainer.android.models.SiteData;
import com.sisindia.ai.mtrainer.android.models.SiteResponseModel;
import com.sisindia.ai.mtrainer.android.models.UnitDetailsRequest;
import com.sisindia.ai.mtrainer.android.models.UnitdetailsData;
import com.sisindia.ai.mtrainer.android.models.UnitsData;
import com.sisindia.ai.mtrainer.android.models.UpdateSite;
import com.sisindia.ai.mtrainer.android.rest.AuthApi;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;
import com.sisindia.ai.mtrainer.android.rest.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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

public class MyUnitstabViewModel extends MTrainerViewModel {

    public MyUnitsRecyclerAdapter myUnitsAdapter;
//    public SectorSpinnerAdapter sectorSpinnerAdapter;

    public ObservableList<RegionData> regionList = new ObservableArrayList<>();
    public ObservableList<BranchDataModel> branchList = new ObservableArrayList<>();
    private int selectedSiteStatusFilterPos = 0;
    public ObservableList<String> unitStatusList = new ObservableArrayList<>();

    {
        unitStatusList.addAll(List.of("All", "Active", "Disbanded"));
    }

    public ObservableList<SiteData> siteList = new ObservableArrayList<>();
    public ObservableList<UnitsData> unitsDataObservableList = new ObservableArrayList<>();
    public ObservableList<SectorDataResponse> sectorTypeObservableList = new ObservableArrayList<>();

    public ObservableField<Integer> branchposition = new ObservableField<>(0);
    public ObservableInt empCount = new ObservableInt(-1);
    public ObservableField<UpdateSite> updateSiteObservableField = new ObservableField<>();
    public final String role = Prefs.getString(PrefsConstants.ROLE);
    //    public ObservableField<Integer> postion = new ObservableField<>();
    /*public ObservableField<String> totalumbrellareceived = new ObservableField<>("");
    public ObservableField<String> totalumbrellainstalled = new ObservableField<>("");
    public ObservableField<String> totalumbrelladamagedcount = new ObservableField<>("");
    public ObservableField<String> totalumbrellastack = new ObservableField<>("");*/
    public ObservableField<String> totalSiteCount = new ObservableField<>("0");
    public ObservableField<String> totalSipMounted = new ObservableField<>("0");
    public ObservableField<String> totalOYCSite = new ObservableField<>("0");
    public ObservableField<String> totalCSATSite = new ObservableField<>("0");
    public ObservableField<String> totalOtherSite = new ObservableField<>("0");

    public MutableLiveData<Boolean> update = new MutableLiveData<>(false);
    List<UnitsData> users = new ArrayList<>();
    List<SectorDataResponse> sectorDataResponses;
    public MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();

    public ObservableField<String> siteName = new ObservableField<>();
    @Inject
    AuthApi authApi;

    @Inject
    DashBoardApi dashBoardApi;
    MtrainerDataBase dataBase;
    public int regionId, branchId, siteId, totalUnitCountFromAPI, totalSpiMountedCount = 0;

    @Inject
    public MyUnitstabViewModel(@NonNull Application application) {
        super(application);
        initRetrofit();
        addDisposable(
                authApi.getPreAuth(RestConstants.GRANT_TYPE_VALUES, RestConstants.USER_NAME_VALUES, RestConstants.PASSWORD_VALUES)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onPreAuthResponse1, this::onApiError));

        dataBase = MtrainerDataBase.getDatabase(application);
        myUnitsAdapter = new MyUnitsRecyclerAdapter(dataBase, users);
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

    void fetchBranchList() {
        setIsLoading(true);

        addDisposable(dashBoardApi.getBranches(new BranchRequest(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onBranchListSuccess, this::onApiError));
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
            Log.d("branchpostion", branchList.get(branchposition.get()).branchId + " " + Prefs.getInt(PrefsConstants.BASE_TRAINER_ID));
            fetchUiDetails();
        }
    }

    public void fetchUiDetails() {
        fetchBranchSummary();
        fetchunitdetails();
        fetchSectorType();
    }

    private void fetchBranchSummary() {
        addDisposable(dashBoardApi.getbranchsummarydata(new UnitDetailsRequest(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID), branchList.get(branchposition.get()).branchId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::OnBranchSummarySuccess, this::onApiError));
    }

    private void OnBranchSummarySuccess(BranchWiseSummaryResponse response) {
        if (response.statusCode == 200) {
            try {
                JSONArray jsonArray = new JSONArray(response.branchdata);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                /*totalumbrelladamagedcount.set("TotalUmbrellDamageCount: " + jsonObject.getInt("TotalUmbrellDamageCount"));
                totalumbrellareceived.set("TotalUmbrellaReceived: " + jsonObject.getInt("TotalUmbrellaReceived"));
                totalumbrellastack.set("TotalUmbrellaStock: " + jsonObject.getInt("TotalUmbrellaStock"));
                totalumbrellainstalled.set("TotalUmbrellInstalled: " + jsonObject.getInt("TotalUmbrellInstalled"));*/

                totalUnitCountFromAPI = jsonObject.getInt("TotalSite");
                totalSipMounted.set(jsonObject.getInt("TotalSPIMounted") + "/" + totalUnitCountFromAPI);
                totalSiteCount.set("" + totalUnitCountFromAPI);
//                totalSipMounted.set(totalSpiMountedCount + "/" + totalUnitCountFromAPI);
                totalOYCSite.set("" + jsonObject.getInt("TotalOYCSite"));
                totalCSATSite.set("" + jsonObject.getInt("TotalCSATSite"));
                totalOtherSite.set("" + jsonObject.getInt("TotalOtherSite"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public LiveData<Boolean> getUpdate() {
        return update;
    }

    private void fetchunitdetails() {
        addDisposable(dashBoardApi.getunitdetails(new UnitDetailsRequest(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID), branchList.get(branchposition.get()).branchId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::OnUnitListSuccess, this::onApiError));
    }

    private void fetchSectorType() {
        addDisposable(dashBoardApi.getSectorList(new SectorRequest(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID), branchList.get(branchposition.get()).branchId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::OnSectorSuccess, this::onApiError));

    }

    private void OnUnitListSuccess(UnitdetailsData unitdetailsData) {

        if (unitdetailsData.getStatusCode() == SUCCESS_RESPONSE) {
            Gson gson = new Gson(); // Or use new GsonBuilder().create();
        /*Type listType = new TypeToken<ArrayList<UnitdetailsData.UnitsData>>() {}.getType();
        ArrayList<UnitdetailsData.UnitsData> users = gson.fromJson(unitdetailsData.getUnitsdata() , listType);*/
            try {
                unitsDataObservableList.clear();
            } catch (Exception e) {

            }

            if (unitdetailsData.getUnitsdata() != null) {
                UnitsData[] object = gson.fromJson(unitdetailsData.getUnitsdata(), UnitsData[].class);
                users = new ArrayList<>(Arrays.asList(object));

                //Commenting below logic as Spi mounted is coming from API
                /*int totalSpiCount = 0;
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getSpi() == 1 &&
                            users.get(i).getIsDisbanded().equalsIgnoreCase("NO")) {
                        totalSpiCount++;
                    }
//                    totalSpiMountedCount += users.get(i).getSpiCount();
                }

                totalSpiMountedCount = totalSpiCount;
                totalSipMounted.set(totalSpiCount + "/" + totalUnitCountFromAPI);*/

                /*myUnitsAdapter.setItemsList(users);
                myUnitsAdapter.clearAndSetItems(users);
                myUnitsAdapter.notifyDataSetChanged();*/

                validateSiteStatusFilterViaPos(selectedSiteStatusFilterPos);
            }

        } else {
            Log.d("unitresponse", "" + unitdetailsData.getStatusCode());
        }
        setIsLoading(false);

    }


    private void OnSectorSuccess(SectorTypeResponse sectorTypeResponse) {

        if (sectorTypeResponse.statusCode == 200) {
            Gson gson = new Gson(); // Or use new GsonBuilder().create();
        /*Type listType = new TypeToken<ArrayList<UnitdetailsData.UnitsData>>() {}.getType();
        ArrayList<UnitdetailsData.UnitsData> users = gson.fromJson(unitdetailsData.getUnitsdata() , listType);*/
            try {
                sectorTypeObservableList.clear();
            } catch (Exception e) {

            }
//            Log.d("SECTYPEDATA", "" + sectorTypeResponse.getData());
            if (sectorTypeResponse.getData() != null) {

                SectorDataResponse[] object = gson.fromJson(sectorTypeResponse.getData(), SectorDataResponse[].class);
                sectorDataResponses = new ArrayList<>(Arrays.asList(object));
//                Log.d("SECRes", "" + sectorDataResponses);
                sectorTypeObservableList.addAll(sectorDataResponses);
//                Log.d("SECOLIST", "" + sectorTypeObservableList);

            }

        } else {
            Log.d("SecSTATUSCODE", "" + sectorTypeResponse.statusCode);
        }
        setIsLoading(false);

    }

    public MutableLiveData<UnitsData> selectedUnitLiveData = new MutableLiveData<>();
    public UnitsiteViewListeners unitsiteViewListeners = new UnitsiteViewListeners() {

        @Override
        public void onunitsiteItemClick(UnitsData item, int position) {

            /*postion.set(position);
            mutableLiveData.setValue(true);*/

            selectedUnitLiveData.setValue(item);
            mutableLiveData.setValue(true);

        }
    };

    public void updatesite(UpdateSite updateSite) {
        updateSiteObservableField.set(updateSite);
        addDisposable(dashBoardApi.updateunit(updateSite)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::OnUpdateUnitSuccess, this::onApiError));
    }

    private void OnUpdateUnitSuccess(SiteResponseModel siteResponseModel) {
        if (siteResponseModel.statusCode == SUCCESS_RESPONSE) {
            /*if (postion.get() != null) {
                int selectedPos = postion.get();
                users.get(selectedPos).setUnitId(updateSiteObservableField.get().unitId);
                users.get(selectedPos).setSpi(updateSiteObservableField.get().spi);
                users.get(selectedPos).setSpiCount(updateSiteObservableField.get().spiCount);
                users.get(selectedPos).setUnitCode(unitsDataObservableList.get(postion.get()).getUnitCode());
                users.get(selectedPos).setUnitName(unitsDataObservableList.get(postion.get()).getUnitName());
                users.get(selectedPos).setUnitType(updateSiteObservableField.get().unitType);
                users.get(selectedPos).setSpiMountTarget(updateSiteObservableField.get().spiMountTarget);
                users.get(selectedPos).setSpiPostCovered(updateSiteObservableField.get().spiPostCovered);
                users.get(selectedPos).setSectorId(updateSiteObservableField.get().sectorId);
                users.get(selectedPos).setSectorName(updateSiteObservableField.get().sectorName);
                users.get(selectedPos).setIsDisbanded(updateSiteObservableField.get().isDisbanded == 0 ? "N0" : "YES");
                myUnitsAdapter.notifyItemChanged(selectedPos);
                myUnitsAdapter.setitems(users);
                myUnitsAdapter.clearAndSetItems(users);
                myUnitsAdapter.notifyDataSetChanged();
                update.setValue(true);

//                users.get(selectedPos).setUmbrella(updateSiteObservableField.get().umbrella);
//                users.get(selectedPos).setIsUmbrellaInstalled(updateSiteObservableField.get().isUmbrellaInstalled);
//                users.get(selectedPos).setUmbrellaInstallTarget(updateSiteObservableField.get().umbrellaInstallTarget);
//                users.get(selectedPos).setUmbrellaPostCovered(updateSiteObservableField.get().umbrellaPostCovered);
//                users.get(selectedPos).setIsUmbrellaDamage(updateSiteObservableField.get().isUmbrellaDamage);
//                users.get(selectedPos).setUmbrellaReceived(updateSiteObservableField.get().umbrellaReceived);
//                users.get(selectedPos).setUmbrellaDamageCount(updateSiteObservableField.get().umbrellaDamageCount);

            }*/

            fetchunitdetails();
            fetchBranchSummary();
            update.setValue(true);
        }
    }

    public BranchViewListeners branchViewListeners = (viewId, position) -> {
        branchposition.set(position);
        fetchBranchSummary();
        fetchunitdetails();
    };

    public SiteStatusListener siteStatusListener = (viewId, position) -> {
        selectedSiteStatusFilterPos = position;
        validateSiteStatusFilterViaPos(position);
    };

    private void validateSiteStatusFilterViaPos(int position) {
        if (position == 0) {
            myUnitsAdapter.setItemsList(users);
            myUnitsAdapter.notifyDataSetChanged();
            unitsDataObservableList.clear();
            unitsDataObservableList.addAll(users);
        } else if (position == 1) {
            filterUnitListViaStatus("NO"); // All Active Sites
        } else if (position == 2) {
            filterUnitListViaStatus("YES"); // All Disbanded Sites
        }
    }

    private void filterUnitListViaStatus(String filterStatus) {
        List<UnitsData> filteredList = new ArrayList<>();
        for (UnitsData item : users) {
            if (item.getIsDisbanded().equalsIgnoreCase(filterStatus)) {
                filteredList.add(item);
            }
        }

        myUnitsAdapter.setItemsList(filteredList);
        myUnitsAdapter.clearAndSetItems(filteredList);
        myUnitsAdapter.notifyDataSetChanged();
    }
}
