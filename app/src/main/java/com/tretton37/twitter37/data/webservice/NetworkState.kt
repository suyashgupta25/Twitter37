package com.tretton37.twitter37.data.webservice

class NetworkState(val status: Status, val msg: String) {
    companion object {

        val LOADED: NetworkState
        val LOADING: NetworkState

        init {
            LOADED = NetworkState(Status.SUCCESS, "Success")
            LOADING = NetworkState(Status.RUNNING, "Running")
        }
    }

}
