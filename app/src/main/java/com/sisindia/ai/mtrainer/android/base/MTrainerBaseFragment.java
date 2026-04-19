package com.sisindia.ai.mtrainer.android.base;

import android.os.Message;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.droidcommons.base.BaseFragment;
import com.droidcommons.base.SingleLiveEvent;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.Logger;

import javax.inject.Inject;

public abstract class MTrainerBaseFragment extends BaseFragment {
    final protected Logger logger = new Logger(this.getClass().getSimpleName());

    @Inject
    protected ViewModelProvider.Factory viewModelFactory;

    @Inject
    protected SingleLiveEvent<Message> liveData;

    protected AndroidViewModel getAndroidViewModel(Class type) {
        return (AndroidViewModel) new ViewModelProvider(this, viewModelFactory).get(type);
    }

    /*protected AndroidViewModel getParentFragmentViewModel(BaseFragment parent, Class type) {
        return (AndroidViewModel) new ViewModelProvider(parent, viewModelFactory).get(type);
    }

    protected AndroidViewModel getActivityViewModel(BaseActivity activity, Class type) {
        return (AndroidViewModel) new ViewModelProvider(activity, viewModelFactory).get(type);
    }*/

}
