package com.tretton37.twitter37.di

import com.tretton37.twitter37.ui.home.tweetsscreen.TweetsFragment
import com.tretton37.twitter37.ui.image.imagedetailsscreen.ImageDetailsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class MainModule {

    @ContributesAndroidInjector
    internal abstract fun contributeTweetsFragmentInjector(): TweetsFragment

    @ContributesAndroidInjector
    internal abstract fun contributeImageDetailsFragmentInjector(): ImageDetailsFragment
}