package com.tretton37.twitter37.ui.home.tweetsscreen

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tretton37.twitter37.R
import com.tretton37.twitter37.data.webservice.NetworkState
import com.tretton37.twitter37.databinding.ItemNetworkStateBinding
import com.tretton37.twitter37.databinding.ItemTwitterListBinding
import com.tretton37.twitter37.ui.common.base.BaseHolder
import com.tretton37.twitter37.ui.common.listeners.ListItemClickListener
import com.tretton37.twitter37.ui.common.viewmodels.ItemNetworkStateViewModel
import com.twitter.sdk.android.core.models.Tweet

class TweetsAdapter(val itemClickListener: ListItemClickListener) : PagedListAdapter<Tweet, BaseHolder>(TweetsAdapter.DIFF_CALLBACK) {

    private var networkState: NetworkState = NetworkState.LOADING

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewHolder: BaseHolder

        when (viewType) {
            R.layout.item_twitter_list -> {
                val binding = ItemTwitterListBinding.inflate(layoutInflater, parent, false)
                viewHolder = TwitterItemViewHolder(binding, itemClickListener)
            }
            R.layout.item_network_state -> {
                val binding = ItemNetworkStateBinding.inflate(layoutInflater, parent, false)
                viewHolder = NetworkStateItemViewHolder(binding, itemClickListener)
            }
            else -> throw IllegalArgumentException("unknown view type")
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.item_twitter_list -> (holder as TwitterItemViewHolder).onBind(position)
            R.layout.item_network_state -> (holder as NetworkStateItemViewHolder).onBind(position)
        }
    }

    private fun hasExtraRow(): Boolean = networkState != NetworkState.LOADED

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == super.getItemCount()) {
            R.layout.item_network_state
        } else {
            R.layout.item_twitter_list
        }
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousItemCount = itemCount
        val previousState = networkState
        val previousExtraRow = hasExtraRow()
        networkState = newNetworkState ?: NetworkState.LOADING
        val newExtraRow = hasExtraRow()
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(previousItemCount)
            } else {
                notifyItemInserted(previousItemCount + 1)
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(previousItemCount - 1)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    inner class TwitterItemViewHolder(binding: ItemTwitterListBinding, itemClickListener: ListItemClickListener) : BaseHolder(binding.root) {
        private val binding: ItemTwitterListBinding

        init {
            itemView.setOnClickListener {
                itemClickListener.onClick(it, adapterPosition)
            }
            this.binding = binding
        }

        override fun onBind(position: Int) {
            val item = this@TweetsAdapter.getItem(position)!!
            val itemViewModel = TweetItemViewModel(item)
            binding.viewModel = itemViewModel
        }
    }

    inner class NetworkStateItemViewHolder(binding: ItemNetworkStateBinding, clickListener: ListItemClickListener) : BaseHolder(binding.root) {
        private val binding: ItemNetworkStateBinding
        private val clickListener: ListItemClickListener

        init {
            this.binding = binding
            this.clickListener = clickListener
        }

        override fun onBind(position: Int) {
            val networkStateObj = this@TweetsAdapter.networkState
            val itemNetworkStateViewModel = ItemNetworkStateViewModel(networkStateObj)
            binding.viewModel = itemNetworkStateViewModel
            binding.retryButton.setOnClickListener { view -> clickListener.onRetryClick(adapterPosition) }
        }
    }

    companion object {
        var DIFF_CALLBACK: DiffUtil.ItemCallback<Tweet> = object : DiffUtil.ItemCallback<Tweet>() {
            override fun areItemsTheSame(oldItem: Tweet, newItem: Tweet): Boolean {
                return if (oldItem.id == null || oldItem.id == 0L)
                    oldItem.idStr.equals(newItem.idStr)
                else
                    oldItem.id === newItem.id
            }

            override fun areContentsTheSame(oldItem: Tweet, newItem: Tweet): Boolean {
                return oldItem == newItem
            }
        }
    }
}