package com.example.xyzreader.retrofit;

import android.app.Application;

import com.example.xyzreader.utils.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by jill on 1/28/16.
 */
public class XyzApplication extends Application {
    private final String TAG = getClass().getSimpleName();
    //End with / as per recommendation at
    //http://inthecheesefactory.com/blog/retrofit-2.0/en
    private static final String BASE_URL = "https://dl.dropboxusercontent.com/u/231329/xyzreader_data/";
    private ApiService mApiService;
    private boolean mIsNetworkAvailable=false;
    private Retrofit mRetrofit;

    /**
     * Create the Retrofit object
     * See https://snow.dog/blog/make-life-easier-retrofit/
     * for error handling code.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mIsNetworkAvailable = Utils.isNetworkAvailable(getApplicationContext());
        setupRestAdapter();
    }

    private void setupRestAdapter() {
        OkHttpClient client = new OkHttpClient();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        client.interceptors().add(interceptor);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
        mApiService = mRetrofit.create(ApiService.class);
    }

    public boolean isNetworkAvailable() {
        return mIsNetworkAvailable;
    }

    /**
     * Proxy to the service.
     * There are other proxies I can add later,
     * like Search and TV.
     * <p/>
     * This should be called one time only in the app
     * and then referenced from other parts of the app.
     */
    public ApiService getApiService() {
        return mApiService;
    }
}
