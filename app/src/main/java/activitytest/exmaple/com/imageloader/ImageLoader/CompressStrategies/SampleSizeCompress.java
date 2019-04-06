package activitytest.exmaple.com.imageloader.ImageLoader.CompressStrategies;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import activitytest.exmaple.com.imageloader.ImageLoader.Interface.CompressStrategy;
import activitytest.exmaple.com.imageloader.ImageLoader.Option.CompressOptions;

/**
 * by zdy on 2019/4/3 01:00
 */
public class SampleSizeCompress implements CompressStrategy {
    private static final SampleSizeCompress instance = new SampleSizeCompress();

    public SampleSizeCompress getInstance() {
        return instance;
    }

    private SampleSizeCompress() {
    }

    @Override
    public Bitmap compress(Bitmap bitmap, CompressOptions options) {
        final int sampleSize = calculateInSampleSize(bitmap, options);
        if (sampleSize != 1) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            BitmapFactory.Options bfo = new BitmapFactory.Options();
            bfo.inJustDecodeBounds = true;
            bfo.inSampleSize = sampleSize;
            bitmap = BitmapFactory.decodeStream(byteArrayInputStream, null, bfo);
            try {
                byteArrayOutputStream.close();
                byteArrayInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return bitmap;
    }


    private int calculateInSampleSize(Bitmap bitmap, CompressOptions options) {
        if(options.sampleSize!=-1){
            return options.sampleSize;
        }
        final int maxSize = options.maxSize,size = bitmap.getRowBytes()*bitmap.getHeight();
        int sampleSize = 1;
        while(size/sampleSize>maxSize){
            sampleSize *=2;
        }
        return sampleSize;
    }
}
