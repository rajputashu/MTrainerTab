package com.sisindia.ai.mtrainer.android.features.server;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;

import com.sisindia.ai.mtrainer.android.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

//not using this class-this is the reference class for starting decryption server
@SuppressLint("Registered")
public class DecryptionServer extends Activity {
    private ToggleButton mToggleButton;
    @SuppressLint("StaticFieldLeak")
    private static ScrollView mScroll;
    //    WebView webView;
    String port;
    private String documentRoot;
    public static String EncryptedFormat = ".htm,.html,.jpg,.jpeg,.bmp,.gif,.png,.swf,.css,.js,.PNG,.xsd,.flv,.mp4,.mp3,.mpg,.mpeg,.dat,.wmv,.gif,.xsd,.avi,.unity3d,.pdf";// ".htm,.html,.jpg,.jpeg,.bmp,.gif,.png,.swf,.css,.js,.PNG,.xml,.xsd,.flv";

    private String lastMessage = "";

    private ServerService mBoundService;

    @SuppressLint("HandlerLeak")
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            generateNoteOnSD(getApplicationContext(), b.getString("msg"));
            log(b.getString("msg"));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpsconfig);
        documentRoot = getDocRoot();
        port = "8082";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(documentRoot + "index.html"));
            bufferedReader.read();
            bufferedReader.close();
        } catch (Exception e) {
        }

        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(View arg0) {
                if (mToggleButton.isChecked()) {
                    startServer(mHandler, documentRoot, Integer.valueOf(port));
                    File file = new File(documentRoot);
                    List<File> fileList = getListFiles(file);

                } else {
                    stopServer();
                }
            }
        });
        doBindService();
        //	startServer(mHandler,documentRoot, Integer.parseInt(port));
    }

    /*@TargetApi(Build.VERSION_CODES.FROYO)
    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {

            files = assetManager.list("");
        } catch (IOException e) {
            /// Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                if (!(new File(documentRoot)).exists()) {
                    (new File(documentRoot)).mkdir();
                    File outFile = new File(documentRoot, filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                } else {
                    File outFile = new File(documentRoot, filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                }
            } catch (IOException e) {
                //Log.e("tag", "Failed to copy asset file: " + filename, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }*/

    public void generateNoteOnSD(Context context, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Decryption");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "licencse.txt");
            if (gpxfile.exists()) {
                FileWriter writer = new FileWriter(gpxfile);
                writer.append(sBody);
                writer.append("\n");
                writer.flush();
                writer.close();
            } else {
                FileWriter writer = new FileWriter(gpxfile);
                writer.append(sBody);
                writer.append("\n");
                writer.flush();
                writer.close();
            }

            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<File> getListFiles(File parentDir) {
        List<File> inFiles = new ArrayList<File>();
        Queue<File> files = new LinkedList<File>();
        files.addAll(Arrays.asList(parentDir.listFiles()));
        FlatFileIO fio = new FlatFileIO();
        while (!files.isEmpty()) {
            File file = files.remove();
            String extension = file.getPath().substring(file.getPath().lastIndexOf("."));
            if (file.isDirectory()) {
                files.addAll(Arrays.asList(file.listFiles()));
            } else if (file.getName().endsWith(String.valueOf(EncryptedFormat.toLowerCase().contains(extension.toLowerCase())))) {
                inFiles.add(file);
            }
        }
        return inFiles;
    }

    /*private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }*/

    public static void log(String s) {
        //mLog.append(s + "\n");
        mScroll.fullScroll(ScrollView.FOCUS_DOWN);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startServer(Handler handler, String documentRoot, int port) {
        if (mBoundService == null) {
            Toast.makeText(DecryptionServer.this, "Service not connected", Toast.LENGTH_SHORT).show();
        } else {
            ///    mBoundService.startServer(handler, documentRoot, port);
        }
    }

    private void stopServer() {
        if (mBoundService == null) {
            Toast.makeText(DecryptionServer.this, "Service not connected", Toast.LENGTH_SHORT).show();
        } else {
            mBoundService.stopServer();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((ServerService.LocalBinder) service).getService();
            Toast.makeText(DecryptionServer.this, "Service connected", Toast.LENGTH_SHORT).show();
            mBoundService.updateNotification(lastMessage);

            mToggleButton.setChecked(mBoundService.isRunning());
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
            Toast.makeText(DecryptionServer.this, "Service disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    private void doUnbindService() {
        if (mBoundService != null) {
            unbindService(mConnection);
        }
    }

    private void doBindService() {
        bindService(new Intent(DecryptionServer.this, ServerService.class), mConnection, Context.BIND_AUTO_CREATE);
        //startServer(mHandler,documentRoot, Integer.parseInt(port));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        stopServer();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private String getDocRoot() {
        //return "/assets/";
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/androidwebserver/Course1/";
    }
}
