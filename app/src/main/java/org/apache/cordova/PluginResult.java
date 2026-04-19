/*     */ package org.apache.cordova;
/*     */ 
/*     */

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
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
/*     */ public class PluginResult
/*     */ {
/*     */   private final int status;
/*     */   private final int messageType;
/*  31 */   private boolean keepCallback = false;
/*     */   private String strMessage;
/*     */   private String encodedMessage;
/*     */   
/*     */   public PluginResult(Status status)
/*     */   {
/*  37 */     this(status, StatusMessages[status.ordinal()]);
/*     */   }
/*     */   
/*     */   public PluginResult(Status status, String message) {
/*  41 */     this.status = status.ordinal();
/*  42 */     this.messageType = (message == null ? 5 : 1);
/*  43 */     this.strMessage = message;
/*     */   }
/*     */   
/*     */   public PluginResult(Status status, JSONArray message) {
/*  47 */     this.status = status.ordinal();
/*  48 */     this.messageType = 2;
/*  49 */     this.encodedMessage = message.toString();
/*     */   }
/*     */   
/*     */   public PluginResult(Status status, JSONObject message) {
/*  53 */     this.status = status.ordinal();
/*  54 */     this.messageType = 2;
/*  55 */     this.encodedMessage = message.toString();
/*     */   }
/*     */   
/*     */   public PluginResult(Status status, int i) {
/*  59 */     this.status = status.ordinal();
/*  60 */     this.messageType = 3;
/*  61 */     this.encodedMessage = ("" + i);
/*     */   }
/*     */   
/*     */   public PluginResult(Status status, float f) {
/*  65 */     this.status = status.ordinal();
/*  66 */     this.messageType = 3;
/*  67 */     this.encodedMessage = ("" + f);
/*     */   }
/*     */   
/*     */   public PluginResult(Status status, boolean b) {
/*  71 */     this.status = status.ordinal();
/*  72 */     this.messageType = 4;
/*  73 */     this.encodedMessage = Boolean.toString(b);
/*     */   }
/*     */   
/*     */   public PluginResult(Status status, byte[] data) {
/*  77 */     this(status, data, false);
/*     */   }
/*     */   
/*     */   public PluginResult(Status status, byte[] data, boolean binaryString) {
/*  81 */     this.status = status.ordinal();
/*  82 */     this.messageType = (binaryString ? 7 : 6);
/*  83 */     this.encodedMessage = Base64.encodeToString(data, 2);
/*     */   }
/*     */   
/*     */   public PluginResult(Status status, List<PluginResult> multipartMessages)
/*     */   {
/*  88 */     this.status = status.ordinal();
/*  89 */     this.messageType = 8;
/*  90 */     this.multipartMessages = multipartMessages;
/*     */   }
/*     */   
/*     */   public void setKeepCallback(boolean b) {
/*  94 */     this.keepCallback = b;
/*     */   }
/*     */   
/*     */   public int getStatus() {
/*  98 */     return this.status;
/*     */   }
/*     */   
/*     */   public int getMessageType() {
/* 102 */     return this.messageType;
/*     */   }
/*     */   
/*     */   public String getMessage() {
/* 106 */     if (this.encodedMessage == null) {
/* 107 */       this.encodedMessage = JSONObject.quote(this.strMessage);
/*     */     }
/* 109 */     return this.encodedMessage;
/*     */   }
/*     */   
/*     */   public int getMultipartMessagesSize() {
/* 113 */     return this.multipartMessages.size();
/*     */   }
/*     */   
/*     */   public PluginResult getMultipartMessage(int index) {
/* 117 */     return (PluginResult)this.multipartMessages.get(index);
/*     */   }
/*     */   
/*     */   private List<PluginResult> multipartMessages;
/*     */   public static final int MESSAGE_TYPE_STRING = 1;
/*     */   public static final int MESSAGE_TYPE_JSON = 2;
/*     */   public String getStrMessage()
/*     */   {
/* 125 */     return this.strMessage;
/*     */   }
/*     */   
/*     */   public boolean getKeepCallback() {
/* 129 */     return this.keepCallback;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public String getJSONString() {
/* 134 */     return "{\"status\":" + this.status + ",\"message\":" + getMessage() + ",\"keepCallback\":" + this.keepCallback + "}";
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public String toCallbackString(String callbackId)
/*     */   {
/* 140 */     if ((this.status == Status.NO_RESULT.ordinal()) && (this.keepCallback)) {
/* 141 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 145 */     if ((this.status == Status.OK.ordinal()) || (this.status == Status.NO_RESULT.ordinal())) {
/* 146 */       return toSuccessCallbackString(callbackId);
/*     */     }
/*     */     
/* 149 */     return toErrorCallbackString(callbackId);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public String toSuccessCallbackString(String callbackId) {
/* 154 */     return "cordova.callbackSuccess('" + callbackId + "'," + getJSONString() + ");";
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public String toErrorCallbackString(String callbackId) {
/* 159 */     return "cordova.callbackError('" + callbackId + "', " + getJSONString() + ");";
/*     */   }
/*     */   
/*     */ 
/*     */   public static final int MESSAGE_TYPE_NUMBER = 3;
/*     */   
/*     */   public static final int MESSAGE_TYPE_BOOLEAN = 4;
/*     */   
/*     */   public static final int MESSAGE_TYPE_NULL = 5;
/*     */   
/*     */   public static final int MESSAGE_TYPE_ARRAYBUFFER = 6;
/*     */   
/*     */   public static final int MESSAGE_TYPE_BINARYSTRING = 7;
/*     */   public static final int MESSAGE_TYPE_MULTIPART = 8;
/* 173 */   public static String[] StatusMessages = { "No result", "OK", "Class not found", "Illegal access", "Instantiation error", "Malformed url", "IO error", "Invalid action", "JSON error", "Error" };
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
/*     */   public static enum Status
/*     */   {
/* 187 */     NO_RESULT, 
/* 188 */     OK, 
/* 189 */     CLASS_NOT_FOUND_EXCEPTION, 
/* 190 */     ILLEGAL_ACCESS_EXCEPTION, 
/* 191 */     INSTANTIATION_EXCEPTION, 
/* 192 */     MALFORMED_URL_EXCEPTION, 
/* 193 */     IO_EXCEPTION, 
/* 194 */     INVALID_ACTION, 
/* 195 */     JSON_EXCEPTION, 
/* 196 */     ERROR;
/*     */     
/*     */     private Status() {}
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\PluginResult.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */