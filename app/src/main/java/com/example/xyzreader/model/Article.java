package com.example.xyzreader.model;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.text.format.DateUtils;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jill on 1/28/16.
 */
public class Article {
    @JsonProperty("id")
    String mId;
    @JsonProperty("author")
    String mAuthor;
    @JsonProperty("title")
    String mTitle;
    @JsonProperty("body")
    String mBody;
    @JsonProperty("thumb")
    String mThumb;
    @JsonProperty("photo")
    String mPhotoUrl;
    @JsonProperty("aspect_ratio")
    String mAspectRatio;
    @JsonProperty("published_date")
    String mPublishedDate;

    String mByLine;

    //Dummy constructor to appease JSon processor
    public Article() {
    }

    public Article (Cursor cursor,Context context) {
        String date;

        mId = cursor.getString(ArticleLoader.Query._ID);
        mTitle = cursor.getString(ArticleLoader.Query.TITLE);
        mAuthor = cursor.getString(ArticleLoader.Query.AUTHOR);
        mBody = cursor.getString(ArticleLoader.Query.BODY);
        date = DateUtils.getRelativeTimeSpanString(
                cursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL).toString();
        mByLine = context.getString(R.string.article_by) + mAuthor + " | " + date;
        mPhotoUrl = cursor.getString(ArticleLoader.Query.PHOTO_URL);
        mAspectRatio = cursor.getString(ArticleLoader.Query.ASPECT_RATIO);
        mThumb = cursor.getString(ArticleLoader.Query.THUMB_URL);
    }

    public String getId() {
        return mId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return mBody;
    }

    public String getThumb() {
        return mThumb;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public String getAspectRatio() {
        return mAspectRatio;
    }

    public String getPublishedDate() {
        return mPublishedDate;
    }

    public String getByLine() {
        return mByLine;
    }

}
