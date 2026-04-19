package com.sisindia.ai.mtrainer.android.base;

import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.sisindia.ai.mtrainer.android.commons.remotelogs.Logger;

import dagger.android.DaggerService;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class MTrainerBaseService extends DaggerService {
    final protected Logger logger = new Logger(this.getClass().getSimpleName());

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    protected void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    @Override
    public void onDestroy() {
        if (compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable.clear();
        }
        Timber.e("Service Destroyed");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
