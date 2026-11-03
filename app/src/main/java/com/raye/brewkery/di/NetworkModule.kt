package com.raye.brewkery.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.raye.brewkery.data.remote.BrewkeryApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val contentTypeInterceptor = Interceptor { chain ->
            val response = chain.proceed(chain.request())
            // GitHub raw endpoints return text/plain. Rewrite or adjust header if needed.
            response
        }

        return OkHttpClient.Builder()
            .addInterceptor(contentTypeInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BrewkeryApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideBrewkeryApiService(retrofit: Retrofit): BrewkeryApiService {
        return retrofit.create(BrewkeryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}