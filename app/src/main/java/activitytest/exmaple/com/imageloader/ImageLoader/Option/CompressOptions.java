package activitytest.exmaple.com.imageloader.ImageLoader.Option;

import android.graphics.Bitmap;

/**
 * by zdy on 2019/4/2 23:45
 */
public class CompressOptions {
    public static final int a = -1;
    public int width = a;
    public int height = a;
    //质量
    public int quality = 85;
    //图片最大尺寸
    public int maxSize = a;
    //取样值
    public int sampleSize = 1;
    public CompressOptions scale(int width, int height){
        this.width = width;
        this.height = height;
        return this;
    }
    public CompressOptions quality(int quality){
        this.quality = quality;
        return this;
    }
    public CompressOptions maxSize(int maxSize){
        this.maxSize = maxSize;
        return this;
    }
    public CompressOptions sampleSzie(int sampleSize){
        this.sampleSize = sampleSize;
        return this;
    }
    public static int calculateSampleSize(Bitmap bitmap, int reqWidth, int reqHeight){
        if(reqHeight<=0||reqWidth<=0){
            return 1;
        }
        int w = bitmap.getWidth(), h = bitmap.getHeight(), inSampleSize = 1;
        if (h > reqHeight || w > reqWidth) {
            final int halfW = w / 2, halfH = h / 2;
            while (halfH / inSampleSize >= reqHeight && halfW / inSampleSize >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    public String toString() {
        return "width=" + width + "&height=" + height + "&quality=" + quality + "&maxSize" + maxSize + "&inSampleSize=" + sampleSize;
    }
}
