/*     */ package org.apache.cordova;
/*     */ 
/*     */

import android.net.Uri;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
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
/*     */ public class Whitelist
/*     */ {
/*     */   private ArrayList<URLPattern> whiteList;
/*     */   public static final String TAG = "Whitelist";
/*     */   
/*     */   private static class URLPattern
/*     */   {
/*     */     public Pattern scheme;
/*     */     public Pattern host;
/*     */     public Integer port;
/*     */     public Pattern path;
/*     */     
/*     */     private String regexFromPattern(String pattern, boolean allowWildcards)
/*     */     {
/*  39 */       String toReplace = "\\.[]{}()^$?+|";
/*  40 */       StringBuilder regex = new StringBuilder();
/*  41 */       for (int i = 0; i < pattern.length(); i++) {
/*  42 */         char c = pattern.charAt(i);
/*  43 */         if ((c == '*') && (allowWildcards)) {
/*  44 */           regex.append(".");
/*  45 */         } else if ("\\.[]{}()^$?+|".indexOf(c) > -1) {
/*  46 */           regex.append('\\');
/*     */         }
/*  48 */         regex.append(c);
/*     */       }
/*  50 */       return regex.toString();
/*     */     }
/*     */     
/*     */     public URLPattern(String scheme, String host, String port, String path) throws MalformedURLException {
/*     */       try {
/*  55 */         if ((scheme == null) || ("*".equals(scheme))) {
/*  56 */           this.scheme = null;
/*     */         } else {
/*  58 */           this.scheme = Pattern.compile(regexFromPattern(scheme, false), 2);
/*     */         }
/*  60 */         if ("*".equals(host)) {
/*  61 */           this.host = null;
/*  62 */         } else if (host.startsWith("*.")) {
/*  63 */           this.host = Pattern.compile("([a-z0-9.-]*\\.)?" + regexFromPattern(host.substring(2), false), 2);
/*     */         } else {
/*  65 */           this.host = Pattern.compile(regexFromPattern(host, false), 2);
/*     */         }
/*  67 */         if ((port == null) || ("*".equals(port))) {
/*  68 */           this.port = null;
/*     */         } else {
/*  70 */           this.port = Integer.valueOf(Integer.parseInt(port, 10));
/*     */         }
/*  72 */         if ((path == null) || ("/*".equals(path))) {
/*  73 */           this.path = null;
/*     */         } else {
/*  75 */           this.path = Pattern.compile(regexFromPattern(path, true));
/*     */         }
/*     */       } catch (NumberFormatException e) {
/*  78 */         throw new MalformedURLException("Port must be a number");
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean matches(Uri uri) {
/*     */       try {
/*  84 */         return ((this.scheme == null) || (this.scheme.matcher(uri.getScheme()).matches())) && ((this.host == null) || (this.host.matcher(uri.getHost()).matches())) && ((this.port == null) || (this.port.equals(Integer.valueOf(uri.getPort())))) && ((this.path == null) || (this.path.matcher(uri.getPath()).matches()));
/*     */ 
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*  89 */         LOG.d("Whitelist", e.toString()); }
/*  90 */       return false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Whitelist()
/*     */   {
/* 100 */     this.whiteList = new ArrayList();
/*     */   }
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
/*     */   public void addWhiteListEntry(String origin, boolean subdomains)
/*     */   {
/* 115 */     if (this.whiteList != null) {
/*     */       try
/*     */       {
/* 118 */         if (origin.compareTo("*") == 0) {
/* 119 */           LOG.d("Whitelist", "Unlimited access to network resources");
/* 120 */           this.whiteList = null;
/*     */         }
/*     */         else {
/* 123 */           Pattern parts = Pattern.compile("^((\\*|[A-Za-z-]+):(//)?)?(\\*|((\\*\\.)?[^*/:]+))?(:(\\d+))?(/.*)?");
/* 124 */           Matcher m = parts.matcher(origin);
/* 125 */           if (m.matches()) {
/* 126 */             String scheme = m.group(2);
/* 127 */             String host = m.group(4);
/*     */             
/* 129 */             if ((("file".equals(scheme)) || ("content".equals(scheme))) && (host == null)) host = "*";
/* 130 */             String port = m.group(8);
/* 131 */             String path = m.group(9);
/* 132 */             if (scheme == null)
/*     */             {
/* 134 */               this.whiteList.add(new URLPattern("http", host, port, path));
/* 135 */               this.whiteList.add(new URLPattern("https", host, port, path));
/*     */             } else {
/* 137 */               this.whiteList.add(new URLPattern(scheme, host, port, path));
/*     */             }
/*     */           }
/*     */         }
/*     */       } catch (Exception e) {
/* 142 */         LOG.d("Whitelist", "Failed to add origin %s", new Object[] { origin });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isUrlWhiteListed(String uri)
/*     */   {
/* 156 */     if (this.whiteList == null) { return true;
/*     */     }
/* 158 */     Uri parsedUri = Uri.parse(uri);
/*     */     
/* 160 */     Iterator<URLPattern> pit = this.whiteList.iterator();
/* 161 */     while (pit.hasNext()) {
/* 162 */       URLPattern p = (URLPattern)pit.next();
/* 163 */       if (p.matches(parsedUri)) {
/* 164 */         return true;
/*     */       }
/*     */     }
/* 167 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\Whitelist.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */