package com.sisindia.ai.mtrainer.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;

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
import java.util.concurrent.TimeUnit;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.WIFI_SERVICE;

public class Utils {


    private Context context;
    private ProgressDialog progressDialog = null;
    private static Bitmap signatureBitmap = null;
    private static Utils utils = null;
    private SharedPreferences sharedPreferences;
    public Utils(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public void takeScreenshot(Activity context) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
            // create bitmap screen capture
            View v1 = context.getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            openScreenshot(imageFile,context);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }
    private void openScreenshot(File imageFile,Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        context.startActivity(intent);
    }
    public static void generateNoteOnSD(Context context, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Encryption");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "licencse.txt");
            if (gpxfile.exists()) {
//				PrintWriter printWriter = new PrintWriter(gpxfile);
//				printWriter.print("");
//				printWriter.close();
                FileOutputStream fileinput = new FileOutputStream(gpxfile, true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileinput);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getDocRoot() {
        final String state = Environment.getExternalStorageState();
        Log.e("state",state);
//        if(state.equals("mounted"))
//        {
        StoragePath storagePath;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            storagePath = new StoragePath(context.getExternalFilesDirs(null));
        }else {
            storagePath = new StoragePath();
        }

        String[] storages;
        if(storagePath !=null)
        {
            try {
                storages = storagePath.getDeviceStorages();
                for(int i=0;i<storages.length;i++)
                {
                    Log.e("paths"+String.valueOf(i),storages[i]);
                }
                if(storages.length>1) {
                    Log.i("path ", storages[1]+"/Download/mtrain_enc/");
                    return  storages[1]+"/mtrain_enc/";
                }
                else {
                    Log.i("path ", storages[0]+"/Download/mtrain_enc/");
                    return  storages[0]+"/Download/mtrain_enc/";
                }


            }catch (Exception e)
            {
                return getExternalSdCardPath() + "/mtrain_enc/";
            }

        }
        else {
            return getExternalSdCardPath() + "/mtrain_enc/";
        }

    }

    public static String getExternalSdCardPath() {
        String path = null;

        File sdCardFile = null;
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
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    public String getIpAddressAndUrl()
    {
        WifiManager wm = (WifiManager) context.getSystemService(WIFI_SERVICE);
        assert wm != null;
        //getting ipaddreess
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        String tempurl = "http://" + Constant.IP_ADDRESS + ":" + Constant.PORT + "/";
        return  tempurl;
    }
    public static long convertToMilli(int seconds) {
        //String time = "07:02";

//        long min = Integer.parseInt(time.substring(0, 2));
        long sec = seconds;

        long t = sec;

        long result = TimeUnit.SECONDS.toMillis(t);
        return  result;

    }


    public static String timeConversion(int totalSeconds) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds /
                SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        //int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        return  String.valueOf(minutes) + ":" +  String.valueOf(seconds) + " mins";
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
                (ConnectivityManager)context.getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    public void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(
                "OK",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }
}
