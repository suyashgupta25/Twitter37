package com.tretton37.twitter37.di

import com.squareup.moshi.Moshi
import com.tretton37.twitter37.BaseApp
import com.tretton37.twitter37.BuildConfig
import com.tretton37.twitter37.utils.AppConstants.Companion.CONNECTION_TIMEOUT
import com.tretton37.twitter37.utils.AppConstants.Companion.READ_TIMEOUT
import com.tretton37.twitter37.utils.AppConstants.Companion.WRITE_TIMEOUT
import com.tretton37.twitter37.utils.ApplicationJsonAdapterFactory
import com.tretton37.twitter37.utils.InstantAdapter
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
open class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(app: BaseApp): OkHttpClient = buildOkHttpClient(app)

    open fun buildOkHttpClient(app: BaseApp): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .build()
    }

    @Singleton
    @Provides
    fun provideMoshi() = Moshi.Builder()
            .add(ApplicationJsonAdapterFactory.INSTANCE)
            .add(InstantAdapter.INSTANCE)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

}