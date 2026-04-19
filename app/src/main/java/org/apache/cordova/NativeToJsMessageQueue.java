/*     */ package org.apache.cordova;
/*     */ 
/*     */

/*     */

import java.util.ArrayList;
import java.util.LinkedList;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NativeToJsMessageQueue
/*     */ {
/*     */   private static final String LOG_TAG = "JsMessageQueue";
/*     */   private static final boolean FORCE_ENCODE_USING_EVAL = false;
/*     */   static final boolean DISABLE_EXEC_CHAINING = false;
/*  42 */   private static int MAX_PAYLOAD_SIZE = 524288000;
/*     */   
/*     */   private boolean paused;
/*     */   
/*     */   private final LinkedList<JsMessage> queue;
/*     */   
/*     */   private ArrayList<BridgeMode> bridgeModes;
/*     */   private BridgeMode activeBridgeMode;
/*     */   
/*     */   public NativeToJsMessageQueue()
/*     */   {
/*  53 */     this.queue = new LinkedList();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  58 */     this.bridgeModes = new ArrayList();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addBridgeMode(BridgeMode bridgeMode)
/*     */   {
/*  68 */     this.bridgeModes.add(bridgeMode);
/*     */   }
/*     */   
/*     */   public boolean isBridgeEnabled() {
/*  72 */     return this.activeBridgeMode != null;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/*  76 */     return this.queue.isEmpty();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setBridgeMode(int value)
/*     */   {
/*  83 */     if ((value < -1) || (value >= this.bridgeModes.size())) {
/*  84 */       LOG.d("JsMessageQueue", "Invalid NativeToJsBridgeMode: " + value);
/*     */     } else {
/*  86 */       BridgeMode newMode = value < 0 ? null : (BridgeMode)this.bridgeModes.get(value);
/*  87 */       if (newMode != this.activeBridgeMode) {
/*  88 */         LOG.d("JsMessageQueue", "Set native->JS mode to " + (newMode == null ? "null" : newMode.getClass().getSimpleName()));
/*  89 */         synchronized (this) {
/*  90 */           this.activeBridgeMode = newMode;
/*  91 */           if (newMode != null) {
/*  92 */             newMode.reset();
/*  93 */             if ((!this.paused) && (!this.queue.isEmpty())) {
/*  94 */               newMode.onNativeToJsMessageAvailable(this);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reset()
/*     */   {
/* 106 */     synchronized (this) {
/* 107 */       this.queue.clear();
/* 108 */       setBridgeMode(-1);
/*     */     }
/*     */   }
/*     */   
/*     */   private int calculatePackedMessageLength(JsMessage message) {
/* 113 */     int messageLen = message.calculateEncodedLength();
/* 114 */     String messageLenStr = String.valueOf(messageLen);
/* 115 */     return messageLenStr.length() + messageLen + 1;
/*     */   }
/*     */   
/*     */   private void packMessage(JsMessage message, StringBuilder sb) {
/* 119 */     int len = message.calculateEncodedLength();
/* 120 */     sb.append(len).append(' ');
/*     */     
/* 122 */     message.encodeAsMessage(sb);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String popAndEncode(boolean fromOnlineEvent)
/*     */   {
/* 131 */     synchronized (this) {
/* 132 */       if (this.activeBridgeMode == null) {
/* 133 */         return null;
/*     */       }
/* 135 */       this.activeBridgeMode.notifyOfFlush(this, fromOnlineEvent);
/* 136 */       if (this.queue.isEmpty()) {
/* 137 */         return null;
/*     */       }
/* 139 */       int totalPayloadLen = 0;
/* 140 */       int numMessagesToSend = 0;
/* 141 */       for (JsMessage message : this.queue) {
/* 142 */         int messageSize = calculatePackedMessageLength(message);
/* 143 */         if ((numMessagesToSend > 0) && (totalPayloadLen + messageSize > MAX_PAYLOAD_SIZE) && (MAX_PAYLOAD_SIZE > 0)) {
/*     */           break;
/*     */         }
/* 146 */         totalPayloadLen += messageSize;
/* 147 */         numMessagesToSend++;
/*     */       }
/*     */       
/* 150 */       StringBuilder sb = new StringBuilder(totalPayloadLen);
/* 151 */       for (int i = 0; i < numMessagesToSend; i++) {
/* 152 */         JsMessage message = (JsMessage)this.queue.removeFirst();
/* 153 */         packMessage(message, sb);
/*     */       }
/*     */       
/* 156 */       if (!this.queue.isEmpty())
/*     */       {
/* 158 */         sb.append('*');
/*     */       }
/* 160 */       String ret = sb.toString();
/* 161 */       return ret;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String popAndEncodeAsJs()
/*     */   {
/* 169 */     synchronized (this) {
/* 170 */       int length = this.queue.size();
/* 171 */       if (length == 0) {
/* 172 */         return null;
/*     */       }
/* 174 */       int totalPayloadLen = 0;
/* 175 */       int numMessagesToSend = 0;
/* 176 */       for (JsMessage message : this.queue) {
/* 177 */         int messageSize = message.calculateEncodedLength() + 50;
/* 178 */         if ((numMessagesToSend > 0) && (totalPayloadLen + messageSize > MAX_PAYLOAD_SIZE) && (MAX_PAYLOAD_SIZE > 0)) {
/*     */           break;
/*     */         }
/* 181 */         totalPayloadLen += messageSize;
/* 182 */         numMessagesToSend++;
/*     */       }
/* 184 */       boolean willSendAllMessages = numMessagesToSend == this.queue.size();
/* 185 */       StringBuilder sb = new StringBuilder(totalPayloadLen + (willSendAllMessages ? 0 : 100));
/*     */       
/*     */ 
/* 188 */       for (int i = 0; i < numMessagesToSend; i++) {
/* 189 */         JsMessage message = (JsMessage)this.queue.removeFirst();
/* 190 */         if ((willSendAllMessages) && (i + 1 == numMessagesToSend)) {
/* 191 */           message.encodeAsJsMessage(sb);
/*     */         } else {
/* 193 */           sb.append("try{");
/* 194 */           message.encodeAsJsMessage(sb);
/* 195 */           sb.append("}finally{");
/*     */         }
/*     */       }
/* 198 */       if (!willSendAllMessages) {
/* 199 */         sb.append("window.setTimeout(function(){cordova.require('cordova/plugin/android/polling').pollOnce();},0);");
/*     */       }
/* 201 */       for (int i = willSendAllMessages ? 1 : 0; i < numMessagesToSend; i++) {
/* 202 */         sb.append('}');
/*     */       }
/* 204 */       String ret = sb.toString();
/* 205 */       return ret;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addJavaScript(String statement)
/*     */   {
/* 213 */     enqueueMessage(new JsMessage(statement));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addPluginResult(PluginResult result, String callbackId)
/*     */   {
/* 220 */     if (callbackId == null) {
/* 221 */       LOG.e("JsMessageQueue", "Got plugin result with no callbackId", new Throwable());
/* 222 */       return;
/*     */     }
/*     */     
/*     */ 
/* 226 */     boolean noResult = result.getStatus() == PluginResult.Status.NO_RESULT.ordinal();
/* 227 */     boolean keepCallback = result.getKeepCallback();
/* 228 */     if ((noResult) && (keepCallback)) {
/* 229 */       return;
/*     */     }
/* 231 */     JsMessage message = new JsMessage(result, callbackId);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 238 */     enqueueMessage(message);
/*     */   }
/*     */   
/*     */   private void enqueueMessage(JsMessage message) {
/* 242 */     synchronized (this) {
/* 243 */       if (this.activeBridgeMode == null) {
/* 244 */         LOG.d("JsMessageQueue", "Dropping Native->JS message due to disabled bridge");
/* 245 */         return;
/*     */       }
/* 247 */       this.queue.add(message);
/* 248 */       if (!this.paused) {
/* 249 */         this.activeBridgeMode.onNativeToJsMessageAvailable(this);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void setPaused(boolean value) {
/* 255 */     if ((this.paused) && (value))
/*     */     {
/*     */ 
/* 258 */       LOG.e("JsMessageQueue", "nested call to setPaused detected.", new Throwable());
/*     */     }
/* 260 */     this.paused = value;
/* 261 */     if (!value) {
/* 262 */       synchronized (this) {
/* 263 */         if ((!this.queue.isEmpty()) && (this.activeBridgeMode != null)) {
/* 264 */           this.activeBridgeMode.onNativeToJsMessageAvailable(this);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract class BridgeMode
/*     */   {
/*     */     public abstract void onNativeToJsMessageAvailable(NativeToJsMessageQueue paramNativeToJsMessageQueue);
/*     */     
/*     */     public void notifyOfFlush(NativeToJsMessageQueue queue, boolean fromOnlineEvent) {}
/*     */     
/*     */     public void reset() {}
/*     */   }
/*     */   
/*     */   public static class NoOpBridgeMode extends BridgeMode {
/*     */     public void onNativeToJsMessageAvailable(NativeToJsMessageQueue queue) {}
/*     */   }
/*     */   
/*     */   public static class LoadUrlBridgeMode extends BridgeMode {
/*     */     private final CordovaWebViewEngine engine;
/*     */     private final CordovaInterface cordova;
/*     */     
/*     */     public LoadUrlBridgeMode(CordovaWebViewEngine engine, CordovaInterface cordova) {
/* 288 */       this.engine = engine;
/* 289 */       this.cordova = cordova;
/*     */     }
/*     */     
/*     */     public void onNativeToJsMessageAvailable(final NativeToJsMessageQueue queue)
/*     */     {
/* 294 */       this.cordova.getActivity().runOnUiThread(new Runnable() {
/*     */         public void run() {
/* 296 */           String js = queue.popAndEncodeAsJs();
/* 297 */           if (js != null) {
/* 298 */             LoadUrlBridgeMode.this.engine.loadUrl("javascript:" + js, false);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static class OnlineEventsBridgeMode
/*     */     extends BridgeMode
/*     */   {
/*     */     private final OnlineEventsBridgeModeDelegate delegate;
/*     */     
/*     */     private boolean online;
/*     */     
/*     */     private boolean ignoreNextFlush;
/*     */     
/*     */     public OnlineEventsBridgeMode(OnlineEventsBridgeModeDelegate delegate)
/*     */     {
/* 317 */       this.delegate = delegate;
/*     */     }
/*     */     
/*     */     public void reset()
/*     */     {
/* 322 */       this.delegate.runOnUiThread(new Runnable() {
/*     */         public void run() {
/* 324 */           OnlineEventsBridgeMode.this.online = false;
/*     */           
/* 326 */           OnlineEventsBridgeMode.this.ignoreNextFlush = true;
/* 327 */           OnlineEventsBridgeMode.this.delegate.setNetworkAvailable(true);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */     public void onNativeToJsMessageAvailable(final NativeToJsMessageQueue queue)
/*     */     {
/* 334 */       this.delegate.runOnUiThread(new Runnable() {
/*     */         public void run() {
/* 336 */           if (!queue.isEmpty()) {
/* 337 */             OnlineEventsBridgeMode.this.ignoreNextFlush = false;
/* 338 */             OnlineEventsBridgeMode.this.delegate.setNetworkAvailable(OnlineEventsBridgeMode.this.online);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */     public void notifyOfFlush(NativeToJsMessageQueue queue, boolean fromOnlineEvent)
/*     */     {
/* 346 */       if ((fromOnlineEvent) && (!this.ignoreNextFlush))
/* 347 */         this.online = (!this.online);
/*     */     }
/*     */     
/*     */     public static abstract interface OnlineEventsBridgeModeDelegate { public abstract void setNetworkAvailable(boolean paramBoolean);
/*     */       
/*     */       public abstract void runOnUiThread(Runnable paramRunnable);
/*     */     } }
/*     */   
/*     */   public static class EvalBridgeMode extends BridgeMode { private final CordovaWebViewEngine engine;
/*     */     private final CordovaInterface cordova;
/*     */     
/* 358 */     public EvalBridgeMode(CordovaWebViewEngine engine, CordovaInterface cordova) { this.engine = engine;
/* 359 */       this.cordova = cordova;
/*     */     }
/*     */     
/*     */     public void onNativeToJsMessageAvailable(final NativeToJsMessageQueue queue)
/*     */     {
/* 364 */       this.cordova.getActivity().runOnUiThread(new Runnable() {
/*     */         public void run() {
/* 366 */           String js = queue.popAndEncodeAsJs();
/* 367 */           if (js != null) {
/* 368 */             EvalBridgeMode.this.engine.evaluateJavascript(js, null);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   private static class JsMessage
/*     */   {
/*     */     final String jsPayloadOrCallbackId;
/*     */     final PluginResult pluginResult;
/*     */     
/*     */     JsMessage(String js) {
/* 381 */       if (js == null) {
/* 382 */         throw new NullPointerException();
/*     */       }
/* 384 */       this.jsPayloadOrCallbackId = js;
/* 385 */       this.pluginResult = null;
/*     */     }
/*     */     
/* 388 */     JsMessage(PluginResult pluginResult, String callbackId) { if ((callbackId == null) || (pluginResult == null)) {
/* 389 */         throw new NullPointerException();
/*     */       }
/* 391 */       this.jsPayloadOrCallbackId = callbackId;
/* 392 */       this.pluginResult = pluginResult;
/*     */     }
/*     */     
/*     */     static int calculateEncodedLengthHelper(PluginResult pluginResult) {
/* 396 */       switch (pluginResult.getMessageType()) {
/*     */       case 4: 
/*     */       case 5: 
/* 399 */         return 1;
/*     */       case 3: 
/* 401 */         return 1 + pluginResult.getMessage().length();
/*     */       case 1: 
/* 403 */         return 1 + pluginResult.getStrMessage().length();
/*     */       case 7: 
/* 405 */         return 1 + pluginResult.getMessage().length();
/*     */       case 6: 
/* 407 */         return 1 + pluginResult.getMessage().length();
/*     */       case 8: 
/* 409 */         int ret = 1;
/* 410 */         for (int i = 0; i < pluginResult.getMultipartMessagesSize(); i++) {
/* 411 */           int length = calculateEncodedLengthHelper(pluginResult.getMultipartMessage(i));
/* 412 */           int argLength = String.valueOf(length).length();
/* 413 */           ret += argLength + 1 + length;
/*     */         }
/* 415 */         return ret;
/*     */       }
/*     */       
/* 418 */       return pluginResult.getMessage().length();
/*     */     }
/*     */     
/*     */     int calculateEncodedLength()
/*     */     {
/* 423 */       if (this.pluginResult == null) {
/* 424 */         return this.jsPayloadOrCallbackId.length() + 1;
/*     */       }
/* 426 */       int statusLen = String.valueOf(this.pluginResult.getStatus()).length();
/* 427 */       int ret = 2 + statusLen + 1 + this.jsPayloadOrCallbackId.length() + 1;
/* 428 */       return ret + calculateEncodedLengthHelper(this.pluginResult);
/*     */     }
/*     */     
/*     */     static void encodeAsMessageHelper(StringBuilder sb, PluginResult pluginResult) {
/* 432 */       switch (pluginResult.getMessageType()) {
/*     */       case 4: 
/* 434 */         sb.append(pluginResult.getMessage().charAt(0));
/* 435 */         break;
/*     */       case 5: 
/* 437 */         sb.append('N');
/* 438 */         break;
/*     */       case 3: 
/* 440 */         sb.append('n').append(pluginResult.getMessage());
/*     */         
/* 442 */         break;
/*     */       case 1: 
/* 444 */         sb.append('s');
/* 445 */         sb.append(pluginResult.getStrMessage());
/* 446 */         break;
/*     */       case 7: 
/* 448 */         sb.append('S');
/* 449 */         sb.append(pluginResult.getMessage());
/* 450 */         break;
/*     */       case 6: 
/* 452 */         sb.append('A');
/* 453 */         sb.append(pluginResult.getMessage());
/* 454 */         break;
/*     */       case 8: 
/* 456 */         sb.append('M');
/* 457 */         for (int i = 0; i < pluginResult.getMultipartMessagesSize(); i++) {
/* 458 */           PluginResult multipartMessage = pluginResult.getMultipartMessage(i);
/* 459 */           sb.append(String.valueOf(calculateEncodedLengthHelper(multipartMessage)));
/* 460 */           sb.append(' ');
/* 461 */           encodeAsMessageHelper(sb, multipartMessage);
/*     */         }
/* 463 */         break;
/*     */       case 2: 
/*     */       default: 
/* 466 */         sb.append(pluginResult.getMessage());
/*     */       }
/*     */     }
/*     */     
/*     */     void encodeAsMessage(StringBuilder sb) {
/* 471 */       if (this.pluginResult == null) {
/* 472 */         sb.append('J').append(this.jsPayloadOrCallbackId);
/*     */         
/* 474 */         return;
/*     */       }
/* 476 */       int status = this.pluginResult.getStatus();
/* 477 */       boolean noResult = status == PluginResult.Status.NO_RESULT.ordinal();
/* 478 */       boolean resultOk = status == PluginResult.Status.OK.ordinal();
/* 479 */       boolean keepCallback = this.pluginResult.getKeepCallback();
/*     */       
/* 481 */       sb.append((noResult) || (resultOk) ? 'S' : 'F').append(keepCallback ? '1' : '0').append(status).append(' ').append(this.jsPayloadOrCallbackId).append(' ');
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 488 */       encodeAsMessageHelper(sb, this.pluginResult);
/*     */     }
/*     */     
/*     */     void buildJsMessage(StringBuilder sb) {
/* 492 */       switch (this.pluginResult.getMessageType()) {
/*     */       case 8: 
/* 494 */         int size = this.pluginResult.getMultipartMessagesSize();
/* 495 */         for (int i = 0; i < size; i++) {
/* 496 */           PluginResult subresult = this.pluginResult.getMultipartMessage(i);
/* 497 */           JsMessage submessage = new JsMessage(subresult, this.jsPayloadOrCallbackId);
/* 498 */           submessage.buildJsMessage(sb);
/* 499 */           if (i < size - 1) {
/* 500 */             sb.append(",");
/*     */           }
/*     */         }
/* 503 */         break;
/*     */       case 7: 
/* 505 */         sb.append("atob('").append(this.pluginResult.getMessage()).append("')");
/*     */         
/*     */ 
/* 508 */         break;
/*     */       case 6: 
/* 510 */         sb.append("cordova.require('cordova/base64').toArrayBuffer('").append(this.pluginResult.getMessage()).append("')");
/*     */         
/*     */ 
/* 513 */         break;
/*     */       default: 
/* 515 */         sb.append(this.pluginResult.getMessage());
/*     */       }
/*     */     }
/*     */     
/*     */     void encodeAsJsMessage(StringBuilder sb) {
/* 520 */       if (this.pluginResult == null) {
/* 521 */         sb.append(this.jsPayloadOrCallbackId);
/*     */       } else {
/* 523 */         int status = this.pluginResult.getStatus();
/* 524 */         boolean success = (status == PluginResult.Status.OK.ordinal()) || (status == PluginResult.Status.NO_RESULT.ordinal());
/* 525 */         sb.append("cordova.callbackFromNative('").append(this.jsPayloadOrCallbackId).append("',").append(success).append(",").append(status).append(",[");
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 532 */         buildJsMessage(sb);
/* 533 */         sb.append("],").append(this.pluginResult.getKeepCallback()).append(");");
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\NativeToJsMessageQueue.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */