/*    */ package org.apache.cordova;
/*    */ 
/*    */

import android.app.Activity;

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
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class Config
/*    */ {
/*    */   private static final String TAG = "Config";
/*    */   static ConfigXmlParser parser;
/*    */   
/*    */   public static void init(Activity action)
/*    */   {
/* 36 */     parser = new ConfigXmlParser();
/* 37 */     parser.parse(action);
/*    */     
/* 39 */     parser.getPreferences().setPreferencesBundle(action.getIntent().getExtras());
/*    */   }
/*    */   
/*    */   public static void init()
/*    */   {
/* 44 */     if (parser == null) {
/* 45 */       parser = new ConfigXmlParser();
/*    */     }
/*    */   }
/*    */   
/*    */   public static String getStartUrl() {
/* 50 */     if (parser == null) {
/* 51 */       return "file:///android_asset/www/index.html";
/*    */     }
/* 53 */     return parser.getLaunchUrl();
/*    */   }
/*    */   
/*    */   public static String getErrorUrl() {
/* 57 */     return parser.getPreferences().getString("errorurl", null);
/*    */   }
/*    */   
/*    */   public static List<PluginEntry> getPluginEntries() {
/* 61 */     return parser.getPluginEntries();
/*    */   }
/*    */   
/*    */   public static CordovaPreferences getPreferences() {
/* 65 */     return parser.getPreferences();
/*    */   }
/*    */   
/*    */   public static boolean isInitialized() {
/* 69 */     return parser != null;
/*    */   }
/*    */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\Config.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */