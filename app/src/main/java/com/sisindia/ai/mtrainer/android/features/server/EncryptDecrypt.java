package com.sisindia.ai.mtrainer.android.features.server;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptDecrypt {

    int EncryptedSize=16;
    int DecryptedSize=15;

    private byte[] demoKeyBytes = new byte[] { 0x00, 0x01, 0x02, 0x03,
            0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c,
            0x0d, 0x0e, 0x0f };

    private byte[] demoIVBytes = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04,
            0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d,
            0x0e, 0x0f };
    private String sPadding = "ISO10126Padding";

    public String EncryptString(String Data)
    {
        try
        {
            byte[] messageBytes=Data.getBytes();
            Cipher cipher = getAESCBCEncryptor(demoKeyBytes, demoIVBytes, sPadding);
            byte[] encrypted= encrypt(cipher, messageBytes);
            String enc=new String(encrypted);
            return enc;
        }
        catch (Exception e) {
            Log.Write("Error occured in encrypt string"+e.toString(), Log._LogLevel.NORAML);
            return null;
        }
    }

    public byte[] Encrypt(byte[] Data)
    {
        try
        {
            Cipher cipher = getAESCBCEncryptor(demoKeyBytes, demoIVBytes, sPadding);
            byte[] encrypted= encrypt(cipher, Data);
            return encrypted;
        } catch (Exception e)
        {
            Log.Write("Error occured in encrypt byte array"+e.toString(), Log._LogLevel.NORAML);
            return null;
        }
    }

    public byte[] Decrypt(byte[] Data)
    {
        try
        {
            Cipher cipher = getAESCBCDecryptor(demoKeyBytes, demoIVBytes, sPadding);
            byte[] decrypted= decrypt(cipher, Data);
            return decrypted;
        } catch (Exception e)
        {
            Log.Write("Error occured in decrypt byte array"+e.toString(), Log._LogLevel.NORAML);
            return null;
        }
    }
    public String DecryptString(String Data)
    {
        try
        {
            //byte[] encryptedMessageBytes=Data.getBytes("UTF-8");
            byte[] encryptedMessageBytes=Data.getBytes();
            Cipher decipher = getAESCBCDecryptor(demoKeyBytes, demoIVBytes, sPadding);
            byte[] decrypted=decrypt(decipher, encryptedMessageBytes);
            String dec=new String(decrypted);
            return dec;
        }
        catch (Exception e)
        {
            Log.Write("Error occured in decrypt string"+e.toString(), Log._LogLevel.NORAML);
            return null;
        }
    }

    private Cipher getAESCBCEncryptor(byte[] keyBytes, byte[] IVBytes, String padding) throws Exception {
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(IVBytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/"+padding);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        return cipher;
    }

    private byte[] encrypt(Cipher cipher, byte[] dataBytes) throws Exception {
        ByteArrayInputStream bIn = new ByteArrayInputStream(dataBytes);
        CipherInputStream cIn = new CipherInputStream(bIn, cipher);
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        int ch;
        while ((ch = cIn.read()) >= 0) {
            bOut.write(ch);
        }
        return bOut.toByteArray();
    }

    private Cipher getAESCBCDecryptor(byte[] keyBytes, byte[] IVBytes,
                                      String padding) throws Exception {
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(IVBytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/" + padding);
        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return cipher;
    }

    private byte[] decrypt(Cipher cipher, byte[] dataBytes)
            throws Exception {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        CipherOutputStream cOut = new CipherOutputStream(bOut, cipher);
        cOut.write(dataBytes);
        cOut.close();
        return bOut.toByteArray();
    }

    public static String compress(String string) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(string.getBytes());
        gos.close();
        //byte[] compressed = os.toByteArray();
        String Data=new String(os.toByteArray());
        os.close();
        return Data;
    }

    public static String decompress(String Data) throws IOException {
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(Data.getBytes());
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gis.read(data)) != -1) {
            string.append(new String(data, 0, bytesRead));
        }
        gis.close();
        is.close();
        return string.toString();
    }

    public static String ProcessString(String InStr)
    {
        try
        {
            StringBuilder OutStr = new StringBuilder();

            for (int i = 0; i < InStr.length(); i++)
            {
                String tmp=InStr.substring(i, i+1);
                if (tmp.compareTo("A") == 0) { OutStr.append("1");}
                else if (tmp.compareTo("B") == 0) { OutStr.append("F");}
                else if (tmp.compareTo("C") == 0) { OutStr.append("2");}
                else if (tmp.compareTo("D") == 0) { OutStr.append("w");}
                else if (tmp.compareTo("E") == 0) { OutStr.append("3");}
                else if (tmp.compareTo("F") == 0) { OutStr.append("B");}
                else if (tmp.compareTo("G") == 0) { OutStr.append("4");}
                else if (tmp.compareTo("H") == 0) { OutStr.append("j");}
                else if (tmp.compareTo("I") == 0) { OutStr.append("m");}
                else if (tmp.compareTo("J") == 0) { OutStr.append("n");}
                else if (tmp.compareTo("K") == 0) { OutStr.append("o");}
                else if (tmp.compareTo("L") == 0) { OutStr.append("a");}
                else if (tmp.compareTo("M") == 0) { OutStr.append("b");}
                else if (tmp.compareTo("N") == 0) { OutStr.append("c");}
                else if (tmp.compareTo("O") == 0) { OutStr.append("e");}
                else if (tmp.compareTo("P") == 0) { OutStr.append("f");}
                else if (tmp.compareTo("Q") == 0) { OutStr.append("g");}
                else if (tmp.compareTo("R") == 0) { OutStr.append("Z");}
                else if (tmp.compareTo("S") == 0) { OutStr.append("d");}
                else if (tmp.compareTo("T") == 0) { OutStr.append("Y");}
                else if (tmp.compareTo("U") == 0) { OutStr.append("h");}
                else if (tmp.compareTo("V") == 0) { OutStr.append("i");}
                else if (tmp.compareTo("W") == 0) { OutStr.append("l");}
                else if (tmp.compareTo("X") == 0) { OutStr.append("k");}
                else if (tmp.compareTo("Y") == 0) { OutStr.append("T");}
                else if (tmp.compareTo("Z") == 0) { OutStr.append("R");}
                else if (tmp.compareTo("a") == 0) { OutStr.append("L");}
                else if (tmp.compareTo("b") == 0) { OutStr.append("M");}
                else if (tmp.compareTo("c") == 0) { OutStr.append("N");}
                else if (tmp.compareTo("d") == 0) { OutStr.append("S");}
                else if (tmp.compareTo("e") == 0) { OutStr.append("O");}
                else if (tmp.compareTo("f") == 0) { OutStr.append("P");}
                else if (tmp.compareTo("g") == 0) { OutStr.append("Q");}
                else if (tmp.compareTo("h") == 0) { OutStr.append("U");}
                else if (tmp.compareTo("i") == 0) { OutStr.append("V");}
                else if (tmp.compareTo("j") == 0) { OutStr.append("H");}
                else if (tmp.compareTo("k") == 0) { OutStr.append("X");}
                else if (tmp.compareTo("l") == 0) { OutStr.append("W");}
                else if (tmp.compareTo("m") == 0) { OutStr.append("I");}
                else if (tmp.compareTo("n") == 0) { OutStr.append("J");}
                else if (tmp.compareTo("o") == 0) { OutStr.append("K");}
                else if (tmp.compareTo("p") == 0) { OutStr.append("0");}
                else if (tmp.compareTo("q") == 0) { OutStr.append("y");}
                else if (tmp.compareTo("r") == 0) { OutStr.append("9");}
                else if (tmp.compareTo("s") == 0) { OutStr.append("u");}
                else if (tmp.compareTo("t") == 0) { OutStr.append("8");}
                else if (tmp.compareTo("u") == 0) { OutStr.append("s");}
                else if (tmp.compareTo("v") == 0) { OutStr.append("7");}
                else if (tmp.compareTo("w") == 0) { OutStr.append("D");}
                else if (tmp.compareTo("x") == 0) { OutStr.append("6");}
                else if (tmp.compareTo("y") == 0) { OutStr.append("q");}
                else if (tmp.compareTo("z") == 0) { OutStr.append("5");}
                else if (tmp.compareTo("0") == 0) { OutStr.append("p");}
                else if (tmp.compareTo("1") == 0) { OutStr.append("A");}
                else if (tmp.compareTo("2") == 0) { OutStr.append("C");}
                else if (tmp.compareTo("3") == 0) { OutStr.append("E");}
                else if (tmp.compareTo("4") == 0) { OutStr.append("G");}
                else if (tmp.compareTo("5") == 0) { OutStr.append("z");}
                else if (tmp.compareTo("6") == 0) { OutStr.append("x");}
                else if (tmp.compareTo("7") == 0) { OutStr.append("v");}
                else if (tmp.compareTo("8") == 0) { OutStr.append("t");}
                else if (tmp.compareTo("9") == 0) { OutStr.append("r");}
                else { OutStr.append(InStr.substring(i, i + 1));}
            }

            return OutStr.toString();

        }
        catch (Exception e)
        {
            Log.Write("Error Occured in processing String: "+e.toString(), Log._LogLevel.NORAML);
            return "ERR";
        }

    }

}
