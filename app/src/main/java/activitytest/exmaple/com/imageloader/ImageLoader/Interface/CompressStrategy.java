package activitytest.exmaple.com.imageloader.ImageLoader.Interface;

import android.graphics.Bitmap;

import activitytest.exmaple.com.imageloader.ImageLoader.Option.CompressOptions;

public interface CompressStrategy {
    Bitmap compress(Bitmap bitmap,CompressOptions options);
}
