/*     */ package org.apache.cordova;
/*     */ 
/*     */

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
/*     */ public class ConfigXmlParser
/*     */ {
/*  34 */   private static String TAG = "ConfigXmlParser";
/*     */   
/*  36 */   private String launchUrl = "file:///android_asset/www/index.html";
/*  37 */   private CordovaPreferences prefs = new CordovaPreferences();
/*  38 */   private ArrayList<PluginEntry> pluginEntries = new ArrayList(20);
/*     */   
/*     */   public CordovaPreferences getPreferences() {
/*  41 */     return this.prefs;
/*     */   }
/*     */   
/*     */   public ArrayList<PluginEntry> getPluginEntries() {
/*  45 */     return this.pluginEntries;
/*     */   }
/*     */   
/*     */   public String getLaunchUrl() {
/*  49 */     return this.launchUrl;
/*     */   }
/*     */   
/*     */   public void parse(Context action)
/*     */   {
/*  54 */     int id = action.getResources().getIdentifier("config", "xml", action.getClass().getPackage().getName());
/*  55 */     if (id == 0)
/*     */     {
/*  57 */       id = action.getResources().getIdentifier("config", "xml", action.getPackageName());
/*  58 */       if (id == 0) {
/*  59 */         LOG.e(TAG, "res/xml/config.xml is missing!");
/*  60 */         return;
/*     */       }
/*     */     }
/*  63 */     parse(action.getResources().getXml(id));
/*     */   }
/*     */   
/*  66 */   boolean insideFeature = false;
/*  67 */   String service = ""; String pluginClass = ""; String paramType = "";
/*  68 */   boolean onload = false;
/*     */   
/*     */   public void parse(XmlPullParser xml) {
/*  71 */     int eventType = -1;
/*     */     
/*  73 */     while (eventType != 1) {
/*  74 */       if (eventType == 2) {
/*  75 */         handleStartTag(xml);
/*     */       }
/*  77 */       else if (eventType == 3)
/*     */       {
/*  79 */         handleEndTag(xml);
/*     */       }
/*     */       try {
/*  82 */         eventType = xml.next();
/*     */       } catch (XmlPullParserException e) {
/*  84 */         e.printStackTrace();
/*     */       } catch (IOException e) {
/*  86 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void handleStartTag(XmlPullParser xml) {
/*  92 */     String strNode = xml.getName();
/*  93 */     if (strNode.equals("feature"))
/*     */     {
/*     */ 
/*  96 */       this.insideFeature = true;
/*  97 */       this.service = xml.getAttributeValue(null, "name");
/*     */     }
/*  99 */     else if ((this.insideFeature) && (strNode.equals("param"))) {
/* 100 */       this.paramType = xml.getAttributeValue(null, "name");
/* 101 */       if (this.paramType.equals("service")) {
/* 102 */         this.service = xml.getAttributeValue(null, "value");
/* 103 */       } else if ((this.paramType.equals("package")) || (this.paramType.equals("android-package"))) {
/* 104 */         this.pluginClass = xml.getAttributeValue(null, "value");
/* 105 */       } else if (this.paramType.equals("onload")) {
/* 106 */         this.onload = "true".equals(xml.getAttributeValue(null, "value"));
/*     */       }
/* 108 */     } else if (strNode.equals("preference")) {
/* 109 */       String name = xml.getAttributeValue(null, "name").toLowerCase(Locale.ENGLISH);
/* 110 */       String value = xml.getAttributeValue(null, "value");
/* 111 */       this.prefs.set(name, value);
/*     */     }
/* 113 */     else if (strNode.equals("content")) {
/* 114 */       String src = xml.getAttributeValue(null, "src");
/* 115 */       if (src != null) {
/* 116 */         setStartUrl(src);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void handleEndTag(XmlPullParser xml) {
/* 122 */     String strNode = xml.getName();
/* 123 */     if (strNode.equals("feature")) {
/* 124 */       this.pluginEntries.add(new PluginEntry(this.service, this.pluginClass, this.onload));
/*     */       
/* 126 */       this.service = "";
/* 127 */       this.pluginClass = "";
/* 128 */       this.insideFeature = false;
/* 129 */       this.onload = false;
/*     */     }
/*     */   }
/*     */   
/*     */   private void setStartUrl(String src) {
/* 134 */     Pattern schemeRegex = Pattern.compile("^[a-z-]+://");
/* 135 */     Matcher matcher = schemeRegex.matcher(src);
/* 136 */     if (matcher.find()) {
/* 137 */       this.launchUrl = src;
/*     */     } else {
/* 139 */       if (src.charAt(0) == '/') {
/* 140 */         src = src.substring(1);
/*     */       }
/* 142 */       this.launchUrl = ("file:///android_asset/www/" + src);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\ConfigXmlParser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */