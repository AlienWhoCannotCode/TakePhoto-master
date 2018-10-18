package com.jph.simple.demo;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.LubanOptions;
import com.jph.takephoto.model.TakePhotoOptions;

import java.io.File;

/**
 * 这个Util 就是随便写写，，，关键词，，，，咳~ ,随便写
 * Created by ~sunan~ on 2017/8/26.
 */

public class TakePhotoUtil {

    // 是否   true 使用 TakePhoto 自带相册(样式统一，可自定义)  false 不使用（手机 系统相册）
    static boolean isUseOwnAlbum = true;
    // 是否 纠正拍照的照片旋转角度 旋转
    static boolean isNeedRotate = true;
    // 是否 压缩
    static boolean isNeedCompress = true;
    // 是否 裁剪
    static boolean isNeedCrop = false;
    // 压缩过程中  是否显示 进度条
    static boolean showProgressBar = true;
    // 拍照 压缩后 是否 保存原图，
    static boolean enableRawFile = true;
    // 压缩工具 选择 ，，true 自带 还是 false luban
    static boolean CompressTool = true;
    //压缩 后 图片 内存大小   和  宽高 尺寸 大小
    static int maxSize = 102400; // B 计算 单位
    static int width = 800; // 压缩之后的   尺寸  PX 计算单位
    static int height = 800;
    // 获取 裁剪 宽高 尺寸 // px 单位
    static int Cropheight = 800;
    static int Cropwidth = 800;
    // 是否 裁切工具（样式）    true TakePhoto自带， false 第三方的(建议选择自带的)
    static boolean withCropTool = true;
    // 获取 裁剪 宽高 比例方式    true 宽 X 高   或者 false  宽/高
    static boolean CropSize = false;
    // 设置一次  选择几张 图片 至少一张
    static int picNumber = 1;
    // 图片来源     文件夹 true  ；  相册选择 false
    static boolean fromPic = false;
    File file;
    Uri imageUri;
    TakePhoto takePhoto;
    CompressConfig config;


    private TakePhotoUtil(TakePhoto takePhoto2) {
        this.takePhoto = takePhoto2;
        initTakePhoto(takePhoto);
    }

    public static TakePhotoUtil instance(TakePhoto takePhoto1) {
        Log.e("SNN", "instance: " + "已经初始化了");
        return new TakePhotoUtil(takePhoto1);
    }

    /**
     * 配置相册
     */
    public void initTakePhoto(TakePhoto takePhoto1) {
        this.takePhoto = takePhoto1;
        file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        imageUri = Uri.fromFile(file);
        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);

    }


    // 从相册选择 照片
    public void getPhoto(TakePhoto takePhoto) {
        initTakePhoto(takePhoto);

        if (picNumber > 1) {
            //  图片选几张   是否裁切
            if (isNeedCrop) {
                takePhoto.onPickMultipleWithCrop(picNumber, getCropOptions());
            } else {
                takePhoto.onPickMultiple(picNumber);
            }
            return;
        }

        // 从文件夹 选择  裁切 或 不裁切
        if (fromPic) {
            if (isNeedCrop) { // 是否裁切
                takePhoto.onPickFromDocumentsWithCrop(imageUri, getCropOptions());
            } else {
                takePhoto.onPickFromDocuments();
            }
            return;
        } else {// 从相册选择   裁切 或 不裁切
            if (isNeedCrop) {
                takePhoto.onPickFromGalleryWithCrop(imageUri, getCropOptions());
            } else {
                takePhoto.onPickFromGallery();
            }
        }


    }

    public void takePhoto(TakePhoto takePhoto) {
        initTakePhoto(takePhoto);
        if (isNeedCrop) {
            takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
        } else {
            takePhoto.onPickFromCapture(imageUri);
        }
    }


    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        // 是否使用  tokephoto 自带相册
        if (isUseOwnAlbum) {
            builder.setWithOwnGallery(true);
        }
        // 矫正拍照 旋转角度
        if (isNeedRotate) {
            builder.setCorrectImage(true);
        }
        takePhoto.setTakePhotoOptions(builder.create());

    }

    //压缩 配置
    private void configCompress(TakePhoto takePhoto) {
        // 不压缩   返回
        if (!isNeedCompress) {
            takePhoto.onEnableCompress(null, false);
            return;
        }
        if (CompressTool) {
            config = new CompressConfig.Builder()
                    .setMaxSize(maxSize)
                    .setMaxPixel(width >= height ? width : height)
                    .enableReserveRaw(enableRawFile)
                    .create();
        } else {
            LubanOptions option = new LubanOptions.Builder()
                    .setMaxHeight(height)
                    .setMaxWidth(width)
                    .setMaxSize(maxSize)
                    .create();
            config = CompressConfig.ofLuban(option);
            config.enableReserveRaw(enableRawFile);
        }
        takePhoto.onEnableCompress(config, showProgressBar);
    }

    // 裁切配置 选项
    private CropOptions getCropOptions() {
        // 如果不裁切 返回
        if (!isNeedCrop)
            return null;

        CropOptions.Builder builder = new CropOptions.Builder();

        if (CropSize) {
            builder.setAspectX(Cropwidth).setAspectY(Cropheight);
        } else {
            builder.setOutputX(Cropwidth).setOutputY(Cropheight);
        }
        builder.setWithOwnCrop(withCropTool);
        return builder.create();
    }

}
