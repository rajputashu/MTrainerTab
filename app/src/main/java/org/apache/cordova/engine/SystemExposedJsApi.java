/*    */ package org.apache.cordova.engine;
/*    */ 
/*    */

import android.webkit.JavascriptInterface;

import org.apache.cordova.CordovaBridge;
import org.apache.cordova.ExposedJsApi;
import org.json.JSONException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class SystemExposedJsApi
/*    */   implements ExposedJsApi
/*    */ {
/*    */   private final CordovaBridge bridge;
/*    */   
/*    */   SystemExposedJsApi(CordovaBridge bridge)
/*    */   {
/* 36 */     this.bridge = bridge;
/*    */   }
/*    */   
/*    */   @JavascriptInterface
/*    */   public String exec(int bridgeSecret, String service, String action, String callbackId, String arguments) throws JSONException, IllegalAccessException {
/* 41 */     return this.bridge.jsExec(bridgeSecret, service, action, callbackId, arguments);
/*    */   }
/*    */   
/*    */   @JavascriptInterface
/*    */   public void setNativeToJsBridgeMode(int bridgeSecret, int value) throws IllegalAccessException {
/* 46 */     this.bridge.jsSetNativeToJsBridgeMode(bridgeSecret, value);
/*    */   }
/*    */   
/*    */   @JavascriptInterface
/*    */   public String retrieveJsMessages(int bridgeSecret, boolean fromOnlineEvent) throws IllegalAccessException {
/* 51 */     return this.bridge.jsRetrieveJsMessages(bridgeSecret, fromOnlineEvent);
/*    */   }
/*    */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\engine\SystemExposedJsApi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */