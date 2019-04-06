package activitytest.exmaple.com.imageloader.ImageLoader.Interface;

import android.graphics.Bitmap;

public interface LoadImageCallback {
    void success(Bitmap bitmap);
    void fail(Exception e );
}
