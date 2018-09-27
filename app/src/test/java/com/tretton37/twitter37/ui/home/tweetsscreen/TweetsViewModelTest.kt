package com.tretton37.twitter37.ui.home.tweetsscreen

import android.app.Application
import android.arch.lifecycle.Observer
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.tretton37.twitter37.BaseApp
import com.tretton37.twitter37.BuildConfig
import com.tretton37.twitter37.data.tweets.TweetsDataSourceFactory
import com.tretton37.twitter37.di.NetworkModule
import com.twitter.sdk.android.core.services.SearchService
import com.twitter.sdk.android.core.services.StatusesService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.concurrent.ExecutorService

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [21], application = Application::class)
open class TweetsViewModelTest {

    var module: NetworkModule = mock()
    var statusesService: StatusesService = mock()
    var searchService: SearchService = mock()
    var baseApp: BaseApp = mock()

    var executor: ExecutorService? = null
    var tweetsViewModel: TweetsViewModel? = null

    var observer: Observer<TweetsDataSourceFactory> = mock()

    @Before
    fun createDependencies() {
        `when`(baseApp.getApplicationContext()).thenReturn(RuntimeEnvironment.application)
        val twitterApiClient = module.buildTwitterStatusService(baseApp)
        statusesService = twitterApiClient.statusesService
        searchService = twitterApiClient.searchService
        executor = module.provideExecutor()
    }

    @Test
    fun testViewModelInit() {
        tweetsViewModel = TweetsViewModel(statusesService, searchService, executor!!)
        tweetsViewModel?.tweetsDataSourceFactoryLiveData?.observeForever(observer)
        tweetsViewModel?.refreshLoadedData()
        verify(observer, times(2)).onChanged(any())
    }

    @After
    fun releaseDependencies() {
        executor = null
        tweetsViewModel = null
    }
}