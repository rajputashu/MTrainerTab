/*    */ package org.apache.cordova;
/*    */ 
/*    */

import android.os.Bundle;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
/*    */ public class CordovaPreferences
/*    */ {
/* 32 */   private HashMap<String, String> prefs = new HashMap(20);
/*    */   private Bundle preferencesBundleExtras;
/*    */   
/*    */   public void setPreferencesBundle(Bundle extras) {
/* 36 */     this.preferencesBundleExtras = extras;
/*    */   }
/*    */   
/*    */   public void set(String name, String value) {
/* 40 */     this.prefs.put(name.toLowerCase(Locale.ENGLISH), value);
/*    */   }
/*    */   
/*    */   public void set(String name, boolean value) {
/* 44 */     set(name, "" + value);
/*    */   }
/*    */   
/*    */   public void set(String name, int value) {
/* 48 */     set(name, "" + value);
/*    */   }
/*    */   
/*    */   public void set(String name, double value) {
/* 52 */     set(name, "" + value);
/*    */   }
/*    */   
/*    */   public Map<String, String> getAll() {
/* 56 */     return this.prefs;
/*    */   }
/*    */   
/*    */   public boolean getBoolean(String name, boolean defaultValue) {
/* 60 */     name = name.toLowerCase(Locale.ENGLISH);
/* 61 */     String value = (String)this.prefs.get(name);
/* 62 */     if (value != null) {
/* 63 */       return Boolean.parseBoolean(value);
/*    */     }
/* 65 */     return defaultValue;
/*    */   }
/*    */   
/*    */   public boolean contains(String name)
/*    */   {
/* 70 */     return getString(name, null) != null;
/*    */   }
/*    */   
/*    */   public int getInteger(String name, int defaultValue) {
/* 74 */     name = name.toLowerCase(Locale.ENGLISH);
/* 75 */     String value = (String)this.prefs.get(name);
/* 76 */     if (value != null)
/*    */     {
/* 78 */       return (int)Long.decode(value).longValue();
/*    */     }
/* 80 */     return defaultValue;
/*    */   }
/*    */   
/*    */   public double getDouble(String name, double defaultValue) {
/* 84 */     name = name.toLowerCase(Locale.ENGLISH);
/* 85 */     String value = (String)this.prefs.get(name);
/* 86 */     if (value != null) {
/* 87 */       return Double.valueOf(value).doubleValue();
/*    */     }
/* 89 */     return defaultValue;
/*    */   }
/*    */   
/*    */   public String getString(String name, String defaultValue) {
/* 93 */     name = name.toLowerCase(Locale.ENGLISH);
/* 94 */     String value = (String)this.prefs.get(name);
/* 95 */     if (value != null) {
/* 96 */       return value;
/*    */     }
/* 98 */     return defaultValue;
/*    */   }
/*    */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\CordovaPreferences.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */