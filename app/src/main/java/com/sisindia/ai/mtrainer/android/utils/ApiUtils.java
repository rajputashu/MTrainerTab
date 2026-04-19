package com.sisindia.ai.mtrainer.android.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ApiUtils {
    public static boolean canCallApi(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            long currentTime = System.currentTimeMillis();
            String currentDate = sdf.format(new Date(currentTime));
            long currentDateTime = sdf.parse(currentDate).getTime();
            long lastCalledDate = sdf.parse(date).getTime();
            return currentDateTime != lastCalledDate;
        } catch (ParseException  | NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }
}