/*    */ package org.apache.cordova;
/*    */ 
/*    */

import android.util.Pair;
import android.util.SparseArray;
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
/*    */ 
/*    */ 
/*    */ public class CallbackMap
/*    */ {
/* 30 */   private int currentCallbackId = 0;
/*    */   private SparseArray<Pair<CordovaPlugin, Integer>> callbacks;
/*    */   
/*    */   public CallbackMap() {
/* 34 */     this.callbacks = new SparseArray();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public synchronized int registerCallback(CordovaPlugin receiver, int requestCode)
/*    */   {
/* 47 */     int mappedId = this.currentCallbackId++;
/* 48 */     this.callbacks.put(mappedId, new Pair(receiver, Integer.valueOf(requestCode)));
/* 49 */     return mappedId;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public synchronized Pair<CordovaPlugin, Integer> getAndRemoveCallback(int mappedId)
/*    */   {
/* 61 */     Pair<CordovaPlugin, Integer> callback = (Pair)this.callbacks.get(mappedId);
/* 62 */     this.callbacks.remove(mappedId);
/* 63 */     return callback;
/*    */   }
/*    */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\CallbackMap.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */