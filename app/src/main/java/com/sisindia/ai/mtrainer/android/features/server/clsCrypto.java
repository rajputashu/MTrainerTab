package com.sisindia.ai.mtrainer.android.features.server;

//loading ndk files for decryption
public class clsCrypto
{
    public clsCrypto()
    {

    }
    static
    {
        System.loadLibrary("CryptoLibrary");
    }

    public native String stringFromJNI();

    public native String CEncryptString(String data);

    public native String CDecryptString(String data);

    public native byte[] Encrypt(byte[] data);
    //public native byte[] Decrypt(byte[] data);
    public native byte[] Decrypt(byte[] data,String key);

}
