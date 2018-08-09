package com.example.marius.musicbrainzforindi.api

import com.example.marius.musicbrainzforindi.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class AppServiceFactory {
  inline fun <reified T> createService(): T {
    return createRetrofit().create(T::class.java)
  }

  fun createRetrofit(): Retrofit {
    val okBuilder = OkHttpClient.Builder()
    if (BuildConfig.DEBUG) {
      val loggingInterceptor = HttpLoggingInterceptor()
      loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
      okBuilder.interceptors().add(loggingInterceptor)
    }
    okBuilder.connectTimeout(timeout, TimeUnit.SECONDS)
    okBuilder.readTimeout(timeout, TimeUnit.SECONDS)
    okBuilder.writeTimeout(timeout, TimeUnit.SECONDS)


    val retrofitBuilder = Retrofit.Builder()
    retrofitBuilder.addConverterFactory(MoshiConverterFactory.create(moshi))
    retrofitBuilder.addCallAdapterFactory(
      RxJava2CallAdapterFactory.create()
    )
    retrofitBuilder.client(okBuilder.build())
    retrofitBuilder.baseUrl(ApiConfig.apiBaseUrl)

    return retrofitBuilder.build()
  }


  companion object {
    val moshi = Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()
    const val timeout = 60L
  }
}