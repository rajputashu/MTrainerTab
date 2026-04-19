/*     */ package org.apache.cordova;
/*     */ 
/*     */ import android.util.Log;
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
/*     */ public class LOG
/*     */ {
/*     */   public static final int VERBOSE = 2;
/*     */   public static final int DEBUG = 3;
/*     */   public static final int INFO = 4;
/*     */   public static final int WARN = 5;
/*     */   public static final int ERROR = 6;
/*  38 */   public static int LOGLEVEL = 6;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setLogLevel(int logLevel)
/*     */   {
/*  46 */     LOGLEVEL = logLevel;
/*  47 */     Log.i("CordovaLog", "Changing log level to " + logLevel);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setLogLevel(String logLevel)
/*     */   {
/*  56 */     if ("VERBOSE".equals(logLevel)) { LOGLEVEL = 2;
/*  57 */     } else if ("DEBUG".equals(logLevel)) { LOGLEVEL = 3;
/*  58 */     } else if ("INFO".equals(logLevel)) { LOGLEVEL = 4;
/*  59 */     } else if ("WARN".equals(logLevel)) { LOGLEVEL = 5;
/*  60 */     } else if ("ERROR".equals(logLevel)) LOGLEVEL = 6;
/*  61 */     Log.i("CordovaLog", "Changing log level to " + logLevel + "(" + LOGLEVEL + ")");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isLoggable(int logLevel)
/*     */   {
/*  71 */     return logLevel >= LOGLEVEL;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void v(String tag, String s)
/*     */   {
/*  81 */     if (2 >= LOGLEVEL) { Log.v(tag, s);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void d(String tag, String s)
/*     */   {
/*  91 */     if (3 >= LOGLEVEL) { Log.d(tag, s);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void i(String tag, String s)
/*     */   {
/* 101 */     if (4 >= LOGLEVEL) { Log.i(tag, s);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void w(String tag, String s)
/*     */   {
/* 111 */     if (5 >= LOGLEVEL) { Log.w(tag, s);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void e(String tag, String s)
/*     */   {
/* 121 */     if (6 >= LOGLEVEL) { Log.e(tag, s);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void v(String tag, String s, Throwable e)
/*     */   {
/* 132 */     if (2 >= LOGLEVEL) { Log.v(tag, s, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void d(String tag, String s, Throwable e)
/*     */   {
/* 143 */     if (3 >= LOGLEVEL) { Log.d(tag, s, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void i(String tag, String s, Throwable e)
/*     */   {
/* 154 */     if (4 >= LOGLEVEL) { Log.i(tag, s, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void w(String tag, Throwable e)
/*     */   {
/* 164 */     if (5 >= LOGLEVEL) { Log.w(tag, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void w(String tag, String s, Throwable e)
/*     */   {
/* 175 */     if (5 >= LOGLEVEL) { Log.w(tag, s, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void e(String tag, String s, Throwable e)
/*     */   {
/* 186 */     if (6 >= LOGLEVEL) { Log.e(tag, s, e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void v(String tag, String s, Object... args)
/*     */   {
/* 197 */     if (2 >= LOGLEVEL) { Log.v(tag, String.format(s, args));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void d(String tag, String s, Object... args)
/*     */   {
/* 208 */     if (3 >= LOGLEVEL) { Log.d(tag, String.format(s, args));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void i(String tag, String s, Object... args)
/*     */   {
/* 219 */     if (4 >= LOGLEVEL) { Log.i(tag, String.format(s, args));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void w(String tag, String s, Object... args)
/*     */   {
/* 230 */     if (5 >= LOGLEVEL) { Log.w(tag, String.format(s, args));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void e(String tag, String s, Object... args)
/*     */   {
/* 241 */     if (6 >= LOGLEVEL) Log.e(tag, String.format(s, args));
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\LOG.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */