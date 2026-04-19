package com.sisindia.ai.mtrainer.android.commons.remotelogs

import android.os.Build
import com.droidcommons.preference.Prefs
import com.sisindia.ai.mtrainer.android.BuildConfig
import com.sisindia.ai.mtrainer.android.constants.PrefsConstants

data class DeviceDetails @JvmOverloads constructor(
    val companyId: String = Prefs.getString(PrefsConstants.COMPANY_ID, ""),
    val trainerId: Int = Prefs.getInt(PrefsConstants.BASE_TRAINER_ID, 0),
    val trainerName : String = Prefs.getString(PrefsConstants.EMPPLOYEE_NAME, ""),
    val role : String = Prefs.getString(PrefsConstants.ROLE, ""),
    val osVersion: String = Build.VERSION.RELEASE,
    val manufacturer: String = Build.MANUFACTURER,
    val brand: String = Build.BRAND,
    val device: String = Build.DEVICE,
    val model: String = Build.MODEL,
    val appVersionName: String = BuildConfig.VERSION_NAME,
    val appVersionCode: Int = BuildConfig.VERSION_CODE
)