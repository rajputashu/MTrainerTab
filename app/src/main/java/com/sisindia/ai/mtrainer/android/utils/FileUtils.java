package com.sisindia.ai.mtrainer.android.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;

import com.sisindia.ai.mtrainer.android.commons.FolderNames;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public final class FileUtils {

    private static final String EXT_JPG = ".jpg";
    private static final String EXT_MP4 = ".mp4";
    private static final String APP_NAME = "MTrainer";
    //    private static final String ASSESMENT_VIDEO = "MTrainerAssesmentVideo";
//    private static final String THREE_GPP = ".3gp";
//    private static final String EXT_PNG = ".png";
    private static final String BASE_DIR = APP_NAME + File.separator;
    //    private static final String ASSESMENT_VIDEO_DIR = ASSESMENT_VIDEO + File.separator;
    public static final String DIR_ROOT = FileUtils.getRootPath() + File.separator + BASE_DIR;

    /**
     * Get SD card root directory, if SD card is not available, get the root directory of internal storage
     */
    public static File getRootPath() {
        File path = null;
        if (sdCardIsAvailable()) {
            path = Environment.getExternalStorageDirectory(); // SD card root directory / storage / emulated / 0
        } else {
            path = Environment.getDataDirectory(); // The root directory of the internal storage / data
        }
        return path;
    }

    /**
     * Whether SD card is available
     */
    public static boolean sdCardIsAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sd = new File(Environment.getExternalStorageDirectory().getPath());
            return sd.canWrite();
        } else
            return false;
    }

    /**
     * Determine if the directory exists, if not, determine whether the creation was successful
     *
     * @param dirPath file path
     * @Return { @code to true }: the presence or creation is successful < br > { @code to false }: creation fails or does not exist
     */
    public static boolean createOrExistsDir(String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    /**
     * Determine if the directory exists, if not, determine whether the creation was successful
     *
     * @param file file
     * @Return { @code to true }: the presence or creation is successful < br > { @code to false }: creation fails or does not exist
     */
    public static boolean createOrExistsDir(File file) {
        // If it exists, it returns true if it is a directory, false if it is a file, and it returns whether it was created successfully.
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * Determine whether the file exists, if not, determine whether the creation was successful
     *
     * @param filePath file path
     * @Return { @code to true }: the presence or creation is successful < br > { @code to false }: creation fails or does not exist
     */
    /*public static boolean createOrExistsFile(String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }*/

    /**
     * Determine whether the file exists, if not, determine whether the creation was successful
     *
     * @param file file
     * @Return { @code to true }: the presence or creation is successful < br > { @code to false }: creation fails or does not exist
     */
    public static boolean createOrExistsFile(File file) {
        if (file == null)
            return false;
        // return true if it is a file, false if it is a directory
        if (file.exists())
            return file.isFile();
        if (!createOrExistsDir(file.getParentFile()))
            return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get files based on file path
     *
     * @param filePath file path
     * @return file
     */
    public static File getFileByPath(String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * Determine if the string is null or all whitespace characters
     *
     * @param s
     * @return
     */
    private static boolean isSpace(final String s) {
        if (s == null)
            return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String createFilePathV1(FolderNames folder, String fileName, Context context) {
        StringBuilder buffer = new StringBuilder();
        CharSequence s = DateFormat.format("_ddMMMyyyy'_'HHmmss", (new Date()).getTime());
        return buffer.append(getRootPathV2(context, folder)).append(fileName)
                .append(s).append(EXT_JPG).toString();
    }

    public static String createFilePathV2(String rootPath, String midFileName) {
        StringBuilder buffer = new StringBuilder();
        CharSequence dateTime = DateFormat.format("_ddMMMyyyy'_'HHmmss", (new Date()).getTime());
        return buffer.append(rootPath).append(midFileName).append(dateTime).append(EXT_JPG).toString();
    }

    public static String createVideoFilePathV2(String rootPath, String midFileName) {
        return rootPath + midFileName + EXT_MP4;
    }

    public static String getRootPathV2(Context context, FolderNames folder) {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) +
                File.separator + BASE_DIR + folder.name() + File.separator;
    }

    public static String getAbsolutePathFromString(String uriFilePath) {
        Uri uri = Uri.parse(uriFilePath);
        return new File(Objects.requireNonNull(uri.getPath())).getAbsolutePath();
    }

    /**
     * Close IO
     *
     * @param closeables closeable
     */
    public static void closeIO(Closeable... closeables) {
        if (closeables == null)
            return;
        try {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public static Uri getUriFromPath(String path) {
        return Uri.fromFile(new File(path));
    }*/

    public static String createFileForTrainerSignature() {
        StringBuilder buffer = new StringBuilder();
        Date d = new Date();
        CharSequence s = DateFormat.format("ddMMMyyyy'_'HHmmss", d.getTime());
        String imagePath;
        imagePath = buffer.append(DIR_ROOT).append(APP_NAME).append("_attendance_signature").append(s).append(EXT_JPG).toString();
        return imagePath;
    }

    /*public static String createFile() {
        StringBuilder buffer = new StringBuilder();
        Date d = new Date();
        CharSequence s = DateFormat.format("ddMMMyyyy'_'HHmmss", d.getTime());
        String imagePath;
        imagePath = buffer.append(DIR_ROOT).append(APP_NAME).append("unkwon").append(s).append(EXT_JPG).toString();
        return imagePath;
    }*/

}
