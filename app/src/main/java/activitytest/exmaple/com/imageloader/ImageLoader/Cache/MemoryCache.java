package activitytest.exmaple.com.imageloader.ImageLoader.Cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import activitytest.exmaple.com.imageloader.ImageLoader.Interface.ImageCache;

public class MemoryCache implements ImageCache {
    private static final MemoryCache instance = new MemoryCache();
    public static MemoryCache getInstance(){
        return instance;
    }
    LruCache<String, Bitmap> mMemoryCache;

    private MemoryCache(){
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
        final int cacheSize = maxMemory/8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
          protected int sizeOf(String key, Bitmap bitmap){
              return bitmap.getRowBytes()*bitmap.getHeight()/1024;
          }
        };
    }
    public void put(String url, Bitmap bitmap){
        if(mMemoryCache!=null){
            mMemoryCache.put(url,bitmap);
        }

    }
    public Bitmap get(String url){
        return mMemoryCache.get(url);
    }

}
