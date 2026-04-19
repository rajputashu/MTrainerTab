/*     */ package org.apache.cordova;
/*     */ 
/*     */

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
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
/*     */ public class CordovaPlugin
/*     */ {
/*     */   public CordovaWebView webView;
/*     */   public CordovaInterface cordova;
/*     */   protected CordovaPreferences preferences;
/*     */   private String serviceName;
/*     */   
/*     */   public final void privateInitialize(String serviceName, CordovaInterface cordova, CordovaWebView webView, CordovaPreferences preferences)
/*     */   {
/*  52 */     assert (this.cordova == null);
/*  53 */     this.serviceName = serviceName;
/*  54 */     this.cordova = cordova;
/*  55 */     this.webView = webView;
/*  56 */     this.preferences = preferences;
/*  57 */     initialize(cordova, webView);
/*  58 */     pluginInitialize();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void initialize(CordovaInterface cordova, CordovaWebView webView) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void pluginInitialize() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getServiceName()
/*     */   {
/*  79 */     return this.serviceName;
/*     */   }
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
/*     */   public boolean execute(String action, String rawArgs, CallbackContext callbackContext)
/*     */     throws JSONException
/*     */   {
/*  97 */     JSONArray args = new JSONArray(rawArgs);
/*  98 */     return execute(action, args, callbackContext);
/*     */   }
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
/*     */   public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
/*     */     throws JSONException
/*     */   {
/* 116 */     CordovaArgs cordovaArgs = new CordovaArgs(args);
/* 117 */     return execute(action, cordovaArgs, callbackContext);
/*     */   }
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
/*     */   public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext)
/*     */     throws JSONException
/*     */   {
/* 135 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onPause(boolean multitasking) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onResume(boolean multitasking) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onStart() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onStop() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onNewIntent(Intent intent) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onDestroy() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Bundle onSaveInstanceState()
/*     */   {
/* 188 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onRestoreStateForActivityResult(Bundle state, CallbackContext callbackContext) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object onMessage(String id, Object data)
/*     */   {
/* 209 */     return null;
/*     */   }
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
/*     */   public void onActivityResult(int requestCode, int resultCode, Intent intent) {}
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
/*     */   public Boolean shouldAllowRequest(String url)
/*     */   {
/* 242 */     return null;
/*     */   }
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
/*     */   public Boolean shouldAllowNavigation(String url)
/*     */   {
/* 256 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Boolean shouldAllowBridgeAccess(String url)
/*     */   {
/* 265 */     return shouldAllowNavigation(url);
/*     */   }
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
/*     */   public Boolean shouldOpenExternalUrl(String url)
/*     */   {
/* 279 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean onOverrideUrlLoading(String url)
/*     */   {
/* 289 */     return false;
/*     */   }
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
/*     */   public Uri remapUri(Uri uri)
/*     */   {
/* 309 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public CordovaResourceApi.OpenForReadResult handleOpenForRead(Uri uri)
/*     */     throws IOException
/*     */   {
/* 318 */     throw new FileNotFoundException("Plugin can't handle uri: " + uri);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Uri toPluginUri(Uri origUri)
/*     */   {
/* 326 */     return new Builder().scheme("cdvplugin").authority(this.serviceName).appendQueryParameter("origUri", origUri.toString()).build();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Uri fromPluginUri(Uri pluginUri)
/*     */   {
/* 338 */     return Uri.parse(pluginUri.getQueryParameter("origUri"));
/*     */   }
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
/*     */   public void onReset() {}
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
/*     */   public boolean onReceivedHttpAuthRequest(CordovaWebView view, ICordovaHttpAuthHandler handler, String host, String realm)
/*     */   {
/* 364 */     return false;
/*     */   }
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
/*     */   public boolean onReceivedClientCertRequest(CordovaWebView view, ICordovaClientCertRequest request)
/*     */   {
/* 378 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onConfigurationChanged(Configuration newConfig) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void requestPermissions(int requestCode) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean hasPermisssion()
/*     */   {
/* 408 */     return true;
/*     */   }
/*     */   
/*     */   public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
/*     */     throws JSONException
/*     */   {}
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\CordovaPlugin.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */