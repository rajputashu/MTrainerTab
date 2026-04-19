/*    */ package org.apache.cordova;
/*    */ 
/*    */

import org.json.JSONException;

import java.util.Arrays;
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
/*    */ public class PermissionHelper
/*    */ {
/*    */   private static final String LOG_TAG = "CordovaPermissionHelper";
/*    */   
/*    */   public static void requestPermission(CordovaPlugin plugin, int requestCode, String permission)
/*    */   {
/* 45 */     requestPermissions(plugin, requestCode, new String[] { permission });
/*    */   }
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
/*    */   public static void requestPermissions(CordovaPlugin plugin, int requestCode, String[] permissions)
/*    */   {
/* 59 */     plugin.cordova.requestPermissions(plugin, requestCode, permissions);
/*    */   }
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
/*    */   public static boolean hasPermission(CordovaPlugin plugin, String permission)
/*    */   {
/* 73 */     return plugin.cordova.hasPermission(permission);
/*    */   }
/*    */   
/*    */   private static void deliverPermissionResult(CordovaPlugin plugin, int requestCode, String[] permissions)
/*    */   {
/* 78 */     int[] requestResults = new int[permissions.length];
/* 79 */     Arrays.fill(requestResults, 0);
/*    */     try
/*    */     {
/* 82 */       plugin.onRequestPermissionResult(requestCode, permissions, requestResults);
/*    */     } catch (JSONException e) {
/* 84 */       LOG.e("CordovaPermissionHelper", "JSONException when delivering permissions results", e);
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\PermissionHelper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */