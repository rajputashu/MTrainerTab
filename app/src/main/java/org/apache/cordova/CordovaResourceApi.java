/*     */ package org.apache.cordova;
/*     */ 
/*     */

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Locale;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CordovaResourceApi
/*     */ {
/*     */   private static final String LOG_TAG = "CordovaResourceApi";
/*     */   public static final int URI_TYPE_FILE = 0;
/*     */   public static final int URI_TYPE_ASSET = 1;
/*     */   public static final int URI_TYPE_CONTENT = 2;
/*     */   public static final int URI_TYPE_RESOURCE = 3;
/*     */   public static final int URI_TYPE_DATA = 4;
/*     */   public static final int URI_TYPE_HTTP = 5;
/*     */   public static final int URI_TYPE_HTTPS = 6;
/*     */   public static final int URI_TYPE_PLUGIN = 7;
/*     */   public static final int URI_TYPE_UNKNOWN = -1;
/*     */   public static final String PLUGIN_URI_SCHEME = "cdvplugin";
/*  79 */   private static final String[] LOCAL_FILE_PROJECTION = { "_data" };
/*     */   
/*     */   public static Thread jsThread;
/*     */   
/*     */   private final AssetManager assetManager;
/*     */   private final ContentResolver contentResolver;
/*     */   private final PluginManager pluginManager;
/*  86 */   private boolean threadCheckingEnabled = true;
/*     */   
/*     */   public CordovaResourceApi(Context context, PluginManager pluginManager)
/*     */   {
/*  90 */     this.contentResolver = context.getContentResolver();
/*  91 */     this.assetManager = context.getAssets();
/*  92 */     this.pluginManager = pluginManager;
/*     */   }
/*     */   
/*     */   public void setThreadCheckingEnabled(boolean value) {
/*  96 */     this.threadCheckingEnabled = value;
/*     */   }
/*     */   
/*     */   public boolean isThreadCheckingEnabled() {
/* 100 */     return this.threadCheckingEnabled;
/*     */   }
/*     */   
/*     */   public static int getUriType(Uri uri)
/*     */   {
/* 105 */     assertNonRelative(uri);
/* 106 */     String scheme = uri.getScheme();
/* 107 */     if ("content".equalsIgnoreCase(scheme)) {
/* 108 */       return 2;
/*     */     }
/* 110 */     if ("android.resource".equalsIgnoreCase(scheme)) {
/* 111 */       return 3;
/*     */     }
/* 113 */     if ("file".equalsIgnoreCase(scheme)) {
/* 114 */       if (uri.getPath().startsWith("/android_asset/")) {
/* 115 */         return 1;
/*     */       }
/* 117 */       return 0;
/*     */     }
/* 119 */     if ("data".equalsIgnoreCase(scheme)) {
/* 120 */       return 4;
/*     */     }
/* 122 */     if ("http".equalsIgnoreCase(scheme)) {
/* 123 */       return 5;
/*     */     }
/* 125 */     if ("https".equalsIgnoreCase(scheme)) {
/* 126 */       return 6;
/*     */     }
/* 128 */     if ("cdvplugin".equalsIgnoreCase(scheme)) {
/* 129 */       return 7;
/*     */     }
/* 131 */     return -1;
/*     */   }
/*     */   
/*     */   public Uri remapUri(Uri uri) {
/* 135 */     assertNonRelative(uri);
/* 136 */     Uri pluginUri = this.pluginManager.remapUri(uri);
/* 137 */     return pluginUri != null ? pluginUri : uri;
/*     */   }
/*     */   
/*     */   public String remapPath(String path) {
/* 141 */     return remapUri(Uri.fromFile(new File(path))).getPath();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public File mapUriToFile(Uri uri)
/*     */   {
/* 149 */     assertBackgroundThread();
/* 150 */     switch (getUriType(uri)) {
/*     */     case 0: 
/* 152 */       return new File(uri.getPath());
/*     */     case 2: 
/* 154 */       Cursor cursor = this.contentResolver.query(uri, LOCAL_FILE_PROJECTION, null, null, null);
/* 155 */       if (cursor != null) {
/*     */         try {
/* 157 */           int columnIndex = cursor.getColumnIndex(LOCAL_FILE_PROJECTION[0]);
/* 158 */           if ((columnIndex != -1) && (cursor.getCount() > 0)) {
/* 159 */             cursor.moveToFirst();
/* 160 */             String realPath = cursor.getString(columnIndex);
/* 161 */             if (realPath != null) {
/* 162 */               return new File(realPath);
/*     */             }
/*     */           }
/*     */         } finally {
/* 166 */           cursor.close();
/*     */         }
/*     */       }
/*     */       break;
/*     */     }
/* 171 */     return null;
/*     */   }
/*     */   
/*     */   public String getMimeType(Uri uri) {
/* 175 */     switch (getUriType(uri)) {
/*     */     case 0: 
/*     */     case 1: 
/* 178 */       return getMimeTypeFromPath(uri.getPath());
/*     */     case 2: 
/*     */     case 3: 
/* 181 */       return this.contentResolver.getType(uri);
/*     */     case 4: 
/* 183 */       return getDataUriMimeType(uri);
/*     */     case 5: 
/*     */     case 6: 
/*     */       try
/*     */       {
/* 188 */         HttpURLConnection conn = (HttpURLConnection)new URL(uri.toString()).openConnection();
/* 189 */         conn.setDoInput(false);
/* 190 */         conn.setRequestMethod("HEAD");
/* 191 */         String mimeType = conn.getHeaderField("Content-Type");
/* 192 */         if (mimeType != null) {}
/* 193 */         return mimeType.split(";")[0];
/*     */       }
/*     */       catch (IOException e) {}
/*     */     }
/*     */     
/*     */     
/*     */ 
/*     */ 
/* 201 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   private String getMimeTypeFromPath(String path)
/*     */   {
/* 207 */     String extension = path;
/* 208 */     int lastDot = extension.lastIndexOf('.');
/* 209 */     if (lastDot != -1) {
/* 210 */       extension = extension.substring(lastDot + 1);
/*     */     }
/*     */     
/* 213 */     extension = extension.toLowerCase(Locale.getDefault());
/* 214 */     if (extension.equals("3ga"))
/* 215 */       return "audio/3gpp";
/* 216 */     if (extension.equals("js"))
/*     */     {
/* 218 */       return "text/javascript";
/*     */     }
/* 220 */     return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public OpenForReadResult openForRead(Uri uri)
/*     */     throws IOException
/*     */   {
/* 232 */     return openForRead(uri, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public OpenForReadResult openForRead(Uri uri, boolean skipThreadCheck)
/*     */     throws IOException
/*     */   {
/* 244 */     if (!skipThreadCheck) {
/* 245 */       assertBackgroundThread();
/*     */     }
/* 247 */     switch (getUriType(uri)) {
/*     */     case 0: 
/* 249 */       FileInputStream inputStream = new FileInputStream(uri.getPath());
/* 250 */       String mimeType = getMimeTypeFromPath(uri.getPath());
/* 251 */       long length = inputStream.getChannel().size();
/* 252 */       return new OpenForReadResult(uri, inputStream, mimeType, length, null);
/*     */     
/*     */     case 1: 
/* 255 */       String assetPath = uri.getPath().substring(15);
/* 256 */       AssetFileDescriptor assetFd = null;
/*     */       
/* 258 */       long length1 = -1L;
/*     */       InputStream inputStream1;
/* 260 */       try { assetFd = this.assetManager.openFd(assetPath);
/* 261 */         inputStream1 = assetFd.createInputStream();
/* 262 */         length1 = assetFd.getLength();
/*     */       }
/*     */       catch (FileNotFoundException e) {
/* 265 */         inputStream1 = this.assetManager.open(assetPath);
/*     */       }
/* 267 */       String mimeType1 = getMimeTypeFromPath(assetPath);
/* 268 */       return new OpenForReadResult(uri, inputStream1, mimeType1, length1, assetFd);
/*     */     
/*     */     case 2: 
/*     */     case 3: 
/* 272 */       String mimeType2 = this.contentResolver.getType(uri);
/* 273 */       AssetFileDescriptor assetFd2 = this.contentResolver.openAssetFileDescriptor(uri, "r");
/* 274 */       InputStream inputStream2 = assetFd2.createInputStream();
/* 275 */       long length2 = assetFd2.getLength();
/* 276 */       return new OpenForReadResult(uri, inputStream2, mimeType2, length2, assetFd2);
/*     */     
/*     */     case 4: 
/* 279 */       OpenForReadResult ret = readDataUri(uri);
/* 280 */       if (ret != null)
/*     */       {
/*     */ 
/* 283 */         return ret;
/*     */       }
/*     */       break;
/*     */     case 5: case 6: 
/* 287 */       HttpURLConnection conn = (HttpURLConnection)new URL(uri.toString()).openConnection();
/* 288 */       conn.setDoInput(true);
/* 289 */       String mimeType3 = conn.getHeaderField("Content-Type");
/* 290 */       if (mimeType3 != null) {
/* 291 */         mimeType3 = mimeType3.split(";")[0];
/*     */       }
/* 293 */       int length3 = conn.getContentLength();
/* 294 */       InputStream inputStream3 = conn.getInputStream();
/* 295 */       return new OpenForReadResult(uri, inputStream3, mimeType3, length3, null);
/*     */     
/*     */     case 7: 
/* 298 */       String pluginId = uri.getHost();
/* 299 */       CordovaPlugin plugin = this.pluginManager.getPlugin(pluginId);
/* 300 */       if (plugin == null) {
/* 301 */         throw new FileNotFoundException("Invalid plugin ID in URI: " + uri);
/*     */       }
/* 303 */       return plugin.handleOpenForRead(uri);
/*     */     }
/*     */     
/* 306 */     throw new FileNotFoundException("URI not supported by CordovaResourceApi: " + uri);
/*     */   }
/*     */   
/*     */   public OutputStream openOutputStream(Uri uri) throws IOException {
/* 310 */     return openOutputStream(uri, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public OutputStream openOutputStream(Uri uri, boolean append)
/*     */     throws IOException
/*     */   {
/* 321 */     assertBackgroundThread();
/* 322 */     switch (getUriType(uri)) {
/*     */     case 0: 
/* 324 */       File localFile = new File(uri.getPath());
/* 325 */       File parent = localFile.getParentFile();
/* 326 */       if (parent != null) {
/* 327 */         parent.mkdirs();
/*     */       }
/* 329 */       return new FileOutputStream(localFile, append);
/*     */     
/*     */     case 2: 
/*     */     case 3: 
/* 333 */       AssetFileDescriptor assetFd = this.contentResolver.openAssetFileDescriptor(uri, append ? "wa" : "w");
/* 334 */       return assetFd.createOutputStream();
/*     */     }
/*     */     
/* 337 */     throw new FileNotFoundException("URI not supported by CordovaResourceApi: " + uri);
/*     */   }
/*     */   
/*     */   public HttpURLConnection createHttpConnection(Uri uri) throws IOException {
/* 341 */     assertBackgroundThread();
/* 342 */     return (HttpURLConnection)new URL(uri.toString()).openConnection();
/*     */   }
/*     */   
/*     */   public void copyResource(OpenForReadResult input, OutputStream outputStream)
/*     */     throws IOException
/*     */   {
/* 348 */     assertBackgroundThread();
/*     */     try {
/* 350 */       InputStream inputStream = input.inputStream;
/* 351 */       if (((inputStream instanceof FileInputStream)) && ((outputStream instanceof FileOutputStream))) {
/* 352 */         FileChannel inChannel = ((FileInputStream)input.inputStream).getChannel();
/* 353 */         FileChannel outChannel = ((FileOutputStream)outputStream).getChannel();
/* 354 */         long offset = 0L;
/* 355 */         long length = input.length;
/* 356 */         if (input.assetFd != null) {
/* 357 */           offset = input.assetFd.getStartOffset();
/*     */         }
/*     */         
/*     */ 
/* 361 */         inChannel.position(offset);
/* 362 */         outChannel.transferFrom(inChannel, 0L, length);
/*     */       } else {
/* 364 */         int BUFFER_SIZE = 8192;
/* 365 */         byte[] buffer = new byte[' '];
/*     */         for (;;)
/*     */         {
/* 368 */           int bytesRead = inputStream.read(buffer, 0, 8192);
/*     */           
/* 370 */           if (bytesRead <= 0) {
/*     */             break;
/*     */           }
/* 373 */           outputStream.write(buffer, 0, bytesRead);
/*     */         }
/*     */       }
/*     */     } finally {
/* 377 */       input.inputStream.close();
/* 378 */       if (outputStream != null) {
/* 379 */         outputStream.close();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void copyResource(Uri sourceUri, OutputStream outputStream) throws IOException {
/* 385 */     copyResource(openForRead(sourceUri), outputStream);
/*     */   }
/*     */   
/*     */   public void copyResource(Uri sourceUri, Uri dstUri) throws IOException
/*     */   {
/* 390 */     copyResource(openForRead(sourceUri), openOutputStream(dstUri));
/*     */   }
/*     */   
/*     */   private void assertBackgroundThread() {
/* 394 */     if (this.threadCheckingEnabled) {
/* 395 */       Thread curThread = Thread.currentThread();
/* 396 */       if (curThread == Looper.getMainLooper().getThread()) {
/* 397 */         throw new IllegalStateException("Do not perform IO operations on the UI thread. Use CordovaInterface.getThreadPool() instead.");
/*     */       }
/* 399 */       if (curThread == jsThread) {
/* 400 */         throw new IllegalStateException("Tried to perform an IO operation on the WebCore thread. Use CordovaInterface.getThreadPool() instead.");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private String getDataUriMimeType(Uri uri) {
/* 406 */     String uriAsString = uri.getSchemeSpecificPart();
/* 407 */     int commaPos = uriAsString.indexOf(',');
/* 408 */     if (commaPos == -1) {
/* 409 */       return null;
/*     */     }
/* 411 */     String[] mimeParts = uriAsString.substring(0, commaPos).split(";");
/* 412 */     if (mimeParts.length > 0) {
/* 413 */       return mimeParts[0];
/*     */     }
/* 415 */     return null;
/*     */   }
/*     */   
/*     */   private OpenForReadResult readDataUri(Uri uri) {
/* 419 */     String uriAsString = uri.getSchemeSpecificPart();
/* 420 */     int commaPos = uriAsString.indexOf(',');
/* 421 */     if (commaPos == -1) {
/* 422 */       return null;
/*     */     }
/* 424 */     String[] mimeParts = uriAsString.substring(0, commaPos).split(";");
/* 425 */     String contentType = null;
/* 426 */     boolean base64 = false;
/* 427 */     if (mimeParts.length > 0) {
/* 428 */       contentType = mimeParts[0];
/*     */     }
/* 430 */     for (int i = 1; i < mimeParts.length; i++) {
/* 431 */       if ("base64".equalsIgnoreCase(mimeParts[i])) {
/* 432 */         base64 = true;
/*     */       }
/*     */     }
/* 435 */     String dataPartAsString = uriAsString.substring(commaPos + 1);
/*     */
/* 437 */     byte[] data; if (base64) {
/* 438 */       data = Base64.decode(dataPartAsString, 0);
/*     */     } else {
/*     */       try {
/* 441 */         data = dataPartAsString.getBytes("UTF-8");
/*     */       } catch (UnsupportedEncodingException e) {
/* 443 */         data = dataPartAsString.getBytes();
/*     */       }
/*     */     }
/* 446 */     InputStream inputStream = new ByteArrayInputStream(data);
/* 447 */     return new OpenForReadResult(uri, inputStream, contentType, data.length, null);
/*     */   }
/*     */   
/*     */   private static void assertNonRelative(Uri uri) {
/* 451 */     if (!uri.isAbsolute()) {
/* 452 */       throw new IllegalArgumentException("Relative URIs are not supported.");
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class OpenForReadResult {
/*     */     public final Uri uri;
/*     */     public final InputStream inputStream;
/*     */     public final String mimeType;
/*     */     public final long length;
/*     */     public final AssetFileDescriptor assetFd;
/*     */     
/*     */     public OpenForReadResult(Uri uri, InputStream inputStream, String mimeType, long length, AssetFileDescriptor assetFd) {
/* 464 */       this.uri = uri;
/* 465 */       this.inputStream = inputStream;
/* 466 */       this.mimeType = mimeType;
/* 467 */       this.length = length;
/* 468 */       this.assetFd = assetFd;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\CordovaResourceApi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */