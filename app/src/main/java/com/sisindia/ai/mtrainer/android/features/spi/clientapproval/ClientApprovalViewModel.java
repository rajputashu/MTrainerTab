package com.sisindia.ai.mtrainer.android.features.spi.clientapproval;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.droidcommons.preference.Prefs;
import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.db.MtrainerDataBase;
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiStatusRequest;
import com.sisindia.ai.mtrainer.android.models.BaseApiResponse;
import com.sisindia.ai.mtrainer.android.models.ClientApprovalRequest;
import com.sisindia.ai.mtrainer.android.models.ClientApprovalResponse;
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_MOUNTED_SPI_SCREEN;
import static com.sisindia.ai.mtrainer.android.constants.NavigationConstants.OPEN_PRINTING_SPI_SCREEN;
import static com.sisindia.ai.mtrainer.android.rest.RestConstants.SUCCESS_RESPONSE;

public class ClientApprovalViewModel extends MTrainerViewModel {
    public ObservableField<String> status= new ObservableField<>();

    public ObservableField<String> customer= new ObservableField<>();
    public ObservableField<String> customerid= new ObservableField<>();
    public ObservableField<String> type= new ObservableField<>();
    public ObservableField<String> currentstatus= new ObservableField<>("pending");
    @Inject
    DashBoardApi dashBoardApi;
    MtrainerDataBase dataBase;
    public  String done="na";

    @Inject
    public ClientApprovalViewModel(@NonNull Application application) {
        super(application);
      dataBase= MtrainerDataBase.getDatabase(getApplication());
        customer.set(Prefs.getString(PrefsConstants.SPI_CUSTOMER));
        customerid.set(Prefs.getString(PrefsConstants.SPI_UNIT_CODE));
        type.set(Prefs.getString(PrefsConstants.SPI_TYPE));
        status.set(Prefs.getString(PrefsConstants.SPI_STATUS));
    }


   public  void ClientApprovalDetails(){
        // Prefs.getInt(PrefsConstants.SPI_ID)
       dataBase.getMountedDao().flushMountedTable()
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe();
       setIsLoading(true);
        addDisposable(dashBoardApi.getClientApproval(new ClientApprovalRequest( Prefs.getInt(PrefsConstants.SPI_ID)
               ))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onClientApprovalSuccess, this::onApiError));
    }

    private void onClientApprovalSuccess(ClientApprovalResponse response) {
        setIsLoading(false);
        if (response.statusCode == SUCCESS_RESPONSE) {

            dataBase.getClientApprovalDao().insertClientApprovalData(response.clientApprovalStatuses)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();

                 setIsLoading(false);
            if(!response.clientApprovalStatuses.isEmpty()){
                currentstatus.set(response.clientApprovalStatuses.get(0).status+" "+response.clientApprovalStatuses.get(0).Date);
                done=response.clientApprovalStatuses.get(0).status;
            }
            else {
                showToast("Not found any rota ");
            }
        }
    }

    public void submitClientaprroval(View view){
       // message.what = OPEN_PRINTING_SPI_SCREEN;
       // liveData.postValue(message);

        if(done.equals("Done")){
            message.what = OPEN_PRINTING_SPI_SCREEN;
            liveData.postValue(message);
        }
        else {
            showToast("Client Approval not Done");
        }

    }


    void getDraftStatus(SpiStatusRequest request) {
        addDisposable(dashBoardApi.getSpiStatus(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSpistatusSuccess, this::onApiError));
    }

    private void onSpistatusSuccess(BaseApiResponse response) {
        if(response.statusCode==SUCCESS_RESPONSE){

        }
    }


}
