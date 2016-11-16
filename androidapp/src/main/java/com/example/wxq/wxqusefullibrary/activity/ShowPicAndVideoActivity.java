package com.example.wxq.wxqusefullibrary.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.wxq.wxqusefullibrary.R;
import com.example.wxq.wxqusefullibrary.widget.FastBlurUtil;
import com.example.wxq.wxqutilslibrary.activity.BaseActivity;
import com.example.wxq.wxqutilslibrary.myutils.imageloader.LoadingImgUtil;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;
import java.util.ArrayList;


//显示模糊效果普通效果的imageview
public class ShowPicAndVideoActivity extends BaseActivity {
    ImageView image;
    ImageView mmohu_image;
    String imagurl = "http://img.pconline.com.cn/images/upload/upc/tx/pcdlc/1606/02/c2/22319575_1464865502726.jpeg";
    ArrayList<String> mOthersList = new ArrayList<String>();
    ArrayList<String> videos= new ArrayList<String>();
    ImageLoader imageLoad = ImageLoader.getInstance();
    Bitmap bitmap;
    Handler mhandler;
    int i = 0;
    int total;
    private VideoView rtsp_player;
    private VideoView rtsp_player_mohu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pic_and_video);
        mOthersList.add("http://img.pconline.com.cn/images/upload/upc/tx/pcdlc/1606/02/c2/22319575_1464865502726.jpeg");
        mOthersList.add("http://www.buhuiwan.com/uploadfile/2015/0611/20150611054907734.jpg");
        mOthersList.add("http://images.liqucn.com/h33/h96/images201412070308310840_info320X534.jpg");

        //判断权限 有权限执行下面
        checkPermissionAndWriteFile();
        mOthersList.add(videos.get(2).toString());//视频地址
        mOthersList.add("http://pic6.huitu.com/res/20130116/84481_20130116142820494200_1.jpg");
        mOthersList.add("http://pic44.nipic.com/20140717/2531170_194615292000_2.jpg");

        // showToast(videos.get(0).toString());
        mhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                bitmap = (Bitmap) msg.obj;
                mmohu_image.setImageBitmap(bitmap);
                startAnim();
            }
        };

        image = (ImageView) findViewById(R.id.image);
        mmohu_image = (ImageView) findViewById(R.id.mmohu_image);
        rtsp_player= (VideoView) findViewById(R.id.rtsp_player);
        rtsp_player_mohu=(VideoView) findViewById(R.id.rtsp_player_mohu);
        if(mOthersList.get(0).endsWith(".mp4")){
            image.setVisibility(View.GONE);
            mmohu_image.setVisibility(View.GONE);
            rtsp_player.setVisibility(View.VISIBLE);
            rtsp_player_mohu.setVisibility(View.VISIBLE);
            PlayLocalFile(mOthersList.get(0).toString());

        }else{
            image.setVisibility(View.VISIBLE);
            mmohu_image.setVisibility(View.VISIBLE);
            rtsp_player_mohu.setVisibility(View.GONE);
            rtsp_player.setVisibility(View.GONE);
            LoadingImgUtil.loading(mOthersList.get(i), image, null, true);
            initBlurPic();
        }







    }

    private void PlayLocalFile(String filePath){
        rtsp_player.setVideoPath(filePath);
        rtsp_player.requestFocus();
        rtsp_player.start();
        rtsp_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                showToast("视频播放结束了");
                i++; //集合移动一位继续下一次操作
                goToNext();

            }
        });

        rtsp_player_mohu.setVideoPath(filePath);
        rtsp_player_mohu.requestFocus();
        rtsp_player_mohu.start();
    }

    private void goToNext() {
        image.setVisibility(View.GONE);
        mmohu_image.setVisibility(View.GONE);
        rtsp_player.setVisibility(View.GONE);
        rtsp_player_mohu.setVisibility(View.GONE);
        if(mOthersList.get(i).endsWith(".mp4")){
            image.setVisibility(View.GONE);
            mmohu_image.setVisibility(View.GONE);
            rtsp_player.setVisibility(View.VISIBLE);
            rtsp_player_mohu.setVisibility(View.VISIBLE);
            PlayLocalFile(mOthersList.get(i).toString());
        }else{
            image.setVisibility(View.VISIBLE);
            mmohu_image.setVisibility(View.VISIBLE);
            rtsp_player_mohu.setVisibility(View.GONE);
            rtsp_player.setVisibility(View.GONE);
            LoadingImgUtil.loading(mOthersList.get(i), image, null, true);
            initBlurPic();
        }
    }

    private void checkPermissionAndWriteFile(){
        //1. 检查我们是否有权限
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //2. 是否应该向用户解释（用户之前拒绝过此权限）
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
//                Toast.makeText(this,"你需要此权限去写文件",Toast.LENGTH_SHORT).show();
                //解释完之后再去请求权限  弹dialog，如果dialog同意就重新请求权限
                //如果是fragment，直接使用requestPermissions
                new AlertDialog.Builder(ShowPicAndVideoActivity.this).setMessage("你需要此权限去写文件").setPositiveButton("申请权限", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ShowPicAndVideoActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},100);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

            }else{
                //3. 请求权限
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},100);
            }
        }else{
         videos= GetVideoFileName(getMediaPath(this)+"/LuPingDaShi/Rec"); //获取sd卡文件
         showToast(videos.get(0).toString());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 100://request code 不超过255
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    videos= GetVideoFileName(getMediaPath(this)+"/LuPingDaShi/Rec"); //获取sd卡文件
                    showToast(videos.get(0).toString());
                } else {
                    //弹dialog，让用户去设置页面打开权限
                    Toast.makeText(this,"写文件失败,没有权限23333", Toast.LENGTH_SHORT).show();

//                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
//                    //省略....

                }
                return;
        }

    }






    private void initBlurPic() {
      //  if(mOthersList.get(i).)
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap originBitmap = imageLoad.loadImageSync(mOthersList.get(i));  //null
                //对bitmap处理  10模糊比例
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap,
                        originBitmap.getWidth() / 20,
                        originBitmap.getHeight() / 20,
                        false);
                Bitmap blurBitmap = doBlur(scaledBitmap, 8, true);// 8位通常设置为8就行。//增大blurRadius，可以得到更高程度的虚化，不过会导致CPU更加intensive
                Message msg = mhandler.obtainMessage();
                msg.arg1 = 1;
                msg.arg2 = 2;
                msg.what = 3;
                msg.obj = blurBitmap;
                mhandler.sendMessage(msg);
            }
        }).start();
    }
    @Override
    public void widgetClick(View v) {

    }
    /**
     * 开启动画
     */
    private void startAnim() {
        // 动画集合
        AnimationSet set = new AnimationSet(false);
//        // 旋转动画
//        RotateAnimation rotate = new RotateAnimation(0, 360,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//                0.5f);
//        rotate.setDuration(1000);// 动画时间
//        rotate.setFillAfter(true);// 保持动画状态
        // 缩放动画
        ScaleAnimation scale = new ScaleAnimation(1, Float.valueOf("1.1"), 1, Float.valueOf("1.1"),
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scale.setDuration(3000);// 动画时间
        scale.setFillAfter(true);// 保持动画状态
//        // 渐变动画
        AlphaAnimation alpha = new AlphaAnimation(Float.valueOf("0.5"), 1);
        alpha.setDuration(3000);// 动画时间
      //  alpha.setFillAfter(true);// 保持动画状态
//
//        set.addAnimation(rotate);
        set.addAnimation(scale);
        if(i==mOthersList.size()-1&&!mOthersList.get(i).endsWith(".mp4")){
            set.setFillAfter(true);
        }

         set.addAnimation(alpha);
        // 设置动画监听
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            // 动画执行结束
            @Override
            public void onAnimationEnd(Animation animation) {
                i++;
                if (i < mOthersList.size()) {
                    goToNext();
                }else{
                    finish();
                }

            }
        });

        image.startAnimation(set);
    }


    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {

        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }


    public ArrayList<String> GetVideoFileName(String fileAbsolutePath) {
        ArrayList<String> vecFile = new ArrayList<String>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            final File fi = subFile[iFileLength];
            if (fi.isFile()) {
                int i = fi.getPath().lastIndexOf(".");
                if (i <= 0) {
                    continue;
                }
                String substring = fi.getPath().substring(i);
                if (substring.toLowerCase().equals(".mp4")) {
                    vecFile.add(fi.getPath());
                }
            }

        }
        return vecFile;
    }

    //获取sdcard路径
    public static String getMediaPath(Context context) {
        String path = "";
        File dirFile = null;
        String exStorageState = Environment.getExternalStorageState();
        if (exStorageState == null || exStorageState.equals(Environment.MEDIA_MOUNTED)
                || exStorageState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            dirFile = Environment.getExternalStorageDirectory();
        } else {
            dirFile = context.getExternalFilesDir(null);
        }

        if (dirFile == null) {
            dirFile = context.getFilesDir();
        } else {
            path = dirFile.getAbsolutePath();
        }

        return path == null ? "" : path;
    }
}