/*    */ package org.apache.cordova;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class PluginEntry
/*    */ {
/*    */   public final String service;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public final String pluginClass;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public final CordovaPlugin plugin;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public final boolean onload;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public PluginEntry(String service, CordovaPlugin plugin)
/*    */   {
/* 52 */     this(service, plugin.getClass().getName(), true, plugin);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public PluginEntry(String service, String pluginClass, boolean onload)
/*    */   {
/* 61 */     this(service, pluginClass, onload, null);
/*    */   }
/*    */   
/*    */   private PluginEntry(String service, String pluginClass, boolean onload, CordovaPlugin plugin) {
/* 65 */     this.service = service;
/* 66 */     this.pluginClass = pluginClass;
/* 67 */     this.onload = onload;
/* 68 */     this.plugin = plugin;
/*    */   }
/*    */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\PluginEntry.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */