/*     */ package org.apache.cordova.engine;
/*     */ 
/*     */

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.apache.cordova.CordovaDialogsHelper;
import org.apache.cordova.CordovaDialogsHelper.Result;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;

import java.util.Arrays;
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
/*     */ public class SystemWebChromeClient
/*     */   extends WebChromeClient
/*     */ {
/*     */   private static final int FILECHOOSER_RESULTCODE = 5173;
/*     */   private static final String LOG_TAG = "SystemWebChromeClient";
/*  59 */   private long MAX_QUOTA = 104857600L;
/*     */   
/*     */   protected final SystemWebViewEngine parentEngine;
/*     */   
/*     */   private View mVideoProgressView;
/*     */   
/*     */   private CordovaDialogsHelper dialogsHelper;
/*     */   private Context appContext;
/*     */   private CustomViewCallback mCustomViewCallback;
/*     */   private View mCustomView;
/*     */   
/*     */   public SystemWebChromeClient(SystemWebViewEngine parentEngine)
/*     */   {
/*  72 */     this.parentEngine = parentEngine;
/*  73 */     this.appContext = parentEngine.webView.getContext();
/*  74 */     this.dialogsHelper = new CordovaDialogsHelper(this.appContext);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean onJsAlert(WebView view, String url, String message, final JsResult result)
/*     */   {
/*  82 */     this.dialogsHelper.showAlert(message, new Result() {
/*     */       public void gotResult(boolean success, String value) {
/*  84 */         if (success) {
/*  85 */           result.confirm();
/*     */         } else {
/*  87 */           result.cancel();
/*     */         }
/*     */       }
/*  90 */     });
/*  91 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean onJsConfirm(WebView view, String url, String message, final JsResult result)
/*     */   {
/*  99 */     this.dialogsHelper.showConfirm(message, new Result()
/*     */     {
/*     */       public void gotResult(boolean success, String value) {
/* 102 */         if (success) {
/* 103 */           result.confirm();
/*     */         } else {
/* 105 */           result.cancel();
/*     */         }
/*     */       }
/* 108 */     });
/* 109 */     return true;
/*     */   }
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
/*     */   public boolean onJsPrompt(WebView view, String origin, String message, String defaultValue, final JsPromptResult result)
/*     */   {
/* 123 */     String handledRet = this.parentEngine.bridge.promptOnJsPrompt(origin, message, defaultValue);
/* 124 */     if (handledRet != null) {
/* 125 */       result.confirm(handledRet);
/*     */     } else {
/* 127 */       this.dialogsHelper.showPrompt(message, defaultValue, new Result()
/*     */       {
/*     */         public void gotResult(boolean success, String value) {
/* 130 */           if (success) {
/* 131 */             result.confirm(value);
/*     */           } else {
/* 133 */             result.cancel();
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/* 138 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize, long totalUsedQuota, QuotaUpdater quotaUpdater)
/*     */   {
/* 148 */     LOG.d("SystemWebChromeClient", "onExceededDatabaseQuota estimatedSize: %d  currentQuota: %d  totalUsedQuota: %d", new Object[] { Long.valueOf(estimatedSize), Long.valueOf(currentQuota), Long.valueOf(totalUsedQuota) });
/* 149 */     quotaUpdater.updateQuota(this.MAX_QUOTA);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onConsoleMessage(String message, int lineNumber, String sourceID)
/*     */   {
/* 159 */     if (VERSION.SDK_INT == 7)
/*     */     {
/* 161 */       LOG.d("SystemWebChromeClient", "%s: Line %d : %s", new Object[] { sourceID, Integer.valueOf(lineNumber), message });
/* 162 */       super.onConsoleMessage(message, lineNumber, sourceID);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   @TargetApi(8)
/*     */   public boolean onConsoleMessage(ConsoleMessage consoleMessage)
/*     */   {
/* 170 */     if (consoleMessage.message() != null)
/* 171 */       LOG.d("SystemWebChromeClient", "%s: Line %d : %s", new Object[] { consoleMessage.sourceId(), Integer.valueOf(consoleMessage.lineNumber()), consoleMessage.message() });
/* 172 */     return super.onConsoleMessage(consoleMessage);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onGeolocationPermissionsShowPrompt(String origin, Callback callback)
/*     */   {
/* 185 */     super.onGeolocationPermissionsShowPrompt(origin, callback);
/* 186 */     callback.invoke(origin, true, false);
/*     */     
/* 188 */     CordovaPlugin geolocation = this.parentEngine.pluginManager.getPlugin("Geolocation");
/* 189 */     if ((geolocation != null) && (!geolocation.hasPermisssion()))
/*     */     {
/* 191 */       geolocation.requestPermissions(0);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void onShowCustomView(View view, CustomViewCallback callback)
/*     */   {
/* 199 */     this.parentEngine.getCordovaWebView().showCustomView(view, callback);
/*     */   }
/*     */   
/*     */   public void onHideCustomView()
/*     */   {
/* 204 */     this.parentEngine.getCordovaWebView().hideCustomView();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public View getVideoLoadingProgressView()
/*     */   {
/* 215 */     if (this.mVideoProgressView == null)
/*     */     {
/*     */ 
/*     */ 
/* 219 */       LinearLayout layout = new LinearLayout(this.parentEngine.getView().getContext());
/* 220 */       layout.setOrientation(LinearLayout.VERTICAL);
/* 221 */       RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
/* 222 */       layoutParams.addRule(13);
/* 223 */       layout.setLayoutParams(layoutParams);
/*     */       
/* 225 */       ProgressBar bar = new ProgressBar(this.parentEngine.getView().getContext());
/* 226 */       LayoutParams barLayoutParams = new LayoutParams(-2, -2);
/* 227 */       barLayoutParams.gravity = 17;
/* 228 */       bar.setLayoutParams(barLayoutParams);
/* 229 */       layout.addView(bar);
/*     */       
/* 231 */       this.mVideoProgressView = layout;
/*     */     }
/* 233 */     return this.mVideoProgressView;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void openFileChooser(ValueCallback<Uri> uploadMsg)
/*     */   {
/* 240 */     openFileChooser(uploadMsg, "*/*");
/*     */   }
/*     */   
/*     */   public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
/* 244 */     openFileChooser(uploadMsg, acceptType, null);
/*     */   }
/*     */   
/*     */   public void openFileChooser(final ValueCallback<Uri> uploadMsg, String acceptType, String capture)
/*     */   {
/* 249 */     Intent intent = new Intent("android.intent.action.GET_CONTENT");
/* 250 */     intent.addCategory("android.intent.category.OPENABLE");
/* 251 */     intent.setType("*/*");
/* 252 */     this.parentEngine.cordova.startActivityForResult(new CordovaPlugin()
/*     */     {
/*     */       public void onActivityResult(int requestCode, int resultCode, Intent intent) {
/* 255 */         Uri result = (intent == null) || (resultCode != -1) ? null : intent.getData();
/* 256 */         LOG.d("SystemWebChromeClient", "Receive file chooser URL: " + result);
/* 257 */         uploadMsg.onReceiveValue(result); } }, intent, 5173);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   @TargetApi(21)
/*     */   public boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathsCallback, FileChooserParams fileChooserParams)
/*     */   {
/* 265 */     Intent intent = fileChooserParams.createIntent();
/*     */     try {
/* 267 */       this.parentEngine.cordova.startActivityForResult(new CordovaPlugin()
/*     */       {
/*     */         public void onActivityResult(int requestCode, int resultCode, Intent intent) {
/* 270 */           Uri[] result = FileChooserParams.parseResult(resultCode, intent);
/* 271 */           LOG.d("SystemWebChromeClient", "Receive file chooser URL: " + result);
/* 272 */           filePathsCallback.onReceiveValue(result); } }, intent, 5173);
/*     */     }
/*     */     catch (ActivityNotFoundException e)
/*     */     {
/* 276 */       LOG.w("No activity found to handle file chooser intent.", e);
/* 277 */       filePathsCallback.onReceiveValue(null);
/*     */     }
/* 279 */     return true;
/*     */   }
/*     */   
/*     */   @TargetApi(21)
/*     */   public void onPermissionRequest(PermissionRequest request)
/*     */   {
/* 285 */     LOG.d("SystemWebChromeClient", "onPermissionRequest: " + Arrays.toString(request.getResources()));
/* 286 */     request.grant(request.getResources());
/*     */   }
/*     */   
/*     */   public void destroyLastDialog() {
/* 290 */     this.dialogsHelper.destroyLastDialog();
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\engine\SystemWebChromeClient.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */