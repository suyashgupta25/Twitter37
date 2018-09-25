package com.tretton37.twitter37.ui.home.tweetsscreen

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tretton37.twitter37.R
import com.tretton37.twitter37.data.webservice.NetworkState
import com.tretton37.twitter37.databinding.FragmentTweetsBinding
import com.tretton37.twitter37.ui.common.listeners.ListItemClickListener
import com.twitter.sdk.android.core.models.Tweet
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class TweetsFragment : Fragment(), ListItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    companion object {
        val TAG = TweetsFragment::class.java.getSimpleName()
    }

    val viewModel: TweetsViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(TweetsViewModel::class.java)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tweets, container, false)
//        val toolbar = inflate.findViewById<Toolbar>(R.id.toolbar)
//        toolbar.setOnMenuItemClickListener {
//            onOptionsItemSelected(it)
//        }
//        //set toolbar appearance
//        toolbar.setBackgroundResource(R.color.colorPrimary)
//
//        //for crate home button
//        val activity = activity as AppCompatActivity?
//        activity!!.setSupportActionBar(toolbar)
//        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        return inflate
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding(view)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val binding = view?.let { DataBindingUtil.bind<FragmentTweetsBinding>(it) }

        val searchAdapter = TweetsAdapter(this)
        binding?.rvTweets?.swapAdapter(searchAdapter, true)
        viewModel.tweetsList.observe(this, Observer<PagedList<Tweet>> { searchAdapter.submitList(it) })
        viewModel.networkState.observe(this, Observer<NetworkState> { searchAdapter.setNetworkState(it) })
    }

    private fun initBinding(view: View) {
        val binding = DataBindingUtil.bind<FragmentTweetsBinding>(view)
        binding.let {
            it!!.viewModel = viewModel
            it.setLifecycleOwner(this)
        }
        initUI(binding!!)
    }

    private fun initUI(binding: FragmentTweetsBinding) {
        binding.srlTweets.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorAccent)
        binding.srlTweets.setOnRefreshListener {
            viewModel.refreshLoadedData()
            binding.srlTweets.isRefreshing = false
        }
    }

    fun updateSearchQuery(query: String) {
        val queryLiveData = viewModel.queryLiveData
        if (query != queryLiveData.value) {
            queryLiveData.value = query
        }
    }

    override fun onClick(view: View, position: Int) {

    }

    override fun onRetryClick(position: Int) {
        viewModel.refreshLoadedData()
    }
}