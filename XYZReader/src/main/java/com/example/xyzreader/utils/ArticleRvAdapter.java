package com.example.xyzreader.utils;

import android.content.Context;
import android.content.Intent;
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
//import com.example.xyzreader.ui.DynamicHeightNetworkImageView;
//import com.example.xyzreader.ui.ImageLoaderHelper;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.example.xyzreader.model.Article;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jill on 1/28/16.
 */
public class ArticleRvAdapter
        extends RecyclerView.Adapter<ArticleRvAdapter.ArticleViewHolder> {
    private final static String TAG = ArticleViewHolder.class.getName();
    private Cursor mCursor;
    private Context mContext;

    /**********************
     * ArticleViewHolder
     **********************/
    public class ArticleViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.article_title)
        public TextView tvTitleView;
        @Bind(R.id.article_subtitle)
        public TextView tvSubtitleView;
        @Bind(R.id.thumbnail)
        public ImageView imgThumbnailView;
        //public DynamicHeightNetworkImageView thumbnailView;

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
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_article, parent, false);
        final ArticleViewHolder vh = new ArticleViewHolder(view);
      /*  view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                        ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));
            }
        }); */
        return vh;
    }

    @Override
    public void onBindViewHolder(final ArticleViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        long publishedDate = mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE);
        String author = mCursor.getString(ArticleLoader.Query.AUTHOR);
        String subtitle = DateUtils.getRelativeTimeSpanString(
                publishedDate,System.currentTimeMillis(),
                DateUtils.HOUR_IN_MILLIS,DateUtils.FORMAT_ABBREV_ALL).toString()
                + " by " + author;
        String thumbUrl = mCursor.getString(ArticleLoader.Query.THUMB_URL);
        Log.d(TAG, thumbUrl);

        holder.tvTitleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
        holder.tvSubtitleView.setText(subtitle);
        Picasso.with(mContext).load(thumbUrl)
                .into(holder.imgThumbnailView);
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
        mCursor.moveToPosition(position);
        return mCursor.getLong(ArticleLoader.Query._ID);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null)
            return 0;
        return mCursor.getCount();
    }
}

