package com.sbmshukla.trendtrack.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sbmshukla.trendtrack.databinding.ItemNewsBinding
import com.sbmshukla.trendtrack.model.Article

class NewsAdapter:RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size

    }

    private var onItemClickListener:((Article)->Unit)? = null

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentArticle:Article = differ.currentList[position]
        holder.bind(currentArticle)
        holder.itemView.setOnClickListener {
            onItemClickListener?.let { it(currentArticle) }
        }
    }

    fun setOnItemClickListener(listener: (Article)->Unit){
        onItemClickListener=listener
    }


    inner class ArticleViewHolder(private val binding:ItemNewsBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item: Article) {
            binding.article=item
            binding.executePendingBindings()
        }
    }

    class NewDiffUtil: DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: Article, newItem: Article): Any? {
            return super.getChangePayload(oldItem, newItem)
        }
    }

    val differ = AsyncListDiffer(this, NewDiffUtil())
}