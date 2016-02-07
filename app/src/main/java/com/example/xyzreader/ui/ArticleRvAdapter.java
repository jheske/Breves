package com.example.xyzreader.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created by jill on 1/28/16.
 */
public class ArticleRvAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
      //  extends RecyclerView.Adapter<ArticleRvAdapter.ArticleViewHolder> {
    private final static String TAG = ArticleViewHolder.class.getName();
    private Cursor mCursor;
    private Context mContext;
    private static final int EMPTY_VIEW = 10;

    /**********************
     * EmptyViewHolder
     **********************/
    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**********************
     * ArticleViewHolder
     **********************/
    public class ArticleViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.article_title)
        TextView tvTitleView;
        @Bind(R.id.article_subtitle)
        TextView tvSubtitleView;
        @Bind(R.id.thumbnail)
        ImageView imgThumbnailView;
        @BindString(R.string.article_by)
        String mBy;

        public ArticleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }
    }

    /**********************
     * ArticleRvAdapter
     **********************/
    public ArticleRvAdapter(Context context) {
        super();
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == EMPTY_VIEW) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_view, parent, false);
            return new EmptyViewHolder(view);
        }

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_article, parent, false);
        final ArticleViewHolder vh = new ArticleViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    //public void onBindViewHolder(final ArticleViewHolder holder, int position) {
        if (holder instanceof ArticleViewHolder) {
            ArticleViewHolder vh = (ArticleViewHolder) holder;

            mCursor.moveToPosition(position);
            long publishedDate = mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE);
            String author = mCursor.getString(ArticleLoader.Query.AUTHOR);
            String subtitle = vh.mBy + author + " | " + DateUtils.getRelativeTimeSpanString(
                    publishedDate, System.currentTimeMillis(),
                    DateUtils.HOUR_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
            String thumbUrl = mCursor.getString(ArticleLoader.Query.THUMB_URL);
            Log.d(TAG, thumbUrl);

            vh.tvTitleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
            vh.tvSubtitleView.setText(subtitle);
            Picasso.with(mContext).load(thumbUrl)
                    .into(vh.imgThumbnailView);
        }
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public void moveCursorToPosition(int position) {
        mCursor.moveToPosition(position);
    }

    @Override
    public long getItemId(int position) {
        if (mCursor == null)
            return 0;
        if (mCursor.getCount() == 0)
            return 0;
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null)
            return 0;
        return mCursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (mCursor.getCount() == 0) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    }
}

