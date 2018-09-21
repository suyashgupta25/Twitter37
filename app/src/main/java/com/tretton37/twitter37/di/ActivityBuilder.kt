package com.tretton37.twitter37.di

import com.tretton37.twitter37.ui.home.HomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [MainModule::class])
    internal abstract fun contributeHomeInjector(): HomeActivity

}