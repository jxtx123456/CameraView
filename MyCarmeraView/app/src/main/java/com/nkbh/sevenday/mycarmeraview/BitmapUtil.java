package com.nkbh.sevenday.mycarmeraview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentValues.TAG;

/**
 * Created by pixel on 2018/5/25.
 */

public class BitmapUtil {

    /**
     * 图片旋转
     * @param bitmap
     * @param angle
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap,int angle){

        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (resizedBitmap != bitmap && bitmap != null && !bitmap.isRecycled()) {
            try {
                bitmap.recycle();
            } catch (Exception e) {
            }
        }
        return resizedBitmap;

    }


    /**
     * 图片镜像翻转+旋转90度
     * @param bitmap
     * @return
     */
    public static Bitmap convert(Bitmap bitmap) {

//        int w = bitmap.getWidth();
//
//        int h = bitmap.getHeight();
//
//        Bitmap newb = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());// 创建一个新的和SRC长度宽度一样的位图
//        Canvas cv = new Canvas(newb);
//        Matrix m = new Matrix();
//        m.postScale(1, -1);   //镜像垂直翻转
//
////        m.postScale(-1, 1);   //镜像水平翻转
//
////        m.postRotate(-90);  //旋转-90度
//
//        Bitmap new2 = Bitmap.createBitmap(bitmap, 0, 0, w, h, m, true);
//        cv.drawBitmap(new2, new Rect(0, 0, new2.getWidth(), new2.getHeight()),new Rect(0, 0, bitmap.getWidth(),  bitmap.getHeight()), null);

//        return newb;

        Matrix m = new Matrix();
//        if (i < 4)
            m.setScale(-1, 1);//水平翻转
//        else
//            m.setScale(1, -1);//垂直翻转
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        //生成的翻转后的bitmap
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, m, true);

    }



    /**
     * 质量压缩方法
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }


    /**
     * 比例大小压缩方法
     * @param srcPath
     * @return
     */
    public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }


    /**
     * 质量和比例大小压缩方法
     * @param image
     * @return
     */
    public static Bitmap comp(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }




    /**
     * 屏幕分辨率和指定清晰度的图片压缩方法
     *
     * @param context
     * @param image  Bitmap图片
     * @return
     */
    public static Bitmap comp(Context context, Bitmap image) {
        int maxLength = 1024 * 1024; // 预定的图片最大内存，单位byte
        // 压缩大小
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length > maxLength) { // 循环判断，大于继续压缩
            options -= 10;// 每次都减少10
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//PNG 压缩options%
        }
        // 压缩尺寸
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options opts = new BitmapFactory.Options(); // 选项对象(在加载图片时使用)
        opts.inJustDecodeBounds = true; // 修改选项, 只获取大小
        BitmapFactory.decodeStream(bais, null, opts);// 加载图片(只得到图片大小)
        // 获取屏幕大小，按比例压缩
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int scaleX = opts.outWidth / manager.getDefaultDisplay().getWidth(); // X轴缩放比例(图片宽度/屏幕宽度)
        int scaleY = opts.outHeight / manager.getDefaultDisplay().getHeight(); // Y轴缩放比例
        int scale = scaleX > scaleY ? scaleX : scaleY; // 图片的缩放比例(X和Y哪个大选哪个)

        opts.inJustDecodeBounds = false; // 修改选项, 不只解码边界
        opts.inSampleSize = scale > 1 ? scale : 1; // 修改选项, 加载图片时的缩放比例
        return BitmapFactory.decodeStream(bais, null, opts); // 加载图片(得到压缩后的图片)
    }

    /**
     * 屏幕分辨率和指定清晰度的图片压缩方法
     *
     * @param context
     * @param path  图片的路径
     * @return
     */
    public static Bitmap comp(Context context, String path) {
        return compressImage1(getUsableImage(context, path));
    }

    /**
     * 获取屏幕分辨率的Bitmap
     *
     * @param context
     * @param path  图片的路径
     * @return
     */
    public static Bitmap getUsableImage(Context context, String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options(); // 选项对象(在加载图片时使用)
        opts.inJustDecodeBounds = true; // 修改选项, 只获取大小
        BitmapFactory.decodeFile(path, opts); // 加载图片(只得到图片大小)
        DisplayMetrics metrics = new DisplayMetrics();
        metrics = context.getApplicationContext().getResources().getDisplayMetrics();
        int scaleX = opts.outWidth / metrics.widthPixels; // X轴缩放比例(图片宽度/屏幕宽度)
        int scaleY = opts.outHeight / metrics.heightPixels; // Y轴缩放比例
        int scale = scaleX > scaleY ? scaleX : scaleY; // 图片的缩放比例(X和Y哪个大选哪个)

        opts.inJustDecodeBounds = false; // 修改选项, 不只解码边界
        opts.inSampleSize = scale > 1 ? scale : 1; // 修改选项, 加载图片时的缩放比例
        return BitmapFactory.decodeFile(path, opts); // 加载图片(得到缩放后的图片)
    }

    /**
     * 压缩图片清晰度，到指定大小
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage1(Bitmap image) {
//        int maxLength = 1024 * 1024; // (byte)
        int maxLength = 800 * 800; // (byte)

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length > maxLength) { // 循环判断如果压缩后图片是否大于1mb,大于继续压缩
            options -= 10;// 每次都减少10
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 指定分辨率和清晰度的图片压缩方法
     *
     * @param fromFile
     * @param toFile
     * @param reqWidth
     * @param reqHeight
     * @param quality
     */
    public static void transImage(String fromFile, String toFile, int reqWidth, int reqHeight, int quality) {
        Bitmap bitmap = BitmapFactory.decodeFile(fromFile);
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        // 缩放的尺寸
        float scaleWidth = (float) reqWidth / bitmapWidth;
        float scaleHeight = (float) reqHeight / bitmapHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 产生缩放后的Bitmap对象
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
        // 保存到文件
        bitmap2File(toFile, quality, resizeBitmap);
        if (!bitmap.isRecycled()) {
            // 释放资源，以防止OOM
            bitmap.recycle();
        }
        if (!resizeBitmap.isRecycled()) {
            resizeBitmap.recycle();
        }
    }


    /**
     * Bitmap转换为文件
     *
     * @param toFile
     * @param quality
     * @param bitmap
     * @return
     */
    public static File bitmap2File(String toFile, int quality, Bitmap bitmap) {
        File captureFile = new File(toFile);
        FileOutputStream out = null;
        try {
            if (!captureFile.getParentFile().exists()){
                captureFile.mkdir();
            }
            out = new FileOutputStream(captureFile);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return captureFile;
    }

    /**
     * Drawable转换为Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitamp(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    // Bitmap、Drawable、InputStream、byte[] 之间转换

    /**********************************************************/
    // 1. Bitmap to InputStream
    public static InputStream bitmap2Input(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public static InputStream bitmap2Input(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    // 2. Bitmap to byte[]
    public static byte[] bitmap2ByteArray(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
        return baos.toByteArray();
    }

    public static byte[] bitmap2ByteArray(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    // 3. Drawable to byte[]
    public static byte[] drawable2ByteArray(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        return out.toByteArray();
    }

    // 4. byte[] to Bitmap
    public static Bitmap byteArray2Bitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }




    /***
     * 获取指定路径图片
     * @param pathname	文件全路径
     * @return bitmap
     */
    public static Bitmap GetBitMapFromPath(String pathname) {
        try {
            int imgSampleSize;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            //FileInputStream is = new FileInputStream(path+File.separator+filename);
            FileInputStream is = new FileInputStream(pathname);
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, options);
            Log.d(TAG, "path:" + pathname);
            if (options.outWidth > options.outHeight) {
                imgSampleSize = (options.outWidth + 360) / 720;
            } else {
                imgSampleSize = (options.outHeight + 360) / 720;
            }
            options.inSampleSize = imgSampleSize;
            Log.d(TAG, "Input bitmap samplesize=" + options.inSampleSize + " mod: w=" + options.outWidth + ",h=" + options.outHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, options);
            is.close();
            Log.d(TAG, "bitmapwg:" + bitmap.getWidth() + " " + bitmap.getHeight());
            return bitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String convertIconToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);

    }


}
