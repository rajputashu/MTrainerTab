/*    */ package org.apache.cordova;
/*    */ 
/*    */

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
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
/*    */ public class ResumeCallback
/*    */   extends CallbackContext
/*    */ {
/* 29 */   private final String TAG = "CordovaResumeCallback";
/*    */   private String serviceName;
/*    */   private PluginManager pluginManager;
/*    */   
/*    */   public ResumeCallback(String serviceName, PluginManager pluginManager) {
/* 34 */     super("resumecallback", null);
/* 35 */     this.serviceName = serviceName;
/* 36 */     this.pluginManager = pluginManager;
/*    */   }
/*    */   
/*    */   public void sendPluginResult(PluginResult pluginResult)
/*    */   {
/* 41 */     synchronized (this) {
/* 42 */       if (this.finished) {
/* 43 */         LOG.w("CordovaResumeCallback", this.serviceName + " attempted to send a second callback to ResumeCallback\nResult was: " + pluginResult.getMessage());
/* 44 */         return;
/*    */       }
/* 46 */       this.finished = true;
/*    */     }
/*    */     
/*    */ 
/* 50 */     JSONObject event = new JSONObject();
/* 51 */     JSONObject pluginResultObject = new JSONObject();
/*    */     try
/*    */     {
/* 54 */       pluginResultObject.put("pluginServiceName", this.serviceName);
/* 55 */       pluginResultObject.put("pluginStatus", PluginResult.StatusMessages[pluginResult.getStatus()]);
/*    */       
/* 57 */       event.put("action", "resume");
/* 58 */       event.put("pendingResult", pluginResultObject);
/*    */     } catch (JSONException e) {
/* 60 */       LOG.e("CordovaResumeCallback", "Unable to create resume object for Activity Result");
/*    */     }
/*    */     
/* 63 */     PluginResult eventResult = new PluginResult(PluginResult.Status.OK, event);
/*    */     
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 69 */     List<PluginResult> result = new ArrayList();
/* 70 */     result.add(eventResult);
/* 71 */     result.add(pluginResult);
/*    */     
/* 73 */     CoreAndroid appPlugin = (CoreAndroid)this.pluginManager.getPlugin("CoreAndroid");
/* 74 */     appPlugin.sendResumeEvent(new PluginResult(PluginResult.Status.OK, result));
/*    */   }
/*    */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\ResumeCallback.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */