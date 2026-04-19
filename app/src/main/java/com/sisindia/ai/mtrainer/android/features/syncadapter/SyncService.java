package com.sisindia.ai.mtrainer.android.features.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/*import com.sisindia.ai.mtrainer.android.di.component.DaggerSyncComponent;
import com.sisindia.ai.mtrainer.android.di.component.SyncComponent;*/
import com.sisindia.ai.mtrainer.android.rest.DashBoardApi;

import javax.inject.Inject;

import dagger.android.DaggerService;

public class SyncService extends Service {
    // Object to use as a thread-safe lock
    private static final Object sSyncAdapterLock = new Object();
    // Storage for an instance of the sync adapter
    private SyncAdapter sSyncAdapter = null;

    @Inject
    DashBoardApi dashBoardApi;

    /*
     * Instantiate the sync adapter object.
     */
    @Override
    public void onCreate() {
        /*
         * Create the sync adapter as a singleton.
         * Set the sync adapter as syncable
         * Disallow parallel syncs
         */
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    /**
     * Return an object that allows the system to invoke
     * the sync adapter.
     */
    @Override
    public IBinder onBind(Intent intent) {
        /*
         * Get the object that allows external processes
         * to call onPerformSync(). The object is created
         * in the base class code when the SyncAdapter
         * constructors call super()
         */
        return sSyncAdapter.getSyncAdapterBinder();
    }
}