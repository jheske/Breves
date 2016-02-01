package com.example.xyzreader.model;

import com.example.xyzreader.data.ItemsContract;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jill on 1/28/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Articles {
    List<Article> mArticles;

    public Articles(List<Article> articles) {
        this.mArticles = articles;
    }

    public List<Article> getArticles() {
        return mArticles;
    }

    public Article getArticle(int index) {
        if (mArticles == null)
            return null;
        else
            return mArticles.get(index);
    }
}
