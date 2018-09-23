package com.tretton37.twitter37.ui.home.tweetsscreen

import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import com.tretton37.twitter37.R
import com.tretton37.twitter37.data.webservice.NetworkState
import com.tretton37.twitter37.databinding.FragmentTweetsBinding
import com.tretton37.twitter37.ui.common.listeners.ListItemClickListener
import com.tretton37.twitter37.utils.AppConstants.Companion.EMPTY
import com.tretton37.twitter37.utils.ext.hideKeyboard
import com.twitter.sdk.android.core.models.Tweet
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


class TweetsFragment : Fragment(), ListItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var searchView: SearchView? = null
    private var queryTextListener: SearchView.OnQueryTextListener? = null

    val viewModel: TweetsViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(TweetsViewModel::class.java)
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.fragment_tweets, container, false)
        val toolbar = inflate.findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }
        return inflate
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

    private fun updateSearchQuery(query: String) {
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.menu_main, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchManager = activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))

        queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                Log.i("onQueryTextChange", newText)
                updateSearchQuery(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                Log.i("onQueryTextSubmit", query)
                activity?.hideKeyboard(activity)
                return true
            }
        }
        searchView?.findViewById<ImageView>(R.id.search_close_btn)?.setOnClickListener({
            activity?.hideKeyboard(activity)
            val et = searchView?.findViewById(R.id.search_src_text) as EditText
            //Clear the text from EditText view
            et.setText(EMPTY)
            //Clear query
            searchView?.setQuery(EMPTY, false)
            //Collapse the action view
            searchView?.onActionViewCollapsed()
            //Collapse the search widget
            searchItem?.collapseActionView()
        })
        searchView?.setOnQueryTextListener(queryTextListener)
        super.onCreateOptionsMenu(menu, inflater)
    }

}