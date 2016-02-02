package com.example.xyzreader.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.utils.ArticleRvAdapter;
import com.example.xyzreader.utils.ArticlesRVTouchListener;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jill on 2/2/16.
 */
public class MainActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

private ArticleRvAdapter mArticleRvAdapter;
private int mLastPosition;

@Bind(R.id.recycler_view)
RecyclerView mRecyclerView;
@Bind(R.id.swipe_refresh_layout)
SwipeRefreshLayout mSwipeRefreshLayout;

/**
 * Set up interface to handle onClick
 * This could also handle have methods to handle
 * onLongPress, or other gestures.
 */
public interface ArticleClickListener {
    void onClick(View view, int position);

}

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_activity_main, container, false);
        ButterKnife.bind(this, rootView);
        setupRecyclerView();
        mSwipeRefreshLayout.setOnRefreshListener(this);
        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        return rootView;
    }

    private void setupRecyclerView() {
        int columnCount = getResources().getInteger(R.integer.list_column_count);

        // set up adapter
        mArticleRvAdapter = new ArticleRvAdapter(getActivity());
        mArticleRvAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mArticleRvAdapter);
        // set up layout manager
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(sglm);
        mRecyclerView.addOnItemTouchListener(new ArticlesRVTouchListener(getActivity(),
                mRecyclerView, new ArticleClickListener() {

            // onClick called back from the GestureDetector
            @Override
            public void onClick(View view, int position) {
                mLastPosition = position;
                mArticleRvAdapter.moveCursorToPosition(mLastPosition);
                startArticleDetailActivity(mArticleRvAdapter.getItemId(mLastPosition));
            }
        }));
    }

    private void startArticleDetailActivity(long articleId) {
        Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(ArticleDetailActivity.ARTICLE_ID_EXTRA, articleId);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
    }

    /**********************
     * SwipeRefreshLayout
     **********************/
    @Override
    public void onRefresh() {
        getActivity().startService(new Intent(getActivity(), UpdaterService.class));
    }

    /**********************
     * LoaderManager
     **********************/
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mArticleRvAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mArticleRvAdapter.swapCursor(null);
    }
}
