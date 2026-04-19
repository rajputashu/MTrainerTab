/*    */ package org.apache.cordova;
/*    */ 
/*    */

import android.content.Context;

import java.lang.reflect.Field;
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
/*    */ public class BuildHelper
/*    */ {
/* 38 */   private static String TAG = "BuildHelper";
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
/*    */   public static Object getBuildConfigValue(Context ctx, String key)
/*    */   {
/*    */     try
/*    */     {
/* 54 */       Class<?> clazz = Class.forName(ctx.getPackageName() + ".BuildConfig");
/* 55 */       Field field = clazz.getField(key);
/* 56 */       return field.get(null);
/*    */     } catch (ClassNotFoundException e) {
/* 58 */       LOG.d(TAG, "Unable to get the BuildConfig, is this built with ANT?");
/* 59 */       e.printStackTrace();
/*    */     } catch (NoSuchFieldException e) {
/* 61 */       LOG.d(TAG, key + " is not a valid field. Check your build.gradle");
/*    */     } catch (IllegalAccessException e) {
/* 63 */       LOG.d(TAG, "Illegal Access Exception: Let's print a stack trace.");
/* 64 */       e.printStackTrace();
/*    */     }
/*    */     
/* 67 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\BuildHelper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */