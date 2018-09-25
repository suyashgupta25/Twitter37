package com.tretton37.twitter37.utils

import android.databinding.BindingAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tretton37.twitter37.R
import com.tretton37.twitter37.utils.ui.zoomableimageview.ZoomableImageView

@BindingAdapter("app:glideBannerImageUri")
fun setGlideBannerImageUri(imv: ImageView, url: String) {
    val transforms = RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.icon)
            .error(R.mipmap.icon)
    Glide.with(imv.context.applicationContext)
            .load(url)
            .apply(transforms)
            .into(imv)
}

@BindingAdapter("app:glideFullImageUri")
fun setGlideBannerImageUri(imv: ZoomableImageView, url: String) {
    val transforms = RequestOptions()
            .placeholder(R.mipmap.icon)
            .error(R.mipmap.icon)
    Glide.with(imv.context.applicationContext)
            .load(url)
            .apply(transforms)
            .into(imv)
}
