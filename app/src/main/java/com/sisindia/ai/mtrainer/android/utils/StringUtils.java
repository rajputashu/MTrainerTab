package com.sisindia.ai.mtrainer.android.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringUtils {
    private static final String TAG = "StringUtils";
    public static int getCount(String string, String c) {
        return string.trim().length() - string.replace(c, "").trim().length();
    }

    public static boolean isEmailValid(String email) {
        boolean result = false;
        Log.d(TAG, "isEmailValid: " + email);
        if(email.contains("@"))
            result = true;
        if(email.contains(","))
            result = false;
        if(email.trim().contains(" "))
            result = false;
        if(StringUtils.getCount(email, "@") != 1)
            result = false;
        return result;
    }

    public static String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ENGLISH);
        return sdf.format(new Date());
    }

    public static String getEmployeeCode(String dirtyCode) {
        return dirtyCode.substring(0, dirtyCode.indexOf("_")) ;
    }
}
