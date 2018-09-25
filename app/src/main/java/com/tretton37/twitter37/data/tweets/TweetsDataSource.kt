package com.tretton37.twitter37.data.tweets

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import android.util.Log
import com.tretton37.twitter37.data.webservice.NetworkState
import com.tretton37.twitter37.data.webservice.Status
import com.tretton37.twitter37.utils.AppConstants.Companion.HTTP_OK
import com.tretton37.twitter37.utils.AppConstants.Companion.SEARCH_RESULT_TYPE
import com.tretton37.twitter37.utils.AppConstants.Companion.USER_LANGUAGE
import com.tretton37.twitter37.utils.AppConstants.Companion.USER_SCREEN_NAME
import com.twitter.sdk.android.core.models.Search
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.SearchService
import com.twitter.sdk.android.core.services.StatusesService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private typealias TweetsLoadedCallback = (List<Tweet>) -> Unit

class TweetsDataSource internal constructor(private val query: String, private val statusesService: StatusesService, private val searchService: SearchService) : PageKeyedDataSource<Long, Tweet>() {
    private val mNetworkState: MutableLiveData<NetworkState> = MutableLiveData()
    val networkState: LiveData<NetworkState>
        get() = mNetworkState

    override fun loadInitial(params: PageKeyedDataSource.LoadInitialParams<Long>, callback: PageKeyedDataSource.LoadInitialCallback<Long, Tweet>) {
        loadTweets(null, params.requestedLoadSize) {
            if (it.isNotEmpty()) {
                callback.onResult(it, null, it.get(it.lastIndex).id)
            } else {
                callback.onResult(it, null, null)
            }
        }
    }

    override fun loadBefore(params: PageKeyedDataSource.LoadParams<Long>, callback: PageKeyedDataSource.LoadCallback<Long, Tweet>) {
        //Implementation not needed. There is no data before initial page loaded.
    }

    override fun loadAfter(params: PageKeyedDataSource.LoadParams<Long>, callback: PageKeyedDataSource.LoadCallback<Long, Tweet>) {
        val maxId = params.key
        loadTweets(maxId, params.requestedLoadSize) {
            callback.onResult(it, maxId - 1)
        }
    }

    private fun loadTweets(maxId: Long?, pageSize: Int, callback: TweetsLoadedCallback) {
        Log.d(TAG, "Loading Page $maxId maxId size $pageSize")
        mNetworkState.postValue(NetworkState.LOADING)

        if (query.isEmpty()) {
            getTweetsForUser(maxId, pageSize, callback)
        } else {
            searchTweets(query, maxId, pageSize, callback)
        }
    }

    private fun getTweetsForUser(maxId: Long?, pageSize: Int, callback: TweetsLoadedCallback) {
        val userTimeline = statusesService.userTimeline(null, USER_SCREEN_NAME, pageSize, null,
                maxId, true, false, false, true)
        userTimeline.enqueue(object : Callback<List<Tweet>> {
            override fun onResponse(call: Call<List<Tweet>>, response: Response<List<Tweet>>) {
                if (response.isSuccessful && response.code() == HTTP_OK) {
                    val tweets = response.body()
                    if (tweets != null) {
                        Log.d(TAG, "Success:" + tweets.size.toString())
                        postLoadedData(tweets, callback)
                    } else {
                        postNetworkError(response.message())
                    }
                } else {
                    Log.e("Error:", response.message())
                    postNetworkError(response.message())
                }
            }

            override fun onFailure(call: Call<List<Tweet>>, t: Throwable) {
                Log.e(TAG, "Error:" + t.message)
                postNetworkError(t.message)
            }
        })
    }

    private fun searchTweets(query: String, maxId: Long?, pageSize: Int, callback: TweetsLoadedCallback) {
        val searchTimeline = searchService.tweets(query, null, USER_LANGUAGE, USER_LANGUAGE,
                SEARCH_RESULT_TYPE, pageSize, null, null, maxId, true)
        searchTimeline.enqueue(object : Callback<Search> {
            override fun onResponse(call: Call<Search>, response: Response<Search>) {
                if (response.isSuccessful && response.code() == HTTP_OK) {
                    val search = response.body()
                    val tweets = search?.tweets
                    if (tweets != null) {
                        if (query.isNotEmpty() && tweets.isEmpty()) {
                            postNetworkError("No results found for '" + query + "'")
                            return
                        }
                        Log.d(TAG, "Success:" + tweets.size.toString())
                        postLoadedData(tweets, callback)
                    } else {
                        postNetworkError(response.message())
                    }
                } else {
                    Log.e(TAG, "Error:" + response.message())
                    postNetworkError(response.message())
                }
            }

            override fun onFailure(call: Call<Search>, t: Throwable) {
                Log.e(TAG, "Error:" + t.message)
                postNetworkError(t.message)
            }
        })
    }

    private fun postLoadedData(tweets: List<Tweet>, callback: TweetsLoadedCallback) {
        callback.invoke(tweets)
        mNetworkState.postValue(NetworkState.LOADED)
    }

    private fun postNetworkError(errorMessage: String?) {
        Log.e(TAG, "API CALL:" + errorMessage)
        mNetworkState.postValue(NetworkState(Status.FAILED, errorMessage ?: "Unknown error"))
    }

    companion object {
        private val TAG = TweetsDataSource::class.java.getSimpleName()
    }

}
