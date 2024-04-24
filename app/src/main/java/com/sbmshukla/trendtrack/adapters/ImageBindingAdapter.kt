package com.sbmshukla.trendtrack.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.sbmshukla.trendtrack.R

@BindingAdapter("imageFromUri")
fun ImageView.loadImageUrl(url: String?) {
    url?.let {
        Glide.with(this)
            .load(it)
            .placeholder(R.drawable.trend_track_icon)
            .error(R.drawable.ic_launcher_background)
            .into(this)
    }
}