package com.example.test12;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    TextView textView1;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取WebView控件的实例
        WebView webView = (WebView) findViewById(R.id.webView1);
        //设置浏览器控件的属性，这里设置它支持javaScript脚本
        webView.getSettings().setJavaScriptEnabled(true);
        //传入一个WebViewClient实例到webView中，使得目标网页在webView中打开
        webView.setWebViewClient(new WebViewClient());
        //获取按钮控件用于点击打开目标网页和获取新浪天气接口的信息
        Button button = (Button) findViewById(R.id.button1);
        //获取textView1控件实例用于存储使用HttpURLConnection请求的网页信息
        textView1 = (TextView) findViewById(R.id.textView1);
        //获取textView2控件实例用于存储使用OkHttp请求的网页信息
        textView2 = (TextView) findViewById(R.id.textView2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //使用loadUrl对传入的url进行加载并在webView进行显示
                webView.loadUrl("http://www.gdpu.edu.cn");
                //使用HttpURLConnection发送http请求
                sendRequestWithHttpURLConnection();
                //使用OkHttp发送http请求
                sendRequestWithOkHttp();
            }
        });
    }

    private void sendRequestWithOkHttp() {
        //开启子线程来发送网络请求，使同一时间可发送多个数据包给服务器
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取OkHttpClient实例
                    OkHttpClient client = new OkHttpClient();
                    //创建Request对象用于存储发送http请求的信息
                    Request request = new Request.Builder()
                            .url("http://m.weather.com.cn/data/101010100.html")
                            .build();
                    //创建Response对象接收请求发送后服务器返回的数据，newCall().execute()用于发送请求
                    Response response = client.newCall(request).execute();
                    String data = response.body().string();
                    showResponse(data,2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendRequestWithHttpURLConnection(){
        //开启子线程来发送网络请求，使同一时间可发送多个数据包给服务器
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    //获取HttpURLConnection实例，网上很多类似的API已无法使用
                    URL url = new URL("http://m.weather.com.cn/data/101010100.html");
                    connection = (HttpURLConnection) url.openConnection();
                    //设置请求方式
                    connection.setRequestMethod("GET");
                    //设置建立连接超时时间
                    connection.setConnectTimeout(8000);
                    //设置传输数据超时时间
                    connection.setReadTimeout(8000);
                    //获取服务器返回的输入流
                    InputStream in = connection.getInputStream();
                    //对获取的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(in));
                    //使用StringBuilder将数据连接起来
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    showResponse(response.toString(),1);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //关闭输入流，关闭HTTP连接
                    if(reader != null){
                        try {
                            reader.close();
                        } catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    if (connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void showResponse(final String response,int i){
        //启动主线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //进行UI操作，将结果显示在界面上，UI操作只能在主线程上进行
                if (i == 1){
                    textView1.setText("第二题：" + response);
                } else{
                    textView2.setText("第三题：" + response);
                }
            }
        });
    }
}