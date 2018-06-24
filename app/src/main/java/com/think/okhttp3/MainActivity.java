package com.think.okhttp3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.okhttputils.OkHttpClientManager;
import com.example.okhttputils.callback.ResultCallback;
import com.example.okhttputils.request.OkHttpRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button btn;
    private TextView mTv;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    String url = "http://www.weather.com.cn/data/sk/101010100.html";
    private String strTag = "--->***" + this.getClass().getSimpleName();
    protected String itemName;
    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        initView();
    }

    //同步get请求.获取对象
    public void synchGet() {
        url = "http://www.weather.com.cn/data/sk/101010100.html";
        new Thread() {
            @Override
            public void run() {
                try {
                    final Weather u = new OkHttpRequest.Builder().url(url).get(Weather.class);
                    showResult(u.getWeatherinfo().toString(), false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //异步get请求.获取对象
    public void asynGet() {
        url = "http://www.weather.com.cn/data/sk/101010100.html";
        new OkHttpRequest.Builder()
                .url(url)
                .get(new ResultCallback<Weather>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        showResult(e.getMessage());
                    }

                    @Override
                    public void onResponse(Weather response) {
                        showResult(response.getWeatherinfo().toString());
                    }
                });
    }

    //异步post请求,获取对象
    public void asynPost() {
        url = "http://www.kuaidi100.com/query";
        //http://www.kuaidi100.com/query?type=yuantong&postid=500379523313;
        Map<String, String> params = new HashMap<>();
//        params.put("type", "yuantong");
//        params.put("postid", "500379523313");
        params.put("type", "shunfeng");
        params.put("postid", "247113599810");
        new OkHttpRequest.Builder().url(url).params(params).post(new ResultCallback<PostQueryInfo>() {
            @Override
            public void onError(Request request, Exception e) {
                showResult(e.getMessage());
            }

            @Override
            public void onResponse(PostQueryInfo users) {
               showResult(users.toString());
            }
        });
    }

    //异步get请求获取字符串.
    public void asynGetString() {
        url = "http://www.weather.com.cn/data/sk/101010100.html";
        new OkHttpRequest.Builder().url(url)
                .get(new ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        showResult(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        showResult(response);
                    }
                });

    }

    //加载网络图片
    public void getImage() {
//        url = "http://images.csdn.net/20150817/1.jpg";
        url = "http://photocdn.sohu.com/20150627/mp20358632_1435372096071_7.jpeg";
        new OkHttpRequest.Builder().url(url).errResId(R.mipmap.ic_launcher).imageView(mImageView).displayImage();
    }

    private void getImageOnCallBack() {
        url = "http://p0.so.qhmsg.com/bdr/_240_/t012b5ca91c8abcdad5.jpg";
        new OkHttpRequest.Builder().url(url).errResId(R.mipmap.ic_launcher).imageView(mImageView).displayImage(new ResultCallback<InputStream>() {
            @Override
            public void onError(Request request, Exception exception) {
                showResult(exception.getMessage());
            }

            @Override
            public void onResponse(InputStream response) {
                showResult("图片加载成功!");

                FileOutputStream fileOutputStream = null;
                try {
                    File file = new File(Environment.getExternalStorageDirectory(), "3.jpg");
                    fileOutputStream = new FileOutputStream(file);
                    byte[] buffer = new byte[2048];
                    int len = 0;
                    while ((len = response.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    //异步上传sd卡中的多个文件.
    public void multiFileUpload() {
        url = "http://172.21.3.56/MyUploadServer/servlet/MyUploadServlet";///servlet/UploadServlet3
        File file2 = new File(Environment.getExternalStorageDirectory(), "1.txt");
        File file = new File(Environment.getExternalStorageDirectory(), "2.png");
        if (!file.exists() && !file2.exists()) {
            Toast.makeText(MainActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("username", "zxn666");
        new OkHttpRequest.Builder()//
                .url(url)//
                .params(params)
                .files(new Pair<String, File>("file", file), new Pair<String, File>("file", file2))//
                .upload(stringResultCallback);
    }

    public void downloadFile() {
         url = "http://pic.vsuch.com/2015/0115/20150115031919166.png";
        new OkHttpRequest.Builder()
                .url(url)
                .destFileDir(Environment.getExternalStorageDirectory().getAbsolutePath())
                .destFileName("d.jpg")
                .download(stringResultCallback);
    }

    private ResultCallback<String> stringResultCallback = new ResultCallback<String>() {
        @Override
        public void onError(Request request, Exception e) {
            showResult(e.getMessage());
        }

        @Override
        public void onResponse(String response) {
            showResult(response);
        }

        @Override
        public void inProgress(float progress) {
            mProgressBar.setProgress((int) (100 * progress));
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn) {
            show();
        }
    }

    private void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("OKHttp封装后的使用");
        final String[] items = {"0同步GET", "1异步get", "2同步POST"
                , "3异步POST", "4异步GET-String",
                "5加载网络图片", "6加载网络图片,并回调!",
                "7上传文件","8下载"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemName = items[which] + strTag;
                switch (which) {
                    case 0:
                        //同步GET,获取结果解析成对象.
                        synchGet();
                        break;
                    case 1:
                        //异步get
                        asynGet();
                        break;
                    case 2:
                        //同步POST，synchPost();+
                        // --->子线程中进行否则:NetworkOnMainThreadException
                        synchPost();
                        break;
                    case 3:
                        //异步POST,asynPost
                        asynPost();
                        break;
                    case 4:
                        //异步GET-String
                        asynGetString();
                        break;
                    case 5:
                        //加载网络图片.
                        getImage();
                        break;
                    case 6:
                        //加载网络图片,并回调!
                        getImageOnCallBack();
                        break;
                    case 7:
                        //上传!
                       multiFileUpload();
                        break;
                    case 8:
                        //下载!
                       downloadFile();
                        break;


                }
            }
        });
        builder.create().show();
    }


    private void synchPost() {
        url = "http://www.kuaidi100.com/query";
        //http://www.kuaidi100.com/query?type=yuantong&postid=500379523313;
        final Map<String, String> params = new HashMap<>();
        params.put("type", "yuantong");
        params.put("postid", "500379523313");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PostQueryInfo info = new OkHttpRequest.Builder().url(url).params(params).post(PostQueryInfo.class);
                    showResult(info.toString(), false);
                } catch (IOException e) {
                    showResult(e.getMessage(), false);
                }
            }
        }).start();
    }

    private void initView() {
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(MainActivity.this);
        mTv = (TextView) findViewById(R.id.id_textview);
        mImageView = (ImageView) findViewById(R.id.id_imageview);
        mProgressBar = (ProgressBar) findViewById(R.id.id_progress);
        mProgressBar.setMax(100);
    }

    private void showResult(String result) {
        String mResult = itemName + result;
        mTv.setText(mResult);
        Toast.makeText(MainActivity.this, mResult, Toast.LENGTH_SHORT).show();
        Log.i(TAG, mResult);
    }

    private void showResult(String result, boolean isMainThread) {
        final String mResult = itemName + result;
        if (isMainThread) {
            mTv.setText(mResult);
            Toast.makeText(MainActivity.this, mResult, Toast.LENGTH_SHORT).show();
        } else {
            OkHttpClientManager.getInstance().getDeliveryHandler().post(new Runnable() {
                @Override
                public void run() {
                    mTv.setText(mResult);
                    Toast.makeText(MainActivity.this, mResult, Toast.LENGTH_SHORT).show();
                }
            });
        }
        Log.i(TAG, mResult);
    }
}
