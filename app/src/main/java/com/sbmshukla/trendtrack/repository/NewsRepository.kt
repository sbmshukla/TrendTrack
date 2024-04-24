package com.sbmshukla.trendtrack.repository

import androidx.room.Query
import com.sbmshukla.trendtrack.api.RetrofitInstance
import com.sbmshukla.trendtrack.db.ArticleDatabase
import com.sbmshukla.trendtrack.model.Article

class NewsRepository(private val db: ArticleDatabase) {

    suspend fun getHeadlines(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getHeadlines(countryCode = countryCode, pageNumber = pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery = searchQuery, pageNumber = pageNumber)

    suspend fun upsert(article: Article)=db.getArticleDao().upsert(article = article)

    fun getFavouriteArticles()=db.getArticleDao().getAllArticles()

    suspend fun deleteFavouriteArticle(article: Article)=db.getArticleDao().deleteArticle(article = article)
}