package com.tretton37.twitter37.ui.home.tweetsscreen

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.tretton37.twitter37.data.tweets.TweetsDataSource
import com.tretton37.twitter37.data.tweets.TweetsDataSourceFactory
import com.tretton37.twitter37.data.webservice.NetworkState
import com.tretton37.twitter37.utils.AppConstants.Companion.EXECUTOR_THREADS
import com.tretton37.twitter37.utils.AppConstants.Companion.PAGE_SIZE
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.SearchService
import com.twitter.sdk.android.core.services.StatusesService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class TweetsViewModel @Inject constructor(private val statusService: StatusesService, private val searchService: SearchService) : ViewModel() {

    private val executor: ExecutorService = Executors.newFixedThreadPool(EXECUTOR_THREADS)

    val tweetsList: LiveData<PagedList<Tweet>>
    val queryLiveData: MutableLiveData<String> = MutableLiveData()
    private val tweetsDataSourceFactoryLiveData: LiveData<TweetsDataSourceFactory>
    private val tweetsDataSourceLiveData: LiveData<TweetsDataSource>
    val networkState: LiveData<NetworkState>

    init {
        queryLiveData.value = ""

        tweetsDataSourceFactoryLiveData = Transformations.switchMap(queryLiveData) {
            val localDataSourceFactoryLiveData = MutableLiveData<TweetsDataSourceFactory>()
            localDataSourceFactoryLiveData.setValue(TweetsDataSourceFactory(it, statusService, searchService))
            localDataSourceFactoryLiveData
        }

        tweetsDataSourceLiveData = Transformations.switchMap(tweetsDataSourceFactoryLiveData) { it.tweetsDataSourceLiveData }

        networkState = Transformations.switchMap(tweetsDataSourceLiveData) { it.networkState }

        tweetsList = Transformations.switchMap(tweetsDataSourceFactoryLiveData) { dataSourceFactory ->
            val pagedListConfig = PagedList.Config.Builder().setEnablePlaceholders(false)
                    .setPageSize(PAGE_SIZE)
                    .build()
            LivePagedListBuilder(dataSourceFactory, pagedListConfig)
                    .setFetchExecutor(executor)
                    .build()
        }
    }

    fun refreshLoadedData() {
        val currentQuery = queryLiveData.value
        queryLiveData.value = currentQuery
    }

}