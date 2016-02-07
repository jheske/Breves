package com.example.xyzreader.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.xyzreader.R;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.retrofit.XyzApplication;
import com.facebook.stetho.Stetho;

//Floating Action Button behavior
//https://guides.codepath.com/android/Floating-Action-Buttons

/**
 * Sample App
 * https://github.com/google/iosched/blob/master/android/src/main/res/values/dimens.xml
 * <p>
 * MATERIAL DESIGN LAYOUT Metrics and Keylines
 * https://www.google.com/design/spec/layout/metrics-keylines.html#metrics-keylines-keylines-spacing
 * <p>
 * TYPOGRAPHY
 * https://www.google.com/design/spec/style/typography.html#typography-styles
 * <p>
 * Icon
 * http://www.iconarchive.com/show/seo-icons-by-thehoth/seo-article-icon.html
 */

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class MainActivity extends AppCompatActivity
        implements MainActivityFragment.MainActivityCallback {

    private final String TAG = getClass().getSimpleName();
    MainActivityFragment mMainActivityFragment;
    ArticleDetailFragment mArticleDetailFragment;
    private boolean mTwoPaneLayout;

    /**********************
     * BroadcastReceiver
     **********************/
    private boolean mIsRefreshing = false;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                Log.d(TAG, "onReceive refreshing = " + mIsRefreshing);
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                mMainActivityFragment.updateRefreshingUI(mIsRefreshing);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_one_pane);
        //layout_activity_main is defined in values-xxx/layouts.xml
        //Android will automatically load the correct one based on device size and orientation:
        //  R.layout.layout_activity_main (portrait for all devices, landscape for phones)
        //  R.layout.layout_activity_main (landscape for tablets only)
        setContentView(R.layout.layout_activity_main);
        setupToolbar();
        setupFragments();
        mMainActivityFragment.displayArticles();
        if (savedInstanceState == null)
            mMainActivityFragment.onRefresh();
        //For viewing database and other metrics in Chrome
        //chrome://inspect
        setupStetho();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupFragments() {
        mTwoPaneLayout = getResources().getBoolean(R.bool.is_two_pane_layout);
        mMainActivityFragment = ((MainActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_articles_list_container));
        if (mTwoPaneLayout) {
            mArticleDetailFragment = ((ArticleDetailFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_article_detail_container));
            setupFab();
        }
    }

    private void setupFab() {
        FloatingActionButton fabShare = (FloatingActionButton) findViewById(R.id.fab_share);
        final Context context = this;

        if (! ((XyzApplication) getApplication()).isNetworkAvailable()) {
            fabShare.setVisibility(View.GONE);
            return;
        }
        fabShare.setVisibility(View.VISIBLE);
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = ArticleDetailFragment
                        .getShareIntent(mArticleDetailFragment.getArticle(), context);
                startActivity(Intent.createChooser(shareIntent,getString(R.string.share_article)));
            }
        });
    }

    @Override
    public void onArticleSelected(long articleId, boolean userSelected) {
        if (mTwoPaneLayout)
            mArticleDetailFragment.displayArticle(articleId);
        else if (userSelected)
            startArticleDetailActivity(articleId);
    }

    private void startArticleDetailActivity(long articleId) {
        Intent intent = new Intent(this, ArticleDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(ArticleDetailActivity.ARTICLE_ID_EXTRA, articleId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }

    /**
     * A very useful library for debugging Android apps
     * using Chrome, even has a database inspector!
     * <p>
     * chrome://inspect
     */
    private void setupStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }
}
