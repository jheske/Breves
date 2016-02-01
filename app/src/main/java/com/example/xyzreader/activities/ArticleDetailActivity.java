package com.example.xyzreader.activities;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created by jill on 1/29/16.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = getClass().getSimpleName();
    private Cursor mCursor;
    private ViewPager mViewPager;
    FragmentStatePagerAdapter mPagerAdapter;
    private long mArticleId;
    private String mArticleTitle;
    public static final String ARTICLE_ID_EXTRA = "ARTICLE_ID_EXTRA";

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @Bind(R.id.img_backdrop)
    ImageView imgBackdrop;
    @BindString(R.string.article_by)
    String mBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        ButterKnife.bind(this);
        setupFab();
        /**
         * Intent that started the Activity contains the id
         * of the Article the user selected.
         */
        if (savedInstanceState == null) {
            if (getIntent() == null)
                return;
            mArticleId = getIntent().getLongExtra(ArticleDetailFragment.ARTICLE_ID_EXTRA, 0);
        }

        setTitle("");
        setupViewPager();
        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void setupFab() {
        FloatingActionButton fabShare = (FloatingActionButton) findViewById(R.id.fab_share);

        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Intent
                        .createChooser(ShareCompat.IntentBuilder.from(ArticleDetailActivity.this)
                                .setType("text/plain")
                                .setText(mArticleTitle)
                                .getIntent(), getString(R.string.action_share)));
            }
        });
    }

    private void setupViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.article_viewpager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeButtonEnabled(true);
            }
        }

        mPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            /**
             * getItem() called when pages are created.
             */
            @Override
            public Fragment getItem(int position) {
                mCursor.moveToPosition(position);
                return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
            }

            /**
             * getCount()
             * Called a zillion times during create and on page changes.
             */
            @Override
            public int getCount() {
                if (mCursor == null)
                    return 0;
                else
                    return mCursor.getCount();
            }
        };

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor == null)
                    return;
                mCursor.moveToPosition(position);
                onArticleChanged(mCursor.getString(ArticleLoader.Query.PHOTO_URL));
                mArticleTitle = mCursor.getString(ArticleLoader.Query.TITLE);
                mArticleId = mCursor.getLong(ArticleLoader.Query._ID);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();

        // Select the first article's id
        if (mArticleId > 0) {
            mCursor.moveToFirst();
            // TODO: optimize
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(ArticleLoader.Query._ID) == mArticleId) {
                    final int position = mCursor.getPosition();
                    mViewPager.setCurrentItem(position);
                    break;
                }
                mCursor.moveToNext();
            }
            mArticleId = 0;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    public void onArticleChanged(String backdropUrl) {
        if(imgBackdrop == null)
            return;

        Picasso.with(this).load(backdropUrl)
                .placeholder(R.drawable.placeholder_backdrop_w300)
                .error(R.drawable.placeholder_backdrop_w300)
                .into(imgBackdrop);
    }
}

