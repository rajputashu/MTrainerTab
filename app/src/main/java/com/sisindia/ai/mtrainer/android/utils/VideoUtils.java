package com.sisindia.ai.mtrainer.android.utils;

import android.content.Context;
import android.content.pm.PackageManager;

public class VideoUtils {
    public static boolean hasCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT)) {
            return true;
        } else {
            return false;
        }
    }
}
