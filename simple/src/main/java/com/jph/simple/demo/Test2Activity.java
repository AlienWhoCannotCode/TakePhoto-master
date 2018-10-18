package com.jph.simple.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jph.simple.R;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.model.TResult;

import java.io.File;

/**
 * 如果你移植到项目里边去的话，，，直接copy TakePhotoUtil 到你的项目里去 ，
 * 然后在（ extends TakePhotoActivity，记得继承 ） 对应的点击事件 里 调用 TakePhotoUtil 的 getphoto  和takephoto  方法就OK了 ，
 * 就像这个类一样 用 就好了。
 * Created by ~sunan~ on 2017/8/26.
 */

public class Test2Activity extends TakePhotoActivity implements View.OnClickListener {


    TakePhotoUtil takePhotoUtil;
    ImageView img1;
    ImageView img2;

    int shoseImg = 0;// 标识一下选择的哪个 img

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testactivity);
        // 本来 提取 Util 的时候，是想着  初始化一次TakePhoto 就可以了，
        // 但是  事实 并不是这样 （因为我踩到坑了），所以  这个初始化  并没有什么卵用，，
        // 应该是  在每次调取 takephoto 的时候 都传过去个 takephoto实例 （ 下边的点击事件 ）
        takePhotoUtil = TakePhotoUtil.instance(getTakePhoto());
        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
/**
 * 关于官方说的  自定义UI ，标题栏，提示文字等等，，，嫌麻烦，也没做更改
 * 感兴趣可以试试
 */
    }


    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        // 我在这里设置的是  每次选择 1 张图片 回调的，
        // 因为自己家项目的需要嘛，，嫌麻烦  也就没有写 像 QQ 微信那样 GridView 的展示效果
        // 如果 返回多张  就 result.getImages() 是个集合  路径 可以去参考 官方的demo SimpleActivity
        // 如果一张 就  result.getImage()  单张 路径
        if (shoseImg == 1)
            Glide.with(this).load(new File(result.getImage().getCompressPath())).into(img1);
        else
            Glide.with(this).load(new File(result.getImage().getCompressPath())).into(img2);

    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPickBySelect:
                shoseImg = 1;
                Toast.makeText(Test2Activity.this, "点击了 选择照片", Toast.LENGTH_SHORT).show();
                takePhotoUtil.getPhoto(getTakePhoto());
                break;
            case R.id.btnPickByTake:
                shoseImg = 2;
                Toast.makeText(Test2Activity.this, "点击了 拍照 ", Toast.LENGTH_SHORT).show();
                takePhotoUtil.takePhoto(getTakePhoto());
                break;
            default:
                break;
        }
    }
}
