package com.tretton37.twitter37.di

import android.util.Log
import com.tretton37.twitter37.BaseApp
import com.tretton37.twitter37.BuildConfig
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.services.SearchService
import com.twitter.sdk.android.core.services.StatusesService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class NetworkModule {

    @Provides
    @Singleton
    fun buildTwitterStatusService(app: BaseApp): TwitterApiClient {
        val config = TwitterConfig.Builder(app.applicationContext)
                .logger(DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(TwitterAuthConfig(
                        BuildConfig.CONSUMER_API_KEY,
                        BuildConfig.CONSUMER_API_SECRET))
                .debug(true)
                .build()
        Twitter.initialize(config)
        return TwitterCore.getInstance().apiClient;
    }

    @Provides
    @Singleton
    fun provideTwitterStatusService(apiClient: TwitterApiClient): StatusesService {
        return apiClient.statusesService
    }

    @Provides
    @Singleton
    fun provideTwitterSearchService(apiClient: TwitterApiClient): SearchService {
        return apiClient.searchService
    }

}