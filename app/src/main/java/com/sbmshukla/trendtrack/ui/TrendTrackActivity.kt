package com.sbmshukla.trendtrack.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.sbmshukla.trendtrack.R
import com.sbmshukla.trendtrack.databinding.ActivityTrendTrackBinding
import com.sbmshukla.trendtrack.db.ArticleDatabase
import com.sbmshukla.trendtrack.repository.NewsRepository

class TrendTrackActivity : AppCompatActivity() {
    lateinit var newsViewModel: NewsViewModel
    private lateinit var newsRepository: NewsRepository
    private lateinit var binding:ActivityTrendTrackBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this@TrendTrackActivity,R.layout.activity_trend_track)

        newsRepository = NewsRepository(ArticleDatabase(this))
        val newsViewModelProviderFactory = NewsViewModelProviderFactory(application,newsRepository)
        newsViewModel= ViewModelProvider(this,newsViewModelProviderFactory)[NewsViewModel::class.java]


        val navHostFragment=supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        navController= navHostFragment.navController



        NavigationUI.setupWithNavController(binding.bottomNavigationView,navController)
//        binding.bottomNavigationView.setupWithNavController(navController)

        // Hide bottom navigation when navigating to SearchFragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.articleFragment) {
                binding.bottomNavigationView.visibility = View.GONE
            } else {
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item->
            when(item.itemId){
                R.id.HeadlinesFragment->{
                    navController.navigate(R.id.headlineFragment)
                    true
                }
                R.id.FavouritesFragment->{
                    navController.navigate(R.id.favouriteFragment)
                    true
                }
                R.id.SearchFragment->{
                    navController.navigate(R.id.searchFragment)
                    true
                }

                else -> {false}
            }
        }



    }
}