package com.sisindia.ai.mtrainer.android.features.syncadapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.internal.Constants;
import com.sisindia.ai.mtrainer.android.constants.Constant;

import timber.log.Timber;

public class SyncAdapterInitialization {

    Account mAccount;
    // A content resolver for accessing the provider
    private ContentResolver mResolver;
    private Context mcontext;
    private static final String LOG = "SyncAdapterInItLOG";

    public SyncAdapterInitialization(Context mcontext) {
        this.mcontext = mcontext;
        init();
    }

    private void init() {
        mAccount = CreateSyncAccount(mcontext);
        mResolver = mcontext.getContentResolver();
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(Constant.ACCOUNT, Constant.ACCOUNT_TYPE);
        // Get an instance of the Android account manager
//        AccountManager accountManager =(AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        AccountManager accountManager = AccountManager.get(context);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
        } else {
            //check condition of failure, if
        }

        return newAccount;
    }

    /**
     * PASSING BUNDEL IS MANDATORY
     * IF DONT HAVE ANYTHING TO PASS IN BUNDEL JUST CREATE THE INSTANCE OF THE BUNDEL AND PASS
     * IF PASS BUNDEL AS NULL WILL THROW EXCEPTION
     *
     * @param bundle
     */

    @SuppressLint("TimberArgCount")
    public void startForceSyncing(Bundle bundle) {
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        Account account = new Account(Constant.ACCOUNT, Constant.ACCOUNT_TYPE);
        ContentResolver.requestSync(account, Constant.AUTHORITY, bundle);
//        ContentResolver.setIsSyncable(account, Constants.AUTHORITY, 1);
//        ContentResolver.setSyncAutomatically(account, Constants.AUTHORITY, true);
        Timber.d(LOG, "startForceSyncing startForceSyncing ");
    }

    /**
     * PASSING BUNDEL IS MANDATORY
     * IF DONT HAVE ANYTHING TO PASS IN BUNDEL JUST CREATE THE INSTANCE OF THE BUNDEL AND PASS
     * IF PASS BUNDEL AS NULL WILL THROW EXCEPTION
     *
     * @param bundle
     */
    public void scheduleSync(Bundle bundle) {
        Account account = new Account(Constant.ACCOUNT, Constant.ACCOUNT_TYPE);
        ContentResolver.cancelSync(account, Constant.ACCOUNT_TYPE);
        // ContentResolver.getPeriodicSyncs(account,AUTHORITY);
        // Inform the system that this account supports sync

        ContentResolver.setIsSyncable(account, Constant.AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(account, Constant.AUTHORITY, true);
        ContentResolver.addPeriodicSync(account, Constant.AUTHORITY, bundle, 60);
    }

    private void refreshSyncStatus() {
        String status;
        if (ContentResolver.isSyncActive(mAccount, Constant.AUTHORITY))
            status = "Status: Syncing..";
        else if (ContentResolver.isSyncPending(mAccount, Constant.AUTHORITY))
            status = "Status: Pending..";
        else
            status = "Status: Idle";
    }
}
