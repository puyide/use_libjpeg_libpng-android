package com.allstar.cinclient.tools.image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 操作图片的工具类
 *
 * @author young
 */
public class ImageTools {
    // 临时文件夹
    private final static String temp = "/sdcard/test/temp/";

    /**
     * 图片质量
     *
     * @author young
     */
    public enum Quality {
        BIG(1), THUM(2), PORTRAIT(3), SMALL(4);

        private int q;

        private Quality(int q) {
            this.q = q;
        }

        public int getQuality() {
            return q;
        }
    }

    /**
     * 压缩图片获取BitMap数据
     *
     * @param oldimage 原图
     * @param q        图片质量枚举
     * @return
     */
    public static Bitmap getCommpressImage(String inputFilepath, Quality q) {
        File dirFile = new File(temp);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File jpegTrueFile = new File(dirFile, new java.util.Date().getTime() + ".jpg");
        ImageNativeUtil.compressBitmap(inputFilepath, jpegTrueFile.getAbsolutePath(), true, q);
        Bitmap nImage = BitmapFactory.decodeFile(jpegTrueFile.getAbsolutePath());
        jpegTrueFile.delete();
        return nImage;
    }

    /**
     * 压缩图片获取字节流
     *
     * @param oldimage
     * @param q
     * @return
     */
    public static byte[] getCommpressImage2Byte(String inputFilepath, Quality q) {
        File dirFile = new File(temp);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File jpegTrueFile = new File(dirFile, new java.util.Date().getTime() + ".jpg");
        ImageNativeUtil.compressBitmap(inputFilepath, jpegTrueFile.getAbsolutePath(), true, q);
        //增加方向exif
        setExif(inputFilepath, jpegTrueFile.getAbsolutePath());
        byte[] newf = getBytesFromFile(jpegTrueFile);
        jpegTrueFile.delete();
        return newf;
    }

    /**
     * 存储压缩图片
     *
     * @param oldimage 原图
     * @param q        图片质量枚举
     * @param filename 存储文件名
     * @return
     */
    public static void saveCommpressImage(String inputFilepath, Quality q, String outputFilepath) {
        ImageNativeUtil.compressBitmap(inputFilepath, outputFilepath, true, q);
        //增加方向exif
        setExif(inputFilepath, outputFilepath);
    }

    /**
     * 文件转化为字节数组
     *
     * @param file
     * @return
     */
    private static byte[] getBytesFromFile(File file) {
        byte[] ret = null;
        try {
            if (file == null) {
                // log.error("helper:the file is null!");
                return null;
            }
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            out.close();
            ret = out.toByteArray();
        } catch (IOException e) {
            // log.error("helper:get bytes from file process error!");
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 加入方向exif
     * @param input
     * @param output
     */
    private static void setExif(String input, String output) {
        try {
            ExifInterface inexif = new ExifInterface(input);
            ExifInterface outexif = new ExifInterface(output);
            String smodel = inexif.getAttribute(ExifInterface.TAG_ORIENTATION);
            outexif.setAttribute(ExifInterface.TAG_ORIENTATION, smodel);
            outexif.saveAttributes();
        } catch (IOException e) {
        }

    }
}
