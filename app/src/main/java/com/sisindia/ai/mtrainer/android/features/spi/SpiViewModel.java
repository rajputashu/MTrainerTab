package com.sisindia.ai.mtrainer.android.features.spi;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.INIT_SPI_STATE;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.ON_OPEN_BASIC_INFO_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_CLIENT_APPROVAL_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_DESIGN_SPI_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_DRAFT_APPROVAL_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_DRAFT_SPI_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_MOUNTED_SPI_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_PRINTING_SPI_SCREEN;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.R;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.models.CsatOycCountRequest;
import com.sisindia.ai.mtrainer.android.models.CsatOycCountResponse;
import com.sisindia.ai.mtrainer.android.models.SpiBranchData;
import com.sisindia.ai.mtrainer.android.models.SpiBranchRequest;
import com.sisindia.ai.mtrainer.android.models.SpiBranchResponse;
import com.sisindia.ai.mtrainer.android.models.SpiTableDetailsRequest;
import com.sisindia.ai.mtrainer.android.models.SpiTableDetailsResponse;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SpiViewModel extends MTrainerViewModel {
    public SitePostRecyclerAdapter sitePostAdapter;
    public ObservableList<SpiBranchData> branchList = new ObservableArrayList<>();
    public ObservableField<String> oyc = new ObservableField<>();
    public ObservableField<String> csat = new ObservableField<>();

    private MutableLiveData<Integer> branchRequestLiveData = new MutableLiveData<>();
    private LiveData<List<SpiBranchData>> branchListLiveData;
    private int spinnerPosition;

    private int branchId, regionId;
    MtrainerDataBase dataBase;
    @Inject
    DashBoardApi dashBoardApi;

    @Inject
    public SpiViewModel(@NonNull Application application) {
        super(application);
        regionId = Prefs.getInt(PrefsConstants.REGION_ID);
        dataBase = MtrainerDataBase.getDatabase(application);
        sitePostAdapter = new SitePostRecyclerAdapter(dataBase);
        dataBase.getSpiTableDetailsDao().flushSpiTable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        branchRequestLiveData.setValue(regionId);
        branchListLiveData = Transformations.switchMap(branchRequestLiveData, regionId -> dataBase.getSpiBranchDao().getSpiBranchList(regionId));
    }

    public AddSpinnerViewListerner viewListeners = new AddSpinnerViewListerner() {
        @Override
        public void onItemSpinnerSelected(int viewId, int position) {
            switch (viewId) {
                case R.id.sp_branch_name:
                    branchId = branchList.get(position).branchId;
                    spinnerPosition = position;
                    Log.v("branch position", "main" + position);

                    dataBase.getSpiTableDetailsDao().flushSpiTable()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                    castOycCount();
                    SpiTableDetails();

                    break;
            }
        }
    };

    LiveData<List<SpiBranchData>> getBranchList() {
        return branchListLiveData;
    }

    void fetchBranchList() {
        setIsLoading(true);
        dataBase.getSpiBranchDao().haveSpiBranchData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> {
                    if (count <= 0) {

                        addDisposable(dashBoardApi.getSpiBranch(new SpiBranchRequest(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID),
                                        Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID))))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(this::onBranchListSuccess, this::onApiError));
                    } else {
                        setIsLoading(false);
                        message.what = INIT_SPI_STATE;
                        liveData.setValue(message);
                    }
                    setIsLoading(false);
                }, throwable -> {
                    showToast("Something went wrong");
                    setIsLoading(false);
                });
    }

    private void onBranchListSuccess(SpiBranchResponse response) {
        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            addDisposable(dataBase.getSpiBranchDao().insertSpiBranchList(response.spibranchDataList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        message.what = INIT_SPI_STATE;
                        liveData.setValue(message);
                    }, Throwable::printStackTrace));
        }
    }

    public SpiViewListeners viewListeners1 = new SpiViewListeners() {

        @Override
        public void onSpiItemClick(SpiTableDetailsResponse.SpiTableDetailsData item) {

            validateSpiItemClick(item.StatusId);
            Prefs.putString(PrefsConstants.SPI_BRANCH, item.branchName);
            Prefs.putString(PrefsConstants.SPI_STATUS, item.Status);
            Prefs.putInt(PrefsConstants.SPI_BRANCHID, item.branchId);
            Prefs.putInt(PrefsConstants.SPI_TYPEID, item.typeId);
            Prefs.putInt(PrefsConstants.SPI_UNIT_ID, item.UnitId);
            Prefs.putInt(PrefsConstants.CUSTOMER_ID, item.CustomerId);
            Prefs.putString(PrefsConstants.SPI_CUSTOMER, item.customerName);
            Prefs.putString(PrefsConstants.SPI_TYPE, item.type);
            Prefs.putString(PrefsConstants.SPI_UNIT_CODE, item.unitCode);

            Prefs.putInt(PrefsConstants.SPI_ID, item.spiId);


        }
    };

   /* public  void onSpiItemClicked(View view){
        message.what = ON_OPEN_BASIC_INFO_SCREEN;
        liveData.postValue(message);
    }*/

    private void validateSpiItemClick(int statusId) {
        if (statusId == 1) {
            setIsLoading(true);
            message.what = ON_OPEN_BASIC_INFO_SCREEN;
            message.arg1 = spinnerPosition;
            // 0 -> New SPI
            message.arg2 = 0;
            liveData.postValue(message);
        } else if (statusId == 2) {
            setIsLoading(true);
            message.what = OPEN_DRAFT_SPI_SCREEN;
            message.arg1 = spinnerPosition;
            liveData.postValue(message);
        } else if (statusId == 3) {
            setIsLoading(true);
            message.what = OPEN_DRAFT_APPROVAL_SCREEN;
            message.arg1 = spinnerPosition;
            liveData.postValue(message);
        } else if (statusId == 4) {
            setIsLoading(true);
            message.what = OPEN_DESIGN_SPI_SCREEN;
            message.arg1 = spinnerPosition;
            liveData.postValue(message);
        } else if (statusId == 5) {
            setIsLoading(true);
            message.what = OPEN_CLIENT_APPROVAL_SCREEN;
            message.arg1 = spinnerPosition;
            liveData.postValue(message);
        } else if (statusId == 6) {
            setIsLoading(true);
            message.what = OPEN_PRINTING_SPI_SCREEN;
            message.arg1 = spinnerPosition;
            liveData.postValue(message);
        } else if (statusId == 7) {
            setIsLoading(true);
            message.what = OPEN_MOUNTED_SPI_SCREEN;
            message.arg1 = spinnerPosition;
            liveData.postValue(message);
        } else if (statusId == 9) {
            //sitePostAdapter.activateClick();
            setIsLoading(true);
            message.what = ON_OPEN_BASIC_INFO_SCREEN;
            message.arg1 = spinnerPosition;
            // 1 -> Completed SPI
            message.arg2 = 1;
            liveData.postValue(message);
        }
    }

    void setBranchList(List<SpiBranchData> branchList) {
        this.branchList.clear();
        this.branchList.addAll(branchList);
    }

    void castOycCount() {

        addDisposable(dashBoardApi.getCastOycCount(new CsatOycCountRequest(branchId,
                        Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID))))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onCsatOycsSuccess, this::onApiError));

    }

    private void onCsatOycsSuccess(CsatOycCountResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {

            if (response.csatOycCountDataList.size() > 0) {
                oyc.set("" + response.csatOycCountDataList.get(0).oyc);
                csat.set("" + response.csatOycCountDataList.get(0).csat);
            }

        }
    }

    void SpiTableDetails() {
        dataBase.getSpiTableDetailsDao().flushSpiTable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        sitePostAdapter.clear();
        sitePostAdapter.notifyDataSetChanged();

        addDisposable(dashBoardApi.getSpiTableDetails(new SpiTableDetailsRequest(Prefs.getInt(PrefsConstants.BASE_TRAINER_ID),
                        Integer.parseInt(Prefs.getString(PrefsConstants.COMPANY_ID)), branchId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSpiTableDetailsSuccess, this::onApiError));

    }

    private void onSpiTableDetailsSuccess(SpiTableDetailsResponse response) {

        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getSpiTableDetailsDao().insertSpiTableDetails(response.spiTableDetailsDataList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
            Prefs.putInt(PrefsConstants.SPI_MAIN_BRANCH_ID, branchId);

            if (response.spiTableDetailsDataList.isEmpty()) {
                showToast("Not found any rota ");


            } else {

            }

        }
    }

    void spiDetails(List<SpiTableDetailsResponse.SpiTableDetailsData> data) {
        sitePostAdapter.clearAndSetItems(data);
    }

    LiveData<List<SpiTableDetailsResponse.SpiTableDetailsData>> getSpiTableDetails() {
        return dataBase.getSpiTableDetailsDao().getSpiTableDetails();
    }
}
