package com.sisindia.ai.mtrainer.android.features.feedback;

import android.app.Application;

import androidx.annotation.NonNull;

import com.sisindia.ai.mtrainer.android.base.MTrainerViewModel;
import com.sisindia.ai.mtrainer.android.constants.NavigationConstants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ClientNAViewModel extends MTrainerViewModel {

    public ClientNARecylerAdapter clientNARecylerAdapter = new ClientNARecylerAdapter();

    @Inject
    public ClientNAViewModel(@NonNull Application application) {
        super(application);
    }

    public void initViewModel() {
        List<String> list = new ArrayList<String>();
        list.add("Client is on leave");
        list.add("Client is busy");
        list.add("Client denied to meet");
        clientNARecylerAdapter.clearAndSetItems(list);
    }

    String selectedNAOption() {
        return clientNARecylerAdapter.selectedNAOption();
    }

    void canFinishActivity() {
        message.what = NavigationConstants.SAVE_FEEDBACK_DATA;
        liveData.postValue(message);
    }
}
