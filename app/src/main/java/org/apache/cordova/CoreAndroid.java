/*     */ package org.apache.cordova;
/*     */ 
/*     */

/*     */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
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
/*     */ public class CoreAndroid
/*     */   extends CordovaPlugin
/*     */ {
/*     */   public static final String PLUGIN_NAME = "CoreAndroid";
/*     */   protected static final String TAG = "CordovaApp";
/*     */   private BroadcastReceiver telephonyReceiver;
/*     */   private CallbackContext messageChannel;
/*     */   private PluginResult pendingResume;
/*  46 */   private final Object messageChannelLock = new Object();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void fireJavascriptEvent(String action)
/*     */   {
/*  54 */     sendEventMessage(action);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void pluginInitialize()
/*     */   {
/*  63 */     initTelephonyReceiver();
/*     */   }
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
/*  75 */     PluginResult.Status status = PluginResult.Status.OK;
/*  76 */     String result = "";
/*     */     try
/*     */     {
/*  79 */       if (action.equals("clearCache")) {
/*  80 */         clearCache();
/*     */       }
/*  82 */       else if (action.equals("show"))
/*     */       {
/*     */ 
/*     */ 
/*  86 */         this.cordova.getActivity().runOnUiThread(new Runnable() {
/*     */           public void run() {
/*  88 */             CoreAndroid.this.webView.getPluginManager().postMessage("spinner", "stop");
/*     */           }
/*     */           
/*     */         });
/*  92 */       } else if (action.equals("loadUrl")) {
/*  93 */         loadUrl(args.getString(0), args.optJSONObject(1));
/*     */       }
/*  95 */       else if (!action.equals("cancelLoadUrl"))
/*     */       {
/*     */ 
/*  98 */         if (action.equals("clearHistory")) {
/*  99 */           clearHistory();
/*     */         }
/* 101 */         else if (action.equals("backHistory")) {
/* 102 */           backHistory();
/*     */         }
/* 104 */         else if (action.equals("overrideButton")) {
/* 105 */           overrideButton(args.getString(0), args.getBoolean(1));
/*     */         }
/* 107 */         else if (action.equals("overrideBackbutton")) {
/* 108 */           overrideBackbutton(args.getBoolean(0));
/*     */         }
/* 110 */         else if (action.equals("exitApp")) {
/* 111 */           exitApp();
/*     */         }
/* 113 */         else if (action.equals("messageChannel")) {
/* 114 */           synchronized (this.messageChannelLock) {
/* 115 */             this.messageChannel = callbackContext;
/* 116 */             if (this.pendingResume != null) {
/* 117 */               sendEventMessage(this.pendingResume);
/* 118 */               this.pendingResume = null;
/*     */             }
/*     */           }
/* 121 */           return true;
/*     */         }
/*     */       }
/* 124 */       callbackContext.sendPluginResult(new PluginResult(status, result));
/* 125 */       return true;
/*     */     } catch (JSONException e) {
/* 127 */       callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION)); }
/* 128 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void clearCache()
/*     */   {
/* 140 */     this.cordova.getActivity().runOnUiThread(new Runnable() {
/*     */       public void run() {
/* 142 */         CoreAndroid.this.webView.clearCache(true);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void loadUrl(String url, JSONObject props)
/*     */     throws JSONException
/*     */   {
/* 155 */     LOG.d("App", "App.loadUrl(" + url + "," + props + ")");
/* 156 */     int wait = 0;
/* 157 */     boolean openExternal = false;
/* 158 */     boolean clearHistory = false;
/*     */     
/*     */ 
/* 161 */     HashMap<String, Object> params = new HashMap();
/* 162 */     if (props != null) {
/* 163 */       JSONArray keys = props.names();
/* 164 */       for (int i = 0; i < keys.length(); i++) {
/* 165 */         String key = keys.getString(i);
/* 166 */         if (key.equals("wait")) {
/* 167 */           wait = props.getInt(key);
/*     */         }
/* 169 */         else if (key.equalsIgnoreCase("openexternal")) {
/* 170 */           openExternal = props.getBoolean(key);
/*     */         }
/* 172 */         else if (key.equalsIgnoreCase("clearhistory")) {
/* 173 */           clearHistory = props.getBoolean(key);
/*     */         }
/*     */         else {
/* 176 */           Object value = props.get(key);
/* 177 */           if (value != null)
/*     */           {
/*     */ 
/* 180 */             if (value.getClass().equals(String.class)) {
/* 181 */               params.put(key, (String)value);
/*     */             }
/* 183 */             else if (value.getClass().equals(Boolean.class)) {
/* 184 */               params.put(key, (Boolean)value);
/*     */             }
/* 186 */             else if (value.getClass().equals(Integer.class)) {
/* 187 */               params.put(key, (Integer)value);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 195 */     if (wait > 0) {
/*     */       try {
/* 197 */         synchronized (this) {
/* 198 */           wait(wait);
/*     */         }
/*     */       } catch (InterruptedException e) {
/* 201 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 204 */     this.webView.showWebPage(url, openExternal, clearHistory, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clearHistory()
/*     */   {
/* 211 */     this.cordova.getActivity().runOnUiThread(new Runnable() {
/*     */       public void run() {
/* 213 */         CoreAndroid.this.webView.clearHistory();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void backHistory()
/*     */   {
/* 223 */     this.cordova.getActivity().runOnUiThread(new Runnable() {
/*     */       public void run() {
/* 225 */         CoreAndroid.this.webView.backHistory();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void overrideBackbutton(boolean override)
/*     */   {
/* 237 */     LOG.i("App", "WARNING: Back Button Default Behavior will be overridden.  The backbutton event will be fired!");
/* 238 */     this.webView.setButtonPlumbedToJs(4, override);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void overrideButton(String button, boolean override)
/*     */   {
/* 249 */     LOG.i("App", "WARNING: Volume Button Default Behavior will be overridden.  The volume event will be fired!");
/* 250 */     if (button.equals("volumeup")) {
/* 251 */       this.webView.setButtonPlumbedToJs(24, override);
/*     */     }
/* 253 */     else if (button.equals("volumedown")) {
/* 254 */       this.webView.setButtonPlumbedToJs(25, override);
/*     */     }
/* 256 */     else if (button.equals("menubutton")) {
/* 257 */       this.webView.setButtonPlumbedToJs(82, override);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isBackbuttonOverridden()
/*     */   {
/* 267 */     return this.webView.isButtonPlumbedToJs(4);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void exitApp()
/*     */   {
/* 274 */     this.webView.getPluginManager().postMessage("exit", null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initTelephonyReceiver()
/*     */   {
/* 284 */     IntentFilter intentFilter = new IntentFilter();
/* 285 */     intentFilter.addAction("android.intent.action.PHONE_STATE");
/*     */     
/* 287 */     this.telephonyReceiver = new BroadcastReceiver()
/*     */     {
/*     */ 
/*     */       public void onReceive(Context context, Intent intent)
/*     */       {
/*     */ 
/* 293 */         if ((intent != null) && (intent.getAction().equals("android.intent.action.PHONE_STATE")) && 
/* 294 */           (intent.hasExtra("state"))) {
/* 295 */           String extraData = intent.getStringExtra("state");
/* 296 */           if (extraData.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
/* 297 */             LOG.i("CordovaApp", "Telephone RINGING");
/* 298 */             CoreAndroid.this.webView.getPluginManager().postMessage("telephone", "ringing");
/*     */           }
/* 300 */           else if (extraData.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
/* 301 */             LOG.i("CordovaApp", "Telephone OFFHOOK");
/* 302 */             CoreAndroid.this.webView.getPluginManager().postMessage("telephone", "offhook");
/*     */           }
/* 304 */           else if (extraData.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
/* 305 */             LOG.i("CordovaApp", "Telephone IDLE");
/* 306 */             CoreAndroid.this.webView.getPluginManager().postMessage("telephone", "idle");
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 313 */     };
/* 314 */     this.webView.getContext().registerReceiver(this.telephonyReceiver, intentFilter);
/*     */   }
/*     */   
/*     */   private void sendEventMessage(String action) {
/* 318 */     JSONObject obj = new JSONObject();
/*     */     try {
/* 320 */       obj.put("action", action);
/*     */     } catch (JSONException e) {
/* 322 */       LOG.e("CordovaApp", "Failed to create event message", e);
/*     */     }
/* 324 */     sendEventMessage(new PluginResult(PluginResult.Status.OK, obj));
/*     */   }
/*     */   
/*     */   private void sendEventMessage(PluginResult payload) {
/* 328 */     payload.setKeepCallback(true);
/* 329 */     if (this.messageChannel != null) {
/* 330 */       this.messageChannel.sendPluginResult(payload);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onDestroy()
/*     */   {
/* 340 */     this.webView.getContext().unregisterReceiver(this.telephonyReceiver);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void sendResumeEvent(PluginResult resumeEvent)
/*     */   {
/* 351 */     synchronized (this.messageChannelLock) {
/* 352 */       if (this.messageChannel != null) {
/* 353 */         sendEventMessage(resumeEvent);
/*     */       }
/*     */       else
/*     */       {
/* 357 */         this.pendingResume = resumeEvent;
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
/*     */   public static Object getBuildConfigValue(Context ctx, String key)
/*     */   {
/*     */     try
/*     */     {
/* 375 */       Class<?> clazz = Class.forName(ctx.getPackageName() + ".BuildConfig");
/* 376 */       Field field = clazz.getField(key);
/* 377 */       return field.get(null);
/*     */     } catch (ClassNotFoundException e) {
/* 379 */       LOG.d("CordovaApp", "Unable to get the BuildConfig, is this built with ANT?");
/* 380 */       e.printStackTrace();
/*     */     } catch (NoSuchFieldException e) {
/* 382 */       LOG.d("CordovaApp", key + " is not a valid field. Check your build.gradle");
/*     */     } catch (IllegalAccessException e) {
/* 384 */       LOG.d("CordovaApp", "Illegal Access Exception: Let's print a stack trace.");
/* 385 */       e.printStackTrace();
/*     */     }
/*     */     
/* 388 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\CoreAndroid.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */