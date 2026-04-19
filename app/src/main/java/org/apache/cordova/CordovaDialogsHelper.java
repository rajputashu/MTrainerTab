/*     */ package org.apache.cordova;
/*     */ 
/*     */

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.widget.EditText;
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
/*     */ public class CordovaDialogsHelper
/*     */ {
/*     */   private final Context context;
/*     */   private AlertDialog lastHandledDialog;
/*     */   
/*     */   public CordovaDialogsHelper(Context context)
/*     */   {
/*  35 */     this.context = context;
/*     */   }
/*     */   
/*     */   public void showAlert(String message, final Result result) {
/*  39 */     Builder dlg = new Builder(this.context);
/*  40 */     dlg.setMessage(message);
/*  41 */     dlg.setTitle("Alert");
/*     */     
/*  43 */     dlg.setCancelable(true);
/*  44 */     dlg.setPositiveButton(17039370, new OnClickListener()
/*     */     {
/*     */       public void onClick(DialogInterface dialog, int which) {
/*  47 */         result.gotResult(true, null);
/*     */       }
/*  49 */     });
/*  50 */     dlg.setOnCancelListener(new OnCancelListener()
/*     */     {
/*     */       public void onCancel(DialogInterface dialog) {
/*  53 */         result.gotResult(false, null);
/*     */       }
/*  55 */     });
/*  56 */     dlg.setOnKeyListener(new OnKeyListener()
/*     */     {
/*     */       public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
/*  59 */         if (keyCode == 4)
/*     */         {
/*  61 */           result.gotResult(true, null);
/*  62 */           return false;
/*     */         }
/*     */         
/*  65 */         return true;
/*     */       }
/*  67 */     });
/*  68 */     this.lastHandledDialog = dlg.show();
/*     */   }
/*     */   
/*     */   public void showConfirm(String message, final Result result) {
/*  72 */     Builder dlg = new Builder(this.context);
/*  73 */     dlg.setMessage(message);
/*  74 */     dlg.setTitle("Confirm");
/*  75 */     dlg.setCancelable(true);
/*  76 */     dlg.setPositiveButton(17039370, new OnClickListener()
/*     */     {
/*     */       public void onClick(DialogInterface dialog, int which) {
/*  79 */         result.gotResult(true, null);
/*     */       }
/*  81 */     });
/*  82 */     dlg.setNegativeButton(17039360, new OnClickListener()
/*     */     {
/*     */       public void onClick(DialogInterface dialog, int which) {
/*  85 */         result.gotResult(false, null);
/*     */       }
/*  87 */     });
/*  88 */     dlg.setOnCancelListener(new OnCancelListener()
/*     */     {
/*     */       public void onCancel(DialogInterface dialog) {
/*  91 */         result.gotResult(false, null);
/*     */       }
/*  93 */     });
/*  94 */     dlg.setOnKeyListener(new OnKeyListener()
/*     */     {
/*     */       public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
/*  97 */         if (keyCode == 4)
/*     */         {
/*  99 */           result.gotResult(false, null);
/* 100 */           return false;
/*     */         }
/*     */         
/* 103 */         return true;
/*     */       }
/* 105 */     });
/* 106 */     this.lastHandledDialog = dlg.show();
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
/*     */   public void showPrompt(String message, String defaultValue, final Result result)
/*     */   {
/* 119 */     Builder dlg = new Builder(this.context);
/* 120 */     dlg.setMessage(message);
/* 121 */     final EditText input = new EditText(this.context);
/* 122 */     if (defaultValue != null) {
/* 123 */       input.setText(defaultValue);
/*     */     }
/* 125 */     dlg.setView(input);
/* 126 */     dlg.setCancelable(false);
/* 127 */     dlg.setPositiveButton(17039370, new OnClickListener()
/*     */     {
/*     */       public void onClick(DialogInterface dialog, int which) {
/* 130 */         String userText = input.getText().toString();
/* 131 */         result.gotResult(true, userText);
/*     */       }
/* 133 */     });
/* 134 */     dlg.setNegativeButton(17039360, new OnClickListener()
/*     */     {
/*     */       public void onClick(DialogInterface dialog, int which) {
/* 137 */         result.gotResult(false, null);
/*     */       }
/* 139 */     });
/* 140 */     this.lastHandledDialog = dlg.show();
/*     */   }
/*     */   
/*     */   public void destroyLastDialog() {
/* 144 */     if (this.lastHandledDialog != null) {
/* 145 */       this.lastHandledDialog.cancel();
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface Result
/*     */   {
/*     */     public abstract void gotResult(boolean paramBoolean, String paramString);
/*     */   }
/*     */ }


/* Location:              C:\Users\nagaraju.lj\Desktop\Comedies\cordova-6.5.0-dev.jar!\org\apache\cordova\CordovaDialogsHelper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */