/*     */ package org.apache.cordova.engine;
/*     */ 
/*     */

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import org.apache.cordova.CordovaBridge;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPreferences;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine;
import org.apache.cordova.ICordovaCookieManager;
import org.apache.cordova.LOG;
import org.apache.cordova.NativeToJsMessageQueue;
import org.apache.cordova.NativeToJsMessageQueue.EvalBridgeMode;
import org.apache.cordova.NativeToJsMessageQueue.OnlineEventsBridgeMode;
import org.apache.cordova.NativeToJsMessageQueue.OnlineEventsBridgeMode.OnlineEventsBridgeModeDelegate;
import org.apache.cordova.PluginManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
/*     */ public class SystemWebViewEngine
/*     */   implements CordovaWebViewEngine
/*     */ {
/*     */   public static final String TAG = "SystemWebViewEngine";
/*     */   protected final SystemWebView webView;
/*     */   protected final SystemCookieManager cookieManager;
/*     */   protected CordovaPreferences preferences;
/*     */   protected CordovaBridge bridge;
/*     */   protected Client client;
/*     */   protected CordovaWebView parentWebView;
/*     */   protected CordovaInterface cordova;
/*     */   protected PluginManager pluginManager;
/*     */   protected CordovaResourceApi resourceApi;
/*     */   protected NativeToJsMessageQueue nativeToJsMessageQueue;
/*     */   private BroadcastReceiver receiver;
/*     */   
/*     */   public SystemWebViewEngine(Context context, CordovaPreferences preferences)
/*     */   {
/*  76 */     this(new SystemWebView(context), preferences);
/*     */   }
/*     */   
/*     */   public SystemWebViewEngine(SystemWebView webView) {
/*  80 */     this(webView, null);
/*     */   }
/*     */   
/*     */   public SystemWebViewEngine(SystemWebView webView, CordovaPreferences preferences) {
/*  84 */     this.preferences = preferences;
/*  85 */     this.webView = webView;
/*  86 */     this.cookieManager = new SystemCookieManager(webView);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void init(CordovaWebView parentWebView, CordovaInterface cordova, Client client, CordovaResourceApi resourceApi, PluginManager pluginManager, NativeToJsMessageQueue nativeToJsMessageQueue)
/*     */   {
/*  93 */     if (this.cordova != null) {
/*  94 */       throw new IllegalStateException();
/*     */     }
/*     */     
/*  97 */     if (this.preferences == null) {
/*  98 */       this.preferences = parentWebView.getPreferences();
/*     */     }
/* 100 */     this.parentWebView = parentWebView;
/* 101 */     this.cordova = cordova;
/* 102 */     this.client = client;
/* 103 */     this.resourceApi = resourceApi;
/* 104 */     this.pluginManager = pluginManager;
/* 105 */     this.nativeToJsMessageQueue = nativeToJsMessageQueue;
/* 106 */     this.webView.init(this, cordova);
/*     */     
/* 108 */     initWebViewSettings();
/*     */     
/* 110 */     nativeToJsMessageQueue.addBridgeMode(new OnlineEventsBridgeMode(new OnlineEventsBridgeModeDelegate()
/*     */     {
/*     */ 
/*     */       public void setNetworkAvailable(boolean value)
/*     */       {
/* 115 */         if (SystemWebViewEngine.this.webView != null) {
/* 116 */           SystemWebViewEngine.this.webView.setNetworkAvailable(value);
/*     */         }
/*     */       }
/*     */       
/*     */       public void runOnUiThread(Runnable r) {
/* 121 */         SystemWebViewEngine.this.cordova.getActivity().runOnUiThread(r);
/*     */       }
/*     */     }));
/* 124 */     if (VERSION.SDK_INT > 18)
/* 125 */       nativeToJsMessageQueue.addBridgeMode(new EvalBridgeMode(this, cordova));
/* 126 */     this.bridge = new CordovaBridge(pluginManager, nativeToJsMessageQueue);
/* 127 */     exposeJsInterface(this.webView, this.bridge);
/*     */   }
/*     */   
/*     */   public CordovaWebView getCordovaWebView()
/*     */   {
/* 132 */     return this.parentWebView;
/*     */   }
/*     */   
/*     */   public ICordovaCookieManager getCookieManager()
/*     */   {
/* 137 */     return this.cookieManager;
/*     */   }
/*     */   
/*     */   public View getView()
/*     */   {
/* 142 */     return this.webView;
/*     */   }
/*     */   
/*     */   @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
/*     */   private void initWebViewSettings()
/*     */   {
/* 148 */     this.webView.setInitialScale(0);
/* 149 */     this.webView.setVerticalScrollBarEnabled(false);
/*     */     
/* 151 */     final WebSettings settings = this.webView.getSettings();
/* 152 */     settings.setJavaScriptEnabled(true);
/* 153 */     settings.setJavaScriptCanOpenWindowsAutomatically(true);
/* 154 */     settings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
/*     */     
/*     */     try
/*     */     {
/* 158 */       Method gingerbread_getMethod = WebSettings.class.getMethod("setNavDump", new Class[] { Boolean.TYPE });
/*     */       
/* 160 */       String manufacturer = Build.MANUFACTURER;
/* 161 */       LOG.d("SystemWebViewEngine", "CordovaWebView is running on device made by: " + manufacturer);
/* 162 */       if ((VERSION.SDK_INT < 11) && (Build.MANUFACTURER.contains("HTC")))
/*     */       {
/*     */ 
/* 165 */         gingerbread_getMethod.invoke(settings, new Object[] { Boolean.valueOf(true) });
/*     */       }
/*     */     } catch (NoSuchMethodException e) {
/* 168 */       LOG.d("SystemWebViewEngine", "We are on a modern version of Android, we will deprecate HTC 2.3 devices in 2.8");
/*     */     } catch (IllegalArgumentException e) {
/* 170 */       LOG.d("SystemWebViewEngine", "Doing the NavDump failed with bad arguments");
/*     */     } catch (IllegalAccessException e) {
/* 172 */       LOG.d("SystemWebViewEngine", "This should never happen: IllegalAccessException means this isn't Android anymore");
/*     */     } catch (InvocationTargetException e) {
/* 174 */       LOG.d("SystemWebViewEngine", "This should never happen: InvocationTargetException means this isn't Android anymore.");
/*     */     }
/*     */     
/*     */ 
/* 178 */     settings.setSaveFormData(false);
/* 179 */     settings.setSavePassword(false);
/*     */     
/*     */ 
/*     */ 
/* 183 */     if (VERSION.SDK_INT >= 16) {
/* 184 */       settings.setAllowUniversalAccessFromFileURLs(true);
/*     */     }
/* 186 */     if (VERSION.SDK_INT >= 17) {
/* 187 */       settings.setMediaPlaybackRequiresUserGesture(false);
/*     */     }
/*     */     
/*     */ 
/* 191 */     String databasePath = this.webView.getContext().getApplicationContext().getDir("database", 0).getPath();
/* 192 */     settings.setDatabaseEnabled(true);
/* 193 */     settings.setDatabasePath(databasePath);
/*     */     
/*     */ 
/*     */ 
/* 197 */     ApplicationInfo appInfo = this.webView.getContext().getApplicationContext().getApplicationInfo();
/* 198 */     if (((appInfo.flags & 0x2) != 0) && (VERSION.SDK_INT >= 19))
/*     */     {
/* 200 */       enableRemoteDebugging();
/*     */     }
/*     */     
/* 203 */     settings.setGeolocationDatabasePath(databasePath);
/*     */     
/*     */ 
/* 206 */     settings.setDomStorageEnabled(true);
/*     */     
/*     */ 
/* 209 */     settings.setGeolocationEnabled(true);
/*     */     
/*     */ 
/*     */ 
/* 213 */     /*settings.setAppCacheMaxSize(5242880L);
*//* 214 *//*     settings.setAppCachePath(databasePath);
*//* 215 *//*     settings.setAppCacheEnabled(true);*/
/*     */     
/*     */ 
/*     */ 
/* 219 */     String defaultUserAgent = settings.getUserAgentString();
/*     */     
/*     */ 
/* 222 */     String overrideUserAgent = this.preferences.getString("OverrideUserAgent", null);
/* 223 */     if (overrideUserAgent != null) {
/* 224 */       settings.setUserAgentString(overrideUserAgent);
/*     */     } else {
/* 226 */       String appendUserAgent = this.preferences.getString("AppendUserAgent", null);
/* 227 */       if (appendUserAgent != null) {
/* 228 */         settings.setUserAgentString(defaultUserAgent + " " + appendUserAgent);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 233 */     IntentFilter intentFilter = new IntentFilter();
/* 234 */     intentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
/* 235 */     if (this.receiver == null) {
/* 236 */       this.receiver = new BroadcastReceiver()
/*     */       {
/*     */         public void onReceive(Context context, Intent intent) {
/* 239 */           settings.getUserAgentString();
/*     */         }
/* 241 */       };
/* 242 */       this.webView.getContext().registerReceiver(this.receiver, intentFilter);
/*     */     }
/*     */   }
/*     */   
/*     */   @TargetApi(19)
/*     */   private void enableRemoteDebugging()
/*     */   {
/*     */     try {
/* 250 */       WebView.setWebContentsDebuggingEnabled(true);
/*     */     } catch (IllegalArgumentException e) {
/* 252 */       LOG.d("SystemWebViewEngine", "You have one job! To turn on Remote Web Debugging! YOU HAVE FAILED! ");
/* 253 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   private static void exposeJsInterface(WebView webView, CordovaBridge bridge) {
/* 258 */     if (VERSION.SDK_INT < 17) {
/* 259 */       LOG.i("SystemWebViewEngine", "Disabled addJavascriptInterface() bridge since Android version is old.");
/*     */       
/*     */ 
/*     */ 
/* 263 */       return;
/*     */     }
/* 265 */     SystemExposedJsApi exposedJsApi = new SystemExposedJsApi(bridge);
/* 266 */     webView.addJavascriptInterface(exposedJsApi, "_cordovaNative");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void loadUrl(String url, boolean clearNavigationStack)
/*     */   {
/* 275 */     this.webView.loadUrl(url);
/*     */   }
/*     */   
/*     */   public String getUrl()
/*     */   {
/* 280 */     return this.webView.getUrl();
/*     */   }
/*     */   
/*     */   public void stopLoading()
/*     */   {
/* 285 */     this.webView.stopLoading();
/*     */   }
/*     */   
/*     */   public void clearCache()
/*     */   {
/* 290 */     this.webView.clearCache(true);
/*     */   }
/*     */   
/*     */   public void clearHistory()
/*     */   {
/* 295 */     this.webView.clearHistory();
/*     */   }
/*     */   
/*     */   public boolean canGoBack()
/*     */   {
/* 300 */     return this.webView.canGoBack();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean goBack()
/*     */   {
/* 312 */     if (this.webView.canGoBack()) {
/* 313 */       this.webView.goBack();
/* 314 */       return true;
/*     */     }
/* 316 */     return false;
/*     */   }
/*     */   
/*     */   public void setPaused(boolean value)
/*     */   {
/* 321 */     if (value) {
/* 322 */       this.webView.onPause();
/* 323 */       this.webView.pauseTimers();
/*     */     } else {
/* 325 */       this.webView.onResume();
/* 326 */       this.webView.resumeTimers();
/*     */     }
/*     */   }
/*     */   
/*     */   public void destroy()
/*     */   {
/* 332 */     this.webView.chromeClient.destroyLastDialog();
/* 333 */     this.webView.destroy();
/*     */     
/* 335 */     if (this.receiver != null) {
/*     */       try {
/* 337 */         this.webView.getContext().unregisterReceiver(this.receiver);
/*     */       } catch (Exception e) {
/* 339 */         LOG.e("SystemWebViewEngine", "Error unregistering configuration receiver: " + e.getMessage(), e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void evaluateJavascript(String js, ValueCallback<String> callback)
/*     */   {
/* 346 */     if (VERSION.SDK_INT >= 19) {
/* 347 */       this.webView.evaluateJavascript(js, callback);
/*     */     }
/*     */     else
/*     */     {
/* 351 */       LOG.d("SystemWebViewEngine", "This webview is using the old bridge");
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\engine\SystemWebViewEngine.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */