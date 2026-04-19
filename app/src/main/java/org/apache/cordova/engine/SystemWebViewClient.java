/*     */ package org.apache.cordova.engine;
/*     */ 
/*     */

import android.annotation.TargetApi;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build.VERSION;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.cordova.AuthenticationToken;
import org.apache.cordova.CordovaClientCertRequest;
import org.apache.cordova.CordovaHttpAuthHandler;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaResourceApi.OpenForReadResult;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
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
/*     */ 
/*     */ 
/*     */ public class SystemWebViewClient
/*     */   extends WebViewClient
/*     */ {
/*     */   private static final String TAG = "SystemWebViewClient";
/*     */   protected final SystemWebViewEngine parentEngine;
/*  59 */   private boolean doClearHistory = false;
/*     */   
/*     */   boolean isCurrentlyLoading;
/*     */   
/*  63 */   private Hashtable<String, AuthenticationToken> authenticationTokens = new Hashtable();
/*     */   
/*     */   public SystemWebViewClient(SystemWebViewEngine parentEngine) {
/*  66 */     this.parentEngine = parentEngine;
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
/*     */   public boolean shouldOverrideUrlLoading(WebView view, String url)
/*     */   {
/*  79 */     return this.parentEngine.client.onNavigationAttempt(url);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm)
/*     */   {
/*  90 */     AuthenticationToken token = getAuthenticationToken(host, realm);
/*  91 */     if (token != null) {
/*  92 */       handler.proceed(token.getUserName(), token.getPassword());
/*  93 */       return;
/*     */     }
/*     */     
/*     */ 
/*  97 */     PluginManager pluginManager = this.parentEngine.pluginManager;
/*  98 */     if ((pluginManager != null) && (pluginManager.onReceivedHttpAuthRequest(null, new CordovaHttpAuthHandler(handler), host, realm))) {
/*  99 */       this.parentEngine.client.clearLoadTimeoutTimer();
/* 100 */       return;
/*     */     }
/*     */     
/*     */ 
/* 104 */     super.onReceivedHttpAuthRequest(view, handler, host, realm);
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
/*     */ 
/*     */   @TargetApi(21)
/*     */   public void onReceivedClientCertRequest(WebView view, ClientCertRequest request)
/*     */   {
/* 120 */     PluginManager pluginManager = this.parentEngine.pluginManager;
/* 121 */     if ((pluginManager != null) && (pluginManager.onReceivedClientCertRequest(null, new CordovaClientCertRequest(request)))) {
/* 122 */       this.parentEngine.client.clearLoadTimeoutTimer();
/* 123 */       return;
/*     */     }
/*     */     
/*     */ 
/* 127 */     super.onReceivedClientCertRequest(view, request);
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
/*     */   public void onPageStarted(WebView view, String url, Bitmap favicon)
/*     */   {
/* 141 */     super.onPageStarted(view, url, favicon);
/* 142 */     this.isCurrentlyLoading = true;
/*     */     
/* 144 */     this.parentEngine.bridge.reset();
/* 145 */     this.parentEngine.client.onPageStarted(url);
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
/*     */   public void onPageFinished(WebView view, String url)
/*     */   {
/* 158 */     super.onPageFinished(view, url);
/*     */     
/* 160 */     if ((!this.isCurrentlyLoading) && (!url.startsWith("about:"))) {
/* 161 */       return;
/*     */     }
/* 163 */     this.isCurrentlyLoading = false;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 171 */     if (this.doClearHistory) {
/* 172 */       view.clearHistory();
/* 173 */       this.doClearHistory = false;
/*     */     }
/* 175 */     this.parentEngine.client.onPageFinishedLoading(url);
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
/*     */ 
/*     */ 
/*     */   public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
/*     */   {
/* 191 */     if (!this.isCurrentlyLoading) {
/* 192 */       return;
/*     */     }
/* 194 */     LOG.d("SystemWebViewClient", "CordovaWebViewClient.onReceivedError: Error code=%s Description=%s URL=%s", new Object[] { Integer.valueOf(errorCode), description, failingUrl });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 199 */     if (errorCode == -10) {
/* 200 */       this.parentEngine.client.clearLoadTimeoutTimer();
/*     */       
/* 202 */       if (view.canGoBack()) {
/* 203 */         view.goBack();
/* 204 */         return;
/*     */       }
/* 206 */       super.onReceivedError(view, errorCode, description, failingUrl);
/*     */     }
/*     */     
/* 209 */     this.parentEngine.client.onReceivedError(errorCode, description, failingUrl);
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
/*     */ 
/*     */ 
/*     */   @TargetApi(8)
/*     */   public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
/*     */   {
/* 226 */     String packageName = this.parentEngine.cordova.getActivity().getPackageName();
/* 227 */     PackageManager pm = this.parentEngine.cordova.getActivity().getPackageManager();
/*     */     
/*     */     try
/*     */     {
/* 231 */       ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 128);
/* 232 */       if ((appInfo.flags & 0x2) != 0)
/*     */       {
/* 234 */         handler.proceed();
/* 235 */         return;
/*     */       }
/*     */       
/* 238 */       super.onReceivedSslError(view, handler, error);
/*     */     }
/*     */     catch (NameNotFoundException e)
/*     */     {
/* 242 */       super.onReceivedSslError(view, handler, error);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAuthenticationToken(AuthenticationToken authenticationToken, String host, String realm)
/*     */   {
/* 255 */     if (host == null) {
/* 256 */       host = "";
/*     */     }
/* 258 */     if (realm == null) {
/* 259 */       realm = "";
/*     */     }
/* 261 */     this.authenticationTokens.put(host.concat(realm), authenticationToken);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AuthenticationToken removeAuthenticationToken(String host, String realm)
/*     */   {
/* 273 */     return (AuthenticationToken)this.authenticationTokens.remove(host.concat(realm));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AuthenticationToken getAuthenticationToken(String host, String realm)
/*     */   {
/* 291 */     AuthenticationToken token = null;
/* 292 */     token = (AuthenticationToken)this.authenticationTokens.get(host.concat(realm));
/*     */     
/* 294 */     if (token == null)
/*     */     {
/* 296 */       token = (AuthenticationToken)this.authenticationTokens.get(host);
/*     */       
/*     */ 
/* 299 */       if (token == null) {
/* 300 */         token = (AuthenticationToken)this.authenticationTokens.get(realm);
/*     */       }
/*     */       
/*     */ 
/* 304 */       if (token == null) {
/* 305 */         token = (AuthenticationToken)this.authenticationTokens.get("");
/*     */       }
/*     */     }
/*     */     
/* 309 */     return token;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void clearAuthenticationTokens()
/*     */   {
/* 316 */     this.authenticationTokens.clear();
/*     */   }
/*     */   
/*     */ 
/*     */   @TargetApi(11)
/*     */   public WebResourceResponse shouldInterceptRequest(WebView view, String url)
/*     */   {
/*     */     try
/*     */     {
/* 325 */       if (!this.parentEngine.pluginManager.shouldAllowRequest(url)) {
/* 326 */         LOG.w("SystemWebViewClient", "URL blocked by whitelist: " + url);
/*     */         
/* 328 */         return new WebResourceResponse("text/plain", "UTF-8", null);
/*     */       }
/*     */       
/* 331 */       CordovaResourceApi resourceApi = this.parentEngine.resourceApi;
/* 332 */       Uri origUri = Uri.parse(url);
/*     */       
/* 334 */       Uri remappedUri = resourceApi.remapUri(origUri);
/*     */       
/* 336 */       if ((!origUri.equals(remappedUri)) || (needsSpecialsInAssetUrlFix(origUri)) || (needsKitKatContentUrlFix(origUri))) {
/* 337 */         OpenForReadResult result = resourceApi.openForRead(remappedUri, true);
/* 338 */         return new WebResourceResponse(result.mimeType, "UTF-8", result.inputStream);
/*     */       }
/*     */       
/* 341 */       return null;
/*     */     } catch (IOException e) {
/* 343 */       if (!(e instanceof FileNotFoundException)) {
/* 344 */         LOG.e("SystemWebViewClient", "Error occurred while loading a file (returning a 404).", e);
/*     */       }
/*     */     }
/* 347 */     return new WebResourceResponse("text/plain", "UTF-8", null);
/*     */   }
/*     */   
/*     */   private static boolean needsKitKatContentUrlFix(Uri uri)
/*     */   {
/* 352 */     return (VERSION.SDK_INT >= 19) && ("content".equals(uri.getScheme()));
/*     */   }
/*     */   
/*     */   private static boolean needsSpecialsInAssetUrlFix(Uri uri) {
/* 356 */     if (CordovaResourceApi.getUriType(uri) != 1) {
/* 357 */       return false;
/*     */     }
/* 359 */     if ((uri.getQuery() != null) || (uri.getFragment() != null)) {
/* 360 */       return true;
/*     */     }
/*     */     
/* 363 */     if (!uri.toString().contains("%")) {
/* 364 */       return false;
/*     */     }
/*     */     
/* 367 */     switch (VERSION.SDK_INT) {
/*     */     case 14: 
/*     */     case 15: 
/* 370 */       return true;
/*     */     }
/* 372 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\engine\SystemWebViewClient.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */