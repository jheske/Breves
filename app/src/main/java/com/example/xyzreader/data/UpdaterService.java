package com.example.xyzreader.data;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.RemoteException;
import android.text.format.Time;
import android.util.Log;

import com.example.xyzreader.Utils;
import com.example.xyzreader.model.Article;
import com.example.xyzreader.retrofit.ApiService;
import com.example.xyzreader.retrofit.XyzApplication;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class UpdaterService extends IntentService {
    private static final String TAG = "UpdaterService";

    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "com.example.xyzreader.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = "com.example.xyzreader.intent.extra.REFRESHING";

    ArrayList<ContentProviderOperation> mCpo = new ArrayList<ContentProviderOperation>();
    Uri mDirUri;

    public UpdaterService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!Utils.isNetworkAvailable(this)) {
            Log.w(TAG, "Network not available, not refreshing.");
            return;
        }
        sendBroadcast(new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));
        getArticles();
    }

    public void dbAddArticles(List<Article> articles) {
        Time time = new Time();
        // Don't even inspect the intent, we only do one thing, and that's fetch content.
        //ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();
        mDirUri = ItemsContract.Items.buildDirUri();
        // Delete all items
        mCpo.add(ContentProviderOperation.newDelete(mDirUri).build());

        for (Article article : articles) {
            ContentValues values = new ContentValues();
            values.put(ItemsContract.Items.SERVER_ID, article.getId());
            values.put(ItemsContract.Items.AUTHOR, article.getAuthor());
            values.put(ItemsContract.Items.TITLE, article.getTitle());
            values.put(ItemsContract.Items.BODY, article.getBody());
            values.put(ItemsContract.Items.THUMB_URL, article.getThumb());
            values.put(ItemsContract.Items.PHOTO_URL, article.getPhotoUrl());
            values.put(ItemsContract.Items.ASPECT_RATIO, article.getAspectRatio());
            time.parse3339(article.getPublishedDate());
            values.put(ItemsContract.Items.PUBLISHED_DATE, time.toMillis(false));
            mCpo.add(ContentProviderOperation.newInsert(mDirUri).withValues(values).build());
        }
        try {
            getContentResolver().applyBatch(ItemsContract.CONTENT_AUTHORITY, mCpo);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
        sendBroadcast(new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
    }

    public void getArticles() {
        XyzApplication app = (XyzApplication) getApplication();
        ApiService apiService = app.getApiService();
        final Context context = this;

        Call<List<Article>> call = apiService.getArticles();
        call.enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Response<List<Article>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    dbAddArticles(response.body());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Utils.showToast(context, "Api call failed " + t.getMessage());
                sendBroadcast(new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
                Log.i(TAG, "Api call error! " + t.getMessage());
            }
        });
    }
}
