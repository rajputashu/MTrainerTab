package com.sisindia.ai.mtrainer.android;

import android.app.Application;
import android.content.ContextWrapper;
import android.provider.Settings;

import androidx.work.Worker;

import com.droidcommons.base.BaseApplication;
import com.droidcommons.dagger.worker.HasWorkerInjector;
import com.droidcommons.preference.Prefs;
import com.facebook.stetho.Stetho;
import com.google.firebase.FirebaseApp;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.sisindia.ai.mtrainer.android.commons.remotelogs.DeviceDetails;

import com.sisindia.ai.mtrainer.android.constants.PrefsConstants;
import com.sisindia.ai.mtrainer.android.di.component.DaggerMTrainerApplicationComponent;
import com.sisindia.ai.mtrainer.android.di.component.MTrainerApplicationComponent;
//import com.tspoon.traceur.Traceur;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerApplication;
import timber.log.Timber;

public class MTrainerApplication extends BaseApplication implements HasWorkerInjector {

    @Inject
    DispatchingAndroidInjector<Worker> workerDispatchingAndroidInjector;

    private Application application = null;

    @Override
    protected void setUpApplicationClass() {
        application = this;

        AndroidThreeTen.init(this);
        FirebaseApp.initializeApp(this);

    }

    @Override
    protected void initDebugLibs() {
      /*  if (!BuildConfig.DEBUG)
            return;*/

        Timber.plant(new Timber.DebugTree());
//        Traceur.enableLogging();
        Stetho.initializeWithDefaults(this);

    }

    @Override
    protected void initSharedPrefs() {
        new Prefs.Builder().setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(BuildConfig.APPLICATION_ID)
                .setUseDefaultSharedPreference(true)
                .build();
    }

    @Override
    protected void clearSharedPrefs() {
        if (Prefs.getBoolean(PrefsConstants.CLEAR_PREFS, true)) {
            Timber.e("Clear Preference Called from Application...!!!");
            Prefs.clear();
            Prefs.putBoolean(PrefsConstants.CLEAR_PREFS, false);
        }
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        MTrainerApplicationComponent component = DaggerMTrainerApplicationComponent.builder().application(this).build();
        component.inject(this);
        return component;
    }

    @Override
    public AndroidInjector<Worker> workerInjector() {
        return workerDispatchingAndroidInjector;
    }
}
