package com.sbmshukla.trendtrack.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sbmshukla.trendtrack.model.Article
import com.sbmshukla.trendtrack.model.NewsResponse
import com.sbmshukla.trendtrack.repository.NewsRepository
import com.sbmshukla.trendtrack.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app: Application, val newsRepository: NewsRepository) : AndroidViewModel(app) {
    val headlines: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headlinePage = 1
    private var headlineResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    private var searchNewsResponse: NewsResponse? = null
    private var newSearchQuery: String? = null
    private var oldSearchQuery: String? = null

    init {
        getHeadLines("in")
    }

    fun getHeadLines(countryCode: String) = viewModelScope.launch {
        headlinesInternet(countryCode)
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }

    private fun handleHeadlineResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                headlinePage++
                if (headlineResponse == null) {
                    headlineResponse = resultResponse
                } else {
                    val oldArticles = headlineResponse?.articles
                    val newArticles = resultResponse.articles

                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(headlineResponse ?: resultResponse)
            }

        }
        return Resource.Error(message = response.message())
    }

    private fun handleSearchResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (searchNewsResponse == null && oldSearchQuery != newSearchQuery) {
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultResponse
                } else {
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles

                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }

        }
        return Resource.Error(message = response.message())
    }

    fun addToFavorite(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getFavorite() = newsRepository.getFavouriteArticles()

    fun deleteFavoriteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteFavouriteArticle(article)
    }

    private fun isInternetConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager?.run {
            val capabilities = getNetworkCapabilities(activeNetwork)
            capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            ) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
        } ?: false
    }

    private suspend fun headlinesInternet(countryCode: String) {
        headlines.postValue(Resource.Loading())
        try {
            if (isInternetConnected(this.getApplication())) {
                val response = newsRepository.getHeadlines(countryCode, headlinePage)
                headlines.postValue(handleHeadlineResponse(response))
            } else {
                headlines.postValue(Resource.Error(message = "No Internet connection...!!!"))
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> headlines.postValue(Resource.Error(message = "Unable to connect...!!!"))
                else -> headlines.postValue(Resource.Error(message = "No Signal...!!!"))
            }
        }
    }


    private suspend fun searchNewsInternet(searchQuery: String) {
        searchNewsResponse = null
        newSearchQuery = searchQuery
        searchNews.postValue(Resource.Loading())
        try {
            if (isInternetConnected(this.getApplication())) {
                val response = newsRepository.searchNews(newSearchQuery!!, searchNewsPage)
                searchNews.postValue(handleSearchResponse(response))
            } else {
                searchNews.postValue(Resource.Error(message = "No Internet connection...!!!"))
            }

        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error(message = "Unable to connect...!!!"))
                else -> searchNews.postValue(Resource.Error(message = "No Signal...!!!"))
            }
        }
    }
}