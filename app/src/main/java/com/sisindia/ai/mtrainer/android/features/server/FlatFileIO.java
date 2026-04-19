package com.sisindia.ai.mtrainer.android.features.server;

import android.os.Build;
import android.os.Environment;

import com.sisindia.ai.mtrainer.android.utils.Base64;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;

//decrption class
public class FlatFileIO {

   // public int BLOCK_SIZE = 1024 * 16;

    public int BLOCK_SIZE = 4096 * 16 *16 ;

    // to read end of file
    public String ReadTillEOF(int startSectorNumber, RandomAccessFile databaseReader) {
        try {
            byte[] buffer = new byte[BLOCK_SIZE];
            databaseReader.seek(startSectorNumber * BLOCK_SIZE);
            int bytesread = 0;
            StringBuilder Data = new StringBuilder();
            boolean result = false;
            while (!result) {
                bytesread = databaseReader.read(buffer, 0, BLOCK_SIZE);
                Data.append(new String(buffer, "US-ASCII"));
                if (Data.toString().contains("!!!EOF!!!")) {
                    result = true;
                }
            }
            Data = new StringBuilder(Data.toString().trim());
            Data = new StringBuilder(Data.substring(0, Data.indexOf("!!!EOF!!!")));
            Data = new StringBuilder(decompress(Data.toString()));
            return Data.toString();
        } catch (Exception ex) {
            return "ERROR";
        }
    }

    //loading oringinal file ....if file is not encrypted
    public void WriteFileInChunk_Original(int startSectorNumber, RandomAccessFile databaseReader, int length, DataOutputStream ns) {
        try {
            byte[] buffer = new byte[BLOCK_SIZE * 4];
            databaseReader.seek(startSectorNumber);
            int bytesread = 0;
            int leftbyte = length;
            while (leftbyte > 0) {
                bytesread = databaseReader.read(buffer, 0, BLOCK_SIZE * 4);
                if (bytesread == BLOCK_SIZE * 4) {
                    //Decrypt buffer and ic_write to network stream
                    ns.write(buffer, 0, bytesread);
                    leftbyte = leftbyte - bytesread;
                } else {
                    //Decrypt buffer and ic_write to network stream
                    ns.write(buffer, 0, leftbyte);
                    leftbyte = 0;
                }
            }
        } catch (Exception ignored) {
                ignored.printStackTrace();
        }
    }

    public void WriteFileInChunck_After_Encryption(RandomAccessFile randomAccessFile, int length, PrintStream printStream, String fname, String dokument) {
        clsCrypto obj = new clsCrypto();
        try {
            String filepath = dokument;
            byte[] buffer = new byte[BLOCK_SIZE * 4];
            int bytesread = 0;
            int leftbyte = length;
            while (leftbyte >= BLOCK_SIZE * 4) {
                bytesread = randomAccessFile.read(buffer, 0, BLOCK_SIZE * 4);
                leftbyte = leftbyte - bytesread;
                //Decrypt buffer and ic_write to network stream
                byte[] decBuffer = obj.Encrypt(buffer);
                printStream.write(decBuffer, 0, bytesread);
            }
            int blockRead = leftbyte / 16;
            int remaingBytes = leftbyte % 16;

            blockRead = blockRead * 16;
            android.util.Log.e("lib", String.valueOf(blockRead));
            android.util.Log.e("remaining", String.valueOf(remaingBytes));
            Log.Write("blockread: " + blockRead, Log._LogLevel.SERVER_DETAILS);
            Log.Write("remaingBytes: " + remaingBytes, Log._LogLevel.SERVER_DETAILS);

            buffer = new byte[blockRead];
            bytesread = randomAccessFile.read(buffer, 0, blockRead);
            //Decrypt buffer and ic_write to network stream
            byte[] decBuffer = obj.Encrypt(buffer);
            printStream.write(decBuffer, 0, bytesread);
            //  bos_e.ic_write(buffer, 0, bytesread);
            //bos.ic_write(decBuffer, 0, bytesread);

//			if(blockRead>4096)
//			{
//				while(blockRead>0)
//				{
//					if(blockRead>4096)
//					{
//						int read=4096*16;
//						buffer = new byte[read];
//						bytesread = databaseReader.read(buffer, 0, read);
//						//Decrypt buffer and ic_write to network stream
//						byte[] decBuffer= obj.Decrypt(buffer, Key);
//						ns.ic_write(decBuffer, 0, bytesread);
//						blockRead=blockRead-bytesread;
//					}
//					else
//					{
//						blockRead=blockRead*16;
//						buffer = new byte[blockRead];
//						bytesread = databaseReader.read(buffer, 0, blockRead);
//						//Decrypt buffer and ic_write to network stream
//						byte[] decBuffer= obj.Decrypt(buffer, Key);
//						ns.ic_write(decBuffer, 0, bytesread);
//						blockRead=blockRead-bytesread;
//					}
//				}
//			}
//			else
//			{
//				blockRead=blockRead*16;
//				buffer = new byte[blockRead];
//				bytesread = databaseReader.read(buffer, 0, blockRead);
//				//Decrypt buffer and ic_write to network stream
//				byte[] decBuffer= obj.Decrypt(buffer, Key);
//				ns.ic_write(decBuffer, 0, bytesread);
//			}

            if (remaingBytes > 0) {
                buffer = new byte[remaingBytes];
                bytesread = randomAccessFile.read(buffer, 0, remaingBytes);
                randomAccessFile.write(buffer, 0, remaingBytes);
                //  bos.ic_write(buffer, 0, remaingBytes);
                // bos_e.ic_write(buffer, 0, remaingBytes);
            }
        } catch (Exception e) {

        }
    }

    public void decryption(RandomAccessFile randomAccessFile, PrintStream printStream, int length, String key, String fname, String dokument, int seek) {
        clsCrypto obj = new clsCrypto();
        byte[] buffer = new byte[length];
        int bytesread = 0;
        try {
            bytesread = randomAccessFile.read(buffer, 0, length);
            byte[] decbuffer = obj.Decrypt(buffer, key);
            printStream.write(decbuffer, 0, bytesread);
        } catch (IOException e) {
            e.printStackTrace();
        }
//		try {
//			String filepath = dokument;
//			byte[] buffer = new byte[BLOCK_SIZE*4];
//			int bytesread=0;
//			int leftbyte = length;
//			while (leftbyte>=BLOCK_SIZE*4)
//			{
//				bytesread = randomAccessFile.read(buffer, 0, BLOCK_SIZE*4);
//				leftbyte = leftbyte - bytesread;
//				//Decrypt buffer and ic_write to network stream
//				byte[] decBuffer= obj.Decrypt(buffer,key);
//				printStream.ic_write(decBuffer, 0, bytesread);
//			}
//			int blockRead=leftbyte/16;
//			int remaingBytes=leftbyte%16;
//
//			blockRead=blockRead*16;
//			android.util.Log.e("lib", String.valueOf(blockRead));
//			android.util.Log.e("remaining", String.valueOf(remaingBytes));
//			Log.Write("blockread: "+blockRead, _LogLevel.SERVER_DETAILS);
//			Log.Write("remaingBytes: "+remaingBytes, _LogLevel.SERVER_DETAILS);
//
//			buffer = new byte[blockRead];
//			bytesread = randomAccessFile.read(buffer, 0, blockRead);
//			//Decrypt buffer and ic_write to network stream
//			byte[] decBuffer= obj.Decrypt(buffer,key);
//			printStream.ic_write(decBuffer, 0, bytesread);
//			//  bos_e.ic_write(buffer, 0, bytesread);
//			//bos.ic_write(decBuffer, 0, bytesread);
//
////			if(blockRead>4096)
////			{
////				while(blockRead>0)
////				{
////					if(blockRead>4096)
////					{
////						int read=4096*16;
////						buffer = new byte[read];
////						bytesread = databaseReader.read(buffer, 0, read);
////						//Decrypt buffer and ic_write to network stream
////						byte[] decBuffer= obj.Decrypt(buffer, Key);
////						ns.ic_write(decBuffer, 0, bytesread);
////						blockRead=blockRead-bytesread;
////					}
////					else
////					{
////						blockRead=blockRead*16;
////						buffer = new byte[blockRead];
////						bytesread = databaseReader.read(buffer, 0, blockRead);
////						//Decrypt buffer and ic_write to network stream
////						byte[] decBuffer= obj.Decrypt(buffer, Key);
////						ns.ic_write(decBuffer, 0, bytesread);
////						blockRead=blockRead-bytesread;
////					}
////				}
////			}
////			else
////			{
////				blockRead=blockRead*16;
////				buffer = new byte[blockRead];
////				bytesread = databaseReader.read(buffer, 0, blockRead);
////				//Decrypt buffer and ic_write to network stream
////				byte[] decBuffer= obj.Decrypt(buffer, Key);
////				ns.ic_write(decBuffer, 0, bytesread);
////			}
//
//			if(remaingBytes>0)
//			{
//				buffer = new byte[remaingBytes];
//				bytesread = randomAccessFile.read(buffer, 0, remaingBytes);
//				randomAccessFile.ic_write(buffer, 0, remaingBytes);
//				//  bos.ic_write(buffer, 0, remaingBytes);
//				// bos_e.ic_write(buffer, 0, remaingBytes);
//			}
//		}
//		catch (Exception e)
//		{
//
//		}
//        try {
//            String filepath = dokument;
//            //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/androidwebserver/Encrypted/"+fName;
//            //bos = new BufferedOutputStream(new FileOutputStream(filepath));
//            // bos_e = new BufferedOutputStream(new FileOutputStream(filepath));
////			range = range.substring(6);
////			long startFrom = 0, endAt = -1;
////			int minus = range.indexOf('-');
////			if(minus > 0){
////				try{
////					String startR = range.substring(0, minus);
////					startFrom = Long.parseLong(startR);
////					String endR = range.substring(minus + 1);
////					endAt = Long.parseLong(endR);
////				}catch(NumberFormatException nfe){
////				}
////			}
//            //randomAccessFile.seek(seek);
//            //if (length >= 104857600) {
//            byte[] buffer = new byte[BLOCK_SIZE * 8];
//            int bytesread = 0;
//            int leftbyte = length;
//            android.util.Log.e("seek", String.valueOf(seek));
//            randomAccessFile.seek(seek);
//            while (leftbyte >= BLOCK_SIZE * 8) {
//                bytesread = randomAccessFile.read(buffer, 0, BLOCK_SIZE * 8);
//                leftbyte = leftbyte - bytesread;
//                //Decrypt buffer and ic_write to network stream
//                byte[] decBuffer = obj.Decrypt(buffer, key);
//                printStream.ic_write(decBuffer, 0, bytesread);
//                //android.util.Log.e("debug", String.valueOf(bytesread));
//                //  bos_e.ic_write(buffer, 0, bytesread);
//                //  bos.ic_write(decBuffer, 0, bytesread);
//            }
//
//            int blockRead = leftbyte / BLOCK_SIZE * 8;
//            int remaingBytes = leftbyte % BLOCK_SIZE * 8;
//            blockRead = blockRead * BLOCK_SIZE * 8;
//            android.util.Log.e("lib", String.valueOf(blockRead));
//            android.util.Log.e("remaining", String.valueOf(remaingBytes));
//            Log.Write("blockread: " + blockRead, _LogLevel.SERVER_DETAILS);
//            Log.Write("remaingBytes: " + remaingBytes, _LogLevel.SERVER_DETAILS);
//
//            buffer = new byte[blockRead];
//            bytesread = randomAccessFile.read(buffer, 0, blockRead);
////            //Decrypt buffer and ic_write to network stream
//            byte[] decBuffer = obj.Decrypt(buffer, key);
//            printStream.ic_write(decBuffer, 0, bytesread);
//            //  bos_e.ic_write(buffer, 0, bytesread);
//            //bos.ic_write(decBuffer, 0, bytesread);
//
////			if(blockRead>4096)
////			{
////				while(blockRead>0)
////				{
////					if(blockRead>4096)
////					{
////						int read=4096*16;
////						buffer = new byte[read];
////						bytesread = databaseReader.read(buffer, 0, read);
////						//Decrypt buffer and ic_write to network stream
////						byte[] decBuffer= obj.Decrypt(buffer, Key);
////						ns.ic_write(decBuffer, 0, bytesread);
////						blockRead=blockRead-bytesread;
////					}
////					else
////					{
////						blockRead=blockRead*16;
////						buffer = new byte[blockRead];
////						bytesread = databaseReader.read(buffer, 0, blockRead);
////						//Decrypt buffer and ic_write to network stream
////						byte[] decBuffer= obj.Decrypt(buffer, Key);
////						ns.ic_write(decBuffer, 0, bytesread);
////						blockRead=blockRead-bytesread;
////					}
////				}
////			}
////			else
////			{
////				blockRead=blockRead*16;
////				buffer = new byte[blockRead];
////				bytesread = databaseReader.read(buffer, 0, blockRead);
////				//Decrypt buffer and ic_write to network stream
////				byte[] decBuffer= obj.Decrypt(buffer, Key);
////				ns.ic_write(decBuffer, 0, bytesread);
////			}
//
//            if (remaingBytes > 0) {
//                buffer = new byte[remaingBytes];
//                bytesread = randomAccessFile.read(buffer, 0, remaingBytes);
//                printStream.ic_write(buffer, 0, remaingBytes);
//                //  bos.ic_write(buffer, 0, remaingBytes);
//                // bos_e.ic_write(buffer, 0, remaingBytes);
//            }
//            android.util.Log.e("decr", "decryption done");
////            } else if (length >= 52428800) {
////                int block_size = 2048 * 2048;
////                byte[] buffer = new byte[block_size];
////                int bytesread = 0;
////                int leftbyte = length;
////
////                while (leftbyte >= block_size) {
////                    bytesread = randomAccessFile.read(buffer, 0, block_size);
////                    leftbyte = leftbyte - bytesread;
////                    //Decrypt buffer and ic_write to network stream
////                    byte[] decBuffer = obj.Decrypt(buffer, key);
////                    printStream.ic_write(decBuffer, 0, bytesread);
////                }
////
////                int blockRead = leftbyte / 16;
////                int remaingBytes = leftbyte % 16;
////                blockRead = blockRead * 16;
////                android.util.Log.e("lib", String.valueOf(blockRead));
////                android.util.Log.e("remaining", String.valueOf(remaingBytes));
////                Log.Write("blockread: " + blockRead, _LogLevel.SERVER_DETAILS);
////                Log.Write("remaingBytes: " + remaingBytes, _LogLevel.SERVER_DETAILS);
////
////                buffer = new byte[blockRead];
////                bytesread = randomAccessFile.read(buffer, 0, blockRead);
////                //Decrypt buffer and ic_write to network stream
////                byte[] decBuffer = obj.Decrypt(buffer, key);
////                printStream.ic_write(decBuffer, 0, bytesread);
////
////                if (remaingBytes > 0) {
////                    buffer = new byte[remaingBytes];
////                    bytesread = randomAccessFile.read(buffer, 0, remaingBytes);
////                    printStream.ic_write(buffer, 0, remaingBytes);
////
////                }
////            } else if (length >= 26214400) {
////                int block_size = BLOCK_SIZE * 1024;
////                byte[] buffer = new byte[block_size];
////                int bytesread = 0;
////                int leftbyte = length;
////
////                while (leftbyte >= block_size) {
////                    bytesread = randomAccessFile.read(buffer, 0, block_size);
////                    leftbyte = leftbyte - bytesread;
////                    //Decrypt buffer and ic_write to network stream
////                    byte[] decBuffer = obj.Decrypt(buffer, key);
////                    printStream.ic_write(decBuffer, 0, bytesread);
////                    //  bos_e.ic_write(buffer, 0, bytesread);
////                    //  bos.ic_write(decBuffer, 0, bytesread);
////                }
////
////                int blockRead = leftbyte / 16;
////                int remaingBytes = leftbyte % 16;
////                blockRead = blockRead * 16;
////                android.util.Log.e("lib", String.valueOf(blockRead));
////                android.util.Log.e("remaining", String.valueOf(remaingBytes));
////                Log.Write("blockread: " + blockRead, _LogLevel.SERVER_DETAILS);
////                Log.Write("remaingBytes: " + remaingBytes, _LogLevel.SERVER_DETAILS);
////
////                buffer = new byte[blockRead];
////                bytesread = randomAccessFile.read(buffer, 0, blockRead);
////                //Decrypt buffer and ic_write to network stream
////                byte[] decBuffer = obj.Decrypt(buffer, key);
////                printStream.ic_write(decBuffer, 0, bytesread);
////
////                if (remaingBytes > 0) {
////                    buffer = new byte[remaingBytes];
////                    bytesread = randomAccessFile.read(buffer, 0, remaingBytes);
////                    printStream.ic_write(buffer, 0, remaingBytes);
////                }
////            } else {
////                int block_size = 512 * 16;
////                byte[] buffer = new byte[block_size * 8];
////                int bytesread = 0;
////                int leftbyte = length;
////
////                while (leftbyte >= block_size * 8) {
////                    bytesread = randomAccessFile.read(buffer, 0, block_size * 8);
////                    leftbyte = leftbyte - bytesread;
////                    //Decrypt buffer and ic_write to network stream
////                    byte[] decBuffer = obj.Decrypt(buffer, key);
////                    printStream.ic_write(decBuffer, 0, bytesread);
////                }
////
////                int blockRead = leftbyte / 16;
////                int remaingBytes = leftbyte % 16;
////                blockRead = blockRead * 16;
////                android.util.Log.e("lib", String.valueOf(blockRead));
////                android.util.Log.e("remaining", String.valueOf(remaingBytes));
////                Log.Write("blockread: " + blockRead, _LogLevel.SERVER_DETAILS);
////                Log.Write("remaingBytes: " + remaingBytes, _LogLevel.SERVER_DETAILS);
////
////                buffer = new byte[blockRead];
////                bytesread = randomAccessFile.read(buffer, 0, blockRead);
////                //Decrypt buffer and ic_write to network stream
////                byte[] decBuffer = obj.Decrypt(buffer, key);
////                printStream.ic_write(decBuffer, 0, bytesread);
////
////                if (remaingBytes > 0) {
////                    buffer = new byte[remaingBytes];
////                    bytesread = randomAccessFile.read(buffer, 0, remaingBytes);
////                    printStream.ic_write(buffer, 0, remaingBytes);
////                }
////            }
//        } catch (Exception ignored) {
//            android.util.Log.e("exception", ignored.getMessage());
//            Crashlytics.logException(ignored);
//        }

    }

    public void Decryption(int startSectorNumber, RandomAccessFile databaseReader, int length, DataOutputStream ns, String Key) throws IOException {
//        try {
//           // Key = "EdukiteDemoKnowledeflex17";
//            clsCrypto obj = new clsCrypto();
//            //   StreamReader sin = new StreamReader(this.HTTPResponse.fs);
//            int len = length;
//            int read = 0;
//
//            byte[] bytes = new byte[1024 * 16]; // the buffer
//            byte[] bytesEnc = new byte[0]; // the buffer
//            byte[] bytesE = new byte[bytes.length + bytesEnc.length]; // the buffer
//
//            long fullblock = 0;
//            long slackblock = 0;
//            long genoffset = 0;
//            int LeftBytes = 0;
//
//
//            LeftBytes = len;
//
//            fullblock = startSectorNumber / bytes.length;
//            slackblock = startSectorNumber % bytes.length;
//            genoffset = (fullblock * (bytes.length + bytesEnc.length));
//
//            databaseReader.seek(startSectorNumber);
//            LeftBytes = (int) (LeftBytes - genoffset);
//
//            long slacklen = 0;
//
//            slacklen = bytes.length - slackblock;
//            if (startSectorNumber > 0) {
//                read = databaseReader.read(bytesE, 0, bytes.length + bytesEnc.length);
//                LeftBytes = LeftBytes - (bytes.length + bytesEnc.length);
//                if (read == (bytes.length + bytesEnc.length)) {
//                    byte[] bytesDec = new byte[bytes.length]; // the buffer
//
//                    bytesDec = obj.Decrypt(bytesE, Key);
//
//                    ns.ic_write(bytesDec, (int) slackblock, (int) slacklen);
//                    //Thread.Sleep(1000 * 1200);
//                    // System.Threading.Thread.Sleep(1000 / kbps);
//                    //  ERRCODE = 3;
//                } else {
//                    //TRACK
//                    //int flag = 0;
//                }
//
//            }
//
//
//            while (LeftBytes > 0) {
//
//                if (LeftBytes > (bytes.length + bytesEnc.length)) {
//                    read = databaseReader.read(bytesE, 0, bytes.length + bytesEnc.length);
//                    byte[] bytesDec = new byte[bytes.length]; // the buffer
//                    bytesDec = obj.Decrypt(bytesE, Key);
//                    ns.ic_write(bytesDec, 0, bytesE.length);
//                    LeftBytes = LeftBytes - bytes.length - bytesEnc.length;
//                } else {
//                    //ERRCODE = 6;
//                    read = databaseReader.read(bytes, 0, bytes.length);
//                    ns.ic_write(bytes, 0, read);
//                    LeftBytes = LeftBytes - read;
//                }
//
//            }
//
//        } catch (Exception es) {
//
//        }
        clsCrypto obj = new clsCrypto();
        int EBLen = 1024 * 16;
        int DBLen = 1024 * 16;
        byte[] bytesEnc = new byte[EBLen];
        byte[] bytesDec = new byte[DBLen];
        int read = 0;


        while ((read = databaseReader.read(bytesEnc, 0,
                bytesEnc.length)) != -1) {
            if (read == bytesEnc.length) {
                bytesDec = obj.Decrypt(bytesEnc, Key);
                ns.write(bytesDec, 0, (int) bytesDec.length);
            } else {
                int current_inb_position = 0;

                while (read >= 1024) {
                    byte[] kk = new byte[1024];
                    for (int ll = 0; ll < kk.length; ll++) {
                        kk[ll] = bytesEnc[current_inb_position];
                        current_inb_position++;
                    }
                    kk = obj.Decrypt(kk, Key);
                    ns.write(kk, 0, (int) kk.length);
                    read = read - kk.length;
                }

                while (read >= 16) {
                    byte[] kk = new byte[16];
                    for (int i1 = 0; i1 < kk.length; i1++) {
                        kk[i1] = bytesEnc[current_inb_position];
                        current_inb_position++;
                    }
                    kk = obj.Decrypt(kk, Key);
                    ns.write(kk, 0, (int) kk.length);
                    read = read - kk.length;
                }
                if (read != 0) {
                    byte[] kk = new byte[16];
                    for (int ih = 0; ih < read; ih++) {
                        kk[ih] = bytesEnc[current_inb_position];
                        current_inb_position++;
                    }
                    ns.write(kk, 0, (int) read);
                }
            }

        }
        ns.flush();
        ns.close();
    }

    public void WriteFileInChunk_After_Decryption(int startSectorNumber, RandomAccessFile databaseReader, int length, DataOutputStream ns, String Key) {
        //android.util.Log.e("dec", "came library");
        clsCrypto obj = new clsCrypto();
        //  android.util.Log.e("lib", "loaded library");
        // BufferedOutputStream bos;
        // BufferedOutputStream bos_e;
        try {
            //  String filepath = dokument;
            //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/androidwebserver/Encrypted/"+fName;
            //bos = new BufferedOutputStream(new FileOutputStream(filepath));
            // bos_e = new BufferedOutputStream(new FileOutputStream(filepath));
            //   Key = "EdukiteDemoKnowledeflex17";
            byte[] buffer = new byte[BLOCK_SIZE];
            databaseReader.seek(startSectorNumber);
            int bytesread = 0;
            int leftbyte = length;

            while (leftbyte >= BLOCK_SIZE) {
                bytesread = databaseReader.read(buffer, 0, BLOCK_SIZE);
                leftbyte = leftbyte - bytesread;
                //Decrypt buffer and ic_write to network stream
                byte[] decBuffer = obj.Decrypt(buffer, Key);
                ns.write(decBuffer, 0, bytesread);
                ns.flush();

                //  bos_e.ic_write(buffer, 0, bytesread);
                //  bos.ic_write(decBuffer, 0, bytesread);
            }
            int blockRead = leftbyte / 16;
            int remaingBytes = leftbyte % 16;

            blockRead = blockRead * 16;
//            android.util.Log.e("lib", String.valueOf(blockRead));
//            android.util.Log.e("remaining", String.valueOf(remaingBytes));
            Log.Write("blockread: " + blockRead, Log._LogLevel.SERVER_DETAILS);
            Log.Write("remaingBytes: " + remaingBytes, Log._LogLevel.SERVER_DETAILS);

            buffer = new byte[blockRead];
            bytesread = databaseReader.read(buffer, 0, blockRead);
            //Decrypt buffer and ic_write to network stream
            byte[] decBuffer = obj.Decrypt(buffer, Key);
            ns.write(decBuffer, 0, bytesread);
            ns.flush();

            //  bos_e.ic_write(buffer, 0, bytesread);
            //bos.ic_write(decBuffer, 0, bytesread);

//			if(blockRead>4096)
//			{
//				while(blockRead>0)
//				{
//					if(blockRead>4096)
//					{
//						int read=4096*16;	
//						buffer = new byte[read];
//						bytesread = databaseReader.read(buffer, 0, read);			 
//						//Decrypt buffer and ic_write to network stream
//						byte[] decBuffer= obj.Decrypt(buffer, Key);
//						ns.ic_write(decBuffer, 0, bytesread);
//						blockRead=blockRead-bytesread;
//					}
//					else
//					{
//						blockRead=blockRead*16;	
//						buffer = new byte[blockRead];
//						bytesread = databaseReader.read(buffer, 0, blockRead);			 
//						//Decrypt buffer and ic_write to network stream
//						byte[] decBuffer= obj.Decrypt(buffer, Key);
//						ns.ic_write(decBuffer, 0, bytesread);
//						blockRead=blockRead-bytesread;
//					}
//				}
//			}
//			else
//			{
//				blockRead=blockRead*16;	
//				buffer = new byte[blockRead];
//				bytesread = databaseReader.read(buffer, 0, blockRead);			 
//				//Decrypt buffer and ic_write to network stream
//				byte[] decBuffer= obj.Decrypt(buffer, Key);
//				ns.ic_write(decBuffer, 0, bytesread);
//			}

            if (remaingBytes > 0) {
                buffer = new byte[remaingBytes];
                bytesread = databaseReader.read(buffer, 0, remaingBytes);
                ns.write(buffer, 0, remaingBytes);
                ns.flush();

                //  bos.ic_write(buffer, 0, remaingBytes);
                // bos_e.ic_write(buffer, 0, remaingBytes);
            }

            //  ns.close();
            //			  buffer = new byte[1024];
            //			  while(leftbyte>=1024)
            //			  {
            //				  bytesread = databaseReader.read(buffer, 0, 1024);
            //				  leftbyte = leftbyte - bytesread;
            //				  //Decrypt buffer and ic_write to network stream
            //				  byte[] decBuffer= obj.Decrypt(buffer, Key);
            //				  ns.ic_write(decBuffer, 0, bytesread);
            //			  }
            //
            //			  buffer = new byte[16];
            //			  while(leftbyte>=16)
            //			  {
            //				  bytesread = databaseReader.read(buffer, 0, 16);
            //				  leftbyte = leftbyte - bytesread;
            //				  //Decrypt buffer and ic_write to network stream
            //				  byte[] decBuffer= obj.Decrypt(buffer, Key);
            //				  ns.ic_write(decBuffer, 0, bytesread);
            //			  }
            //
            //			  if(leftbyte>0)
            //			  {
            //				  bytesread = databaseReader.read(buffer, 0, leftbyte);

            //				  leftbyte = leftbyte - bytesread;
            //				  ns.ic_write(buffer, 0, leftbyte);
            //			  }
            //  bos.close();

            ns.flush();
            ns.close();
        } catch (Exception ignored) {
            //  ns.close();
            ignored.fillInStackTrace();
        }
    }

// decrpting ecryted file with sector value
    public void WriteFileInChunk_After_Decryption1(int startSectorNumber, RandomAccessFile databaseReader, int length, DataOutputStream ns, String Key) {
        clsCrypto obj = new clsCrypto();

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
          //  BLOCK_SIZE = 4096 * 16 * 8; // webview
       //     BLOCK_SIZE = 8192 * 16 * 8; // webview
         //   BLOCK_SIZE = 4096 * 16 * 16; // webview
           BLOCK_SIZE = 4096 * 16 * 32;
          //  BLOCK_SIZE = 5120 * 16 * 16;

        } else {

          //  BLOCK_SIZE = 1024 * 16; //exomedia player
           // BLOCK_SIZE = 4096 * 16; //exomedia player

         //   BLOCK_SIZE = 2048 * 16;
            BLOCK_SIZE = 3072 * 16 ;
          //  BLOCK_SIZE = 4096 * 16;

        }
        // String filepath = Environment.getExternalStorageDirectory() + "/test.txt";
        //BufferedOutputStream bos;
        //StringWriter sr = new StringWriter();
        try {
            //bos = new BufferedOutputStream(new FileOutputStream(filepath));
            //   Key = "EdukiteDemoKnowledeflex17";
            System.gc();
            byte[] buffer = new byte[BLOCK_SIZE];
            databaseReader.seek(startSectorNumber);
            int bytesread = 0;
            int leftbyte = length;

            if (length > BLOCK_SIZE) {
                bytesread = databaseReader.read(buffer, 0, BLOCK_SIZE);
            } else {
                bytesread = databaseReader.read(buffer, 0, length);
            }
            // bos.ic_write(buffer, 0, bytesread);
            Log.Write("bytesRead: " + bytesread, Log._LogLevel.SERVER_DETAILS);
            /*File file = new File(Environment.getExternalStorageDirectory() + "/"+ "Test.mp4");
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStream fo = new FileOutputStream(file);*/
            while (leftbyte > 0) {
                leftbyte = leftbyte - bytesread;
                if (bytesread == BLOCK_SIZE) {
                    //Decrypt buffer and ic_write to network stream
                    byte[] decBuffer = obj.Decrypt(buffer, Key);
                    ns.write(decBuffer, 0, bytesread);

/*
                    if (file.exists()){

                        fo.write(decBuffer,0,bytesread);

                    }
*/
                    //   sr.getBuffer().setLength(0);
                    // ns.ic_write(decBuffer,0,bytesread);
                    // ns.flush();
                    // bos.ic_write(decBuffer);
                    if (leftbyte > BLOCK_SIZE) {
                        bytesread = databaseReader.read(buffer, 0, BLOCK_SIZE);
                    } else {
                        bytesread = databaseReader.read(buffer, 0, leftbyte);
                    }

                } else {
                    //Decrypt buffer and ic_write to network stream
                    int current_inb_position = 0;

                    while (bytesread >= 1024) {
                        //Log.Write("LOOP-ELSE-1", _LogLevel.SERVER_DETAILS);
                        byte[] kk = new byte[1024];
                        for (int i = 0; i < kk.length; i++) {
                            kk[i] = buffer[current_inb_position];
                            current_inb_position++;
                        }
                        kk = obj.Decrypt(kk, Key);
                        ns.write(kk, 0, kk.length);
                        android.util.Log.d("ghgjghjgh","write");
                        /*if (file==null){
                            File file1 = new File(Environment.getExternalStorageDirectory() + "/"+ "Test.mp4");
                            if (!file1.exists()) {
                                file1.createNewFile();
                            }else {
                                fo.write(kk,0,bytesread);
                            }
                        }else {
                            if (file.exists()) {

                                fo.write(kk, 0, bytesread);

                            }
                        }*/
                        // bos.ic_write(kk);
                        bytesread = bytesread - kk.length;

                    }

                    while (bytesread >= 16) {
                        //Log.Write("LOOP-ELSE-2-A", _LogLevel.SERVER_DETAILS);
                        byte[] kk = new byte[16];
                        for (int i = 0; i < kk.length; i++) {
                            kk[i] = buffer[current_inb_position];
                            current_inb_position++;
                        }
                        //	Log.Write("LOOP-ELSE-2-B", _LogLevel.SERVER_DETAILS);
                        kk = obj.Decrypt(kk, Key);
                        //Log.Write("LOOP-ELSE-2-C", _LogLevel.SERVER_DETAILS);
                        ns.write(kk, 0, kk.length);
                        android.util.Log.d("ghgjghjgh1","write");
                        /*if (file==null){
                            File file1 = new File(Environment.getExternalStorageDirectory() + "/"+ "Test.mp4");
                            if (!file1.exists()) {
                                file1.createNewFile();
                            }else {
                                fo.write(kk,0,bytesread);
                            }
                        }else {
                            if (file.exists()) {

                                fo.write(kk, 0, bytesread);

                            }
                        }*/
                        // bos.ic_write(kk);
                        bytesread = bytesread - kk.length;

                        //Log.Write("LOOP-ELSE-2-D", _LogLevel.SERVER_DETAILS);
                    }
                    if (bytesread != 0) {
                        //Log.Write("LOOP-ELSE-3", _LogLevel.SERVER_DETAILS);
                        byte[] kk = new byte[16];
                        for (int i = 0; i < bytesread; i++) {
                            kk[i] = buffer[current_inb_position];
                            current_inb_position++;
                        }
                        ns.write(kk, 0, bytesread);
                        android.util.Log.d("ghgjghjgh2","write");

                        /*if (file==null){
                            File file1 = new File(Environment.getExternalStorageDirectory() + "/"+ "Test.mp4");
                            if (!file1.exists()) {
                                file1.createNewFile();
                            }else {
                                fo.write(kk,0,bytesread);
                            }
                        }else {
                            if (file.exists()) {

                                fo.write(kk, 0, bytesread);

                            }
                        }*/

                        // bos.ic_write(kk);
                    }
                }
            }
            //fo.close();
            ns.flush();
            ns.close();
            System.gc();

            //bos.flush();
            //bos.close();
        } catch (Exception ignored) {
            android.util.Log.d("bytesRead: ",ignored.getMessage());
            ignored.fillInStackTrace();
        }

    }

    //decompressing compressed files
    private String decompress(String Data) throws IOException {
        byte[] compressed = Base64.decode(Data);
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed, 4, compressed.length - 4);
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

    public void WriteFileInChunk_After_Decryption(int startSector, RandomAccessFile inputBuffer_local_1, int contentLength, PrintStream ps, String decKey) {
        //  android.util.Log.e("dec", "came library");
        clsCrypto obj = new clsCrypto();
        // android.util.Log.e("lib", "loaded library");
        // BufferedOutputStream bos;
        // BufferedOutputStream bos_e;
        try {
            //  String filepath = dokument;
            //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/androidwebserver/Encrypted/"+fName;
            //bos = new BufferedOutputStream(new FileOutputStream(filepath));
            // bos_e = new BufferedOutputStream(new FileOutputStream(filepath));

            byte[] buffer = new byte[BLOCK_SIZE * 16];
            inputBuffer_local_1.seek(startSector);
            int bytesread = 0;
            int leftbyte = contentLength;

            while (leftbyte >= BLOCK_SIZE * 16) {
                bytesread = inputBuffer_local_1.read(buffer, 0, BLOCK_SIZE * 16);
                leftbyte = leftbyte - bytesread;
                //Decrypt buffer and ic_write to network stream
                byte[] decBuffer = obj.Decrypt(buffer, decKey);
                ps.write(decBuffer, 0, bytesread);
                //  bos_e.ic_write(buffer, 0, bytesread);
                //  bos.ic_write(decBuffer, 0, bytesread);
            }
            int blockRead = leftbyte / 16;
            int remaingBytes = leftbyte % 16;

            blockRead = blockRead * 16;
//            android.util.Log.e("lib", String.valueOf(blockRead));
//            android.util.Log.e("remaining", String.valueOf(remaingBytes));
            Log.Write("blockread: " + blockRead, Log._LogLevel.SERVER_DETAILS);
            Log.Write("remaingBytes: " + remaingBytes, Log._LogLevel.SERVER_DETAILS);

            buffer = new byte[blockRead];
            bytesread = inputBuffer_local_1.read(buffer, 0, blockRead);
            //Decrypt buffer and ic_write to network stream
            byte[] decBuffer = obj.Decrypt(buffer, decKey);
            ps.write(decBuffer, 0, bytesread);
            //  bos_e.ic_write(buffer, 0, bytesread);
            //bos.ic_write(decBuffer, 0, bytesread);

//			if(blockRead>4096)
//			{
//				while(blockRead>0)
//				{
//					if(blockRead>4096)
//					{
//						int read=4096*16;
//						buffer = new byte[read];
//						bytesread = databaseReader.read(buffer, 0, read);
//						//Decrypt buffer and ic_write to network stream
//						byte[] decBuffer= obj.Decrypt(buffer, Key);
//						ns.ic_write(decBuffer, 0, bytesread);
//						blockRead=blockRead-bytesread;
//					}
//					else
//					{
//						blockRead=blockRead*16;
//						buffer = new byte[blockRead];
//						bytesread = databaseReader.read(buffer, 0, blockRead);
//						//Decrypt buffer and ic_write to network stream
//						byte[] decBuffer= obj.Decrypt(buffer, Key);
//						ns.ic_write(decBuffer, 0, bytesread);
//						blockRead=blockRead-bytesread;
//					}
//				}
//			}
//			else
//			{
//				blockRead=blockRead*16;
//				buffer = new byte[blockRead];
//				bytesread = databaseReader.read(buffer, 0, blockRead);
//				//Decrypt buffer and ic_write to network stream
//				byte[] decBuffer= obj.Decrypt(buffer, Key);
//				ns.ic_write(decBuffer, 0, bytesread);
//			}

            if (remaingBytes > 0) {
                buffer = new byte[remaingBytes];
                bytesread = inputBuffer_local_1.read(buffer, 0, remaingBytes);
                ps.write(buffer, 0, remaingBytes);
                //  bos.ic_write(buffer, 0, remaingBytes);
                // bos_e.ic_write(buffer, 0, remaingBytes);
            }
            //			  buffer = new byte[1024];
            //			  while(leftbyte>=1024)
            //			  {
            //				  bytesread = databaseReader.read(buffer, 0, 1024);
            //				  leftbyte = leftbyte - bytesread;
            //				  //Decrypt buffer and ic_write to network stream
            //				  byte[] decBuffer= obj.Decrypt(buffer, Key);
            //				  ns.ic_write(decBuffer, 0, bytesread);
            //			  }
            //
            //			  buffer = new byte[16];
            //			  while(leftbyte>=16)
            //			  {
            //				  bytesread = databaseReader.read(buffer, 0, 16);
            //				  leftbyte = leftbyte - bytesread;
            //				  //Decrypt buffer and ic_write to network stream
            //				  byte[] decBuffer= obj.Decrypt(buffer, Key);
            //				  ns.ic_write(decBuffer, 0, bytesread);
            //			  }
            //
            //			  if(leftbyte>0)
            //			  {
            //				  bytesread = databaseReader.read(buffer, 0, leftbyte);
            //				  leftbyte = leftbyte - bytesread;
            //				  ns.ic_write(buffer, 0, leftbyte);
            //			  }
            //  bos.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }


}
