package com.tretton37.twitter37.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.tretton37.twitter37.ui.home.tweetsscreen.TweetsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by suyashg
 *
 * Viewmodel provider module
 */
@Module
interface ViewModelModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(TweetsViewModel::class)
    fun bindTweetsViewModel(viewModel: TweetsViewModel): ViewModel

}