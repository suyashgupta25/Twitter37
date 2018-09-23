package com.tretton37.twitter37.ui.common.viewmodels

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.tretton37.twitter37.data.webservice.NetworkState
import com.tretton37.twitter37.data.webservice.Status
import com.tretton37.twitter37.utils.AppConstants.Companion.EMPTY

class ItemNetworkStateViewModel(networkState: NetworkState) {

    val showProgress = ObservableBoolean(false)
    val showError = ObservableBoolean(false)
    val errorMsg = ObservableField<String>(EMPTY)

    init {
        if (networkState.status === Status.RUNNING) { showProgress.set(true) }
            else { showProgress.set(false) }
        if (networkState.status === Status.FAILED) {
            showError.set(true)
            errorMsg.set(networkState.msg)
        } else {
            showError.set(false)
            errorMsg.set(EMPTY)
        }
    }

}