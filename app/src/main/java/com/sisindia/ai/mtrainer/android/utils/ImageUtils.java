package com.sisindia.ai.mtrainer.android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageUtils {



    /**
     * save Picture
     *
     * @param src      source image
     * @param filePath File path to save to
     * @param format
     * @Return { @code to true }: Success < br > { @code to false }: Failed
     */
    public static boolean save(Bitmap src, String filePath, Bitmap.CompressFormat format) {
        return save(src, FileUtils.getFileByPath(filePath), format, false);
    }

    /**
     * save Picture
     *
     * @param src    source image
     * @param file   The file to save to
     * @param format
     * @Return { @code to true }: Success < br > { @code to false }: Failed
     */
    public static boolean save(Bitmap src, File file, Bitmap.CompressFormat format) {
        return save(src, file, format, false);
    }

    /**
     * save Picture
     *
     * @param src      source image
     * @param filePath File path to save to
     * @param format
     * @param recycle  whether to recycle
     * @Return { @code to true }: Success < br > { @code to false }: Failed
     */
    public static boolean save(Bitmap src, String filePath, Bitmap.CompressFormat format, boolean recycle) {
        return save(src, FileUtils.getFileByPath(filePath), format, recycle);
    }

    /**
     * save Picture
     *
     * @param src     source image
     * @param file    The file to save to
     * @param format
     * @param recycle whether to recycle
     * @Return { @code to true }: Success < br > { @code to false }: Failed
     */
    public static boolean save(Bitmap src, File file, Bitmap.CompressFormat format, boolean recycle) {
        if (isEmptyBitmap(src) || !FileUtils.createOrExistsFile(file)) {
            return false;
        }
        Log.d("", src.getWidth() + ", " + src.getHeight());
        OutputStream os = null;
        boolean ret = false;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            ret = src.compress(format, 100, os);
            if (recycle && !src.isRecycled()) {
                src.recycle();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeIO(os);
        }
        return ret;
    }

    /**
     * Determine if the bitmap object is empty
     *
     * @param src source image
     * @return {@code true}: 是<br>{@code false}: 否
     */
    private static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    /**
     * Convert byte [] to Bitmap
     *
     * @param bytes
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapFromByte(byte[] bytes, int width, int height) {
        final YuvImage image = new YuvImage(bytes, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream(bytes.length);
        if (!image.compressToJpeg(new Rect(0, 0, width, height), 100, os)) {
            return null;
        }
        byte[] tmp = os.toByteArray();
        Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
        return bmp;
    }

}
