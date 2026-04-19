package com.sisindia.ai.mtrainer.android.utils;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.WIFI_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;

import com.sisindia.ai.mtrainer.android.commons.FolderNames;
import com.sisindia.ai.mtrainer.android.constants.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Utils {

    private final Context context;
    private SharedPreferences sharedPreferences;

    public Utils(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void generateNoteOnSD(Context context, String sBody) {
        try {
//            File root = new File(Environment.getExternalStorageDirectory(), "Encryption");
            String rootPath = FileUtils.getRootPathV2(context, FolderNames.Encryption);
            if (FileUtils.createOrExistsDir(rootPath)) {
                File root = FileUtils.getFileByPath(rootPath);
                File gpxfile = new File(root, "licencse.txt");
                if (gpxfile.exists()) {
                    FileOutputStream fileInput = new FileOutputStream(gpxfile, true);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileInput);
                    outputStreamWriter.write(sBody);
                    outputStreamWriter.write("\n");
                    outputStreamWriter.flush();
                    outputStreamWriter.close();
                } else {
                    FileWriter writer = new FileWriter(gpxfile);
                    writer.append(sBody);
                    writer.append("\n");
                    writer.flush();
                    writer.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDocRoot() {
        StoragePath storagePath;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            storagePath = new StoragePath(context.getExternalFilesDirs(null));
        } else {
            storagePath = new StoragePath();
        }

        String[] storages;
        try {
            storages = storagePath.getDeviceStorages();
            for (int i = 0; i < storages.length; i++) {
                Log.e("UtilsStoragePath" + i, storages[i]);
            }
            if (storages.length > 1) {
                Log.i("UtilsStoragePath", storages[1] + "/mtrain_enc/");
                return storages[1] + "/mtrain_enc/";
            } else {
                Log.i("UtilsStoragePath", storages[0] + "/Download/mtrain_enc/");
                return storages[0] + "/Download/mtrain_enc/";
            }
        } catch (Exception e) {
            return getExternalSdCardPath() + "/mtrain_enc/";
        }
    }

    public static String getExternalSdCardPath() {
        String path = null;

        File sdCardFile;
        List<String> sdCardPossiblePath = Arrays.asList("external_sd", "ext_sd", "external", "extSdCard");

        for (String sdPath : sdCardPossiblePath) {
            File file = new File("/mnt/", sdPath);

            if (file.isDirectory() && file.canWrite()) {
                path = file.getAbsolutePath();

                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
                File testWritable = new File(path, "test_" + timeStamp);

                if (testWritable.mkdirs()) {
                    testWritable.delete();
                } else {
                    path = null;
                }
            }
        }

        if (path != null) {
            sdCardFile = new File(path);
        } else {
            sdCardFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        }

        return sdCardFile.getAbsolutePath();
    }

    /*public static String getExternalSdCardPathV2() {
        List<String> sdCardPossiblePaths = List.of("external_sd", "ext_sd", "external", "extSdCard");
        String sdCardPath = findSdCardPath(sdCardPossiblePaths);

        if (sdCardPath != null) {
            return sdCardPath;
        } else {
            // Fallback to the Downloads directory
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        }
    }*/

    /*private static String findSdCardPath(List<String> sdCardPossiblePaths) {
        for (String sdPath : sdCardPossiblePaths) {
            File file = new File("/mnt/", sdPath);
            if (file.isDirectory() && file.canWrite()) {
                // Validate if we can write to the directory
                File testWritable = new File(file, "test_temp");
                if (testWritable.mkdirs()) {
                    testWritable.delete();
                    return file.getAbsolutePath();
                } else {
                    Log.d("StorageManager", "Cannot write to: " + file.getAbsolutePath());
                }
            }
        }
        return null;
    }*/


    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public String getIpAddressAndUrl() {
        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        assert wm != null;
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return "http://" + Constant.IP_ADDRESS + ":" + Constant.PORT + "/";
    }

    public static String timeConversion(int totalSeconds) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds /
                SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        return minutes + ":" + seconds + " mins";
    }

    private SharedPreferences getSharedPref() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences;
    }

    public void saveString(String key, String value) {
        if (sharedPreferences == null)
            getSharedPref();
        sharedPreferences.edit().putString(key, value).apply();
    }

    public void saveBoolean(String key, boolean value) {
        if (sharedPreferences == null)
            getSharedPref();
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key) {
        if (sharedPreferences == null)
            getSharedPref();
        return sharedPreferences.getBoolean(key, false);
    }

    public String getString(String key) {
        if (sharedPreferences == null)
            getSharedPref();
        return sharedPreferences.getString(key, "");
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    public void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("OK",
                (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }
}
