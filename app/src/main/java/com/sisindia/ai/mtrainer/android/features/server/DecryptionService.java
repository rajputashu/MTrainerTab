package com.sisindia.ai.mtrainer.android.features.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.Socket;

/**
 * * Created by Vinod on 5/2/2018.
 */
//reference class for adding server content
public class DecryptionService extends Service {
    private boolean isRunning = false;
    public static String EncryptedFormat = ".htm,.ppt,.pot,.pps,.pptx,.potx,.ppsx,.html,.jpg,.jpeg,.bmp,.gif,.png,.swf,.css,.js,.PNG,.xsd,.flv,.mp4,.mp3,.mpg,.mpeg,.dat,.wmv,.gif,.xsd,.avi,.unity3d,.pdf";// ".htm,.html,.jpg,.jpeg,.bmp,.gif,.png,.swf,.css,.js,.PNG,.xml,.xsd,.flv";
    //  RandomAccessFile randomAccessFile;
    private PrintStream out;
    int length, seek;
    String key, fileName, dokument, fileext;
    private String documentRoot;
    private String range;
    private Socket toClient;
    private Context context;

    public void startDecryption(String dokument, Context context, String range, Socket socket, String documentRoot)
    {
        String fileName = dokument.substring(dokument.lastIndexOf('/') + 1);
//        if(!fileName.contains("."))
//        {
//           // Toast.makeText(context,"Enter a valid url",Toast.LENGTH_LONG).show();
//            Log.e("url","Enter a valid url");
//        }
//        else {
        if (dokument.contains(".")) {
            String fileext = dokument.substring(dokument.lastIndexOf('.'));
            Log.e("filename", fileName);
            Log.e("ext", fileext);
            String contentType = GetContentType(fileext);
//        if (dokument.contains("index.html")) {
//            dokument = "index.html";
//        }

            // Don't allow directory traversal
            if (dokument.contains("..")) {
                dokument = "403.html";
            }

            // Search for files in docroot
            dokument = documentRoot + dokument;
            Log.e("doc1", dokument);
            Log.d("Webserver", "Got " + dokument);
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
                    header = header.replace("%code%", "200 OK");
                }
                //header = getHeaderBase().replace("%code%", "200 OK");
            }


            Log.d("Webserver", "Serving " + dokument);

            try {
                File f = new File(dokument);
                if (f.exists()) {
                    //  InputStream in = new BufferedInputStream(new FileInputStream(dokument));
                    out = new PrintStream(toClient.getOutputStream());
                    //  ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
                    //  MappedByteBuffer  mappedByteBuffer
                    //   BufferedOutputStream outputStream = new BufferedOutputStream(toClient.getOutputStream());
                    int file_size = Integer.parseInt(String.valueOf(f.length()));
//                    DataInputStream is = new DataInputStream(new FileInputStream(f));
//                    byte[] bytes = new byte[(int) f.length()];
//                    is.readFully(bytes);
//                    is.close();

                    //  Log.e("filesizeinbytes", String.valueOf(file_size));

                    String decKey = fileName.substring(0, fileName.lastIndexOf('.')).toLowerCase() + "mtrain";
                    //String key = GeneratePrivateToken("Test.mp4");
                    //int iFile = fileName.lastIndexOf(".");
                    //String passW = fileName.substring(0, iFile);
                    //String pwd = GeneratePrivateToken(passW.toLowerCase() + "N");
                    // Log.e("key", pwd);
//                if (fileext.equals(".png")) {
//                    decKey = "ogiui4554$%%$%$ttctdrcjcj$%$%^*hjhggkghv!@#$%^&*())(*&^%$#@!ggjgvyt&^t1()2tyunU^&%#$%^&kgf(*&^3546HJGDGFHKJH456876457564%^$#$%^&*&r*(&";
//                } else if (fileext.equals(".html")) {
//                    decKey = "867cgf9s4554$%%$%$ttctdrcjcj$%$%^*hjhggkghv!@#$%^&*())(*&^%$#@!g9ghp98hhgi&^nggjd&*ej)(xy9nu^&6#$%^&kgf(*&^3546HJGDGFHKJH456876457564%^$#$%^&*&gfuyte";
//                }
//                else if(fileName.equals("Test.mp4"))
//                {
//                    decKey ="67gyuJB4554$%%$%$ttctdrcjcj$%$%^*hjhggkghv!@#$%^&*())(*&^%$#@!jgvytuy&^tt()etyus89tui7n&%TY#$%^&kgf(*&^3546HJGDGFHKJH456876457564%^$#$%^&*&75rth";
//                }
//                else if(fileName.equals("physicaltrainingdrill.mp4"))
//                {
//                    decKey="s789csag798as97G(^G8og78794554$%%$%$ttctdrcjcj$%$%^*hjhggkghv!@#$%^&*())(*&^%$#@!uibgo87ghhgp&^hggjy&*sj)(iy9c(*^ay8l^&* 9ht*(Hry(aHUYiOInYjhigynh67guo 75$duvr$RUigilUFDlugnyjvycvi7yvKK#$%^&kgf(*&^3546HJGDGFHKJH456876457564%^$#$%^&*&765rfhjgfvi%^RFUyv85";
//                }
////              //  String decKey=fileext.substring(0,fileext.lastIndexOf('.'));
//                //String decKey = fileName;
//                // String decKey = "867cgf9s4554$%%$%$ttctdrcjcj$%$%^*hjhggkghv!@#$%^&*())(*&^%$#@!g9ghp98hhgi&^nggjd&*ej)(xy9nu^&6#$%^&kgf(*&^3546HJGDGFHKJH456876457564%^$#$%^&*&gfuyte";
//                //dokument.substring(0, dokument.lastIndexOf('.'));
//                //  String decKey1 ="ogiui4554$%%$%$ttctdrcjcj$%$%^*hjhggkghv!@#$%^&*())(*&^%$#@!ggjgvyt&^t1()1tyunU^&%#$%^&kgf(*&^3546HJGDGFHKJH456876457564%^$#$%^&*&r*(&";
//                // clsCrypto clsCrypto= new clsCrypto();
//                // String decKey=fileName.substring(0,fileName.lastIndexOf('.'));
//                // com.bolutions.webserver.Log.Write("abcc2: "+decKey, com.bolutions.webserver.Log._LogLevel.SERVER_DETAILS);
//                //LicensingService.ProjectName="D-Vault";
//                //decKey=decKey.toLowerCase()+"minigenie";
//                //   decKey = GeneratePrivateToken(decKey);
//                // String deckey = clsCrypto.CDecryptString(decKey);
//                //   Log.e("key",deckey);
                    //     byte[] buf = new byte[(int) f.length()];
                    int count;
//                    while ((count = in.read(buf)) != -1) {
//                        tempOut.ic_write(buf, 0, count);
//                    }
//                    tempOut.flush();

                    Log.e("buf", String.valueOf(file_size));
                    Log.e("ext", fileext);
                    Log.e("filext", String.valueOf(EncryptedFormat.toLowerCase().contains(fileext.toLowerCase())));
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
                            range = String.valueOf(start) + "-" + String.valueOf(file_size - 1);
                        }

                        //end = Integer.parseInt(split[1]);
                    } else {

                    }
                    if (range.equals("0")) {
                        range = String.valueOf(start) + "-" + String.valueOf(file_size - 1);
                    }
                    if (fileext.equals(".mp4")) {
                        header = header.replace("%length%", "" + String.valueOf(contentlength));
                        header = header.replace("%contenttype%", "" + contentType);
                        header = header.replace("%code%", "206 Partial Content");
                        header = header.replace("%range%", "bytes " + range + "/" + String.valueOf(file_size));
                    } else {
                        header = header.replace("%length%", "" + file_size);
                        header = header.replace("%code%", "200 OK");
                        header = header.replace("%contenttype%", "" + contentType);
                    }
                    out.print(header);

                    Log.e("header", header);
                    //  new DecryptionThread(out, file_size, decKey, fileName, dokument, start,fileext).start();
                    FlatFileIO fio = new FlatFileIO();
                    if (EncryptedFormat.toLowerCase().contains(fileext.toLowerCase())) {
                        RandomAccessFile inputBuffer_local_1 = new RandomAccessFile(dokument, "r");
                        Log.e("decryption", "writing after decryption");
                      //  fio.WriteFileInChunk_After_Decryption(start, inputBuffer_local_1, file_size, out, decKey);
                        //  fio.decryption(inputBuffer_local_1, out, file_size, decKey, fileName, dokument, start);
                        //fio.WriteFileInChunk_After_Decryption(0, inputBuffer_local_1, tempOut.size(), out, decKey, fileName, dokument);
                        inputBuffer_local_1.close();
                    } else {
                        RandomAccessFile inputBuffer_local_1 = new RandomAccessFile(dokument, "r");
                        Log.e("decryption", "writing original file");
                        //   Log.Write("Render Original File", com.bolutions.webserver.Log._LogLevel.SERVER_DETAILS);
                        //fio.WriteFileInChunk_Original(StartSector, LicensingService.inputBuffer, ContentLength,ps);
                    //    fio.WriteFileInChunk_Original(start, inputBuffer_local_1, file_size, out);
                        inputBuffer_local_1.close();
                    }
                    out.flush();
//                out.ic_write(header.getBytes());
//                out.ic_write(tempOut.toByteArray());
//                out.flush();
                    // Send HTML-File (Ascii, not as a stream)
                } else {
                    header = getHeaderBase();
                    header = header.replace("%code%", "404 File not found");
                    header = header.replace("%length%", "" + "404 - File not Found".length());
                    header = header.replace("%contenttype%", "" + contentType);
                    out = new PrintStream(toClient.getOutputStream(), true);
                    out.print(header);
                    out.print("404 - File not Found");
                    out.flush();
                }
                //   PreferenceActivity.Header[] headers = response.getAllHeaders();
                Log.e("header", header);
                out.close();
                Server.remove(toClient);
//                toClient.shutdownInput();
//                toClient.shutdownOutput();
                toClient.close();
            } catch (Exception ignored) {

            }
        } else {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setMessage("File not found");
//            builder.setCancelable(true);
//            builder.setPositiveButton(
//                    "OK",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                        }
//                    });
//
//            AlertDialog alert = builder.create();
//            alert.show();
        }
    }
    public void stopServer() {

            // mNM.cancel(NOTIFICATION_ID);
            // mNM.cancelAll();
            isRunning = false;

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
        }
//        else if (Ext.contains(".PPT")) {
//            varcontenttype = " application/vnd.ms-powerpoint";
//        } else if (Ext.contains(".POT")) {
//            varcontenttype = "application/vnd.ms-powerpoint";
//        } else if (Ext.contains(".PPS")) {
//            varcontenttype = "application/vnd.ms-powerpoint";
//        } else if (Ext.contains(".PPTX")) {
//            varcontenttype = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
//        } else if (Ext.contains(".POTX")) {
//            varcontenttype = "application/vnd.openxmlformats-officedocument.presentationml.template";
//        } else if (Ext.contains(".PPSX")) {
//            varcontenttype = "application/vnd.openxmlformats-officedocument.presentationml.slideshow";
//        }
        else {
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
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public DecryptionService getService() {
            return DecryptionService.this;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}
