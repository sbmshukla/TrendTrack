package com.sbmshukla.trendtrack.ui.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sbmshukla.trendtrack.R
import com.sbmshukla.trendtrack.adapters.NewsAdapter
import com.sbmshukla.trendtrack.databinding.FragmentArticleBinding
import com.sbmshukla.trendtrack.databinding.FragmentFavouriteBinding
import com.sbmshukla.trendtrack.ui.NewsViewModel
import com.sbmshukla.trendtrack.ui.TrendTrackActivity
import com.sbmshukla.trendtrack.utils.Constants
import com.sbmshukla.trendtrack.utils.Resource

class FavouriteFragment : Fragment(R.layout.fragment_favourite) {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    private lateinit var binding: FragmentFavouriteBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavouriteBinding.bind(view)

        newsViewModel = (activity as TrendTrackActivity).newsViewModel
        setUpFavoriteRecycler()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_favouriteFragment_to_articleFragment, bundle)
        }

        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                newsViewModel.deleteFavoriteArticle(article)
                Snackbar.make(view, "Removed from favorites", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        newsViewModel.addToFavorite(article)
                    }
                    show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.recyclerFavourites)
        }
        newsViewModel.getFavorite().observe(viewLifecycleOwner){articles->
            newsAdapter.differ.submitList(articles)
        }
    }

    private fun setUpFavoriteRecycler() {
        newsAdapter = NewsAdapter()
        binding.recyclerFavourites.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}