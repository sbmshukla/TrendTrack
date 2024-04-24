package com.sbmshukla.trendtrack.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.sbmshukla.trendtrack.R
import com.sbmshukla.trendtrack.databinding.ActivityTrendTrackBinding
import com.sbmshukla.trendtrack.db.ArticleDatabase
import com.sbmshukla.trendtrack.repository.NewsRepository

class TrendTrackActivity : AppCompatActivity() {
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsRepository: NewsRepository
    private lateinit var binding:ActivityTrendTrackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this@TrendTrackActivity,R.layout.activity_trend_track)

        newsRepository = NewsRepository(ArticleDatabase(this))
        val newsViewModelProviderFactory = NewsViewModelProviderFactory(application,newsRepository)
        newsViewModel= ViewModelProvider(this,newsViewModelProviderFactory)[NewsViewModel::class.java]


        val navHostFragment=supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController= navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }
}