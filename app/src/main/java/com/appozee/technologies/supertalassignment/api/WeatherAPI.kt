package com.appozee.technologies.supertalassignment.api

import com.appozee.technologies.supertalassignment.model.rain.RainResponse
import com.appozee.technologies.supertalassignment.model.weather.WeatherResponse
import com.appozee.technologies.supertalassignment.utils.Constants.GET_RAIN
import com.appozee.technologies.supertalassignment.utils.Constants.GET_WEATHER
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface representing the Weather API endpoints.
 */

interface WeatherAPI {

    /**
     * Fetches weather data for a specific city.
     * @param cityName The name of the city for which weather data is requested.
     * @param apiKey The API key used for authentication.
     * @return A Response containing weather data.
     */

    @GET(GET_WEATHER)
    suspend fun getWeatherData(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String
    ): Response<WeatherResponse>


    /**
     * Fetches rain data based on latitude and longitude.
     * @param latitude The latitude of the location for which rain data is requested.
     * @param longitude The longitude of the location for which rain data is requested.
     * @param apiKey The API key used for authentication.
     * @return A Response containing rain data.
     */

    @GET(GET_RAIN)
    suspend fun getRainData(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("appid") apiKey: String
    ): Response<RainResponse>
}