package com.example.xyzreader.model;

import android.database.Cursor;

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
    String mPhoto;
    @JsonProperty("aspect_ratio")
    String mAspectRatio;
    @JsonProperty("published_date")
    String mPublishedDate;

    //Dummy constructor to appease JSon processor
    public Article() {
    }

    public Article (Cursor cursor) {
        mAuthor = "Jill";
        mBody = "This is the text of my great article";
        mTitle = "My Great Article";
        mAspectRatio = "1.5";
        mId = "385";
        mThumb = "https://dl.dropboxusercontent.com/u/231329/xyzreader_data/thumbs/p004.jpg";
        mPhoto = "https://dl.dropboxusercontent.com/u/231329/xyzreader_data/images/p004.jpg";
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

    public String getPhoto() {
        return mPhoto;
    }

    public String getAspectRatio() {
        return mAspectRatio;
    }

    public String getPublishedDate() {
        return mPublishedDate;
    }

}
