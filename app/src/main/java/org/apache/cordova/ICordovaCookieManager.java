package org.apache.cordova;

public abstract interface ICordovaCookieManager
{
  public abstract void setCookiesEnabled(boolean paramBoolean);
  
  public abstract void setCookie(String paramString1, String paramString2);
  
  public abstract String getCookie(String paramString);
  
  public abstract void clearCookies();
  
  public abstract void flush();
}


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\ICordovaCookieManager.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */