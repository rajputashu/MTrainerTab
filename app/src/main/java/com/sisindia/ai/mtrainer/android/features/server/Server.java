package com.sisindia.ai.mtrainer.android.features.server;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sisindia.ai.mtrainer.android.constants.Constant;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedList;

//server class
public class Server extends Thread {
    private ServerSocket listener = null;
    private boolean running ;
    private String documentRoot;
    private String duration;
    private static Handler mHandler;
    private Context context;
    public static String _ActivationStatus = "NOT_SET";
    public static String LicenseFilePath = "";
    public static String ConfigFilePath = "";
    public static String _SmsStatus = "NOT_SET";
    public static String _DownloadStatus = "NOT_SET";
    public static String _RenewStatus = "NOT_SET";
    public static String _ApplyStatus = "NOT_SET";

    public static Boolean isValidCall = false;
    public static String PhoneNumber = "";

    public static String ftpServer = "";
    public static String ftpUserName = "";
    public static String ftpPassword = "";

    public static boolean isUpgradeAvailable = false;
    public static String upgradetoVersion = "";
    public static String ProjectName = "";

    public static String ContentPath = "";
    public static String ProjectId = "";
    public static String CurrentVersion = "";
    public static String LicenseId = "";
    public static String ExpiryDate = "";

    public static String TabletId = "NOT_SET";
    public static String SdCardId = "NOT_SET";

    public static boolean isValidLicense = false;

    public static boolean isLicenseActivated = false;
    public static boolean isForceUpgrade = false;
    public static boolean isServerStarted = false;
    public static boolean isWorkaroundDone = true;

    public static boolean isDeleteOnExpiry = false;
    public static int BufferDays = 0;
    public static int CurrentLicenseCount = 0;

    public static String rootContentFolder = "Content";
    public static RandomAccessFile inputBuffer;
    @SuppressLint("StaticFieldLeak")
    public static XMLParser objFileStruct = new XMLParser();

    @SuppressLint("StaticFieldLeak")
    public static XMLParser objLicenseStruct = new XMLParser();

    public static String _getActivationStatus() {
        return _ActivationStatus;
    }

    public static void _setActivationStatus(String status) {
        _ActivationStatus = status;
    }

    public static String _getSmsStatus() {
        return _SmsStatus;
    }

    public static void _setSmsStatus(String status) {
        _SmsStatus = status;
    }

    public static String _getDownloadStatus() {
        return _DownloadStatus;
    }

    public static void _setDownloadStatus(String status) {
        _DownloadStatus = status;
    }

    public static String _getRenewStatus() {
        return _RenewStatus;
    }

    public static void _setRenewStatus(String status) {
        _RenewStatus = status;
    }

    public static String _getApplyStatus() {
        return _ApplyStatus;
    }

    public static void _setApplyStatus(String status) {
        _ApplyStatus = status;
    }

    public static LinkedList<Socket> clientList = new LinkedList<>();

    public Server(Handler handler, String documentRoot, String ip, int port, Context context, boolean isRunning) throws IOException {
        super();
        this.documentRoot = documentRoot;
        this.context = context;
        this.running = isRunning;
        Server.mHandler = handler;
        InetAddress ipadr = InetAddress.getByName(Constant.IP_ADDRESS);
        listener = new ServerSocket(port, 0 , ipadr);
        listener.setReuseAddress(true);
        listener.setSoTimeout(86400000);


    }

    @Override
    public void run() {
        Log.i("socket trainner running", "running ="+running);
        while (running) {
            Socket client = null;
            try {
                clearingSockteList();
                send("Waiting for connections");
                Log.i("socket trainner", "Waiting for connections");

                if(!listener.isClosed()){
                    client = listener.accept();

                    // Toast.makeText(MTrainerApplication.getAppContext(),"Socket connected",Toast.LENGTH_SHORT).show();

                    new ServerHandler(documentRoot, context, client).start();
                    send("New connection from " + client.getInetAddress().toString());
                    Log.e("socket trainner", "BUFFER " + client.getReceiveBufferSize());
                    Log.e("socket trainner", "New connection from " + client.getInetAddress().toString());

                    clientList.add(client);
                    client.setKeepAlive(true);
                    client.setSoTimeout(86400000);// 1 day



                    // clientList.add(client);
                    // client.setKeepAlive(true);

                    //Toast.makeText(MTrainerApplication.getAppContext(),"Socket "+clientList.size(),Toast.LENGTH_SHORT).show();
                    //    client.setSoTimeout(86400000); // 1 day
                    //   client.setSoTimeout(100000);
                    Log.e("socket trainner clientList", "clientList = " + clientList.size());
                }
                else{
                    Log.i("socket trainner", "SOCKET ");

                }
            } catch (SocketTimeoutException e) {
                Log.d("sockettimeout",e.getMessage());
                // Toast.makeText(MTrainerApplication.getAppContext(), "Socket timeout", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                //    if (client.isConnected()) {
                //      stopServer();
                //    }
                // reconnectingToSocket();
                //reconnect
//                Intent i = new Intent(VideoLandingActivity.SERVER_SOCKET_BROADCAST_ACTION);
//                context.sendBroadcast(i);
//                send(e.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
                // reconnectingToSocket();
                // Toast.makeText(MTrainerApplication.getAppContext(), "Socket IOException", Toast.LENGTH_SHORT).show();
                send(e.getMessage());
                //Log.e("Webserver", e.getMessage());
            }
        }
    }



    public void stopServer() {
        running = false;
        try {
            listener.close();
            // Toast.makeText(MTrainerApplication.getAppContext(),"Socket close",Toast.LENGTH_SHORT).show();

            //  Log.e("socket trainner","stopServer close") ;

        } catch (IOException e) {
            send(e.getMessage());
            // Log.e("Webserver", e.getMessage());
        }
    }

    public synchronized static void remove(Socket s) {
        send("Closing connection: " + s.getInetAddress().toString());
        Log.i("socket trainner","Closing connection: " + s.getInetAddress().toString()) ;

        Log.e("closing","thread closed");
        clientList.remove(s);
        mHandler.removeCallbacksAndMessages(null);
    }

    private static void send(String s) {
        if (s != null) {
            Message msg = new Message();
            Bundle b = new Bundle();
            b.putString("msg", s);
            msg.setData(b);
            mHandler.sendMessage(msg);
        }
    }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private void clearingSockteList(){
        if(clientList.size()!=0){
            for(Socket socket : clientList){
                if(socket.isConnected()){

                    remove(socket);

                }
            }
        }
    }

    private void reconnectingToSocket()
    {
        InetAddress ipadr = null;
        try {
            ipadr = InetAddress.getByName(Constant.IP_ADDRESS);
            listener = new ServerSocket(Integer.valueOf(Constant.PORT), 0, ipadr);
            listener.setReuseAddress(true);

            run();

        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.i("socket trainner", "UnknownHostException" );

        } catch (SocketException e) {
            e.printStackTrace();
            Log.i("socket trainner", "SocketException" );

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("socket trainner", "IOException" );

        }

    }



}
