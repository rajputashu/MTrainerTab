/*     */ package org.apache.cordova;
/*     */ 
/*     */

import org.json.JSONArray;
import org.json.JSONObject;
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
/*     */ public class CallbackContext
/*     */ {
/*     */   private static final String LOG_TAG = "CordovaPlugin";
/*     */   private String callbackId;
/*     */   private CordovaWebView webView;
/*     */   protected boolean finished;
/*     */   private int changingThreads;
/*     */   
/*     */   public CallbackContext(String callbackId, CordovaWebView webView)
/*     */   {
/*  36 */     this.callbackId = callbackId;
/*  37 */     this.webView = webView;
/*     */   }
/*     */   
/*     */   public boolean isFinished() {
/*  41 */     return this.finished;
/*     */   }
/*     */   
/*     */   public boolean isChangingThreads() {
/*  45 */     return this.changingThreads > 0;
/*     */   }
/*     */   
/*     */   public String getCallbackId() {
/*  49 */     return this.callbackId;
/*     */   }
/*     */   
/*     */   public void sendPluginResult(PluginResult pluginResult) {
/*  53 */     synchronized (this) {
/*  54 */       if (this.finished) {
/*  55 */         LOG.w("CordovaPlugin", "Attempted to send a second callback for ID: " + this.callbackId + "\nResult was: " + pluginResult.getMessage());
/*  56 */         return;
/*     */       }
/*  58 */       this.finished = (!pluginResult.getKeepCallback());
/*     */     }
/*     */     
/*  61 */     this.webView.sendPluginResult(pluginResult, this.callbackId);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void success(JSONObject message)
/*     */   {
/*  70 */     sendPluginResult(new PluginResult(PluginResult.Status.OK, message));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void success(String message)
/*     */   {
/*  79 */     sendPluginResult(new PluginResult(PluginResult.Status.OK, message));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void success(JSONArray message)
/*     */   {
/*  88 */     sendPluginResult(new PluginResult(PluginResult.Status.OK, message));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void success(byte[] message)
/*     */   {
/*  97 */     sendPluginResult(new PluginResult(PluginResult.Status.OK, message));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void success(int message)
/*     */   {
/* 106 */     sendPluginResult(new PluginResult(PluginResult.Status.OK, message));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void success()
/*     */   {
/* 113 */     sendPluginResult(new PluginResult(PluginResult.Status.OK));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void error(JSONObject message)
/*     */   {
/* 122 */     sendPluginResult(new PluginResult(PluginResult.Status.ERROR, message));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void error(String message)
/*     */   {
/* 131 */     sendPluginResult(new PluginResult(PluginResult.Status.ERROR, message));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void error(int message)
/*     */   {
/* 140 */     sendPluginResult(new PluginResult(PluginResult.Status.ERROR, message));
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\CallbackContext.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */