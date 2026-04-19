/*    */ package org.apache.cordova;
/*    */ 
/*    */

import android.webkit.ClientCertRequest;

import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
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
/*    */ public class CordovaClientCertRequest
/*    */   implements ICordovaClientCertRequest
/*    */ {
/*    */   private final ClientCertRequest request;
/*    */   
/*    */   public CordovaClientCertRequest(ClientCertRequest request)
/*    */   {
/* 35 */     this.request = request;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void cancel()
/*    */   {
/* 43 */     this.request.cancel();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String getHost()
/*    */   {
/* 51 */     return this.request.getHost();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String[] getKeyTypes()
/*    */   {
/* 59 */     return this.request.getKeyTypes();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public int getPort()
/*    */   {
/* 67 */     return this.request.getPort();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public Principal[] getPrincipals()
/*    */   {
/* 75 */     return this.request.getPrincipals();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void ignore()
/*    */   {
/* 83 */     this.request.ignore();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void proceed(PrivateKey privateKey, X509Certificate[] chain)
/*    */   {
/* 94 */     this.request.proceed(privateKey, chain);
/*    */   }
/*    */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\CordovaClientCertRequest.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */