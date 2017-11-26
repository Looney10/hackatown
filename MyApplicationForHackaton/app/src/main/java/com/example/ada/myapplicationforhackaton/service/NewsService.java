package com.example.ada.myapplicationforhackaton.service;

import com.example.ada.myapplicationforhackaton.entities.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by student on 10/14/2017.
 */

public interface NewsService {

    @GET("https://newsapi.org/v1/articles?source=abc-news-au&apikey=2ecf7a8f8583401fa680efa914abf7e5")
    public Call<NewsResponse> getNews();
}
