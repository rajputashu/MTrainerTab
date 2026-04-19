package com.sisindia.ai.mtrainer.android.features.server;


import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Log {
    public static enum _LogLevel {
        NORAML,ACTIVATE_DETAILS,UPGRADE_DETAILS,RENEW_DETAILS,LICENSE_DETAILS,SERVER_DETAILS,GENERAL_DETAILS,LIC_DATA_DEATILS
    }

    //public static _LogLevel[] logLevel={_LogLevel.NORAML,_LogLevel.LIC_DATA_DEATILS};
    public static List<_LogLevel> logLevel = new ArrayList<_LogLevel>();
    public static void Write(String Contents, _LogLevel LogLevel)
    {

        if(logLevel.contains(LogLevel))
        {
            try
            {
                String filepath = Environment.getExternalStorageDirectory() + "/";
                File File1 = new File(filepath);
                try {
                    FileWriter out = new FileWriter(new File(File1, "Licensing-Service.txt"),true);
                    out.write(Contents+"\n");
                    out.flush();
                    out.close();
                }
                catch (Exception e) {
                    // TODO: handle exception
                }
            }
            catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

}