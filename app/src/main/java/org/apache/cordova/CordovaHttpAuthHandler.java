/*    */ package org.apache.cordova;
/*    */ 
/*    */ import android.webkit.HttpAuthHandler;
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
/*    */ public class CordovaHttpAuthHandler
/*    */   implements ICordovaHttpAuthHandler
/*    */ {
/*    */   private final HttpAuthHandler handler;
/*    */   
/*    */   public CordovaHttpAuthHandler(HttpAuthHandler handler)
/*    */   {
/* 32 */     this.handler = handler;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void cancel()
/*    */   {
/* 39 */     this.handler.cancel();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void proceed(String username, String password)
/*    */   {
/* 49 */     this.handler.proceed(username, password);
/*    */   }
/*    */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\CordovaHttpAuthHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */