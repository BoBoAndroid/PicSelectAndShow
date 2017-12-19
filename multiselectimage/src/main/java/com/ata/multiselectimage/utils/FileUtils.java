package com.ata.multiselectimage.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by hakeezfg on 2016/8/20.
 */
public class FileUtils {

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    public static File createTmpFile(Context context) throws IOException {
        File dir = null;
        if(TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (!dir.exists()) {
                dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera");
                if (!dir.exists()) {
                    dir = getCacheDirectory(context, true);
                }
            }
        }else{
            dir = getCacheDirectory(context, true);
        }
        return File.createTempFile(JPEG_FILE_PREFIX, JPEG_FILE_SUFFIX, dir);
    }

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * <i>("/Android/data/[app_package_name]/cache")</i> if card is mounted and app has appropriate permission. Else -
     * Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @return Cache {@link File directory}.<br />
     * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
     * {@link android.content.Context#getCacheDir() Context.getCacheDir()} returns null).
     */
    public static File getCacheDirectory(Context context) {
        return getCacheDirectory(context, true);
    }

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * <i>("/Android/data/[app_package_name]/cache")</i> (if card is mounted and app has appropriate permission) or
     * on device's file system depending incoming parameters.
     *
     * @param context        Application context
     * @param preferExternal Whether prefer external location for cache
     * @return Cache {@link File directory}.<br />
     * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
     * {@link android.content.Context#getCacheDir() Context.getCacheDir()} returns null).
     */
    public static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        } catch (IncompatibleClassChangeError e) { // (sh)it happens too (Issue #989)
            externalStorageState = "";
        }
        //temp by zf
        if (preferExternal && Environment.MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }

//        if (preferExternal && hasExternalStoragePermission(context)) {
//            appCacheDir = getExternalCacheDir(context);
//        }

        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    /**
     * Returns individual application cache directory (for only image caching from ImageLoader). Cache directory will be
     * created on SD card <i>("/Android/data/[app_package_name]/cache/uil-images")</i> if card is mounted and app has
     * appropriate permission. Else - Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @param cacheDir Cache directory path (e.g.: "AppCacheDir", "AppDir/cache/images")
     * @return Cache {@link File directory}
     */
    public static File getIndividualCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = getCacheDirectory(context);
        File individualCacheDir = new File(appCacheDir, cacheDir);
        if (!individualCacheDir.exists()) {
            if (!individualCacheDir.mkdir()) {
                individualCacheDir = appCacheDir;
            }
        }
        return individualCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
            }
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }
    /** 从给定的路径载入图片，并指定是否自己主动旋转方向*/
    public static Bitmap loadBitmap(String imgpath, boolean adjustOritation) {
        if (!adjustOritation) {
            return loadBitmap(imgpath);
        } else {
            Bitmap bm = loadBitmap(imgpath);
            int digree = 0;
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imgpath);
            } catch (IOException e) {
                e.printStackTrace();
                exif = null;
            }
            if (exif != null) {
                // 读取图片中相机方向信息
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                // 计算旋转角度
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                        break;
                }
            }
            if (digree != 0) {
                // 旋转图片
                Matrix m = new Matrix();
                m.postRotate(digree);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
            }
            return bm;
        }
    }

    /** 从给定的路径载入图片，并指定是否自己主动旋转方向*/
    public static String loadBitmap(String imgpath, boolean adjustOritation,String fileName) {
        String filePath = null;
        if (!adjustOritation) {
            filePath = imgpath;
        } else {
            Bitmap bm = loadBitmap(imgpath);
            int digree = 0;
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imgpath);
            } catch (IOException e) {
                e.printStackTrace();
                exif = null;
            }
            if (exif != null) {
                // 读取图片中相机方向信息
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                // 计算旋转角度
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                        break;
                }
            }
            if (digree != 0) {
                if(TextUtils.isEmpty(fileName)){
                    //拍照后需要旋转图片，但是图片名称为null的情况
                    Matrix m = new Matrix();
                    m.postRotate(digree);
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                            bm.getHeight(), m, true);
                    try {
                        filePath = storeImage(bm, fileName);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }else {
                    //正常选择的图片，但是需要旋转的情况
                    String resultPath = FileUtils.haveImage(fileName);
                    if (TextUtils.isEmpty(resultPath)) {
                        // 旋转图片（以前没有旋转过，这次需要旋转在存起来）
                        Matrix m = new Matrix();
                        m.postRotate(digree);
                        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                                bm.getHeight(), m, true);
                        try {
                            filePath = storeImage(bm, fileName);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //以前已经旋转过了，这次直接拿来用就行
                        filePath = resultPath;
                    }
                }
            }else {
                filePath = imgpath;
            }

        }
        return filePath;
    }
    /** 从给定路径载入图片*/
    public static Bitmap loadBitmap(String imgpath) {
        BitmapFactory.Options options1=new BitmapFactory.Options();
        options1.inSampleSize=2;
        return BitmapFactory.decodeFile(imgpath,options1);
    }

    /**
     * Store bitmap into specified image path
     *
     * @param bitmap
     *
     * @throws FileNotFoundException
     */
    public static String storeImage(Bitmap bitmap, String fileName) throws FileNotFoundException {
        // 首先保存图片
        File appDir =new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Lrucache");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        if(fileName==null) {
            fileName = System.currentTimeMillis() + ".jpg";
        }
        File file1 = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file1);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file1.getAbsolutePath();
    }
    /**
     * 删除文件
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists())
            return file.delete();
        return true;
    }
    //判断特定的文件是否存在
    public static String haveImage(String fileName){
        String filePath=null;
        File appDir =new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Lrucache");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file1 = new File(appDir, fileName);
        if(file1.exists()){
            filePath=file1.getAbsolutePath();
        }
        return filePath;
    }
    public static boolean judgeRollImg(String imgPath){
        boolean flag=false;
        if(TextUtils.isEmpty(imgPath)){
            flag = false;
        }else {
            int digree = 0;
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imgPath);
            } catch (IOException e) {
                e.printStackTrace();
                exif = null;
            }
            if (exif != null) {
                // 读取图片中相机方向信息
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                // 计算旋转角度
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                        break;
                }
            }
            if (digree == 0) {
                flag = false;
            }else {
                flag = true;
            }
        }
        return flag;
    }
}