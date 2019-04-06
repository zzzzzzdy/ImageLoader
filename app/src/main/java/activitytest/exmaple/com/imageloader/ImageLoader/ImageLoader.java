package activitytest.exmaple.com.imageloader.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import activitytest.exmaple.com.imageloader.ImageLoader.Cache.DiskCache;
import activitytest.exmaple.com.imageloader.ImageLoader.Cache.MemoryCache;
import activitytest.exmaple.com.imageloader.ImageLoader.CompressStrategies.ScaleCompress;
import activitytest.exmaple.com.imageloader.ImageLoader.Encrypts.MD5E;
import activitytest.exmaple.com.imageloader.ImageLoader.Interface.CompressStrategy;
import activitytest.exmaple.com.imageloader.ImageLoader.Interface.Encrypt;
import activitytest.exmaple.com.imageloader.ImageLoader.Interface.ImageCache;
import activitytest.exmaple.com.imageloader.ImageLoader.Interface.LoadImageCallback;
import activitytest.exmaple.com.imageloader.ImageLoader.Option.CompressOptions;

/**
 * by zdy on 2019/4/3 23:08
 */
public class ImageLoader {
    private static final Handler MY_HANDLER = new Handler();
    private static final int THREAD_N = Runtime.getRuntime().availableProcessors();
    private static ExecutorService sFixedTreadPool = new ThreadPoolExecutor(THREAD_N,
            THREAD_N,0L,TimeUnit.MILLISECONDS,
            new StackLinkedBlockingDeque<Runnable>()
            );
    private ImageLoaderBuilder builder;
    private String mAddress;
    private ImageView mImageView;
    private ImageLoader(ImageLoaderBuilder builder) {
        this.builder = builder;
    }
    public static ImageLoaderBuilder with(Context context){
        return new ImageLoaderBuilder(context);
    }
    public ImageLoader load(@NonNull String address) {
        if (address.isEmpty()) {
            return null;
        }
        mAddress = address;
        return this;
    }
    public void into(@NonNull ImageView imageView) {
        this.mImageView = imageView;
        load2ImageView(true);
    }
    public void into(@NonNull ImageView imageView,boolean autoCompress) {
        this.mImageView = imageView;
        load2ImageView(autoCompress);
    }
    //缓存图片，自定义接口
    public void getImage(LoadImageCallback callback) {
        loadBitmap(callback,getEncodedAddress());
    }
    //仅缓存
    public void intoCache(final String label) {
        loadBitmap(new LoadImageCallback() {
            @Override
            public void success(Bitmap bitmap) {
                bitmap = compress(bitmap);
                boolean temp = builder.noMemoryCache;
                builder.noMemoryCache = true;
                cache(bitmap, builder.mEncrypt.encode(mAddress + label));
                builder.noMemoryCache = temp;
            }

            @Override
            public void fail(Exception e) {
                e.printStackTrace();
            }
        },builder.mEncrypt.encode(mAddress));
    }
    public void intoCache(){
        intoCache(builder.mCompressOptions.toString());
    }
    private void load2ImageView(boolean autoCompress){
        if(autoCompress){
            if(builder.mCompressOptions == null){
                builder.mCompressOptions = new CompressOptions();
            }
            builder.mCompressOptions.scale(mImageView.getWidth(),mImageView.getHeight());
            boolean hadScale = false;
            for(CompressStrategy compressStrategy : builder.compressStrategies){
                if(compressStrategy instanceof ScaleCompress){
                    hadScale = true;
                }
            }
            if(!hadScale){
                builder.compressStrategies.add(0,ScaleCompress.getInstance());
            }

        }
        mImageView.setTag(getEncodedAddress());
        if(builder.mPlaceHolder!=null&&mImageView.getTag().equals(getEncodedAddress())){
            mImageView.setImageDrawable(builder.mPlaceHolder);
        }
        loadBitmap(new LoadImageCallback() {
            @Override
            public void success(Bitmap bitmap) {
                if (mImageView.getTag().equals(getEncodedAddress())){
                    mImageView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void fail(Exception e) {
                if (builder.mErrorHolder != null && mImageView.getTag().equals(getEncodedAddress())) {
                    mImageView.setImageDrawable(builder.mErrorHolder);
                }
                e.printStackTrace();
            }
        },getEncodedAddress());
    }
    private void loadBitmap(final LoadImageCallback callback, final  String url){
        sFixedTreadPool.submit(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = null;
                if(builder.noMemoryCache){
                    bitmap = builder.mImageCache.get(url);
                }else {
                    bitmap = MemoryCache.getInstance().get(url);
                    if(bitmap == null){
                        bitmap = builder.mImageCache.get(url);
                    }
                }
                if(bitmap==null){
                    bitmap = mAddress.startsWith("http")? readFromHttp():readFromFile();
                }
                if(bitmap==null){
                    runOnUIThread(callback, null, new Exception("eroor address"));
                }
                if(builder.withOriginal){
                    cache(bitmap, builder.mEncrypt.encode(mAddress+new CompressOptions()));
                }
                bitmap = compress(bitmap);
                cache(bitmap, url);
                runOnUIThread(callback, bitmap, null);
            }

        });
    }
    private Bitmap compress(Bitmap bitmap){
        for(CompressStrategy compressStrategy : builder.compressStrategies){
            bitmap = compressStrategy.compress(bitmap, builder.mCompressOptions);
        }
        return bitmap;
    }
    private void cache(Bitmap bitmap, String url){
        if(!builder.noMemoryCache){
            MemoryCache.getInstance().put(url, bitmap);
        }
        builder.mImageCache.put(url, bitmap);
    }
    private Bitmap readFromHttp(){
        Bitmap bitmap = null;
        try {
            URL url = new URL(mAddress);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            bitmap = BitmapFactory.decodeStream(url.openStream());
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap readFromFile() {
        return BitmapFactory.decodeFile(mAddress);
    }
    private String getEncodedAddress(){
        if(builder.compressStrategies.size()<1){
            return builder.mEncrypt.encode(mAddress);
        }
        else {
            return builder.mEncrypt.encode(mAddress+builder.mCompressOptions);
        }
    }
    private void runOnUIThread(final LoadImageCallback callback, final Bitmap bitmap, final Exception e){
        MY_HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if(e==null){
                    callback.success(bitmap);
                }
                else {
                    callback.fail(e);
                }
            }
        });

    }




    public static class ImageLoaderBuilder{
        private Context mContext;
        private Encrypt mEncrypt;
        private ImageCache mImageCache;
        private List <CompressStrategy> compressStrategies = new ArrayList<>();
        private CompressOptions mCompressOptions;
        private Drawable mPlaceHolder;
        private Drawable mErrorHolder;
        private Boolean noMemoryCache;
        private Boolean withOriginal;
        private ImageLoaderBuilder(Context context){
            this.mContext = context;
        }
        public ImageLoaderBuilder noMEmoryCache(boolean noMemoryCache){
            this.noMemoryCache = noMemoryCache;
            return this;
        }
        public ImageLoaderBuilder encrypt(Encrypt encrypt){
            mEncrypt = encrypt;
            return this;
        }
        //是否缓存原版图
        public ImageLoaderBuilder withOriginal(boolean withOriginal){
            this.withOriginal = withOriginal;
            return this;
        }
        public ImageLoaderBuilder imageCache(ImageCache imageCache){
            mImageCache = imageCache;
            if(mImageCache instanceof DiskCache&&((DiskCache) mImageCache).getCacheDir()==null){
                ((DiskCache)mImageCache).setCacheDir(mContext.getExternalCacheDir().getPath());

            }
            return this;
        }
        public ImageLoaderBuilder addCompress(CompressStrategy compressStrategy){
            compressStrategies.add(compressStrategy);
            return this;
        }
        public ImageLoaderBuilder compressOptions(CompressOptions compressOptions){
            mCompressOptions = compressOptions;
            return this;
        }
        public ImageLoaderBuilder place(Drawable place){
            mPlaceHolder = place;
            return this;
        }
        public ImageLoaderBuilder place(int id){
            mPlaceHolder = mContext.getResources().getDrawable(id);
            return this;
        }
        public ImageLoaderBuilder error(Drawable error){
            mErrorHolder = error;
            return this;
        }
        public ImageLoaderBuilder error(int id){
            mErrorHolder = mContext.getResources().getDrawable(id);
            return this;
        }
        public ImageLoader build(){
            autoCheck();
            return new ImageLoader(this);
        }
        public ImageLoader load(String address){
            return build().load(address);
        }
        private void autoCheck(){
            if(compressStrategies.size()>0&&mCompressOptions ==null){
                compressOptions(new CompressOptions());
            }
            if(mEncrypt == null){
                encrypt(MD5E.getInstance());
            }
            if(mImageCache==null){
                imageCache(DiskCache.getInstance());
            }
        }

    }
    private static class StackLinkedBlockingDeque<T> extends LinkedBlockingDeque<T> {
        @Override
        public T take() throws InterruptedException {
            return takeLast();
        }

        @Override
        public T poll() {
            return pollLast();
        }
    }
}
