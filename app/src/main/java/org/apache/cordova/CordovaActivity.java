/*     */ package org.apache.cordova;
/*     */ 
/*     */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

//import io.fabric.sdk.android.Fabric;

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
/*     */ public abstract class CordovaActivity
/*     */   extends Activity
/*     */ {
/*  78 */   public static String TAG = "CordovaActivity";
/*     */   
/*     */ 
/*     */   protected CordovaWebView appView;
/*     */   
/*  83 */   private static int ACTIVITY_STARTING = 0;
/*  84 */   private static int ACTIVITY_RUNNING = 1;
/*  85 */   private static int ACTIVITY_EXITING = 2;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  90 */   protected boolean keepRunning = true;
/*     */   
/*     */ 
/*     */   protected boolean immersiveMode;
/*     */   
/*     */ 
/*     */   protected CordovaPreferences preferences;
/*     */   
/*     */   protected String launchUrl;
/*     */   
/*     */   protected ArrayList<PluginEntry> pluginEntries;
/*     */   
/*     */   protected CordovaInterfaceImpl cordovaInterface;
/*     */   
/*     */ 
/*     */   public void onCreate(Bundle savedInstanceState)
/*     */   {
/* 107 */     loadConfig();
/*     */     
/* 109 */     String logLevel = this.preferences.getString("loglevel", "ERROR");
/* 110 */     LOG.setLogLevel(logLevel);
/*     */     
/* 112 */     LOG.i(TAG, "Apache Cordova native platform version 6.5.0-dev is starting");
/* 113 */     LOG.d(TAG, "CordovaActivity.onCreate()");
/*     */     
/* 115 */     if (!this.preferences.getBoolean("ShowTitle", false)) {
/* 116 */       getWindow().requestFeature(1);
/*     */     }
/*     */     
/* 119 */     if (this.preferences.getBoolean("SetFullscreen", false)) {
/* 120 */       LOG.d(TAG, "The SetFullscreen configuration is deprecated in favor of Fullscreen, and will be removed in a future version.");
/* 121 */       this.preferences.set("Fullscreen", true);
/*     */     }
/* 123 */     if (this.preferences.getBoolean("Fullscreen", false))
/*     */     {
/*     */ 
/* 126 */       if ((VERSION.SDK_INT >= 19) && (!this.preferences.getBoolean("FullscreenNotImmersive", false))) {
/* 127 */         this.immersiveMode = true;
/*     */       } else {
/* 129 */         getWindow().setFlags(1024, 1024);
/*     */       }
/*     */     }
/*     */     else {
/* 133 */       getWindow().setFlags(2048, 2048);
/*     */     }
/*     */     
/*     */ 
/* 137 */     super.onCreate(savedInstanceState);
              //Fabric.with(this, new Crashlytics());
/*     */     
/* 139 */     this.cordovaInterface = makeCordovaInterface();
/* 140 */     if (savedInstanceState != null) {
/* 141 */       this.cordovaInterface.restoreInstanceState(savedInstanceState);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void init() {
/* 146 */     this.appView = makeWebView();
/* 147 */     createViews();
/* 148 */     if (!this.appView.isInitialized()) {
/* 149 */       this.appView.init(this.cordovaInterface, this.pluginEntries, this.preferences);
/*     */     }
/* 151 */     this.cordovaInterface.onCordovaInit(this.appView.getPluginManager());
/*     */     
/*     */ 
/* 154 */     String volumePref = this.preferences.getString("DefaultVolumeStream", "");
/* 155 */     if ("media".equals(volumePref.toLowerCase(Locale.ENGLISH))) {
/* 156 */       setVolumeControlStream(3);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void loadConfig()
/*     */   {
/* 162 */     ConfigXmlParser parser = new ConfigXmlParser();
/* 163 */     parser.parse(this);
/* 164 */     this.preferences = parser.getPreferences();
/* 165 */     this.preferences.setPreferencesBundle(getIntent().getExtras());
/* 166 */     this.launchUrl = parser.getLaunchUrl();
/* 167 */     this.pluginEntries = parser.getPluginEntries();
/* 168 */     Config.parser = parser;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void createViews()
/*     */   {
/* 175 */     this.appView.getView().setId(100);
/* 176 */     this.appView.getView().setLayoutParams(new LayoutParams(-1, -1));
/*     */     
/*     */ 
/*     */ 
/* 180 */     setContentView(this.appView.getView());
/*     */     
/* 182 */     if (this.preferences.contains("BackgroundColor")) {
/*     */       try {
/* 184 */         int backgroundColor = this.preferences.getInteger("BackgroundColor", -16777216);
/*     */         
/* 186 */         this.appView.getView().setBackgroundColor(backgroundColor);
/*     */       }
/*     */       catch (NumberFormatException e) {
/* 189 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/* 193 */     this.appView.getView().requestFocusFromTouch();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected CordovaWebView makeWebView()
/*     */   {
/* 202 */     return new CordovaWebViewImpl(makeWebViewEngine());
/*     */   }
/*     */   
/*     */   protected CordovaWebViewEngine makeWebViewEngine() {
/* 206 */     return CordovaWebViewImpl.createEngine(this, this.preferences);
/*     */   }
/*     */   
/*     */   protected CordovaInterfaceImpl makeCordovaInterface() {
/* 210 */     return new CordovaInterfaceImpl(this)
/*     */     {
/*     */       public Object onMessage(String id, Object data)
/*     */       {
/* 214 */         return CordovaActivity.this.onMessage(id, data);
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void loadUrl(String url)
/*     */   {
/* 223 */     if (this.appView == null) {
/* 224 */       init();
/*     */     }
/*     */     
/*     */ 
/* 228 */     this.keepRunning = this.preferences.getBoolean("KeepRunning", true);
/*     */     
/* 230 */     this.appView.loadUrlIntoView(url, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void onPause()
/*     */   {
/* 238 */     super.onPause();
/* 239 */     LOG.d(TAG, "Paused the activity.");
/*     */     
/* 241 */     if (this.appView != null)
/*     */     {
/*     */ 
/* 244 */       boolean keepRunning = (this.keepRunning) || (this.cordovaInterface.activityResultCallback != null);
/* 245 */       this.appView.handlePause(keepRunning);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void onNewIntent(Intent intent)
/*     */   {
/* 254 */     super.onNewIntent(intent);
/*     */     
/* 256 */     if (this.appView != null) {
/* 257 */       this.appView.onNewIntent(intent);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void onResume()
/*     */   {
/* 265 */     super.onResume();
/* 266 */     LOG.d(TAG, "Resumed the activity.");
/*     */     
/* 268 */     if (this.appView == null) {
/* 269 */       return;
/*     */     }
/*     */     
/*     */ 
/* 273 */     getWindow().getDecorView().requestFocus();
/*     */     
/* 275 */     this.appView.handleResume(this.keepRunning);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void onStop()
/*     */   {
/* 283 */     super.onStop();
/* 284 */     LOG.d(TAG, "Stopped the activity.");
/*     */     
/* 286 */     if (this.appView == null) {
/* 287 */       return;
/*     */     }
/* 289 */     this.appView.handleStop();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void onStart()
/*     */   {
/* 297 */     super.onStart();
/* 298 */     LOG.d(TAG, "Started the activity.");
/*     */     
/* 300 */     if (this.appView == null) {
/* 301 */       return;
/*     */     }
/* 303 */     this.appView.handleStart();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onDestroy()
/*     */   {
/* 311 */     LOG.d(TAG, "CordovaActivity.onDestroy()");
/* 312 */     super.onDestroy();
/*     */     
/* 314 */     if (this.appView != null) {
/* 315 */       this.appView.handleDestroy();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onWindowFocusChanged(boolean hasFocus)
/*     */   {
/* 324 */     super.onWindowFocusChanged(hasFocus);
/* 325 */     if ((hasFocus) && (this.immersiveMode)) {
/* 326 */       int uiOptions = 5894;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 333 */       getWindow().getDecorView().setSystemUiVisibility(5894);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @SuppressLint({"NewApi"})
/*     */   public void startActivityForResult(Intent intent, int requestCode, Bundle options)
/*     */   {
/* 341 */     this.cordovaInterface.setActivityResultRequestCode(requestCode);
/* 342 */     super.startActivityForResult(intent, requestCode, options);
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
/*     */   protected void onActivityResult(int requestCode, int resultCode, Intent intent)
/*     */   {
/* 356 */     LOG.d(TAG, "Incoming Result. Request code = " + requestCode);
/* 357 */     super.onActivityResult(requestCode, resultCode, intent);
/* 358 */     this.cordovaInterface.onActivityResult(requestCode, resultCode, intent);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onReceivedError(int errorCode, final String description, final String failingUrl)
/*     */   {
/* 370 */     final CordovaActivity me = this;
/*     */     
/*     */ 
/* 373 */     final String errorUrl = this.preferences.getString("errorUrl", null);
/* 374 */     if ((errorUrl != null) && (!failingUrl.equals(errorUrl)) && (this.appView != null))
/*     */     {
/* 376 */       me.runOnUiThread(new Runnable() {
/*     */         public void run() {
/* 378 */           me.appView.showWebPage(errorUrl, false, true, null);
/*     */         }
/*     */       });
/*     */     }
/*     */     else
/*     */     {
/* 384 */       final boolean exit = errorCode != -2;
/* 385 */       me.runOnUiThread(new Runnable() {
/*     */         public void run() {
/* 387 */           if (exit) {
/* 388 */             me.appView.getView().setVisibility(View.GONE);
/* 389 */             me.displayError("Application Error", description + " (" + failingUrl + ")", "OK", exit);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void displayError(final String title, final String message, final String button, final boolean exit)
/*     */   {
/* 400 */     final CordovaActivity me = this;
/* 401 */     me.runOnUiThread(new Runnable() {
/*     */       public void run() {
/*     */         try {
/* 404 */           Builder dlg = new Builder(me);
/* 405 */           dlg.setMessage(message);
/* 406 */           dlg.setTitle(title);
/* 407 */           dlg.setCancelable(false);
/* 408 */           dlg.setPositiveButton(button, new OnClickListener()
/*     */           {
/*     */             public void onClick(DialogInterface dialog, int which) {
/* 411 */               dialog.dismiss();
/* 412 */               if (exit) {
/* 413 */                 CordovaActivity.this.finish();
/*     */               }
/*     */             }
/* 416 */           });
/* 417 */           dlg.create();
/* 418 */           dlg.show();
/*     */         } catch (Exception e) {
/* 420 */           CordovaActivity.this.finish();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean onCreateOptionsMenu(Menu menu)
/*     */   {
/* 431 */     if (this.appView != null) {
/* 432 */       this.appView.getPluginManager().postMessage("onCreateOptionsMenu", menu);
/*     */     }
/* 434 */     return super.onCreateOptionsMenu(menu);
/*     */   }
/*     */   
/*     */   public boolean onPrepareOptionsMenu(Menu menu)
/*     */   {
/* 439 */     if (this.appView != null) {
/* 440 */       this.appView.getPluginManager().postMessage("onPrepareOptionsMenu", menu);
/*     */     }
/* 442 */     return true;
/*     */   }
/*     */   
/*     */   public boolean onOptionsItemSelected(MenuItem item)
/*     */   {
/* 447 */     if (this.appView != null) {
/* 448 */       this.appView.getPluginManager().postMessage("onOptionsItemSelected", item);
/*     */     }
/* 450 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object onMessage(String id, Object data)
/*     */   {
/* 461 */     if ("onReceivedError".equals(id)) {
/* 462 */       JSONObject d = (JSONObject)data;
/*     */       try {
/* 464 */         onReceivedError(d.getInt("errorCode"), d.getString("description"), d.getString("url"));
/*     */       } catch (JSONException e) {
/* 466 */         e.printStackTrace();
/*     */       }
/* 468 */     } else if ("exit".equals(id)) {
/* 469 */       finish();
/*     */     }
/* 471 */     return null;
/*     */   }
/*     */   
/*     */   protected void onSaveInstanceState(Bundle outState) {
/* 475 */     this.cordovaInterface.onSaveInstanceState(outState);
/* 476 */     super.onSaveInstanceState(outState);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onConfigurationChanged(Configuration newConfig)
/*     */   {
/* 486 */     super.onConfigurationChanged(newConfig);
/* 487 */     if (this.appView == null) {
/* 488 */       return;
/*     */     }
/* 490 */     PluginManager pm = this.appView.getPluginManager();
/* 491 */     if (pm != null) {
/* 492 */       pm.onConfigurationChanged(newConfig);
/*     */     }
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
/*     */   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
/*     */   {
/*     */     try
/*     */     {
/* 508 */       this.cordovaInterface.onRequestPermissionResult(requestCode, permissions, grantResults);
/*     */     }
/*     */     catch (JSONException e)
/*     */     {
/* 512 */       LOG.d(TAG, "JSONException: Parameters fed into the method are not valid");
/* 513 */       e.printStackTrace();
/*     */     }
/*     */   }

    protected abstract void extractBundle();
    /*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\CordovaActivity.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */