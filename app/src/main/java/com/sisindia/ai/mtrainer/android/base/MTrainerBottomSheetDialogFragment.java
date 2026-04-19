package com.sisindia.ai.mtrainer.android.base;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.droidcommons.base.BaseBottomSheetDialogFragment;
import com.droidcommons.base.SingleLiveEvent;
import com.droidcommons.dagger.bottomsheet.AndroidBottomSheetDialogFragmentInjection;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.Logger;

import javax.inject.Inject;

public abstract class MTrainerBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {
    final protected Logger logger = new Logger(this.getClass().getSimpleName());
    @Inject
    protected ViewModelProvider.Factory viewModelFactory;

    @Inject
    protected SingleLiveEvent<Message> liveData;

    @Override
    public void onAttach(@NonNull Context context) {
        AndroidBottomSheetDialogFragmentInjection.inject(this);
        super.onAttach(context);
    }

    protected AndroidViewModel getAndroidViewModel(Class type) {
        return (AndroidViewModel) new ViewModelProvider(this, viewModelFactory).get(type);
    }

    protected ViewDataBinding bindFragmentView(@LayoutRes int layoutResource, LayoutInflater inflater, ViewGroup container) {
        return DataBindingUtil.inflate(inflater, layoutResource, container, false);
    }
}
