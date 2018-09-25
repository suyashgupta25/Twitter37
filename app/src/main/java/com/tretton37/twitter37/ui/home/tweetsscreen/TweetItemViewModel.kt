package com.tretton37.twitter37.ui.home.tweetsscreen

import android.databinding.ObservableField
import com.tretton37.twitter37.utils.AppConstants.Companion.EMPTY
import com.tretton37.twitter37.utils.DateUtils
import com.twitter.sdk.android.core.models.Tweet

class TweetItemViewModel(tweet: Tweet?) {

    val text = ObservableField<String>(EMPTY)
    val retweetCount = ObservableField<String>(EMPTY)
    val favoriteCount = ObservableField<String>(EMPTY)
    val date = ObservableField<String>(EMPTY)
    val url = ObservableField<String>(EMPTY)

    init {
        text.set(tweet?.text)
        retweetCount.set(tweet?.retweetCount.toString())
        favoriteCount.set(tweet?.favoriteCount.toString())
        com.tretton37.twitter37.utils.DateUtils
        tweet?.let { date.set(DateUtils.formatDateString(it.createdAt)) }
        val mediaUrl = tweet?.entities?.media?.firstOrNull()?.mediaUrl
        mediaUrl?.let { url.set(mediaUrl) }
    }
}