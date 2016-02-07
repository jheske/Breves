package com.example.xyzreader.activities;

import android.content.Context;
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
import android.widget.LinearLayout;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;
import com.example.xyzreader.retrofit.XyzApplication;
import com.example.xyzreader.ui.ArticleRvAdapter;
import com.example.xyzreader.ui.ArticlesRVTouchListener;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created by jill on 2/2/16.
 */
public class MainActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener {

    private ArticleRvAdapter mArticleRvAdapter;
    private int mLastPosition = 0;
    private MainActivityCallback mCallback;

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.empty_view)
    LinearLayout mEmptyView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindString(R.string.error_implement_method)
    String mErrorMissingInterfaceMethod;

    /**
     * Set up interface to handle onClick
     * This could also handle have methods to handle
     * onLongPress, or other gestures.
     */
    public interface ArticleClickListener {
        void onClick(View view, int position);

    }

    public interface MainActivityCallback {
        void onArticleSelected(long articleId, boolean userSelected);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_activity_main, container, false);
        ButterKnife.bind(this, rootView);
        setupRecyclerView();
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // The hosting Activity must implement
        // Callback interface.
        try {
            mCallback = (MainActivityCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + mErrorMissingInterfaceMethod
                    + mCallback.getClass().getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
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
                //MainActivity knows whether to display Article in
                //detail pane vs its own Activity
                mCallback.onArticleSelected(mArticleRvAdapter.getItemId(mLastPosition), true);
            }
        }));
    }

    /**********************
     * SwipeRefreshLayout
     **********************/
    @Override
    public void onRefresh() {
        getActivity().startService(new Intent(getActivity(), UpdaterService.class));
    }

    public void updateRefreshingUI(boolean isRefreshing) {
        mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

    public void displayArticles() {
        if (((XyzApplication)getActivity().getApplication()).isNetworkAvailable()) {
            mEmptyView.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            getActivity().getSupportLoaderManager().initLoader(0, null, this);
        }
        else {
          mSwipeRefreshLayout.setVisibility(View.GONE);
          mEmptyView.setVisibility(View.VISIBLE);
        }

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
        mArticleRvAdapter.moveCursorToPosition(mLastPosition);
        mCallback.onArticleSelected(mArticleRvAdapter.getItemId(mLastPosition), false);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mArticleRvAdapter.swapCursor(null);
    }
}
