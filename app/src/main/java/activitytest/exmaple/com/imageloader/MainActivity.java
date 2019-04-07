package activitytest.exmaple.com.imageloader;



import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import activitytest.exmaple.com.imageloader.ImageLoader.ImageLoader;


public class MainActivity extends AppCompatActivity {
    private ImageView imageView1;
    private ImageView imageView2;
    private List<String> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView1 = findViewById(R.id.image1);

        ImageLoader.with(this).load("https://ws1.sinaimg.cn/large/0065oQSqly1g0ajj4h6ndj30sg11xdmj.jpg").into(imageView1);
    }
//        initData();
//        Log.d("cccccc"," "+images.size());
//
//    }
//    private void initData() {
//
//        TheOtherHttp theOtherHttp = new TheOtherHttp.Builder("http://gank.io/api/data/%e7%a6%8f%e5%88%a9/15/1").setMethod("GET").build();
//
//        theOtherHttp.sendRequest(new TheOtherHttp.Parsing() {
//            @Override
//            public void success(String result) {
//                parseJSON(result);
//                }
//
//            @Override
//            public void onError(Exception e) {
//                e.printStackTrace();
//                }
//        });
//
//
//    }
//    private void parseJSON(String response) {
//        try {
//            JSONArray array = new JSONObject(response).getJSONArray("results");
//
//            for (int i = 0, length = array.length(); i < length; i++) {
//                images.add(array.getJSONObject(i).getString("url"));
//
//            }
//            ImageLoader.with(this).load(images.get(2)).into(imageView1);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }


}
