package org.apache.cordova;

import android.app.Activity;
import android.content.Intent;

import java.util.concurrent.ExecutorService;

public abstract interface CordovaInterface
{
  public abstract void startActivityForResult(CordovaPlugin paramCordovaPlugin, Intent paramIntent, int paramInt);
  
  public abstract void setActivityResultCallback(CordovaPlugin paramCordovaPlugin);
  
  public abstract Activity getActivity();
  
  public abstract Object onMessage(String paramString, Object paramObject);
  
  public abstract ExecutorService getThreadPool();
  
  public abstract void requestPermission(CordovaPlugin paramCordovaPlugin, int paramInt, String paramString);
  
  public abstract void requestPermissions(CordovaPlugin paramCordovaPlugin, int paramInt, String[] paramArrayOfString);
  
  public abstract boolean hasPermission(String paramString);
}


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\CordovaInterface.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */