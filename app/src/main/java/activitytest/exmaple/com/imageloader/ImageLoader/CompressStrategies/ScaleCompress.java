package activitytest.exmaple.com.imageloader.ImageLoader.CompressStrategies;

import android.graphics.Bitmap;

import activitytest.exmaple.com.imageloader.ImageLoader.Interface.CompressStrategy;
import activitytest.exmaple.com.imageloader.ImageLoader.Option.CompressOptions;

/**
 * by zdy on 2019/4/3 01:47
 */
public class ScaleCompress implements CompressStrategy {
    private static final ScaleCompress instance = new ScaleCompress();
    public static ScaleCompress getInstance(){
        return instance;
    }
    private ScaleCompress(){}
    @Override
    public Bitmap compress(Bitmap bitmap, CompressOptions options) {
        int srcWidth = bitmap.getWidth(),
                srcHeight = bitmap.getHeight(),
                outWidth = options.width,
                outHeight = options.height;
        float srcRatio = 1f* srcWidth/srcHeight;
        if(outHeight<=0&&outWidth<=0){
            return bitmap;
        }else if(outHeight<=0){
            outHeight = (int)(outWidth/srcRatio);
        }else if(outWidth<=0){
            outWidth = (int)(outHeight*srcRatio);
        }float outRatio = 1f * outWidth / outHeight;
        if (outRatio < srcRatio) {
            outHeight = (int) (outWidth / srcRatio);
        } else if (outRatio > srcRatio) {
            outWidth = (int) (outHeight * srcRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, true);
    }
}
