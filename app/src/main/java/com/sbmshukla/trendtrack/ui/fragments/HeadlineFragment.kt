package com.sbmshukla.trendtrack.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.sbmshukla.trendtrack.R
import com.sbmshukla.trendtrack.adapters.NewsAdapter
import com.sbmshukla.trendtrack.databinding.FragmentArticleBinding
import com.sbmshukla.trendtrack.databinding.FragmentHeadlineBinding
import com.sbmshukla.trendtrack.model.Source
import com.sbmshukla.trendtrack.ui.NewsViewModel
import com.sbmshukla.trendtrack.ui.TrendTrackActivity
import com.sbmshukla.trendtrack.utils.Constants
import com.sbmshukla.trendtrack.utils.Resource

class HeadlineFragment : Fragment(R.layout.fragment_headline) {

    private lateinit var retryButton: Button
    private lateinit var errorText: TextView
    private lateinit var itemHeadlineCard: CardView
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var binding:FragmentHeadlineBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding=FragmentHeadlineBinding.bind(view)
        itemHeadlineCard = view.findViewById(R.id.itemHeadlinesError)

        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val errorView: View = inflater.inflate(R.layout.item_error, null)

        retryButton = errorView.findViewById(R.id.retryButton)
        errorText = errorView.findViewById(R.id.errorText)

        newsViewModel = (activity as TrendTrackActivity).newsViewModel
        setUpHeadlineRecycler()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
/*            val bundle = Bundle().apply {
                putSerializable("article", it.copy(
                    id = it.id ?: 0,
                    author = it.author ?: "",
                    content = it.content?:"",
                    description=it.description?:"",
                    publishedAt=it.publishedAt?:"",
                    source = it.source ?: Source(id=it.source.id?:"", name=it.source.name?:""),
                    title=it.title?:"",
                    url = it.url?:"",
                    urlToImage =it.urlToImage ?:""
                ))
            }*/
            findNavController().navigate(R.id.action_headlineFragment_to_articleFragment, bundle)
        }
        newsViewModel.headlines.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success<*> -> {
                    hideProgressbar()
                    hideErrorMessage()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages =
                            newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.headlinePage == totalPages
                        if (isLastPage) {
                            binding.recyclerHeadlines.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is Resource.Error<*> -> {
                    hideProgressbar()
                    response.message?.let {
                        Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                        showErrorMessage(it)
                    }
                }

                is Resource.Loading<*> -> {
                    showProgressbar()
                }
            }
        }

        retryButton.setOnClickListener {
            newsViewModel.getHeadLines("in")
        }
    }

    private var isError = false
    private var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private fun hideProgressbar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressbar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage() {
        itemHeadlineCard.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessage(message: String) {
        itemHeadlineCard.visibility = View.VISIBLE
        errorText.text = message
        isError = true
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                newsViewModel.getHeadLines("in")
                isScrolling = false
            }

        }

        override fun onScrolled(recyclerView: RecyclerView, newState: Int, dy: Int) {
            super.onScrolled(recyclerView, newState, dy)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun setUpHeadlineRecycler() {
        newsAdapter = NewsAdapter()
        binding.recyclerHeadlines.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HeadlineFragment.scrollListener)
        }
    }
}