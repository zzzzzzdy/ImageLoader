package activitytest.exmaple.com.imageloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TheOtherHttp {
    private String url;
    private String method;

    private TheOtherHttp() {
    }

    public static class Builder {
        private TheOtherHttp theOtherHttp = new TheOtherHttp();

        public Builder(String url) {
            theOtherHttp.url = url;
        }

        public Builder setMethod(String method) {
            theOtherHttp.method = method;
            return this;
        }

        public TheOtherHttp build() {
            return theOtherHttp;
        }
    }
    final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 5, 1, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(100));
    public void sendRequest(final Parsing parsing){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                HttpURLConnection connection=null;
                BufferedReader reader;
                InputStream inputStream;
                StringBuilder response;
                String line;
                String mResult = null;

                try {
                    URL a = new URL(url);
                    connection = (HttpURLConnection) a.openConnection();
                    connection.setRequestMethod(method);
                    connection.setConnectTimeout(7000);
                    connection.setReadTimeout(7000);
                    connection.setRequestProperty("Content-type", "application/json");
                    inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    mResult = response.toString();
                    inputStream.close();
                    if(parsing!=null)
                    {
                        parsing.success(mResult);
                    }


                } catch (IOException e) {
                    if (parsing != null) {
                        parsing.onError(e);
                    }

                } finally {
                    if(connection!=null)connection.disconnect();

                }

            }


        };
        threadPoolExecutor.execute(runnable);


    }
    //重载发送请求方法
    public void sendRequest(final Parsing parsing, final Map<String, String> form){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection= null;
                PrintWriter pw = null ;
                BufferedReader reader = null ;
                StringBuilder out = new StringBuilder();
                StringBuilder sb = new StringBuilder();
                String line = null ;
                String mResponse = null;
                for (String key : form.keySet()) {
                    if(out.length()!=0){
                        out.append("&");
                    }
                    out.append(key).append("=").append(form.get(key));
                }
                try {
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod(method);
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);
                    connection.setUseCaches(false);
                    connection.connect();
                    pw = new PrintWriter(connection.getOutputStream());
                    pw.print(out.toString());
                    pw.flush();
                    reader  = new BufferedReader( new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    while ((line = reader.readLine()) != null ) {
                        sb.append(line);
                    }
                    mResponse = sb.toString();
                    if (parsing != null) {
                        parsing.success(mResponse);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    if (parsing != null) {
                        parsing.onError(e);
                    }
                }finally{
                    try {
                        if(pw != null){
                            pw.close();
                        }
                        if(reader != null){
                            reader.close();
                        }
                        if(connection != null){
                            connection.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }
    public interface Parsing{
        void success(String result);
        void onError(Exception e);
    }


}
