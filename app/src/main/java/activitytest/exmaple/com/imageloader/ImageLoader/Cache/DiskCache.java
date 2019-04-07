package activitytest.exmaple.com.imageloader.ImageLoader.Cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import activitytest.exmaple.com.imageloader.ImageLoader.Interface.ImageCache;

public class DiskCache implements ImageCache {
    private static final DiskCache instance = new DiskCache();
    private String mCacheDir;
    public static DiskCache getInstance(){
        return instance;
    }

    public Bitmap get(String url){
        if(mCacheDir == null)
        {
            return null;
        }
        return BitmapFactory.decodeFile(mCacheDir + url);
    }
    public void put(String url, Bitmap bmp){
        if(mCacheDir == null)
        {
            return ;
        }
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(mCacheDir,url);
            File parentFile = file.getParentFile();
            if(!parentFile.exists()&&!parentFile.mkdirs()){
                return;
            }
            fileOutputStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(fileOutputStream!=null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public void setCacheDir(String path){
        if(mCacheDir == null){
            this.mCacheDir = path.endsWith("/")?path:path+"/";
        }
    }
    public String getCacheDir(){
        return mCacheDir;
    }

}
