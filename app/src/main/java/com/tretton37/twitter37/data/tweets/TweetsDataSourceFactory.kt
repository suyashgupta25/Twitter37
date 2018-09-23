package com.tretton37.twitter37.data.tweets

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import android.arch.paging.DataSource.Factory
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.SearchService
import com.twitter.sdk.android.core.services.StatusesService

class TweetsDataSourceFactory(val query: String, private val webService: StatusesService, private val searchService: SearchService) : Factory<Long, Tweet>() {

    private val mutableLiveData: MutableLiveData<TweetsDataSource> = MutableLiveData()
    val tweetsDataSourceLiveData: LiveData<TweetsDataSource>
        get() = mutableLiveData

    override fun create(): DataSource<Long, Tweet> {
        val tweetsDataSource = TweetsDataSource(query, webService, searchService)
        mutableLiveData.postValue(tweetsDataSource)
        return tweetsDataSource
    }
}
