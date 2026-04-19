/*    */ package org.apache.cordova.engine;
/*    */ 
/*    */

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewEngine.EngineView;
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
/*    */ public class SystemWebView
/*    */   extends WebView
/*    */   implements EngineView
/*    */ {
/*    */   private SystemWebViewClient viewClient;
/*    */   SystemWebChromeClient chromeClient;
/*    */   private SystemWebViewEngine parentEngine;
/*    */   private CordovaInterface cordova;
/*    */   
/*    */   public SystemWebView(Context context)
/*    */   {
/* 43 */     this(context, null);
/*    */   }
/*    */   
/*    */   public SystemWebView(Context context, AttributeSet attrs) {
/* 47 */     super(context, attrs);
/*    */   }
/*    */   
/*    */   void init(SystemWebViewEngine parentEngine, CordovaInterface cordova)
/*    */   {
/* 52 */     this.cordova = cordova;
/* 53 */     this.parentEngine = parentEngine;
/* 54 */     if (this.viewClient == null) {
/* 55 */       setWebViewClient(new SystemWebViewClient(parentEngine));
/*    */     }
/*    */     
/* 58 */     if (this.chromeClient == null) {
/* 59 */       setWebChromeClient(new SystemWebChromeClient(parentEngine));
/*    */     }
/*    */   }
/*    */   
/*    */   public CordovaWebView getCordovaWebView()
/*    */   {
/* 65 */     return this.parentEngine != null ? this.parentEngine.getCordovaWebView() : null;
/*    */   }
/*    */   
/*    */   public void setWebViewClient(WebViewClient client)
/*    */   {
/* 70 */     this.viewClient = ((SystemWebViewClient)client);
/* 71 */     super.setWebViewClient(client);
/*    */   }
/*    */   
/*    */   public void setWebChromeClient(WebChromeClient client)
/*    */   {
/* 76 */     this.chromeClient = ((SystemWebChromeClient)client);
/* 77 */     super.setWebChromeClient(client);
/*    */   }
/*    */   
/*    */   public boolean dispatchKeyEvent(KeyEvent event)
/*    */   {
/* 82 */     Boolean ret = this.parentEngine.client.onDispatchKeyEvent(event);
/* 83 */     if (ret != null) {
/* 84 */       return ret.booleanValue();
/*    */     }
/* 86 */     return super.dispatchKeyEvent(event);
/*    */   }
/*    */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\engine\SystemWebView.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */