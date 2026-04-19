package org.apache.cordova;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebChromeClient.CustomViewCallback;

import java.util.List;
import java.util.Map;

public abstract interface CordovaWebView
{
  public static final String CORDOVA_VERSION = "6.5.0-dev";
  
  public abstract void init(CordovaInterface paramCordovaInterface, List<PluginEntry> paramList, CordovaPreferences paramCordovaPreferences);
  
  public abstract boolean isInitialized();
  
  public abstract View getView();
  
  public abstract void loadUrlIntoView(String paramString, boolean paramBoolean);
  
  public abstract void stopLoading();
  
  public abstract boolean canGoBack();
  
  public abstract void clearCache();
  
  @Deprecated
  public abstract void clearCache(boolean paramBoolean);
  
  public abstract void clearHistory();
  
  public abstract boolean backHistory();
  
  public abstract void handlePause(boolean paramBoolean);
  
  public abstract void onNewIntent(Intent paramIntent);
  
  public abstract void handleResume(boolean paramBoolean);
  
  public abstract void handleStart();
  
  public abstract void handleStop();
  
  public abstract void handleDestroy();
  
  @Deprecated
  public abstract void sendJavascript(String paramString);
  
  public abstract void showWebPage(String paramString, boolean paramBoolean1, boolean paramBoolean2, Map<String, Object> paramMap);
  
  @Deprecated
  public abstract boolean isCustomViewShowing();
  
  @Deprecated
  public abstract void showCustomView(View paramView, CustomViewCallback paramCustomViewCallback);
  
  @Deprecated
  public abstract void hideCustomView();
  
  public abstract CordovaResourceApi getResourceApi();
  
  public abstract void setButtonPlumbedToJs(int paramInt, boolean paramBoolean);
  
  public abstract boolean isButtonPlumbedToJs(int paramInt);
  
  public abstract void sendPluginResult(PluginResult paramPluginResult, String paramString);
  
  public abstract PluginManager getPluginManager();
  
  public abstract CordovaWebViewEngine getEngine();
  
  public abstract CordovaPreferences getPreferences();
  
  public abstract ICordovaCookieManager getCookieManager();
  
  public abstract String getUrl();
  
  public abstract Context getContext();
  
  public abstract void loadUrl(String paramString);
  
  public abstract Object postMessage(String paramString, Object paramObject);
}


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\CordovaWebView.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */