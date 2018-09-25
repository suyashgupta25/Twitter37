package com.tretton37.twitter37

import android.content.Context
import com.crashlytics.android.Crashlytics
import com.tretton37.twitter37.di.DaggerAppComponent
import com.tretton37.twitter37.di.NetworkModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.fabric.sdk.android.Fabric

open class BaseApp : DaggerApplication() {

    lateinit var androidInjector: AndroidInjector<out DaggerApplication>

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        androidInjector = DaggerAppComponent.builder()
                .application(this)
                .network(networkModule())
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
    }

    public override fun applicationInjector(): AndroidInjector<out DaggerApplication> = androidInjector

    protected open fun networkModule(): NetworkModule = NetworkModule()

}