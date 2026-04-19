package com.sisindia.ai.mtrainer.android.features.bordcastrecever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSListener extends BroadcastReceiver {

    private static OtpListner otpListner;

    public static void getOtp(OtpListner otpListner) {
        SMSListener.otpListner = otpListner;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();

        Object[] pdus = new Object[0];
        if (data != null) {
            pdus = (Object[]) data.get("pdus"); // the pdus key will contain the newly received SMS
        }

        if (pdus != null) {
            for (Object pdu : pdus) { // loop through and pick up the SMS of interest
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                if (smsMessage != null) {
                    String otpOrigin = getOtp(smsMessage.getDisplayOriginatingAddress().intern().toUpperCase().trim());
                    if (otpOrigin != null && otpOrigin.equals("SISGRC")) {
                        String otp = getOtp(smsMessage.getDisplayMessageBody());
                        if (otp != null) {
                            otpListner.onOptRecever(otp);
                        }
                    }
                }
            }
        }
    }

    private String getOtp(String message) {
        String lastsixdigit = "";     //substring containing last 6 characters
        if (message.length() > 6) {
            lastsixdigit = message.substring(message.length() - 6);
        } else {
            lastsixdigit = null;
        }
        return lastsixdigit;
    }

    public interface OtpListner {
        void onOptRecever(String otp);
    }
}
