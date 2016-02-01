package com.example.xyzreader.retrofit;


import com.example.xyzreader.model.Article;
import com.example.xyzreader.model.Articles;

import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;;

public interface ApiService {
    //Don't start @Urls with / as recommended by
    //http://inthecheesefactory.com/blog/retrofit-2.0/en
    //Recommended by http://inthecheesefactory.com/blog/retrofit-2.0/en
    //When Retrofit declaration includes a default
    //BaseUrl, don't start GET/POST Urls with /
    //The BaseUrl should include the / at the end
    @GET("data.json")
    Call<List<Article>> getArticles();

}
