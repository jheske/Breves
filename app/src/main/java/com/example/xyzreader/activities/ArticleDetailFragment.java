package com.example.xyzreader.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created by jill on 1/29/16.
 */
public class ArticleDetailFragment extends Fragment {

    private static final String TAG = ArticleDetailFragment.class.getName();
    private ObservableScrollView mScrollView;
    public static final String ARTICLE_ID_EXTRA = "ARTICLE_ID_EXTRA";
    private long mArticleId;
    private Cursor mCursor;
    private String mTitle="My Articles";
    private String mAuthor;
    private String mByLine;
    private String mBody;
    private String mDate;

    @Bind(R.id.article_title)
    TextView tvTitle;
    @Bind(R.id.article_byline)
    TextView tvByLine;
    @Bind(R.id.article_body)
    TextView tvBody;
    @BindString(R.string.error_implement_method)
    String errorMissingMethod;

    /**
     * Called by ViewPager (see ArticleDetailActivity)
     * when user scrolls left/right between articles.
     *
     * @param articleId ID of article to display
     * @return the new ArticleDetailFragment instance
     */
    public static ArticleDetailFragment newInstance(long articleId) {
        ArticleDetailFragment articleDetailFragment = new ArticleDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARTICLE_ID_EXTRA, articleId);
        articleDetailFragment.setArguments(bundle);
        return articleDetailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        ButterKnife.bind(this, rootView);

        if (getArguments() != null) {
            if (getArguments().containsKey(ARTICLE_ID_EXTRA)) {
                mArticleId = getArguments().getLong(ARTICLE_ID_EXTRA);
                getArticleData(mArticleId);
                displayArticle();
            }
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mScrollView = (ObservableScrollView) view.findViewById(R.id.scrollView);
        MaterialViewPagerHelper.registerScrollView(getActivity(), mScrollView, null);
    }

    private void getArticleData(long articleId) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        mCursor = contentResolver.query(ItemsContract.Items.buildItemUri(articleId),
                ArticleLoader.Query.PROJECTION, null, null, null);
        mCursor.moveToFirst();
        mTitle = mCursor.getString(ArticleLoader.Query.TITLE);
        mAuthor = mCursor.getString(ArticleLoader.Query.AUTHOR);
        mDate = DateUtils.getRelativeTimeSpanString(
                mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL).toString();
        mByLine = mDate + " by " + mAuthor;
        mBody = mCursor.getString(ArticleLoader.Query.BODY);
    }

    private void displayArticle() {
        tvTitle.setText(mTitle);
        tvByLine.setText(Html.fromHtml(mByLine));
        tvBody.setText(Html.fromHtml(mBody + mBody + mBody));
    }
}
