package com.sisindia.ai.mtrainer.android.features.myunits;

import android.app.Application;
import android.util.Log;
import android.view.View;
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
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.features.addrota.AddTaskViewListeners;
import com.sisindia.ai.mtrainer.android.models.BranchData;
import com.sisindia.ai.mtrainer.android.models.BranchRegionRequest;
import com.sisindia.ai.mtrainer.android.models.BranchResponse;
import com.sisindia.ai.mtrainer.android.models.MyUnitsRequest;
import com.sisindia.ai.mtrainer.android.models.MyUnitsResponse;
import com.sisindia.ai.mtrainer.android.models.RegionData;
import com.sisindia.ai.mtrainer.android.models.RegionResponse;
import com.sisindia.ai.mtrainer.android.models.SiteData;
import com.sisindia.ai.mtrainer.android.models.SiteResponse;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

public class MyUnitsViewModel extends MTrainerViewModel {

    public MyUnitsRecyclerAdapter myUnitsAdapter;
    public ObservableField<LocalDate> startDate = new ObservableField<>(LocalDate.now());
    public ObservableField<LocalDate> endDate = new ObservableField<>(LocalDate.now());

    public ObservableList<RegionData> regionList = new ObservableArrayList<>();
    public ObservableList<BranchData> branchList = new ObservableArrayList<>();
    public ObservableList<SiteData> siteList = new ObservableArrayList<>();

    private MutableLiveData<Integer> branchRequestLiveData = new MutableLiveData<>();
    private LiveData<List<BranchData>> branchListLiveData;
    private MutableLiveData<Integer> siteRequestLiveData = new MutableLiveData<>();
    private LiveData<List<SiteData>> siteListLiveData;
    public ObservableField<String> selectedSiteAddress = new ObservableField<>("");
    public ObservableInt empCount = new ObservableInt(-1);
    public final String role = Prefs.getString(PrefsConstants.ROLE);
    public   ObservableField<String> siteName= new ObservableField<>();
    public   ObservableField<String> NoTrainingDone= new ObservableField<>();
    public   ObservableField<String> NoGuardTrained= new ObservableField<>();
    @Inject
    DashBoardApi dashBoardApi;
    String startdate,enddate;
    MtrainerDataBase dataBase;
    public int  regionId,branchId,siteId;



    @Inject
    public MyUnitsViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);
        myUnitsAdapter = new MyUnitsRecyclerAdapter(dataBase, null);
        dataBase.getMyUnitsDao().flushMyUnits()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        branchListLiveData = Transformations.switchMap(branchRequestLiveData, regionId -> dataBase.getBranchDao().getBranchList(regionId));
        siteListLiveData = Transformations.switchMap(siteRequestLiveData, branchId ->dataBase.getSite1Dao().getSiteList(branchId));
    }

    public AddTaskViewListeners viewListeners = new AddTaskViewListeners() {

        @Override
        public void onDateChanged(int viewId, LocalDate date) {
            switch (viewId) {
                case R.id.add_rota_start_date:
                    startDate.set(date);
                    startdate= String.valueOf(startDate);
                    //  Log.v("time","current"+startDate);

                    // endDate.set(date);
                    break;

                case R.id.add_rota_end_date :
                    endDate.set(date);
                    enddate= String.valueOf(endDate);
                    break;
            }

        }

        @Override
        public void onStartTimeSelected(LocalTime time) {
            // taskStartTime.set(time);

        }

        @Override
        public void onEndTimeSelected(LocalTime time) {
            //  taskEndTime.set(time);

        }

        @Override
        public void onItemSpinnerSelected(int viewId, int position) {
            switch (viewId){
                case R.id.sp_region_name:
                    regionId = regionList.get(position).regionId;
                    branchRequestLiveData.setValue(regionId);
                    siteRequestLiveData.setValue(-1);
                    siteId = 0;
                    empCount.set(-1);
                    fetchBranchList();
                    break;
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
            }

        }
    };
    void fetchRegionList() {
        if(!role.equals("Manager")) {
            regionId = Prefs.getInt(PrefsConstants.REGION_ID);
            branchRequestLiveData.setValue(regionId);
            siteRequestLiveData.setValue(-1);
            siteId = 0;
            fetchBranchList();
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
        dataBase.getBranchDao().haveBranchData(regionId)
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
                });
    }

    private void fetchSiteList() {
        setIsLoading(true);
        dataBase.getSite1Dao().haveSiteData(branchId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> {
                    if (count <= 0) {
                        addDisposable(dashBoardApi.getSite(new BranchRegionRequest("GetSite",
                                Integer.valueOf(Prefs.getString(PrefsConstants.COMPANY_ID)), regionId, branchId, 0))
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
            dataBase.getSite1Dao().insertSiteList(response.siteDataList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
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





    public  void getMyUnits(View view){

        dataBase.getMyUnitsDao().flushMyUnits()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        myUnitsAdapter.clear();
        myUnitsAdapter.notifyDataSetChanged();
        List<MyUnitsResponse.MyUnitsDetailList> myUnitsResponses = new ArrayList<>();

        if (branchId == 0 && siteId == 0) {
            Toast.makeText(getApplication(), "Please select Site name.", Toast.LENGTH_SHORT).show();
            return;
        }

        else if(myUnitsResponses.size()!=0)
        {
            dataBase.getMyUnitsDao().flushMyUnits()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
            getMyUnitsDetails();
        }
        else {
            getMyUnitsDetails();
        }

    }


    void getMyUnitsDetails() {
        setIsLoading(true);
        // String empid = String.valueOf(Prefs.getInt(PrefsConstants.EMPLOYEE_ID));
        LocalDate sDate = startDate.get();
        LocalDate eDate=endDate.get();
        //  LocalDate taskStartDateTime = LocalDate.of(sDate);
        //  LocalDateTime taskEndDateTime = LocalDateTime.of(eDate);
        String format = "yyyy-MM-dd";

        MyUnitsRequest request = new MyUnitsRequest(sDate.format(DateTimeFormatter.ofPattern(format)),eDate.format(DateTimeFormatter.ofPattern(format)),
                String.valueOf(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID)), String.valueOf(branchId), String.valueOf(siteId));

        addDisposable(dashBoardApi.getMyUnitsDetails(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onMyUnitsSuccess, this::onApiError));
    }

    private void onMyUnitsSuccess(MyUnitsResponse response) {


        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            showToast("Successfully got your units ");
            dataBase.getMyUnitsDao().insertMyUnits(response.headerLists)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();


            if(response.BodyLists.size()>0){

                siteName.set(""+response.BodyLists.get(0).siteName);
            }
            else {
                siteName.set("");
            }

            if (response.BodyLists.size()>0)
            {
                NoTrainingDone.set(""+response.BodyLists.get(0).noOfTrainingDone);
            }
            else {
                NoTrainingDone.set((""));
            }
            if (response.BodyLists.size()>0)
            {
                NoGuardTrained.set(""+response.BodyLists.get(0).nnoOfGaurdTrained);
            }
            else {
                NoGuardTrained.set("");
            }



            if(response.headerLists.isEmpty()){

                showToast("Not found my units ");
                //  noCompleteRota.set("No data found");

            }
            else {

            }
            Log.v("current ","data"+response.headerLists);
            Log.e("current ","data"+response.headerLists);

        }
        else {

        }

    }

    void refreshMyUnitsView(List<MyUnitsResponse.MyUnitsDetailList> data) {
        // myUnitsAdapter.clear();
        /*myUnitsAdapter.clearAndSetItems(data);
        myUnitsAdapter.notifyDataSetChanged();*/
    }

    public  LiveData<List<MyUnitsResponse.MyUnitsDetailList>> getMyUnitsList(){
        return dataBase.getMyUnitsDao().getMyUnitsList();
    }

}
