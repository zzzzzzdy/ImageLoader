package activitytest.exmaple.com.imageloader.ImageLoader.CompressStrategies;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import activitytest.exmaple.com.imageloader.ImageLoader.Interface.CompressStrategy;
import activitytest.exmaple.com.imageloader.ImageLoader.Option.CompressOptions;

/**
 * by zdy on 2019/4/3 00:25
 */
public class QualityCompress implements CompressStrategy {
    private static final QualityCompress instance = new QualityCompress();
    public QualityCompress getInstance(){return instance;}
    private QualityCompress(){}
    @Override
    public Bitmap compress(Bitmap bitmap, CompressOptions options) {
        int quality,maxSize;
        if(options.quality == CompressOptions.a){
            if(options.maxSize == CompressOptions.a){
                return bitmap;
            }
            quality = 100;
            maxSize = options.maxSize;
        }else {
            quality = options.quality;
            maxSize = bitmap.getRowBytes()*bitmap.getHeight();
        }
        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,quality,byteArrayOutputStream);
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        bitmap = BitmapFactory.decodeStream(byteArrayInputStream,null,null);
        while (byteArrayOutputStream.toByteArray().length>maxSize&&quality>10) {
            quality-=5;
            byteArrayOutputStream.reset();
            bitmap.compress(Bitmap.CompressFormat.PNG,quality,byteArrayOutputStream);
        }
        try {
            byteArrayOutputStream.close();
            byteArrayInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
