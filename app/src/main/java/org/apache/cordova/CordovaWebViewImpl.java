/*     */ package org.apache.cordova;
/*     */ 
/*     */

/*     */

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.FrameLayout.LayoutParams;

import org.apache.cordova.engine.SystemWebViewEngine;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
/*     */ public class CordovaWebViewImpl
/*     */   implements CordovaWebView
/*     */ {
/*     */   public static final String TAG = "CordovaWebViewImpl";
/*     */   private PluginManager pluginManager;
/*     */   protected final CordovaWebViewEngine engine;
/*     */   private CordovaInterface cordova;
/*  56 */   private int loadUrlTimeout = 0;
/*     */   
/*     */   private CordovaResourceApi resourceApi;
/*     */   private CordovaPreferences preferences;
/*     */   private CoreAndroid appPlugin;
/*     */   private NativeToJsMessageQueue nativeToJsMessageQueue;
/*  62 */   private EngineClient engineClient = new EngineClient();
/*     */   
/*     */   private boolean hasPausedEver;
/*     */   
/*     */   String loadedUrl;
/*     */   
/*     */   private View mCustomView;
/*     */   
/*     */   private CustomViewCallback mCustomViewCallback;
/*     */   
/*  72 */   private Set<Integer> boundKeyCodes = new HashSet();
/*     */   
/*     */   public static CordovaWebViewEngine createEngine(Context context, CordovaPreferences preferences) {
/*  75 */     String className = preferences.getString("webview", SystemWebViewEngine.class.getCanonicalName());
/*     */     try {
/*  77 */       Class<?> webViewClass = Class.forName(className);
/*  78 */       Constructor<?> constructor = webViewClass.getConstructor(new Class[] { Context.class, CordovaPreferences.class });
/*  79 */       return (CordovaWebViewEngine)constructor.newInstance(new Object[] { context, preferences });
/*     */     } catch (Exception e) {
/*  81 */       throw new RuntimeException("Failed to create webview. ", e);
/*     */     }
/*     */   }
/*     */   
/*     */   public CordovaWebViewImpl(CordovaWebViewEngine cordovaWebViewEngine) {
/*  86 */     this.engine = cordovaWebViewEngine;
/*     */   }
/*     */   
/*     */   public void init(CordovaInterface cordova)
/*     */   {
/*  91 */     init(cordova, new ArrayList(), new CordovaPreferences());
/*     */   }
/*     */   
/*     */   public void init(CordovaInterface cordova, List<PluginEntry> pluginEntries, CordovaPreferences preferences)
/*     */   {
/*  96 */     if (this.cordova != null) {
/*  97 */       throw new IllegalStateException();
/*     */     }
/*  99 */     this.cordova = cordova;
/* 100 */     this.preferences = preferences;
/* 101 */     this.pluginManager = new PluginManager(this, this.cordova, pluginEntries);
/* 102 */     this.resourceApi = new CordovaResourceApi(this.engine.getView().getContext(), this.pluginManager);
/* 103 */     this.nativeToJsMessageQueue = new NativeToJsMessageQueue();
/* 104 */     this.nativeToJsMessageQueue.addBridgeMode(new NativeToJsMessageQueue.NoOpBridgeMode());
/* 105 */     this.nativeToJsMessageQueue.addBridgeMode(new NativeToJsMessageQueue.LoadUrlBridgeMode(this.engine, cordova));
/*     */     
/* 107 */     if (preferences.getBoolean("DisallowOverscroll", false)) {
/* 108 */       this.engine.getView().setOverScrollMode(2);
/*     */     }
/* 110 */     this.engine.init(this, cordova, this.engineClient, this.resourceApi, this.pluginManager, this.nativeToJsMessageQueue);
/*     */     
/* 112 */     assert ((this.engine.getView() instanceof CordovaWebViewEngine.EngineView));
/*     */     
/* 114 */     this.pluginManager.addService("CoreAndroid", "org.apache.cordova.CoreAndroid");
/* 115 */     this.pluginManager.init();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isInitialized()
/*     */   {
/* 121 */     return this.cordova != null;
/*     */   }
/*     */   
/*     */   public void loadUrlIntoView(final String url, boolean recreatePlugins)
/*     */   {
/* 126 */     LOG.d("CordovaWebViewImpl", ">>> loadUrl(" + url + ")");
/* 127 */     if ((url.equals("about:blank")) || (url.startsWith("javascript:"))) {
/* 128 */       this.engine.loadUrl(url, false);
/* 129 */       return;
/*     */     }
/*     */     
/* 132 */     recreatePlugins = (recreatePlugins) || (this.loadedUrl == null);
/*     */     
/* 134 */     if (recreatePlugins)
/*     */     {
/* 136 */       if (this.loadedUrl != null) {
/* 137 */         this.appPlugin = null;
/* 138 */         this.pluginManager.init();
/*     */       }
/* 140 */       this.loadedUrl = url;
/*     */     }
/*     */     
/*     */ 
/* 144 */     final int currentLoadUrlTimeout = this.loadUrlTimeout;
/* 145 */     final int loadUrlTimeoutValue = this.preferences.getInteger("LoadUrlTimeoutValue", 20000);
/*     */     
/*     */ 
/* 148 */     final Runnable loadError = new Runnable() {
/*     */       public void run() {
/* 150 */         CordovaWebViewImpl.this.stopLoading();
/* 151 */         LOG.e("CordovaWebViewImpl", "CordovaWebView: TIMEOUT ERROR!");
/*     */         
/*     */ 
/* 154 */         JSONObject data = new JSONObject();
/*     */         try {
/* 156 */           data.put("errorCode", -6);
/* 157 */           data.put("description", "The connection to the server was unsuccessful.");
/* 158 */           data.put("url", url);
/*     */         }
/*     */         catch (JSONException e) {}
/*     */         
/* 162 */         CordovaWebViewImpl.this.pluginManager.postMessage("onReceivedError", data);
/*     */       }
/*     */       
/*     */ 
/* 166 */     };
/* 167 */     final Runnable timeoutCheck = new Runnable() {
/*     */       public void run() {
/*     */         try {
/* 170 */           synchronized (this) {
/* 171 */             wait(loadUrlTimeoutValue);
/*     */           }
/*     */         } catch (InterruptedException e) {
/* 174 */           e.printStackTrace();
/*     */         }
/*     */         
/*     */ 
/* 178 */         if (CordovaWebViewImpl.this.loadUrlTimeout == currentLoadUrlTimeout) {
/* 179 */           CordovaWebViewImpl.this.cordova.getActivity().runOnUiThread(loadError);
/*     */         }
/*     */         
/*     */       }
/* 183 */     };
/* 184 */     final boolean _recreatePlugins = recreatePlugins;
/* 185 */     this.cordova.getActivity().runOnUiThread(new Runnable() {
/*     */       public void run() {
/* 187 */         if (loadUrlTimeoutValue > 0) {
/* 188 */           CordovaWebViewImpl.this.cordova.getThreadPool().execute(timeoutCheck);
/*     */         }
/* 190 */         CordovaWebViewImpl.this.engine.loadUrl(url, _recreatePlugins);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public void loadUrl(String url)
/*     */   {
/* 198 */     loadUrlIntoView(url, true);
/*     */   }
/*     */   
/*     */   public void showWebPage(String url, boolean openExternal, boolean clearHistory, Map<String, Object> params)
/*     */   {
/* 203 */     LOG.d("CordovaWebViewImpl", "showWebPage(%s, %b, %b, HashMap)", new Object[] { url, Boolean.valueOf(openExternal), Boolean.valueOf(clearHistory) });
/*     */     
/*     */ 
/* 206 */     if (clearHistory) {
/* 207 */       this.engine.clearHistory();
/*     */     }
/*     */     
/*     */ 
/* 211 */     if (!openExternal)
/*     */     {
/* 213 */       if (this.pluginManager.shouldAllowNavigation(url))
/*     */       {
/*     */ 
/* 216 */         loadUrlIntoView(url, true);
/*     */       } else {
/* 218 */         LOG.w("CordovaWebViewImpl", "showWebPage: Refusing to load URL into webview since it is not in the <allow-navigation> whitelist. URL=" + url);
/*     */       }
/*     */     }
/* 221 */     if (!this.pluginManager.shouldOpenExternalUrl(url).booleanValue()) {
/* 222 */       LOG.w("CordovaWebViewImpl", "showWebPage: Refusing to send intent for URL since it is not in the <allow-intent> whitelist. URL=" + url);
/* 223 */       return;
/*     */     }
/*     */     try {
/* 226 */       Intent intent = new Intent("android.intent.action.VIEW");
/*     */       
/* 228 */       intent.addCategory("android.intent.category.BROWSABLE");
/* 229 */       Uri uri = Uri.parse(url);
/*     */       
/*     */ 
/* 232 */       if ("file".equals(uri.getScheme())) {
/* 233 */         intent.setDataAndType(uri, this.resourceApi.getMimeType(uri));
/*     */       } else {
/* 235 */         intent.setData(uri);
/*     */       }
/* 237 */       this.cordova.getActivity().startActivity(intent);
/*     */     } catch (ActivityNotFoundException e) {
/* 239 */       LOG.e("CordovaWebViewImpl", "Error loading url " + url, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @Deprecated
/*     */   public void showCustomView(View view, CustomViewCallback callback)
/*     */   {
/* 247 */     LOG.d("CordovaWebViewImpl", "showing Custom View");
/*     */     
/* 249 */     if (this.mCustomView != null) {
/* 250 */       callback.onCustomViewHidden();
/* 251 */       return;
/*     */     }
/*     */     
/*     */ 
/* 255 */     this.mCustomView = view;
/* 256 */     this.mCustomViewCallback = callback;
/*     */     
/*     */ 
/* 259 */     ViewGroup parent = (ViewGroup)this.engine.getView().getParent();
/* 260 */     parent.addView(view, new LayoutParams(-1, -1, 17));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 266 */     this.engine.getView().setVisibility(View.GONE);
/*     */     
/*     */ 
/* 269 */     parent.setVisibility(View.VISIBLE);
/* 270 */     parent.bringToFront();
/*     */   }
/*     */   
/*     */ 
/*     */   @Deprecated
/*     */   public void hideCustomView()
/*     */   {
/* 277 */     if (this.mCustomView == null) return;
/* 278 */     LOG.d("CordovaWebViewImpl", "Hiding Custom View");
/*     */     
/*     */ 
/* 281 */     this.mCustomView.setVisibility(8);
/*     */     
/*     */ 
/* 284 */     ViewGroup parent = (ViewGroup)this.engine.getView().getParent();
/* 285 */     parent.removeView(this.mCustomView);
/* 286 */     this.mCustomView = null;
/* 287 */     this.mCustomViewCallback.onCustomViewHidden();
/*     */     
/*     */ 
/* 290 */     this.engine.getView().setVisibility(View.VISIBLE);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public boolean isCustomViewShowing()
/*     */   {
/* 296 */     return this.mCustomView != null;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void sendJavascript(String statement)
/*     */   {
/* 302 */     this.nativeToJsMessageQueue.addJavaScript(statement);
/*     */   }
/*     */   
/*     */   public void sendPluginResult(PluginResult cr, String callbackId)
/*     */   {
/* 307 */     this.nativeToJsMessageQueue.addPluginResult(cr, callbackId);
/*     */   }
/*     */   
/*     */   public PluginManager getPluginManager()
/*     */   {
/* 312 */     return this.pluginManager;
/*     */   }
/*     */   
/*     */   public CordovaPreferences getPreferences() {
/* 316 */     return this.preferences;
/*     */   }
/*     */   
/*     */   public ICordovaCookieManager getCookieManager() {
/* 320 */     return this.engine.getCookieManager();
/*     */   }
/*     */   
/*     */   public CordovaResourceApi getResourceApi() {
/* 324 */     return this.resourceApi;
/*     */   }
/*     */   
/*     */   public CordovaWebViewEngine getEngine() {
/* 328 */     return this.engine;
/*     */   }
/*     */   
/*     */   public View getView() {
/* 332 */     return this.engine.getView();
/*     */   }
/*     */   
/*     */   public Context getContext() {
/* 336 */     return this.engine.getView().getContext();
/*     */   }
/*     */   
/*     */   private void sendJavascriptEvent(String event) {
/* 340 */     if (this.appPlugin == null) {
/* 341 */       this.appPlugin = ((CoreAndroid)this.pluginManager.getPlugin("CoreAndroid"));
/*     */     }
/*     */     
/* 344 */     if (this.appPlugin == null) {
/* 345 */       LOG.w("CordovaWebViewImpl", "Unable to fire event without existing plugin");
/* 346 */       return;
/*     */     }
/* 348 */     this.appPlugin.fireJavascriptEvent(event);
/*     */   }
/*     */   
/*     */   public void setButtonPlumbedToJs(int keyCode, boolean override)
/*     */   {
/* 353 */     switch (keyCode)
/*     */     {
/*     */     case 4: 
/*     */     case 24: 
/*     */     case 25: 
/*     */     case 82: 
/* 359 */       if (override) {
/* 360 */         this.boundKeyCodes.add(Integer.valueOf(keyCode));
/*     */       } else {
/* 362 */         this.boundKeyCodes.remove(Integer.valueOf(keyCode));
/*     */       }
/* 364 */       return;
/*     */     }
/* 366 */     throw new IllegalArgumentException("Unsupported keycode: " + keyCode);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isButtonPlumbedToJs(int keyCode)
/*     */   {
/* 372 */     return this.boundKeyCodes.contains(Integer.valueOf(keyCode));
/*     */   }
/*     */   
/*     */   public Object postMessage(String id, Object data)
/*     */   {
/* 377 */     return this.pluginManager.postMessage(id, data);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUrl()
/*     */   {
/* 383 */     return this.engine.getUrl();
/*     */   }
/*     */   
/*     */ 
/*     */   public void stopLoading()
/*     */   {
/* 389 */     this.loadUrlTimeout += 1;
/*     */   }
/*     */   
/*     */   public boolean canGoBack()
/*     */   {
/* 394 */     return this.engine.canGoBack();
/*     */   }
/*     */   
/*     */   public void clearCache()
/*     */   {
/* 399 */     this.engine.clearCache();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void clearCache(boolean b)
/*     */   {
/* 405 */     this.engine.clearCache();
/*     */   }
/*     */   
/*     */   public void clearHistory()
/*     */   {
/* 410 */     this.engine.clearHistory();
/*     */   }
/*     */   
/*     */   public boolean backHistory()
/*     */   {
/* 415 */     return this.engine.goBack();
/*     */   }
/*     */   
/*     */ 
/*     */   public void onNewIntent(Intent intent)
/*     */   {
/* 421 */     if (this.pluginManager != null) {
/* 422 */       this.pluginManager.onNewIntent(intent);
/*     */     }
/*     */   }
/*     */   
/*     */   public void handlePause(boolean keepRunning) {
/* 427 */     if (!isInitialized()) {
/* 428 */       return;
/*     */     }
/* 430 */     this.hasPausedEver = true;
/* 431 */     this.pluginManager.onPause(keepRunning);
/* 432 */     sendJavascriptEvent("pause");
/*     */     
/*     */ 
/* 435 */     if (!keepRunning)
/*     */     {
/* 437 */       this.engine.setPaused(true);
/*     */     }
/*     */   }
/*     */   
/*     */   public void handleResume(boolean keepRunning) {
/* 442 */     if (!isInitialized()) {
/* 443 */       return;
/*     */     }
/*     */     
/*     */ 
/* 447 */     this.engine.setPaused(false);
/* 448 */     this.pluginManager.onResume(keepRunning);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 453 */     if (this.hasPausedEver) {
/* 454 */       sendJavascriptEvent("resume");
/*     */     }
/*     */   }
/*     */   
/*     */   public void handleStart() {
/* 459 */     if (!isInitialized()) {
/* 460 */       return;
/*     */     }
/* 462 */     this.pluginManager.onStart();
/*     */   }
/*     */   
/*     */   public void handleStop() {
/* 466 */     if (!isInitialized()) {
/* 467 */       return;
/*     */     }
/* 469 */     this.pluginManager.onStop();
/*     */   }
/*     */   
/*     */   public void handleDestroy() {
/* 473 */     if (!isInitialized()) {
/* 474 */       return;
/*     */     }
/*     */     
/* 477 */     this.loadUrlTimeout += 1;
/*     */     
/*     */ 
/* 480 */     this.pluginManager.onDestroy();
/*     */     
/*     */ 
/*     */ 
/* 484 */     loadUrl("about:blank");
/*     */     
/*     */ 
/* 487 */     this.engine.destroy();
/* 488 */     hideCustomView();
/*     */   }
/*     */   
/*     */   protected class EngineClient implements CordovaWebViewEngine.Client {
/*     */     protected EngineClient() {}
/*     */     
/* 494 */     public void clearLoadTimeoutTimer() {  loadUrlTimeout++; }
/*     */     
/*     */ 
/*     */     public void onPageStarted(String newUrl)
/*     */     {
/* 499 */       LOG.d("CordovaWebViewImpl", "onPageDidNavigate(" + newUrl + ")");
/* 500 */       CordovaWebViewImpl.this.boundKeyCodes.clear();
/* 501 */       CordovaWebViewImpl.this.pluginManager.onReset();
/* 502 */       CordovaWebViewImpl.this.pluginManager.postMessage("onPageStarted", newUrl);
/*     */     }
/*     */     
/*     */     public void onReceivedError(int errorCode, String description, String failingUrl)
/*     */     {
/* 507 */       clearLoadTimeoutTimer();
/* 508 */       JSONObject data = new JSONObject();
/*     */       try {
/* 510 */         data.put("errorCode", errorCode);
/* 511 */         data.put("description", description);
/* 512 */         data.put("url", failingUrl);
/*     */       } catch (JSONException e) {
/* 514 */         e.printStackTrace();
/*     */       }
/* 516 */       CordovaWebViewImpl.this.pluginManager.postMessage("onReceivedError", data);
/*     */     }
/*     */     
/*     */     public void onPageFinishedLoading(String url)
/*     */     {
/* 521 */       LOG.d("CordovaWebViewImpl", "onPageFinished(" + url + ")");
/*     */       
/* 523 */       clearLoadTimeoutTimer();
/*     */       
/*     */ 
/* 526 */       CordovaWebViewImpl.this.pluginManager.postMessage("onPageFinished", url);
/*     */       
/*     */ 
/* 529 */       if (CordovaWebViewImpl.this.engine.getView().getVisibility() != 0) {
/* 530 */         Thread t = new Thread(new Runnable() {
/*     */           public void run() {
/*     */             try {
/* 533 */               Thread.sleep(2000L);
/* 534 */               CordovaWebViewImpl.this.cordova.getActivity().runOnUiThread(new Runnable() {
/*     */                 public void run() {
/* 536 */                   CordovaWebViewImpl.this.pluginManager.postMessage("spinner", "stop");
/*     */                 }
/*     */               });
/*     */             }
/*     */             catch (InterruptedException e) {}
/*     */           }
/* 542 */         });
/* 543 */         t.start();
/*     */       }
/*     */       
/*     */ 
/* 547 */       if (url.equals("about:blank")) {
/* 548 */         CordovaWebViewImpl.this.pluginManager.postMessage("exit", null);
/*     */       }
/*     */     }
/*     */     
/*     */     public Boolean onDispatchKeyEvent(KeyEvent event)
/*     */     {
/* 554 */       int keyCode = event.getKeyCode();
/* 555 */       boolean isBackButton = keyCode == 4;
/* 556 */       if (event.getAction() == 0) {
/* 557 */         if ((isBackButton) && (CordovaWebViewImpl.this.mCustomView != null))
/* 558 */           return Boolean.valueOf(true);
/* 559 */         if (CordovaWebViewImpl.this.boundKeyCodes.contains(Integer.valueOf(keyCode)))
/* 560 */           return Boolean.valueOf(true);
/* 561 */         if (isBackButton) {
/* 562 */           return Boolean.valueOf(CordovaWebViewImpl.this.engine.canGoBack());
/*     */         }
/* 564 */       } else if (event.getAction() == 1) {
/* 565 */         if ((isBackButton) && (CordovaWebViewImpl.this.mCustomView != null)) {
/* 566 */           CordovaWebViewImpl.this.hideCustomView();
/* 567 */           return Boolean.valueOf(true); }
/* 568 */         if (CordovaWebViewImpl.this.boundKeyCodes.contains(Integer.valueOf(keyCode))) {
/* 569 */           String eventName = null;
/* 570 */           switch (keyCode) {
/*     */           case 25: 
/* 572 */             eventName = "volumedownbutton";
/* 573 */             break;
/*     */           case 24: 
/* 575 */             eventName = "volumeupbutton";
/* 576 */             break;
/*     */           case 84: 
/* 578 */             eventName = "searchbutton";
/* 579 */             break;
/*     */           case 82: 
/* 581 */             eventName = "menubutton";
/* 582 */             break;
/*     */           case 4: 
/* 584 */             eventName = "backbutton";
/*     */           }
/*     */           
/* 587 */           if (eventName != null) {
/* 588 */             CordovaWebViewImpl.this.sendJavascriptEvent(eventName);
/* 589 */             return Boolean.valueOf(true);
/*     */           }
/* 591 */         } else if (isBackButton) {
/* 592 */           return Boolean.valueOf(CordovaWebViewImpl.this.engine.goBack());
/*     */         }
/*     */       }
/* 595 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean onNavigationAttempt(String url)
/*     */     {
/* 601 */       if (CordovaWebViewImpl.this.pluginManager.onOverrideUrlLoading(url))
/* 602 */         return true;
/* 603 */       if (CordovaWebViewImpl.this.pluginManager.shouldAllowNavigation(url))
/* 604 */         return false;
/* 605 */       if (CordovaWebViewImpl.this.pluginManager.shouldOpenExternalUrl(url).booleanValue()) {
/* 606 */         CordovaWebViewImpl.this.showWebPage(url, true, false, null);
/* 607 */         return true;
/*     */       }
/* 609 */       LOG.w("CordovaWebViewImpl", "Blocked (possibly sub-frame) navigation to non-allowed URL: " + url);
/* 610 */       return true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\CordovaWebViewImpl.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */