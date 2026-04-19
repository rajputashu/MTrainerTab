/*     */ package org.apache.cordova;
/*     */ 
/*     */

import android.app.Activity;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
/*     */ public class CordovaInterfaceImpl
/*     */   implements CordovaInterface
/*     */ {
/*     */   private static final String TAG = "CordovaInterfaceImpl";
/*     */   protected Activity activity;
/*     */   protected ExecutorService threadPool;
/*     */   protected PluginManager pluginManager;
/*     */   protected ActivityResultHolder savedResult;
/*     */   protected CallbackMap permissionResultCallbacks;
/*     */   protected CordovaPlugin activityResultCallback;
/*     */   protected String initCallbackService;
/*     */   protected int activityResultRequestCode;
/*  49 */   protected boolean activityWasDestroyed = false;
/*     */   protected Bundle savedPluginState;
/*     */   
/*     */   public CordovaInterfaceImpl(Activity activity) {
/*  53 */     this(activity, Executors.newCachedThreadPool());
/*     */   }
/*     */   
/*     */   public CordovaInterfaceImpl(Activity activity, ExecutorService threadPool) {
/*  57 */     this.activity = activity;
/*  58 */     this.threadPool = threadPool;
/*  59 */     this.permissionResultCallbacks = new CallbackMap();
/*     */   }
/*     */   
/*     */   public void startActivityForResult(CordovaPlugin command, Intent intent, int requestCode)
/*     */   {
/*  64 */     setActivityResultCallback(command);
/*     */     try {
/*  66 */       this.activity.startActivityForResult(intent, requestCode);
/*     */     } catch (RuntimeException e) {
/*  68 */       this.activityResultCallback = null;
/*  69 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void setActivityResultCallback(CordovaPlugin plugin)
/*     */   {
/*  76 */     if (this.activityResultCallback != null) {
/*  77 */       this.activityResultCallback.onActivityResult(this.activityResultRequestCode, 0, null);
/*     */     }
/*  79 */     this.activityResultCallback = plugin;
/*     */   }
/*     */   
/*     */   public Activity getActivity()
/*     */   {
/*  84 */     return this.activity;
/*     */   }
/*     */   
/*     */   public Object onMessage(String id, Object data)
/*     */   {
/*  89 */     if ("exit".equals(id)) {
/*  90 */       this.activity.finish();
/*     */     }
/*  92 */     return null;
/*     */   }
/*     */   
/*     */   public ExecutorService getThreadPool()
/*     */   {
/*  97 */     return this.threadPool;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onCordovaInit(PluginManager pluginManager)
/*     */   {
/* 105 */     this.pluginManager = pluginManager;
/* 106 */     if (this.savedResult != null) {
/* 107 */       onActivityResult(this.savedResult.requestCode, this.savedResult.resultCode, this.savedResult.intent);
/* 108 */     } else if (this.activityWasDestroyed)
/*     */     {
/*     */ 
/* 111 */       this.activityWasDestroyed = false;
/* 112 */       if (pluginManager != null)
/*     */       {
/* 114 */         CoreAndroid appPlugin = (CoreAndroid)pluginManager.getPlugin("CoreAndroid");
/* 115 */         if (appPlugin != null) {
/* 116 */           JSONObject obj = new JSONObject();
/*     */           try {
/* 118 */             obj.put("action", "resume");
/*     */           } catch (JSONException e) {
/* 120 */             LOG.e("CordovaInterfaceImpl", "Failed to create event message", e);
/*     */           }
/* 122 */           appPlugin.sendResumeEvent(new PluginResult(PluginResult.Status.OK, obj));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean onActivityResult(int requestCode, int resultCode, Intent intent)
/*     */   {
/* 133 */     CordovaPlugin callback = this.activityResultCallback;
/* 134 */     if ((callback == null) && (this.initCallbackService != null))
/*     */     {
/*     */ 
/* 137 */       this.savedResult = new ActivityResultHolder(requestCode, resultCode, intent);
/* 138 */       if (this.pluginManager != null) {
/* 139 */         callback = this.pluginManager.getPlugin(this.initCallbackService);
/* 140 */         if (callback != null) {
/* 141 */           callback.onRestoreStateForActivityResult(this.savedPluginState.getBundle(callback.getServiceName()), new ResumeCallback(callback.getServiceName(), this.pluginManager));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 146 */     this.activityResultCallback = null;
/*     */     
/* 148 */     if (callback != null) {
/* 149 */       LOG.d("CordovaInterfaceImpl", "Sending activity result to plugin");
/* 150 */       this.initCallbackService = null;
/* 151 */       this.savedResult = null;
/* 152 */       callback.onActivityResult(requestCode, resultCode, intent);
/* 153 */       return true;
/*     */     }
/* 155 */     LOG.w("CordovaInterfaceImpl", "Got an activity result, but no plugin was registered to receive it" + (this.savedResult != null ? " yet!" : "."));
/* 156 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setActivityResultRequestCode(int requestCode)
/*     */   {
/* 165 */     this.activityResultRequestCode = requestCode;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onSaveInstanceState(Bundle outState)
/*     */   {
/* 172 */     if (this.activityResultCallback != null) {
/* 173 */       String serviceName = this.activityResultCallback.getServiceName();
/* 174 */       outState.putString("callbackService", serviceName);
/*     */     }
/* 176 */     if (this.pluginManager != null) {
/* 177 */       outState.putBundle("plugin", this.pluginManager.onSaveInstanceState());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void restoreInstanceState(Bundle savedInstanceState)
/*     */   {
/* 186 */     this.initCallbackService = savedInstanceState.getString("callbackService");
/* 187 */     this.savedPluginState = savedInstanceState.getBundle("plugin");
/* 188 */     this.activityWasDestroyed = true;
/*     */   }
/*     */   
/*     */   private static class ActivityResultHolder {
/*     */     private int requestCode;
/*     */     private int resultCode;
/*     */     private Intent intent;
/*     */     
/*     */     public ActivityResultHolder(int requestCode, int resultCode, Intent intent) {
/* 197 */       this.requestCode = requestCode;
/* 198 */       this.resultCode = resultCode;
/* 199 */       this.intent = intent;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
/*     */     throws JSONException
/*     */   {
/* 212 */     Pair<CordovaPlugin, Integer> callback = this.permissionResultCallbacks.getAndRemoveCallback(requestCode);
/* 213 */     if (callback != null) {
/* 214 */       ((CordovaPlugin)callback.first).onRequestPermissionResult(((Integer)callback.second).intValue(), permissions, grantResults);
/*     */     }
/*     */   }
/*     */   
/*     */   public void requestPermission(CordovaPlugin plugin, int requestCode, String permission) {
/* 219 */     String[] permissions = new String[1];
/* 220 */     permissions[0] = permission;
/* 221 */     requestPermissions(plugin, requestCode, permissions);
/*     */   }
/*     */   
/*     */   public void requestPermissions(CordovaPlugin plugin, int requestCode, String[] permissions) {
/* 225 */     int mappedRequestCode = this.permissionResultCallbacks.registerCallback(plugin, requestCode);
/* 226 */     getActivity().requestPermissions(permissions, mappedRequestCode);
/*     */   }
/*     */   
/*     */   public boolean hasPermission(String permission)
/*     */   {
/* 231 */     if (VERSION.SDK_INT >= 23)
/*     */     {
/* 233 */       int result = this.activity.checkSelfPermission(permission);
/* 234 */       return 0 == result;
/*     */     }
/*     */     
/*     */ 
/* 238 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\CordovaInterfaceImpl.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */