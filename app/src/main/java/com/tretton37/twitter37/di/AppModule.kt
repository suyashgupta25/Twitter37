package com.tretton37.twitter37.di

import dagger.Module

@Module(includes = [NetworkModule::class, ViewModelModule::class])
open class AppModule {
    //USE it for app level modules for e.g. Database
}