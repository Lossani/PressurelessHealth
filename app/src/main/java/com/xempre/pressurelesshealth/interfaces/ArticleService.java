package com.xempre.pressurelesshealth.interfaces;

import com.xempre.pressurelesshealth.models.Article;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ArticleService {
    @GET("health/articles/random/")
    public Call<Article> getRandomArticle();
}
