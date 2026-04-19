/*    */ package org.apache.cordova.engine;
/*    */ 
/*    */

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.webkit.CookieManager;
import android.webkit.WebView;

import org.apache.cordova.ICordovaCookieManager;
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
/*    */ class SystemCookieManager
/*    */   implements ICordovaCookieManager
/*    */ {
/*    */   protected final WebView webView;
/*    */   private final CookieManager cookieManager;
/*    */   
/*    */   @TargetApi(21)
/*    */   public SystemCookieManager(WebView webview)
/*    */   {
/* 37 */     this.webView = webview;
/* 38 */     this.cookieManager = CookieManager.getInstance();
/*    */     
/*    */ 
/* 41 */     CookieManager.setAcceptFileSchemeCookies(true);
/*    */     
/* 43 */     if (VERSION.SDK_INT >= 21) {
/* 44 */       this.cookieManager.setAcceptThirdPartyCookies(this.webView, true);
/*    */     }
/*    */   }
/*    */   
/*    */   public void setCookiesEnabled(boolean accept) {
/* 49 */     this.cookieManager.setAcceptCookie(accept);
/*    */   }
/*    */   
/*    */   public void setCookie(String url, String value) {
/* 53 */     this.cookieManager.setCookie(url, value);
/*    */   }
/*    */   
/*    */   public String getCookie(String url) {
/* 57 */     return this.cookieManager.getCookie(url);
/*    */   }
/*    */   
/*    */   public void clearCookies() {
/* 61 */     this.cookieManager.removeAllCookie();
/*    */   }
/*    */   
/*    */   public void flush() {
/* 65 */     if (VERSION.SDK_INT >= 21) {
/* 66 */       this.cookieManager.flush();
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\engine\SystemCookieManager.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */