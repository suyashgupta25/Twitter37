package com.tretton37.twitter37.ui.common.listeners

import android.view.View

interface ListItemClickListener {
    fun onClick(view: View, position: Int)

    fun onRetryClick(position: Int)
}
