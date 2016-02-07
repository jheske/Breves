package com.example.xyzreader.activities;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.model.Article;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created by jill on 1/29/16.
 */
public class ArticleDetailFragment extends Fragment {

    private static final String TAG = ArticleDetailFragment.class.getName();
    public static final String ARTICLE_ID_EXTRA = "ARTICLE_ID_EXTRA";
    private Article mArticle;
    private long mArticleId;
    private Cursor mCursor;

    @Bind(R.id.article_title)
    TextView tvTitle;
    @Bind(R.id.article_byline)
    TextView tvByLine;
    @Bind(R.id.article_body)
    TextView tvBody;

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
        View rootView = inflater.inflate(R.layout.fragment_activity_article_detail, container, false);
        ButterKnife.bind(this, rootView);

        if (getArguments() != null) {
            if (getArguments().containsKey(ARTICLE_ID_EXTRA)) {
                mArticleId = getArguments().getLong(ARTICLE_ID_EXTRA);
                displayArticle(mArticleId);
            }
        }
        return rootView;
    }

    @Override public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    /**
     * Retrieve Article data from the database
     *
     * @param articleId
     */
    private Article dbGetArticle(long articleId) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        mCursor = contentResolver.query(ItemsContract.Items.buildItemUri(articleId),
                ArticleLoader.Query.PROJECTION, null, null, null);
        mCursor.moveToFirst();
        return new Article(mCursor,getActivity());
    }

    public void displayArticle(long articleId) {
        mArticle = dbGetArticle(articleId);
        tvTitle.setText(mArticle.getTitle());
        tvByLine.setText(Html.fromHtml(mArticle.getByLine()));
        tvBody.setText(Html.fromHtml(mArticle.getBody()));
    }

    public Article getArticle() {
        return mArticle;
    }

    public static Intent getShareIntent(Article article,Context context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.share_subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                article.getTitle() + "\n\n"
                        + Html.fromHtml(article.getByLine()).toString() + "\n\n"
                        + Html.fromHtml(article.getBody()).toString());
        return shareIntent;
    }
}
