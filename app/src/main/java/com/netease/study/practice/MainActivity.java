package com.netease.study.practice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.loopj.android.http.RequestHandle;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnAsyncHttp).setOnClickListener(this);
        findViewById(R.id.btnOkHttp).setOnClickListener(this);
        findViewById(R.id.btnRetrofit).setOnClickListener(this);
        findViewById(R.id.btnVolley).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnAsyncHttp:
                sendHttpByAsyncHttp();
                break;
            case R.id.btnOkHttp:
                try{
                    sendHttpByOkHttp();
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
            case R.id.btnRetrofit:
                try {

                    sendHttpByRetrofit();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.btnVolley:
                sendHttpByVolley();
                break;
            default:
                break;
        }

    }

    private void sendHttpByVolley(){

        final RequestQueue mQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest httpRequest = new StringRequest(
                "http://httpbin.org/ip",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        StringRequest stringRequest2= new
                                StringRequest("http://httpbin.org/get",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {}
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {}
                                });
                        mQueue.add(stringRequest2);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    //请求失败
            }
        });

        httpRequest.setRetryPolicy(
                new DefaultRetryPolicy(2500,3,1f));

//        httpRequest.cancel();

        mQueue.add(httpRequest);

    }

    /*
       try{
                    String strResponse =   new String(response,"utf-8");
                    Toast.makeText(MainActivity.this,strResponse,Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    e.printStackTrace();;
                }
     */

    private void sendHttpByAsyncHttp(){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestHandle requestHandle = client.get("http://httpbin.org/ip",
                new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode,
                                  Header[] headers, byte[] response) {
                //请求成功
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] errorResponse, Throwable e) {
                //请求失败
            }
        });

    }


    private void sendHttpByOkHttp () throws  Exception{
         MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        Dispatcher dispatcher = new Dispatcher();
       OkHttpClient client = new OkHttpClient.Builder().build();
        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url("http://httpbin.org/ip").get().build();
        okhttp3.Response response =
                client.newCall(request).execute();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }

            @Override
            public void onResponse(Call call, okhttp3.Response response)
                    throws IOException {
                Log.e("http",response.body().string());
            }
        });

    }

    private void sendHttpByRetrofit() throws Exception{

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://httpbin.org/").
                        addConverterFactory(GsonConverterFactory.create())
                .build();

        HttpBinService service = retrofit.create(HttpBinService.class);
        retrofit2.Call<IpRepo> call =   service.getIp();
        retrofit2.Response<IpRepo>  response =   call.execute();
        Toast.makeText(this,response.body().origin,Toast.LENGTH_SHORT).show();


        call.enqueue(new retrofit2.Callback<IpRepo>() {
            @Override
            public void onResponse(retrofit2.Call<IpRepo> call,
                                   retrofit2.Response<IpRepo> response) {
                Toast.makeText(MainActivity.this,response.body().origin,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(retrofit2.Call<IpRepo> call, Throwable t) {}
        });


    }
}
