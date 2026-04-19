/*     */ package org.apache.cordova;
/*     */ 
/*     */

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
/*     */ public class CordovaArgs
/*     */ {
/*     */   private JSONArray baseArgs;
/*     */   
/*     */   public CordovaArgs(JSONArray args)
/*     */   {
/*  31 */     this.baseArgs = args;
/*     */   }
/*     */   
/*     */   public Object get(int index)
/*     */     throws JSONException
/*     */   {
/*  37 */     return this.baseArgs.get(index);
/*     */   }
/*     */   
/*     */   public boolean getBoolean(int index) throws JSONException {
/*  41 */     return this.baseArgs.getBoolean(index);
/*     */   }
/*     */   
/*     */   public double getDouble(int index) throws JSONException {
/*  45 */     return this.baseArgs.getDouble(index);
/*     */   }
/*     */   
/*     */   public int getInt(int index) throws JSONException {
/*  49 */     return this.baseArgs.getInt(index);
/*     */   }
/*     */   
/*     */   public JSONArray getJSONArray(int index) throws JSONException {
/*  53 */     return this.baseArgs.getJSONArray(index);
/*     */   }
/*     */   
/*     */   public JSONObject getJSONObject(int index) throws JSONException {
/*  57 */     return this.baseArgs.getJSONObject(index);
/*     */   }
/*     */   
/*     */   public long getLong(int index) throws JSONException {
/*  61 */     return this.baseArgs.getLong(index);
/*     */   }
/*     */   
/*     */   public String getString(int index) throws JSONException {
/*  65 */     return this.baseArgs.getString(index);
/*     */   }
/*     */   
/*     */   public Object opt(int index)
/*     */   {
/*  70 */     return this.baseArgs.opt(index);
/*     */   }
/*     */   
/*     */   public boolean optBoolean(int index) {
/*  74 */     return this.baseArgs.optBoolean(index);
/*     */   }
/*     */   
/*     */   public double optDouble(int index) {
/*  78 */     return this.baseArgs.optDouble(index);
/*     */   }
/*     */   
/*     */   public int optInt(int index) {
/*  82 */     return this.baseArgs.optInt(index);
/*     */   }
/*     */   
/*     */   public JSONArray optJSONArray(int index) {
/*  86 */     return this.baseArgs.optJSONArray(index);
/*     */   }
/*     */   
/*     */   public JSONObject optJSONObject(int index) {
/*  90 */     return this.baseArgs.optJSONObject(index);
/*     */   }
/*     */   
/*     */   public long optLong(int index) {
/*  94 */     return this.baseArgs.optLong(index);
/*     */   }
/*     */   
/*     */   public String optString(int index) {
/*  98 */     return this.baseArgs.optString(index);
/*     */   }
/*     */   
/*     */   public boolean isNull(int index) {
/* 102 */     return this.baseArgs.isNull(index);
/*     */   }
/*     */   
/*     */   public byte[] getArrayBuffer(int index)
/*     */     throws JSONException
/*     */   {
/* 108 */     String encoded = this.baseArgs.getString(index);
/* 109 */     return Base64.decode(encoded, 0);
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\CordovaArgs.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */