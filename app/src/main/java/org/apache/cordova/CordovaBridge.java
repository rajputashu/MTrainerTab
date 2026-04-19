/*     */ package org.apache.cordova;
/*     */ 
/*     */

import org.json.JSONArray;
import org.json.JSONException;

import java.security.SecureRandom;
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
/*     */ public class CordovaBridge
/*     */ {
/*     */   private static final String LOG_TAG = "CordovaBridge";
/*     */   private PluginManager pluginManager;
/*     */   private NativeToJsMessageQueue jsMessageQueue;
/*  35 */   private volatile int expectedBridgeSecret = -1;
/*     */   
/*     */   public CordovaBridge(PluginManager pluginManager, NativeToJsMessageQueue jsMessageQueue) {
/*  38 */     this.pluginManager = pluginManager;
/*  39 */     this.jsMessageQueue = jsMessageQueue;
/*     */   }
/*     */   
/*     */   public String jsExec(int bridgeSecret, String service, String action, String callbackId, String arguments) throws JSONException, IllegalAccessException {
/*  43 */     if (!verifySecret("exec()", bridgeSecret)) {
/*  44 */       return null;
/*     */     }
/*     */     
/*     */ 
/*  48 */     if (arguments == null) {
/*  49 */       return "@Null arguments.";
/*     */     }
/*     */     
/*  52 */     this.jsMessageQueue.setPaused(true);
/*     */     try
/*     */     {
/*  55 */       CordovaResourceApi.jsThread = Thread.currentThread();
/*     */       
/*  57 */       this.pluginManager.exec(service, action, callbackId, arguments);
/*  58 */       String ret = null;
/*     */       
/*  60 */       ret = this.jsMessageQueue.popAndEncode(false);
/*     */       
/*  62 */       return ret;
/*     */     } catch (Throwable e) { String str1;
/*  64 */       e.printStackTrace();
/*  65 */       return "";
/*     */     } finally {
/*  67 */       this.jsMessageQueue.setPaused(false);
/*     */     }
/*     */   }
/*     */   
/*     */   public void jsSetNativeToJsBridgeMode(int bridgeSecret, int value) throws IllegalAccessException {
/*  72 */     if (!verifySecret("setNativeToJsBridgeMode()", bridgeSecret)) {
/*  73 */       return;
/*     */     }
/*  75 */     this.jsMessageQueue.setBridgeMode(value);
/*     */   }
/*     */   
/*     */   public String jsRetrieveJsMessages(int bridgeSecret, boolean fromOnlineEvent) throws IllegalAccessException {
/*  79 */     if (!verifySecret("retrieveJsMessages()", bridgeSecret)) {
/*  80 */       return null;
/*     */     }
/*  82 */     return this.jsMessageQueue.popAndEncode(fromOnlineEvent);
/*     */   }
/*     */   
/*     */   private boolean verifySecret(String action, int bridgeSecret) throws IllegalAccessException {
/*  86 */     if (!this.jsMessageQueue.isBridgeEnabled()) {
/*  87 */       if (bridgeSecret == -1) {
/*  88 */         LOG.d("CordovaBridge", action + " call made before bridge was enabled.");
/*     */       } else {
/*  90 */         LOG.d("CordovaBridge", "Ignoring " + action + " from previous page load.");
/*     */       }
/*  92 */       return false;
/*     */     }
/*     */     
/*  95 */     if ((this.expectedBridgeSecret < 0) || (bridgeSecret != this.expectedBridgeSecret)) {
/*  96 */       LOG.e("CordovaBridge", "Bridge access attempt with wrong secret token, possibly from malicious code. Disabling exec() bridge!");
/*  97 */       //clearBridgeSecret();  // commented from original file
					if (!this.jsMessageQueue.isBridgeEnabled()) { // new if inserted just to ignore warnings
						throw new IllegalAccessException();
					}
/*  98 */       //
				return true; // added to original file
/*     */     }
/* 100 */     return true;
/*     */   }
/*     */   
/*     */   void clearBridgeSecret()
/*     */   {
/* 105 */     this.expectedBridgeSecret = -1;
/*     */   }
/*     */   
/*     */   public boolean isSecretEstablished() {
/* 109 */     return this.expectedBridgeSecret != -1;
/*     */   }
/*     */   
/*     */   int generateBridgeSecret()
/*     */   {
/* 114 */     SecureRandom randGen = new SecureRandom();
/* 115 */     this.expectedBridgeSecret = randGen.nextInt(Integer.MAX_VALUE);
/* 116 */     return this.expectedBridgeSecret;
/*     */   }
/*     */   
/*     */   public void reset() {
/* 120 */     this.jsMessageQueue.reset();
/* 121 */     clearBridgeSecret();
/*     */   }
/*     */   
/*     */   public String promptOnJsPrompt(String origin, String message, String defaultValue) {
/* 125 */     if ((defaultValue != null) && (defaultValue.length() > 3) && (defaultValue.startsWith("gap:")))
/*     */     {
/*     */       try {
/* 128 */         JSONArray array = new JSONArray(defaultValue.substring(4));
/* 129 */         int bridgeSecret = array.getInt(0);
/* 130 */         String service = array.getString(1);
/* 131 */         String action = array.getString(2);
/* 132 */         String callbackId = array.getString(3);
/* 133 */         String r = jsExec(bridgeSecret, service, action, callbackId, message);
/* 134 */         return r == null ? "" : r;
/*     */       } catch (JSONException e) {
/* 136 */         e.printStackTrace();
/*     */       } catch (IllegalAccessException e) {
/* 138 */         e.printStackTrace();
/*     */       }
/* 140 */       return "";
/*     */     }
/*     */     
/* 143 */     if ((defaultValue != null) && (defaultValue.startsWith("gap_bridge_mode:"))) {
/*     */       try {
/* 145 */         int bridgeSecret = Integer.parseInt(defaultValue.substring(16));
/* 146 */         jsSetNativeToJsBridgeMode(bridgeSecret, Integer.parseInt(message));
/*     */       } catch (NumberFormatException e) {
/* 148 */         e.printStackTrace();
/*     */       } catch (IllegalAccessException e) {
/* 150 */         e.printStackTrace();
/*     */       }
/* 152 */       return "";
/*     */     }
/*     */     
/* 155 */     if ((defaultValue != null) && (defaultValue.startsWith("gap_poll:"))) {
/* 156 */       int bridgeSecret = Integer.parseInt(defaultValue.substring(9));
/*     */       try {
/* 158 */         String r = jsRetrieveJsMessages(bridgeSecret, "1".equals(message));
/* 159 */         return r == null ? "" : r;
/*     */       } catch (IllegalAccessException e) {
/* 161 */         e.printStackTrace();
/*     */         
/* 163 */         return "";
/*     */       } }
/* 165 */     if ((defaultValue != null) && (defaultValue.startsWith("gap_init:")))
/*     */     {
/*     */ 
/* 168 */       if (this.pluginManager.shouldAllowBridgeAccess(origin))
/*     */       {
/* 170 */         int bridgeMode = Integer.parseInt(defaultValue.substring(9));
/* 171 */         this.jsMessageQueue.setBridgeMode(bridgeMode);
/*     */         
/* 173 */         int secret = generateBridgeSecret();
/* 174 */         return "" + secret;
/*     */       }
/* 176 */       LOG.e("CordovaBridge", "gap_init called from restricted origin: " + origin);
/*     */       
/* 178 */       return "";
/*     */     }
/* 180 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\CordovaBridge.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */