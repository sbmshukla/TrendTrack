package com.sbmshukla.trendtrack.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.sbmshukla.trendtrack.R
import com.sbmshukla.trendtrack.databinding.FragmentArticleBinding
import com.sbmshukla.trendtrack.ui.NewsViewModel
import com.sbmshukla.trendtrack.ui.TrendTrackActivity


class ArticleFragment : Fragment(R.layout.fragment_article) {
    private lateinit var newsViewModel: NewsViewModel
    private val args:ArticleFragmentArgs by navArgs()

    private lateinit var binding: FragmentArticleBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentArticleBinding.bind(view)
        newsViewModel=(activity as TrendTrackActivity).newsViewModel

        val article=args.article

        binding.webView.apply {
            webViewClient= WebViewClient()
            article.url?.let {
                loadUrl(it)
            }
        }

        binding.fab.setOnClickListener {
            newsViewModel.addToFavorite(article)
            Snackbar.make(view,"Added to favorites...!!!",Snackbar.LENGTH_SHORT).show()
        }
    }
}