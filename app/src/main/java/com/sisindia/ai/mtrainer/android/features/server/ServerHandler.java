package com.sisindia.ai.mtrainer.android.features.server;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.Socket;

class ServerHandler extends Thread {
    private final Socket toClient;
    private final String documentRoot;
    private final Context context;
    //  PrintStream out;
    DataOutputStream out;
    String range = "0", duration;
    public static String EncryptedFormat = ".htm,.ppt,.pot,.pps,.pptx,.potx,.ppsx,.html,.jpg,.jpeg,.bmp,.gif,.png,.swf,.css,.js,.PNG,.xsd,.flv,.mp4,.mp3,.mpg,.mpeg,.dat,.wmv,.gif,.xsd,.avi,.unity3d,.pdf,.xml";// ".htm,.html,.jpg,.jpeg,.bmp,.gif,.png,.swf,.css,.js,.PNG,.xml,.xsd,.flv";
    String dokument = "";

    //MTrainerDB mTrainerDB;
    @SuppressLint("HandlerLeak")

    public ServerHandler(String d, Context c, Socket s) {
        toClient = s;
        documentRoot = d;
        context = c;
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
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
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(toClient.getInputStream()));

            // Receive data
            while (true) {
                String s = in.readLine().trim();

                Log.d("readlinedata", s);

                if (s.equals("")) {
                    break;
                }
                if (s.substring(0, 3).equals("GET")) {
                    int leerstelle = s.indexOf(" HTTP/");
                    dokument = s.substring(5, leerstelle);
                    dokument = dokument.replaceAll("[/]+", "/");
                }
                if (s.contains("Range")) {
                    String[] split = s.split("Range");
                    String firstSubString = split[0];
                    String second = split[1].replace(": bytes=", "");
                    getValue(second);
                }
            }

        } catch (Exception e) {
            Log.e("error", e.getMessage());
            Server.remove(toClient);
            try {
                toClient.close();
            } catch (Exception ex) {
            }
        }

        Log.d("ServerHandler : run() ", dokument);
        showHtml(dokument);
    }

    public void getValue(String value) {
        range = value;
    }

    /*private void sendTasker(String taskName) {
//        if (TaskerIntent.testStatus(context).equals(TaskerIntent.Status.OK)) {
//            TaskerIntent i = new TaskerIntent(taskName);
//            context.sendBroadcast(i);

        send("Sent intent \"" + taskName + "\" to tasker.");
//        } else {
//            send("Could not sent intent \"" + taskName + "\" to tasker (" +
//                    TaskerIntent.testStatus(context) + ").");
//        }
    }*/

    /*@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void send(String text) {
        String header = getHeaderBase();
        //     String contentType = GetContentType(text);
        header = header.replace("%code%", "200 ok");
        header = header.replace("%length%", "" + text.length());
        try {
            out = new DataOutputStream(new BufferedOutputStream(toClient.getOutputStream()));
            out.writeBytes(header);
            out.writeBytes(text);
            out.flush();
            out.close();
            Server.remove(toClient);
            toClient.close();
        } catch (Exception ignored) {

        }
    }*/

    private void showHtml(String dokument) {
        // Standard-Doc
        String fileName = dokument.substring(dokument.lastIndexOf('/') + 1);

        Log.d("fewgfewgew", fileName);
        if (dokument.contains(".")) {
            String fileext = dokument.substring(dokument.lastIndexOf('.'));
            String contentType = GetContentType(fileext);
            // Don't allow directory traversal
            if (dokument.contains("..")) {
                dokument = "403.html";
            }

            dokument = documentRoot + dokument;
            dokument = dokument.replaceAll("[/]+", "/");

            if (dokument.charAt(dokument.length() - 1) == '/') {
                dokument = documentRoot + "404.html";
            }

            String header = getHeaderBase();
            // header = header.replace("%code%", "403 Forbidden");

            try {
                File f = new File(dokument);
                if (!f.exists()) {
                    header = getHeaderBase();
                    header = header.replace("%code%", "404 File not found");
                    dokument = "404.html";
                }
            } catch (Exception e) {
            }

            if (!dokument.equals(documentRoot + "403.html")) {
                if (fileext.equals(".mp4")) {
                    header = header.replace("%code%", "206 PARTIAL");
                } else {
                    Log.d("headercode", "200 OK");
                    header = header.replace("%code%", "200 OK");
                }
                //header = getHeaderBase().replace("%code%", "200 OK");
            }

            try {
                File f = new File(dokument);
                if (f.exists()) {
                    out = new DataOutputStream(new BufferedOutputStream(toClient.getOutputStream()));
                    int file_size = Integer.parseInt(String.valueOf(f.length()));

                    String decKey = fileName.substring(0, fileName.lastIndexOf('.')).toLowerCase() + "mtrain";

                    Log.d("fewgfewgew", decKey);
                    Log.d("deprecationkey", decKey);

                    int start = 0, end = 0, contentlength = file_size;
                    if (range.contains("-")) {

                        String[] split = range.split("-");
                        if (split.length > 1) {
                            start = Integer.parseInt(split[0]);
                            contentlength = file_size - start;
                            end = Integer.parseInt(split[1]);
                        } else {
                            start = Integer.parseInt(split[0]);
                            contentlength = file_size - start;
                            end = file_size;
                            range = start + "-" + (file_size - 1);
                        }
                        //end = Integer.parseInt(split[1]);
                    }

                    if (range.equals("0")) {
                        range = start + "-" + (file_size - 1);
                    }
                    //.flv,.mp4,.mp3,.mpg,.mpeg,.dat,.wmv,.gif,.xsd,.avi
                    if (fileext.equals(".mp4") || fileext.equals(".mp3") || fileext.equals(".mpg") || fileext.equals(".mpeg") || fileext.equals(".wmv") || fileext.equals(".flv") || fileext.equals(".avi")) {
                        header = header.replace("%length%", "" + contentlength);
                        header = header.replace("%contenttype%", "" + contentType);
                        header = header.replace("%code%", "206 Partial Content");
                        header = header.replace("%range%", "bytes " + range + "/" + file_size);
                    } else {
                        header = header.replace("%length%", "" + file_size);
                        header = header.replace("%code%", "200 OK");
                        header = header.replace("%contenttype%", "" + contentType);
                    }

                    out.writeBytes(header);
                    Log.e("headervalues", header);

                    //  new DecryptionThread(out, file_size, decKey, fileName, dokument, start,fileext).start();
                    FlatFileIO fio = new FlatFileIO();
                    if (EncryptedFormat.toLowerCase().contains(fileext.toLowerCase())) {
                        RandomAccessFile inputBuffer_local_1 = new RandomAccessFile(dokument, "r");
                        fio.WriteFileInChunk_After_Decryption1(start, inputBuffer_local_1, file_size, out, decKey);
                        inputBuffer_local_1.close();
                    } else {
                        RandomAccessFile inputBuffer_local_1 = new RandomAccessFile(dokument, "r");
                        fio.WriteFileInChunk_Original(start, inputBuffer_local_1, file_size, out);
                        inputBuffer_local_1.close();
                    }
                    // Send HTML-File (Ascii, not as a stream)
                } else {
                    header = getHeaderBase();
                    header = header.replace("%code%", "404 File not found");
                    header = header.replace("%length%", "" + "404 - File not Found".length());
                    header = header.replace("%contenttype%", "" + contentType);
                    out = new DataOutputStream(new BufferedOutputStream(toClient.getOutputStream()));
                    out.writeBytes(header);
                    out.writeBytes("404 - File not Found");
                    out.flush();
                }
                Server.remove(toClient);
                deleteCache(context);
                toClient.close();

            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    private String GetContentType(String Ext) {
        String varcontenttype = "text/html";
        Ext = Ext.toUpperCase();
        if (Ext.contains(".XML")) {
            varcontenttype = "text/xml";
        } else if (Ext.contains(".HTM")) {
            varcontenttype = "text/html";
        } else if (Ext.contains(".XSD")) {
            varcontenttype = "application/xml";
        } else if (Ext.contains(".HTML")) {
            varcontenttype = "text/html";
        } else if (Ext.contains(".JPG")) {
            varcontenttype = "image/jpeg";
        } else if (Ext.contains(".JPEG")) {
            varcontenttype = "image/jpeg";
        } else if (Ext.contains(".BMP")) {
            varcontenttype = "image/bmp";
        } else if (Ext.contains(".GIF")) {
            varcontenttype = "image/gif";
        } else if (Ext.contains(".SWF")) {
            varcontenttype = "application/x-shockwave-flash";
        } else if (Ext.contains(".FLV")) {
            varcontenttype = "application/octet-stream";
        } else if (Ext.contains(".PNG")) {
            varcontenttype = "image/png";
        } else if (Ext.contains(".PDF")) {
            varcontenttype = "application/pdf";
        } else if (Ext.contains(".DOC")) {
            varcontenttype = "application/msword";
        } else if (Ext.contains(".CSS")) {
            varcontenttype = "text/css";
        } else if (Ext.contains(".MP4")) {
            varcontenttype = "video/mp4";
        } else if (Ext.contains(".MP3")) {
            varcontenttype = "audio/mpeg";
        } else if (Ext.contains(".OGA")) {
            varcontenttype = "audio/ogg";
        } else if (Ext.contains(".OGG")) {
            varcontenttype = "audio/ogg";
        } else if (Ext.contains(".OGV")) {
            varcontenttype = "video/ogg";
        } else if (Ext.contains(".WEBM")) {
            varcontenttype = "video/webm";
        } else if (Ext.contains(".SVG")) {
            varcontenttype = "image/svg+xml";
        } else if (Ext.contains(".SVGZ")) {
            varcontenttype = "gzip";
        } else if (Ext.contains(".VTT")) {
            varcontenttype = "text/xml";
        } else if (Ext.contains(".EOT")) {
            varcontenttype = "application/vnd.ms-fontobject";
        } else if (Ext.contains(".TTF")) {
            varcontenttype = "font/truetype";
        } else if (Ext.contains(".OTF")) {
            varcontenttype = "font/opentype";
        } else if (Ext.contains(".WOFF")) {
            varcontenttype = "application/x-font-woff";
        } else if (Ext.contains(".ICO")) {
            varcontenttype = "image/x-icon";
        } else if (Ext.contains(".WEBP")) {
            varcontenttype = "image/webp";
        } else if (Ext.contains(".APPCACHE")) {
            varcontenttype = "text/cache-manifest";
        } else if (Ext.contains(".MANIFEST")) {
            varcontenttype = "text/cache-manifest";
        } else if (Ext.contains(".HTC")) {
            varcontenttype = "text/x-component";
        } else if (Ext.contains(".CRX")) {
            varcontenttype = "application/x-chrome-extension";
        } else if (Ext.contains(".XPI")) {
            varcontenttype = "application/x-xpinstall";
        } else if (Ext.contains(".JS")) {
            varcontenttype = "application/x-javascript";
        } else if (Ext.contains(".SAFARIEXTZ")) {
            varcontenttype = "application/octet-stream";
        } else {
            varcontenttype = "text/html";
        }
        return varcontenttype;
    }

    private String getHeaderBase() {
        return "HTTP/1.1 %code%\n" +
                "Server: AndroidWebserver/1.0\n" +
                "Content-Length: %length%\n" +
                "Accept-Ranges: bytes\n" +
                "Content-Range: %range%\n" +
                "Cache-Control: max-age=0\n" +
                "Content-Type: %contenttype%\n\n";
    }

}