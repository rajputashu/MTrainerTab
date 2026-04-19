package com.sisindia.ai.mtrainer.android.features.spi.basicinformation;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_DRAFT_SPI_SCREEN;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.lifecycle.LiveData;
import androidx.room.EmptyResultSetException;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.features.spi.SpiPostDialogAdapter;
import com.sisindia.ai.mtrainer.android.features.spi.basicinformation.model.CompletedDataItem;
import com.sisindia.ai.mtrainer.android.features.spi.basicinformation.model.DataItem;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiPostReFetchRequest;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiPostRequest;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiPostResponse;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiStatusRequest;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;
import com.sisindia.ai.mtrainer.android.models.SpiBasicInfoRequest;
import com.sisindia.ai.mtrainer.android.models.SpiBasicInfoResponse;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BasicInformationViewModel extends MTrainerViewModel {
    public ObservableField<String> customer = new ObservableField<>();
    public ObservableField<String> customerid = new ObservableField<>();
    public ObservableField<String> type = new ObservableField<>();
    MtrainerDataBase dataBase;
    public SpiPostDialogAdapter postDialogAdapter = new SpiPostDialogAdapter();
    public ObservableList<SpiPostResponse.SpiPostdata> spiPostList = new ObservableArrayList<>();
    public ObservableBoolean isCompleted = new ObservableBoolean(false);
    public ObservableField<String> status = new ObservableField<>();
    public ObservableField<String> postcount = new ObservableField<>();
    public ObservableField<String> tvPostCount = new ObservableField<>("");
    public ObservableField<String> tvPostVisited = new ObservableField<>("");
    public ObservableField<String> tvPostIops = new ObservableField<>("");
    public ObservableField<String> tvPostMissing = new ObservableField<>("");
    public ObservableField<String> tvPostVerified = new ObservableField<>("");

    BasicInfoActvity basicInfoFragment;
    public List<SpiPostResponse.SpiPostdata> postListLiveData = new ArrayList<>();

    // String allpost ="",allpostavailable="",missingpost="",branchid="",typeid="",unitid="",customerid="";
    @Inject
    DashBoardApi dashBoardApi;
    int branchid, typeid, unitid, spiCustomerid;

    @Inject
    public BasicInformationViewModel(@NonNull Application application) {
        super(application);
        dataBase = MtrainerDataBase.getDatabase(application);

        customer.set(Prefs.getString(PrefsConstants.SPI_CUSTOMER));
        customerid.set(Prefs.getString(PrefsConstants.SPI_UNIT_CODE));
        type.set(Prefs.getString(PrefsConstants.SPI_TYPE));
        status.set(Prefs.getString(PrefsConstants.SPI_STATUS));

        branchid = Prefs.getInt(PrefsConstants.SPI_BRANCHID);
        typeid = Prefs.getInt(PrefsConstants.SPI_TYPEID);
        unitid = Prefs.getInt(PrefsConstants.SPI_UNIT_ID);
        spiCustomerid = Prefs.getInt(PrefsConstants.CUSTOMER_ID);
        dataBase.getDraftSpiPhotoDao().flushDraftSpiPhotoTable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    void getBasicInfoStatus(SpiStatusRequest request) {
        addDisposable(dashBoardApi.getSpiStatus(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSpistatusSuccess, this::onApiError));
    }

    private void onSpistatusSuccess(BaseApiResponse response) {
        if (response.statusCode == SUCCESS_RESPONSE) {

        }
    }


    void getBasicInfo(SpiBasicInfoRequest request) {
        addDisposable(dashBoardApi.getSpiBasicInfo(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSpiBasicInfoSuccess, this::onApiError));
    }

    void getBasicInfoForCompleted() {
        addDisposable(
                dataBase.getSpiPostsDao().getSpiPendingPosts()
                        .flatMap(data -> {
                            dataBase.getSpiBasicInfoDao().removeSpiBasicTableData(Prefs.getInt(PrefsConstants.SPI_ID));
                            return dashBoardApi.reFetchPostInfo(new SpiPostReFetchRequest(Prefs.getInt(PrefsConstants.SPI_ID)));
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onSpiBasicInfoSuccess, th -> {
                            if (th instanceof EmptyResultSetException) {
                                showToast("No new Post");
                            } else {
                                onApiError(th);
                            }
                        }));
    }


    void getBasicInfopost(SpiPostRequest request) {
        setIsLoading(true);
        // Removing posts because not maintaining siteId
        addDisposable(dataBase.getSpiPostsDao().flushSpiPostsTable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    fetchPosts(request);
                }, throwable -> {
                    setIsLoading(false);
                }));
    }

    private void fetchPosts(SpiPostRequest request) {
        addDisposable(dashBoardApi.getSpiPosts(request)
                .flatMap(data -> {
                    onSpiPostsSuccess(data);
                    return dashBoardApi.completedPostInfo(new SpiPostReFetchRequest(Prefs.getInt(PrefsConstants.SPI_ID)));
                })
                .map(data -> {
                    List<CompletedDataItem> items = data.getData();
                    if (items.size() > 0)
                        isCompleted.set(true);
                    for (CompletedDataItem item : items) {
                        dataBase.getSpiPostsDao().markPostCompleted(item.getPostId());
                    }
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    if (!isCompleted.get())
                        setIsLoading(false);
                    else
                        setIsCompleteState();
                }, this::onApiError));
    }

    private void onSpiPostsSuccess(SpiPostResponse response) {
        if (response.statusCode == SUCCESS_RESPONSE) {
            dataBase.getSpiPostsDao().insertSpiPosts(response.spiPostdata);
        }

    }

    private void onSpiBasicInfoSuccess(SpiBasicInfoResponse response) {
        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {
           /* dataBase.getSpiBasicInfoDao().flushSpiBasicTable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();*/
            if (response.spiBasicInfoDetailsData != null)
                Observable.fromIterable(response.spiBasicInfoDetailsData)
                        .map(data -> {
                            data.uniqueId = data.keyid + "_" + data.postid;
                            return data;
                        }).toList()
                        .map(data -> {
                            dataBase.getSpiBasicInfoDao().insertSpiBasicinfo(response.spiBasicInfoDetailsData);
                            return true;
                        })
                        .flatMap(data -> dashBoardApi.completedPostInfo(new SpiPostReFetchRequest(Prefs.getInt(PrefsConstants.SPI_ID))))
                        .map(data -> {
                            List<CompletedDataItem> items = data.getData();
                            for (CompletedDataItem item : items) {
                                dataBase.getSpiBasicInfoDao().markPostCompleted(item.getPostId());
                            }
                            return true;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(data -> {
                            if (!response.spiBasicInfoDetailsData.isEmpty()) {
                                message.what = OPEN_DRAFT_SPI_SCREEN;
                                liveData.postValue(message);
                                Prefs.putInt(PrefsConstants.SPI_ID, response.spiBasicInfoDetailsData.get(0).keyid);
                            }
                        }, throwable -> {
                            showToast("Error Occoured while getting spi");
                        });
        }
    }

    LiveData<List<SpiPostResponse.SpiPostdata>> getSpiPostist() {
        return dataBase.getSpiPostsDao().getSpiPosts();
    }

    void setSpiPost(List<SpiPostResponse.SpiPostdata> postlist) {
        this.spiPostList.clear();
        this.spiPostList.addAll(postlist);
        this.spiPostList.addAll(postListLiveData);
        postDialogAdapter.setData(postlist);
    }

    private void setIsCompleteState() {
        addDisposable(dashBoardApi.reFetchBasicInfo(new SpiPostReFetchRequest(Prefs.getInt(PrefsConstants.SPI_ID)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.getStatusCode() == 200) {
                        List<DataItem> data = response.getData();
                        if (!data.isEmpty()) {
                            try {
                                tvPostCount.set(String.valueOf(data.get(0).getPostCount()));
                                tvPostIops.set(getStringValue(data.get(0).getAllPostsWereAvailableInIOPS()));
                                tvPostMissing.set(getStringValue(data.get(0).getMissingPostsCreatedbyTrainer()));
                                tvPostVerified.set(getStringValue(data.get(0).getVerifiedPostCountWithAreaOfficer()));
                                tvPostVisited.set(getStringValue(data.get(0).getAllPostsVisited()));
                                setIsLoading(false);
                            } catch (Exception e) {
                                showToast("Oops! Completed SPI data invalid");
                                setIsLoading(false);
                            }
                        }
                    }
                }, throwable -> {
                }));
    }

    private String getStringValue(int index) {
        switch (index) {
            case 1:
                return "YES";
            case 2:
                return "NO";
            case 3:
                return "NA";
            default:
                return "";
        }
    }

}
