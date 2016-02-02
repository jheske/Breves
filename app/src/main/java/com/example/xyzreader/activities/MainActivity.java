package com.example.xyzreader.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.utils.ArticleRvAdapter;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.utils.ArticlesRVTouchListener;
import com.facebook.stetho.Stetho;


//Floating Action Button behavior
//https://guides.codepath.com/android/Floating-Action-Buttons

/**
 * MATERIAL DESIGN LAYOUT Metrics and Keylines
 * https://www.google.com/design/spec/layout/metrics-keylines.html#metrics-keylines-keylines-spacing
 *
 * TYPOGRAPHY
 * https://www.google.com/design/spec/style/typography.html#typography-styles
 */
/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class MainActivity extends AppCompatActivity
      //  implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener
{

    //private SwipeRefreshLayout mSwipeRefreshLayout;
    //private RecyclerView mRecyclerView;
    //private ArticleRvAdapter mArticleRvAdapter;
    //private int mLastPosition;

    /**
     * Set up interface to handle onClick
     * This could also handle have methods to handle
     * onLongPress, or other gestures.
     */
   // public interface ArticleClickListener {
   //     void onClick(View view, int position);
   // }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        //mSwipeRefreshLayout.setOnRefreshListener(this);
        setupToolbar();
        //setupRecyclerView();
        //getSupportLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
  //          onRefresh();
        }

        setupFragments();
        //For viewing database and other metrics in Chrome
        setupStetho();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupFragments() {

    }

 /*   private void setupRecyclerView() {
        int columnCount = getResources().getInteger(R.integer.list_column_count);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // set up adapter
        mArticleRvAdapter = new ArticleRvAdapter(this);
        mArticleRvAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mArticleRvAdapter);
        // set up layout manager
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
        mRecyclerView.addOnItemTouchListener(new ArticlesRVTouchListener(this,
                mRecyclerView, new ArticleClickListener() {
            *//**
             * onClick called back from the GestureDetector
             *//*
            @Override
            public void onClick(View view, int position) {
                mLastPosition = position;
                mArticleRvAdapter.moveCursorToPosition(mLastPosition);
                startArticleDetailActivity(mArticleRvAdapter.getItemId(mLastPosition));
            }
        }));
    }

    private void startArticleDetailActivity(long articleId) {
        Intent intent = new Intent(this, ArticleDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(ArticleDetailActivity.ARTICLE_ID_EXTRA, articleId);
        intent.putExtras(bundle);
        this.startActivity(intent);
    }


    @Override
    public void onRefresh() {
        startService(new Intent(this, UpdaterService.class));
    }
*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
    //            onRefresh();
                return true;
            default:
                return false;
        }
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

    /**********************
     * BroadcastReceiver
     **********************/
    private boolean mIsRefreshing = false;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }
    };

    private void updateRefreshingUI() {
     //   mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    /**********************
     * LoaderManager
     **********************/
/*    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mArticleRvAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mArticleRvAdapter.swapCursor(null);
    }*/


    /**
     * A very useful library for debugging Android apps
     * using Chrome, even has a database inspector!
     * <p/>
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
