package com.think.okhttp3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttp3Activity extends AppCompatActivity implements View.OnClickListener {

    protected ImageView ivDemo;
    private TextView tv_content;
    private String result;

    private String strTag = "--->***";
    //handler
    private Handler handler = new Handler() {
        //重写处理消息的方法.

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            //更新ui的操作.
            //判断字符串是否为null或者为空.
            if (!TextUtils.isEmpty(result)) {
                Toast.makeText(OkHttp3Activity.this, result, Toast.LENGTH_LONG).show();
                tv_content.setText(result);
            } else {
                Toast.makeText(OkHttp3Activity.this, "没有获取结果", Toast.LENGTH_LONG).show();
            }
            String threadStr = "线程名字:" + Thread.currentThread().getName() + "线程ID:" + Thread.currentThread().getId();
            //ivDemo.setImageBitmap(BitmapFactory.decodeStream(inputStream));
            Log.i(TAG, "handleMessage: what" + what + strTag + threadStr);
            if (what == 1) {
                ivDemo.setImageURI(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "a.jpg")));
            }
        }
    };
    private String TAG = this.getClass().getName();
    protected OkHttpClient okHttpClient;
    protected String url;
    protected String mItem;
    protected Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_okhttp);

        initView();

        //1,创建okhttpclient对象
        initOkHttpClient();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_okhttp:
                show();
                break;
        }
    }

    private void show() {
        //弹一个对话框，分类选择：
        //创建builder对象。
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置标题.
        builder.setTitle("okhttp操作");
        //设置标题的图标.
        builder.setIcon(R.mipmap.ic_launcher);
        //设置列表内容,以及点击事件.
        //参数:1,String数组.2,点击事件.
        final String[] items = {"0同步GET", "1异步get", "2同步POST", "3异步POST"
                , "4多文件上传,携带参数.", "5下载"

        };
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //i就是数组的索引.
                mItem = items[i] + strTag;
                switch (i) {
                    case 0:
                        //同步get请求。
                        syncGet();
                        break;
                    case 1:
                        //异步get请求.asynGet
                        asynGet();
                        break;
                    case 2:
                        //同步post请求
                        syncPost();
                        break;
                    case 3:
                        //异步post请求.asyn
                        asynPost();
                        break;
                    case 4:
                        //多文件上传,携带参数.
                        multipartUpload();
                        break;
                    case 5:
                        //下载.
                        asynDown();
                        break;
                }
            }
        });
        builder.create().show();
    }

    /**
     * 异步下载文件
     */
    private void asynDown() {
//        url = "http://img.my.csdn.net/uploads/201603/26/1458988468_5804.jpg";
        //url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1507747052013&di=9cca000a95c294906fcfb50ad8732851&imgtype=0&src=http%3A%2F%2Fp4.image.hiapk.com%2Fuploads%2Fallimg%2F150331%2F7730-1503311G209-52.jpg";
//        url ="http://p4.so.qhimgs1.com/t015276cde9c72fdb94.jpg";
        url = "http://photocdn.sohu.com/20110812/Img316178813.jpg";
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: 下载失败!" + strTag + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fileOutputStream = null;
                try {
                    File file = new File(Environment.getExternalStorageDirectory(), "a.jpg");
                    fileOutputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[2048];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                result = "onResponse: 文件下载成功" + strTag;
                handler.sendEmptyMessage(1);
                Log.i(TAG, result);
            }
        });
    }


    private void multipartUpload() {
        url = "http://172.21.3.52/MyUploadServer/servlet/MyUploadServlet";
        File file = new File(Environment.getExternalStorageDirectory(), "1.txt");
        File file2 = new File(Environment.getExternalStorageDirectory(), "2.png");
        //请求体
        //addFormDataPart(String name, String filename, RequestBody body)
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)//设置数据类型
                .addFormDataPart("username", "zxn")//添加表单参数
                .addFormDataPart("file", "1.txt", RequestBody.create(MultipartBody.FORM, file))//参数1:变量名字,参数2:文件名字,参数3:请求体.
                .addFormDataPart("file", "2.png", RequestBody.create(MultipartBody.FORM, file2))
                .build();
        //请求.
        Request request = new Request.Builder()
                .header("Authorization", "Client-ID " + "...")
                .url(url)
                .post(requestBody).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, strTag + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    result = mItem + new String(response.body().bytes(), "utf-8");
                    handler.sendEmptyMessage(0);
                    Log.i(TAG, "onResponse: " + result);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "onResponse: " + strTag + e.getMessage());
                }
            }
        });
    }

    private void asynGet() {
        //2,构建请求.
        //type", "yuantong").add("postid", "500379523313"
        url = "http://www.kuaidi100.com/query?type=yuantong&postid=500379523313";
        Request request = new Request.Builder().url(url).build();
        //3,获取call
        final Call call = okHttpClient.newCall(request);
        //4,发送异步请求.
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: --->" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String threadStr = "线程名字:" + Thread.currentThread().getName() + "线程ID:" + Thread.currentThread().getId();
                Log.i(TAG, "onResponse: " + strTag + threadStr);
                result = mItem + response.body().string();
                handler.sendEmptyMessage(0);
            }
        });
    }

    private void asynPost() {
        url = "http://www.kuaidi100.com/query";
        //2,创建请求体
        //参数:"yuantong", "500379523313----type,postid
        RequestBody body = new FormBody.Builder().add("type", "yuantong").add("postid", "500379523313").build();
        //3,构建请求
        Request request = new Request.Builder().url(url).post(body).build();
        //4,获取call
        final Call call = okHttpClient.newCall(request);
        //5,发送请求.
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "onFailure: --->" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                result = mItem + response.body().string();
                handler.sendEmptyMessage(0);
            }
        });
    }

    private void syncPost() {
        url = "http://www.kuaidi100.com/query";
        //2,创建请求体
        RequestBody body = new FormBody.Builder().add("type", "yuantong").add("postid", "500379523313").build();
        //3,构建请求post(body)--method("POST",body)
        Request request = new Request.Builder().url(url).method("POST", body).build();
        //4,获取call
        final Call call = okHttpClient.newCall(request);
        //5,发送请求,获取响应.
        new Thread() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    result = mItem + response.body().string();
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void syncGet() {
        url = "http://litchiapi.jstv.com/api/GetFeeds?column=3&PageSize=10&pageIndex=1&val=100511D3BE5301280E0992C73A9DEC41";
        //2,构建请求
        Request request = new Request.Builder().url(url).build();
        //3,获取call对象
        final Call call = okHttpClient.newCall(request);
        //4,开启线程,发送同步请求
        new Thread() {
            @Override
            public void run() {
                try {
                    //5,获取响应体
                    Response response = call.execute();
                    result = mItem + response.body().string();

                    //6,变轨主线程更新UI.
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void initOkHttpClient() {
        //Cache(File directory, long maxSize)
        //缓存的目录.
        File sdcache = getExternalCacheDir();
        //缓存大小.10MB
        int cacheSize = 10 * 1024 * 1024;
        //设置连接,写入,读取时间.
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .cache(new Cache(sdcache.getAbsoluteFile(), cacheSize));
        okHttpClient = builder.build();
    }

    private void initView() {
        findViewById(R.id.btn_okhttp).setOnClickListener(this);
        tv_content = (TextView) findViewById(R.id.tv_content);
        ivDemo = (ImageView) findViewById(R.id.iv_demo);
    }
}
