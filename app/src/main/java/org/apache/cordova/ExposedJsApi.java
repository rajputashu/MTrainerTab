package org.apache.cordova;

import org.json.JSONException;

public abstract interface ExposedJsApi
{
  public abstract String exec(int paramInt, String paramString1, String paramString2, String paramString3, String paramString4)
    throws JSONException, IllegalAccessException;
  
  public abstract void setNativeToJsBridgeMode(int paramInt1, int paramInt2)
    throws IllegalAccessException;
  
  public abstract String retrieveJsMessages(int paramInt, boolean paramBoolean)
    throws IllegalAccessException;
}


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\ExposedJsApi.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */