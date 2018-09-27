package com.tretton37.twitter37.ui.home

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import com.tretton37.twitter37.R
import com.tretton37.twitter37.databinding.ActivityHomeBinding
import com.tretton37.twitter37.ui.common.base.BaseActivity
import com.tretton37.twitter37.ui.home.tweetsscreen.TweetsFragment
import com.tretton37.twitter37.ui.home.tweetsscreen.customsearchview.SuggestionsSearchView
import com.tretton37.twitter37.ui.home.tweetsscreen.customsearchview.listeners.OnSuggestionsQueryTextListener
import com.tretton37.twitter37.ui.home.tweetsscreen.customsearchview.listeners.SuggestionsSearchViewListener
import com.tretton37.twitter37.utils.ext.findFragmentByTag
import com.tretton37.twitter37.utils.ext.replaceFragment


class HomeActivity : BaseActivity(), OnSuggestionsQueryTextListener,
        SuggestionsSearchViewListener, AdapterView.OnItemClickListener {

    companion object {
        val TAG = HomeActivity::class.java.getSimpleName()
    }

    private var suggestionsSearchView: SuggestionsSearchView? = null

    val binding: ActivityHomeBinding by lazy {
        DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentContainerId = binding.flHomeContent.id
        replaceFragment(fragmentContainerId, ::TweetsFragment, TweetsFragment.TAG)
        suggestionsSearchView = binding.ssvTweets
        suggestionsSearchView?.setOnQueryTextListener(this)
        suggestionsSearchView?.setSearchViewListener(this)
        suggestionsSearchView?.setOnItemClickListener(this)
        setSupportActionBar(binding.toolbar)
    }

    override fun onResume() {
        super.onResume()
        binding.ssvTweets.activityResumed();
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId()
        when (id) {
            R.id.action_search -> {
                // Open the search view on the menu item click.
                binding.ssvTweets.openSearch()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        Log.i(TAG, "onQueryTextChange:" + query)
        updateSearchQuery(query)
        return false
    }

    private fun updateSearchQuery(query: String) {
        val tweetsFragment = this.findFragmentByTag<TweetsFragment>(TweetsFragment.TAG)
        tweetsFragment.updateSearchQuery(query)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // Do something when the suggestion list is clicked.
        val suggestion = suggestionsSearchView?.getSuggestionAtPosition(position)
        suggestionsSearchView?.setQuery(suggestion, true)
    }

    override fun onSearchViewOpened() {
        // Do something once the view is open.
        Log.i(TAG, "onSearchViewOpened")
    }

    override fun onSearchViewClosed() {
        // Do something once the view is closed.
        Log.i(TAG, "onSearchViewClosed")
    }

    override fun onBackPressed() {
        if (suggestionsSearchView?.isOpen()!!) {
            // Close the search on the back button press.
            suggestionsSearchView?.closeSearch()
        } else {
            super.onBackPressed()
        }
    }
}