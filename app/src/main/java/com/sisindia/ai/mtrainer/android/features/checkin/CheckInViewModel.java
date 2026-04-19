package com.sisindia.ai.mtrainer.android.features.checkin;

import android.app.Application;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckInViewModel extends MTrainerViewModel {

    public ObservableField<RadioGroup> radiogroup = new ObservableField<>();
   // private final ObservableInt radiogroup = new ObservableInt();

    @Inject
    public CheckInViewModel(@NonNull Application application) {
        super(application);
    }

/*
    public void susubmitCheckin(View view){

        if(radiogroup.getCheckedRadioButtonId()!=-1) {

            if (utils.isConnectingToInternet()) {
                apiClient.checkin(user, String.valueOf(gpsTrackerService.getLatitude()), String.valueOf(gpsTrackerService.getLongitude()),
                        val, other_isue.getText().toString()).enqueue(new Callback<DataResponse>() {
                    @Override
                    public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                        if (response.code() == 200) {
                            finish();
                            Toast.makeText(CheckInActivity.this, "You have successfully Checkd in !", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<DataResponse> call, Throwable t) {

                    }
                });
            }else {
                Toast.makeText(CheckInActivity.this, "please connect to internet", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Please Select any one !", Toast.LENGTH_SHORT).show();
        }


    }*/
}
