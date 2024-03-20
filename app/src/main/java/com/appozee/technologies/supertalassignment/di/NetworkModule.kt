package com.appozee.technologies.supertalassignment.di

import com.appozee.technologies.supertalassignment.api.WeatherAPI
import com.appozee.technologies.supertalassignment.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Module providing network-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides a singleton instance of Retrofit.
     * @return A Retrofit instance configured with OkHttpClient and GsonConverterFactory.
     */
    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit {
        // Create OkHttpClient with customized timeouts
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS) // Adjust the timeout duration as needed
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()

        // Create and return Retrofit instance
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    /**
     * Provides a singleton instance of WeatherAPI.
     * @param retrofit The Retrofit instance used to create the WeatherAPI instance.
     * @return A singleton instance of WeatherAPI.
     */
    @Singleton
    @Provides
    fun provideWeatherAPI(retrofit: Retrofit): WeatherAPI {
        // Create and return WeatherAPI instance using Retrofit
        return retrofit.create(WeatherAPI::class.java)
    }
}