package com.jph.simple.demo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.jph.simple.R;
import com.jph.simple.ResultActivity;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.LubanOptions;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;

import java.io.File;
import java.util.ArrayList;

/**
 * 这个类  是我测试 能不能提取 配置参数的 时候 随便写的，，，，，可以直接去看 Test2Activity
 * Created by ~sunan~ on 2017/8/26.
 */

public class TestActivity extends TakePhotoActivity implements View.OnClickListener {
    // 是否 使用 TakePhoto 自带相册
    boolean isNeedzidaixiangce = true;
    // 是否 纠正拍照的照片旋转角度 旋转
    boolean isNeedXuanZhuan = false;
    // 是否 压缩
    boolean isNeedYaSuo = true;
    // 是否 裁剪
    boolean isNeedCrop = true;
    //压缩 后 图片 内存大小   和  宽高 尺寸 大小
    int maxSize = 102400; // B 计算 单位
    int width = 800; // 压缩之后的   尺寸  PX 计算单位
    int height = 800;
    // 压缩过程中  是否显示 进度条
    boolean showProgressBar = true;
    // 拍照 压缩后 是否 保存原图，
    boolean enableRawFile = true;
    // 压缩工具 选择 ，，自带 还是 luban
    boolean CompressTool = true;
    // 获取 裁剪 宽高 尺寸 // px 单位
    int Cropheight = 800;
    int Cropwidth = 800;
    // 是否是 自带 裁切 （ 默认选择 自带的  ）
    boolean withCropTool = false;
    // 获取 裁剪 宽高 比例方式    true 宽X 高   或者 false  宽/高
    boolean CropSize = false;
    CompressConfig config;

    // 从 相册选择  还是 显示 文件夹 选择图片  默认 从 文件夹选择
    boolean fromPic = true;
    // 设置一次  选择几张 图片 至少一张
    int picNumber = 3;


    File file;
    Uri imageUri;
    TakePhoto takePhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_layout);
        init();
    }

    /**
     * 配置相册
     */
    public void init() {
        takePhoto = getTakePhoto();
        file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        imageUri = Uri.fromFile(file);
        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);

    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        Log.e("SNN", "takeFail: " + msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        //做 成功 操作，返回的是个  list  图片 集合
        showImg(result.getImages());

    }


    private void showImg(ArrayList<TImage> images) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llImages);
        for (int i = 0, j = images.size(); i < j - 1; i += 2) {
            View view = LayoutInflater.from(this).inflate(R.layout.image_show, null);
            ImageView imageView1 = (ImageView) view.findViewById(R.id.imgShow1);
            ImageView imageView2 = (ImageView) view.findViewById(R.id.imgShow2);
            Log.e("SNN", " 路径 " + images.get(i).getCompressPath().toString());
            Glide.with(this).load(new File(images.get(i).getCompressPath())).into(imageView1);
            Glide.with(this).load(new File(images.get(i + 1).getCompressPath())).into(imageView2);
            linearLayout.addView(view);
        }
        if (images.size() % 2 == 1) {
            View view = LayoutInflater.from(this).inflate(R.layout.image_show, null);
            ImageView imageView1 = (ImageView) view.findViewById(R.id.imgShow1);
            Glide.with(this).load(new File(images.get(images.size() - 1).getCompressPath())).into(imageView1);
            linearLayout.addView(view);
        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPickBySelect:
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

                break;
            case R.id.btnPickByTake://  拍照   裁切 或不裁切
                if (isNeedCrop) {
                    takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
                } else {
                    takePhoto.onPickFromCapture(imageUri);
                }
                break;
            default:
                break;
        }
    }

    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        // 是否使用  tokephoto 自带相册
        if (isNeedzidaixiangce) {
            builder.setWithOwnGallery(true);
        }
        // 矫正拍照 旋转角度
        if (isNeedXuanZhuan) {
            builder.setCorrectImage(true);
        }
        takePhoto.setTakePhotoOptions(builder.create());

    }

    //压缩 配置
    private void configCompress(TakePhoto takePhoto) {
        // 不压缩   返回
        if (!isNeedYaSuo) {
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
