/*     */ package org.apache.cordova;
/*     */ 
/*     */

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;

import org.json.JSONException;

import java.util.Collection;
import java.util.LinkedHashMap;
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
/*     */ public class PluginManager
/*     */ {
/*  39 */   private static String TAG = "PluginManager";
/*  40 */   private static final int SLOW_EXEC_WARNING_THRESHOLD = Debug.isDebuggerConnected() ? 60 : 16;
/*     */   
/*     */ 
/*  43 */   private final LinkedHashMap<String, CordovaPlugin> pluginMap = new LinkedHashMap();
/*  44 */   private final LinkedHashMap<String, PluginEntry> entryMap = new LinkedHashMap();
/*     */   
/*     */   private final CordovaInterface ctx;
/*     */   private final CordovaWebView app;
/*     */   private boolean isInitialized;
/*     */   private CordovaPlugin permissionRequester;
/*     */   
/*     */   public PluginManager(CordovaWebView cordovaWebView, CordovaInterface cordova, Collection<PluginEntry> pluginEntries)
/*     */   {
/*  53 */     this.ctx = cordova;
/*  54 */     this.app = cordovaWebView;
/*  55 */     setPluginEntries(pluginEntries);
/*     */   }
/*     */   
/*     */   public Collection<PluginEntry> getPluginEntries() {
/*  59 */     return this.entryMap.values();
/*     */   }
/*     */   
/*     */   public void setPluginEntries(Collection<PluginEntry> pluginEntries) {
/*  63 */     if (this.isInitialized) {
/*  64 */       onPause(false);
/*  65 */       onDestroy();
/*  66 */       this.pluginMap.clear();
/*  67 */       this.entryMap.clear();
/*     */     }
/*  69 */     for (PluginEntry entry : pluginEntries) {
/*  70 */       addService(entry);
/*     */     }
/*  72 */     if (this.isInitialized) {
/*  73 */       startupPlugins();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void init()
/*     */   {
/*  81 */     LOG.d(TAG, "init()");
/*  82 */     this.isInitialized = true;
/*  83 */     onPause(false);
/*  84 */     onDestroy();
/*  85 */     this.pluginMap.clear();
/*  86 */     startupPlugins();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void startupPlugins()
/*     */   {
/*  93 */     for (PluginEntry entry : this.entryMap.values())
/*     */     {
/*     */ 
/*  96 */       if (entry.onload) {
/*  97 */         getPlugin(entry.service);
/*     */       } else {
/*  99 */         this.pluginMap.put(entry.service, null);
/*     */       }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void exec(String service, String action, String callbackId, String rawArgs)
/*     */   {
/* 122 */     CordovaPlugin plugin = getPlugin(service);
/* 123 */     if (plugin == null) {
/* 124 */       LOG.d(TAG, "exec() call to unknown plugin: " + service);
/* 125 */       PluginResult cr = new PluginResult(PluginResult.Status.CLASS_NOT_FOUND_EXCEPTION);
/* 126 */       this.app.sendPluginResult(cr, callbackId);
/* 127 */       return;
/*     */     }
/* 129 */     CallbackContext callbackContext = new CallbackContext(callbackId, this.app);
/*     */     try {
/* 131 */       long pluginStartTime = System.currentTimeMillis();
/* 132 */       boolean wasValidAction = plugin.execute(action, rawArgs, callbackContext);
/* 133 */       long duration = System.currentTimeMillis() - pluginStartTime;
/*     */       
/* 135 */       if (duration > SLOW_EXEC_WARNING_THRESHOLD) {
/* 136 */         LOG.w(TAG, "THREAD WARNING: exec() call to " + service + "." + action + " blocked the main thread for " + duration + "ms. Plugin should use CordovaInterface.getThreadPool().");
/*     */       }
/* 138 */       if (!wasValidAction) {
/* 139 */         PluginResult cr = new PluginResult(PluginResult.Status.INVALID_ACTION);
/* 140 */         callbackContext.sendPluginResult(cr);
/*     */       }
/*     */     } catch (JSONException e) {
/* 143 */       PluginResult cr = new PluginResult(PluginResult.Status.JSON_EXCEPTION);
/* 144 */       callbackContext.sendPluginResult(cr);
/*     */     } catch (Exception e) {
/* 146 */       LOG.e(TAG, "Uncaught exception from plugin", e);
/* 147 */       callbackContext.error(e.getMessage());
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
/*     */   public CordovaPlugin getPlugin(String service)
/*     */   {
/* 160 */     CordovaPlugin ret = (CordovaPlugin)this.pluginMap.get(service);
/* 161 */     if (ret == null) {
/* 162 */       PluginEntry pe = (PluginEntry)this.entryMap.get(service);
/* 163 */       if (pe == null) {
/* 164 */         return null;
/*     */       }
/* 166 */       if (pe.plugin != null) {
/* 167 */         ret = pe.plugin;
/*     */       } else {
/* 169 */         ret = instantiatePlugin(pe.pluginClass);
/*     */       }
/* 171 */       ret.privateInitialize(service, this.ctx, this.app, this.app.getPreferences());
/* 172 */       this.pluginMap.put(service, ret);
/*     */     }
/* 174 */     return ret;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addService(String service, String className)
/*     */   {
/* 185 */     PluginEntry entry = new PluginEntry(service, className, false);
/* 186 */     addService(entry);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addService(PluginEntry entry)
/*     */   {
/* 196 */     this.entryMap.put(entry.service, entry);
/* 197 */     if (entry.plugin != null) {
/* 198 */       entry.plugin.privateInitialize(entry.service, this.ctx, this.app, this.app.getPreferences());
/* 199 */       this.pluginMap.put(entry.service, entry.plugin);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onPause(boolean multitasking)
/*     */   {
/* 209 */     for (CordovaPlugin plugin : this.pluginMap.values()) {
/* 210 */       if (plugin != null) {
/* 211 */         plugin.onPause(multitasking);
/*     */       }
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
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean onReceivedHttpAuthRequest(CordovaWebView view, ICordovaHttpAuthHandler handler, String host, String realm)
/*     */   {
/* 229 */     for (CordovaPlugin plugin : this.pluginMap.values()) {
/* 230 */       if ((plugin != null) && (plugin.onReceivedHttpAuthRequest(this.app, handler, host, realm))) {
/* 231 */         return true;
/*     */       }
/*     */     }
/* 234 */     return false;
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
/* 248 */     for (CordovaPlugin plugin : this.pluginMap.values()) {
/* 249 */       if ((plugin != null) && (plugin.onReceivedClientCertRequest(this.app, request))) {
/* 250 */         return true;
/*     */       }
/*     */     }
/* 253 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onResume(boolean multitasking)
/*     */   {
/* 262 */     for (CordovaPlugin plugin : this.pluginMap.values()) {
/* 263 */       if (plugin != null) {
/* 264 */         plugin.onResume(multitasking);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onStart()
/*     */   {
/* 273 */     for (CordovaPlugin plugin : this.pluginMap.values()) {
/* 274 */       if (plugin != null) {
/* 275 */         plugin.onStart();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onStop()
/*     */   {
/* 284 */     for (CordovaPlugin plugin : this.pluginMap.values()) {
/* 285 */       if (plugin != null) {
/* 286 */         plugin.onStop();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onDestroy()
/*     */   {
/* 295 */     for (CordovaPlugin plugin : this.pluginMap.values()) {
/* 296 */       if (plugin != null) {
/* 297 */         plugin.onDestroy();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object postMessage(String id, Object data)
/*     */   {
/* 310 */     for (CordovaPlugin plugin : this.pluginMap.values()) {
/* 311 */       if (plugin != null) {
/* 312 */         Object obj = plugin.onMessage(id, data);
/* 313 */         if (obj != null) {
/* 314 */           return obj;
/*     */         }
/*     */       }
/*     */     }
/* 318 */     return this.ctx.onMessage(id, data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onNewIntent(Intent intent)
/*     */   {
/* 325 */     for (CordovaPlugin plugin : this.pluginMap.values()) {
/* 326 */       if (plugin != null) {
/* 327 */         plugin.onNewIntent(intent);
/*     */       }
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
/*     */ 
/*     */ 
/*     */   public boolean shouldAllowRequest(String url)
/*     */   {
/* 344 */     for (PluginEntry entry : this.entryMap.values()) {
/* 345 */       CordovaPlugin plugin = (CordovaPlugin)this.pluginMap.get(entry.service);
/* 346 */       if (plugin != null) {
/* 347 */         Boolean result = plugin.shouldAllowRequest(url);
/* 348 */         if (result != null) {
/* 349 */           return result.booleanValue();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 355 */     if ((url.startsWith("blob:")) || (url.startsWith("data:")) || (url.startsWith("about:blank"))) {
/* 356 */       return true;
/*     */     }
/*     */     
/* 359 */     if (url.startsWith("https://ssl.gstatic.com/accessibility/javascript/android/")) {
/* 360 */       return true;
/*     */     }
/* 362 */     if (url.startsWith("file://"))
/*     */     {
/*     */ 
/* 365 */       return !url.contains("/app_webview/");
/*     */     }
/* 367 */     return false;
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
/*     */   public boolean shouldAllowNavigation(String url)
/*     */   {
/* 382 */     for (PluginEntry entry : this.entryMap.values()) {
/* 383 */       CordovaPlugin plugin = (CordovaPlugin)this.pluginMap.get(entry.service);
/* 384 */       if (plugin != null) {
/* 385 */         Boolean result = plugin.shouldAllowNavigation(url);
/* 386 */         if (result != null) {
/* 387 */           return result.booleanValue();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 393 */     return (url.startsWith("file://")) || (url.startsWith("about:blank"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean shouldAllowBridgeAccess(String url)
/*     */   {
/* 401 */     for (PluginEntry entry : this.entryMap.values()) {
/* 402 */       CordovaPlugin plugin = (CordovaPlugin)this.pluginMap.get(entry.service);
/* 403 */       if (plugin != null) {
/* 404 */         Boolean result = plugin.shouldAllowBridgeAccess(url);
/* 405 */         if (result != null) {
/* 406 */           return result.booleanValue();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 412 */     return url.startsWith("file://");
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
/*     */   public Boolean shouldOpenExternalUrl(String url)
/*     */   {
/* 428 */     for (PluginEntry entry : this.entryMap.values()) {
/* 429 */       CordovaPlugin plugin = (CordovaPlugin)this.pluginMap.get(entry.service);
/* 430 */       if (plugin != null) {
/* 431 */         Boolean result = plugin.shouldOpenExternalUrl(url);
/* 432 */         if (result != null) {
/* 433 */           return result;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 439 */     return Boolean.valueOf(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean onOverrideUrlLoading(String url)
/*     */   {
/* 449 */     for (PluginEntry entry : this.entryMap.values()) {
/* 450 */       CordovaPlugin plugin = (CordovaPlugin)this.pluginMap.get(entry.service);
/* 451 */       if ((plugin != null) && (plugin.onOverrideUrlLoading(url))) {
/* 452 */         return true;
/*     */       }
/*     */     }
/* 455 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onReset()
/*     */   {
/* 462 */     for (CordovaPlugin plugin : this.pluginMap.values()) {
/* 463 */       if (plugin != null) {
/* 464 */         plugin.onReset();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   Uri remapUri(Uri uri) {
/* 470 */     for (CordovaPlugin plugin : this.pluginMap.values()) {
/* 471 */       if (plugin != null) {
/* 472 */         Uri ret = plugin.remapUri(uri);
/* 473 */         if (ret != null) {
/* 474 */           return ret;
/*     */         }
/*     */       }
/*     */     }
/* 478 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private CordovaPlugin instantiatePlugin(String className)
/*     */   {
/* 485 */     CordovaPlugin ret = null;
/*     */     try {
/* 487 */       Class<?> c = null;
/* 488 */       if ((className != null) && (!"".equals(className))) {
/* 489 */         c = Class.forName(className);
/*     */       }
/* 491 */       if ((c != null & CordovaPlugin.class.isAssignableFrom(c))) {
/* 492 */         ret = (CordovaPlugin)c.newInstance();
/*     */       }
/*     */     } catch (Exception e) {
/* 495 */       e.printStackTrace();
/* 496 */       System.out.println("Error adding plugin " + className + ".");
/*     */     }
/* 498 */     return ret;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onConfigurationChanged(Configuration newConfig)
/*     */   {
/* 507 */     for (CordovaPlugin plugin : this.pluginMap.values()) {
/* 508 */       if (plugin != null) {
/* 509 */         plugin.onConfigurationChanged(newConfig);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public Bundle onSaveInstanceState() {
/* 515 */     Bundle state = new Bundle();
/* 516 */     for (CordovaPlugin plugin : this.pluginMap.values()) {
/* 517 */       if (plugin != null) {
/* 518 */         Bundle pluginState = plugin.onSaveInstanceState();
/* 519 */         if (pluginState != null) {
/* 520 */           state.putBundle(plugin.getServiceName(), pluginState);
/*     */         }
/*     */       }
/*     */     }
/* 524 */     return state;
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\PluginManager.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */