package com.nkbh.sevenday.mycarmeraview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.cameraview.AspectRatio;
import com.google.android.cameraview.CameraView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.bitmap;
import static android.R.attr.width;
import static com.google.android.cameraview.CameraView.FACING_BACK;
import static com.google.android.cameraview.CameraView.FACING_FRONT;
import static com.google.android.cameraview.CameraView.FLASH_AUTO;
import static com.google.android.cameraview.CameraView.FLASH_OFF;
import static com.google.android.cameraview.CameraView.FLASH_ON;
import static com.nkbh.sevenday.mycarmeraview.BitmapUtil.convert;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    ImageView img_back,img_camera_flipping,img_flash,img_take_photo,img_photo,img_guide;
    CameraView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    public void initView() {
        img_back= (ImageView) findViewById(R.id.img_back);
        img_camera_flipping= (ImageView) findViewById(R.id.img_camera_flipping);
        img_flash= (ImageView) findViewById(R.id.img_flash);
        img_take_photo= (ImageView) findViewById(R.id.img_take_photo);
        img_photo= (ImageView) findViewById(R.id.img_photo);
        img_guide= (ImageView) findViewById(R.id.img_guide);
//        surfaceView= (SurfaceView) findViewById(R.id.surfaceView);

        mCameraView= (CameraView) findViewById(R.id.camera);

        img_camera_flipping.setOnClickListener(this);
        img_flash.setOnClickListener(this);
        img_take_photo.setOnClickListener(this);

        initCamera();
    }


    private void initCamera() {
        mCameraView.addCallback(new CameraView.Callback() {
            @Override
            public void onCameraOpened(CameraView cameraView) {
                super.onCameraOpened(cameraView);

                //
//                DisplayMetrics displayMetrics=new DisplayMetrics();
//                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                int heigth = displayMetrics.heightPixels;
//                int width = displayMetrics.widthPixels;
//                Log.i("lw", "onCameraOpened: heigth===="+heigth);
//                Log.i("lw", "onCameraOpened: width===="+width);
//                mCameraView.setAspectRatio(AspectRatio.of(width, heigth));
//                mCameraView.setPictureSize(width, heigth);
//                mCameraView.setAspectRatio(AspectRatio.of(1920, 1080));
//                mCameraView.setPictureSize(1920, 1080);
                mCameraView.setAutoFocus(true);


            }

            @Override
            public void onCameraClosed(CameraView cameraView) {
                super.onCameraClosed(cameraView);
            }

            @Override
            public void onPictureTaken(CameraView cameraView, byte[] data) {
                super.onPictureTaken(cameraView, data);

//                Bitmap bitmap=BitmapFactory.decodeByteArray(data,0,data.length);
//                File file = byte2File(data);
//                int degree = readPictureDegree(file.getPath());
//                Log.i("lw", "onPictureTaken: degree====="+degree);


//                Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);

                Bitmap realImage = BitmapUtil.compressImage1(BitmapFactory.decodeByteArray(data,0,data.length));
                Bitmap bitmap = null;
                int facing = mCameraView.getFacing();
                if (mCameraView.FACING_BACK==facing){
                    bitmap=realImage;
                }else {
                    bitmap=BitmapUtil.convert(realImage);
                }
                String filepath = Environment.getExternalStorageDirectory().getPath() + "/MyCameraView/image/";
                File file=new File(filepath);
                if (!file.exists()){
                    file.mkdirs();
                }
                BitmapUtil.bitmap2File(filepath+"img.jpg",100,bitmap);
                pic(bitmap);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    protected void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //翻转相机
            case R.id.img_camera_flipping:
                if (mCameraView!=null) {
                    int facing = mCameraView.getFacing();
                    Log.i("lw", "onClick: facing====" + facing);
                    if (facing == FACING_BACK) {
                        mCameraView.setFacing(FACING_FRONT);
                        //切换到前置摄像头的时候，需要将闪光灯关闭
                        mCameraView.setFlash(CameraView.FLASH_AUTO);
                        img_flash.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.btn_flash_no));
                    } else {
                        mCameraView.setFacing(CameraView.FACING_BACK);
                    }
                }

                break;
            //照相
            case R.id.img_take_photo:
                if (mCameraView!=null) {
                    mCameraView.takePicture();
                }
                break;
            //闪光灯
            case R.id.img_flash:
                if (mCameraView!=null) {
                    int facing1 = mCameraView.getFacing();
                    Log.i("lw", "onClick: facing====" + facing1);
                    if (facing1 == FACING_FRONT) {
                        return;
                    }

                    /**
                     * FLASH_AUTO:闪光灯会在需要时自动发射
                     * FLASH_RED_EYE:闪光灯将在红眼还原模式下发射。
                     */
                    int flash = mCameraView.getFlash();
                    Log.i("lw", "onClick: flash====" + flash);
                    Log.i("lw", "onClick: FLASH_OFF====" + FLASH_OFF);
                    Log.i("lw", "onClick: FLASH_ON====" + FLASH_ON);
                    if (flash != CameraView.FLASH_ON) {
                        mCameraView.setFlash(CameraView.FLASH_ON);
                        img_flash.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.btn_flash));
                    } else if (flash == CameraView.FLASH_ON) {
                        mCameraView.setFlash(CameraView.FLASH_AUTO);
                        img_flash.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.btn_flash_no));
                    }
                }
                break;

        }
    }


    public void pic(Bitmap bitmap){
        // 用于PopupWindow的View
        View contentView= LayoutInflater.from(this).inflate(R.layout.pw_view, null, false);
        ImageView img=contentView.findViewById(R.id.img);
        Button btn_back=contentView.findViewById(R.id.btn_back);
        // 创建PopupWindow对象，其中：
        // 第一个参数是用于PopupWindow中的View，第二个参数是PopupWindow的宽度，
        // 第三个参数是PopupWindow的高度，第四个参数指定PopupWindow能否获得焦点
        final PopupWindow window=new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, true);
        // 设置PopupWindow的背景
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        // 设置PopupWindow是否能响应外部点击事件
        window.setOutsideTouchable(true);
        // 设置PopupWindow是否能响应点击事件
        window.setTouchable(true);

//        window.setAnimationStyle(R.style.animTranslate);
        // 显示PopupWindow，其中：
        // 第一个参数是PopupWindow的锚点，第二和第三个参数分别是PopupWindow相对锚点的x、y偏移
//        window.showAsDropDown(anchor, xoff, yoff);
        // 或者也可以调用此方法显示PopupWindow，其中：
        // 第一个参数是PopupWindow的父View，第二个参数是PopupWindow相对父View的位置，
        // 第三和第四个参数分别是PopupWindow相对父View的x、y偏移
        window.showAtLocation(MainActivity.this.findViewById(R.id.frame_layout), Gravity.BOTTOM, 0, 0);


        img.setImageBitmap(bitmap);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
    }



    /**
     * 保存图片
     * @param buf
     * @return
     */
    public static File byte2File(byte[] buf) {
        String filepath = Environment.getExternalStorageDirectory().getPath() + "/MyCameraView/image/";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.CHINA).format(new Date());
        String filename = "IMG_" + timeStamp + ".jpg";
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filepath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(filepath + filename);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }


}
