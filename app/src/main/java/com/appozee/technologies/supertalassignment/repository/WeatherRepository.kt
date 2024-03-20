package com.appozee.technologies.supertalassignment.repository

import com.appozee.technologies.supertalassignment.api.WeatherAPI
import com.appozee.technologies.supertalassignment.model.rain.RainResponse
import com.appozee.technologies.supertalassignment.model.weather.WeatherResponse
import com.appozee.technologies.supertalassignment.utils.Constants.API_KEY
import retrofit2.Response
import javax.inject.Inject

/**
 * Repository class responsible for fetching weather and rain data.
 * @param weatherAPI The WeatherAPI instance used for network requests.
 */
class WeatherRepository @Inject constructor(
    private val weatherAPI: WeatherAPI
) {

    /**
     * Fetches weather data for a specific city.
     * @param cityName The name of the city for which weather data is requested.
     * @return A Response containing weather data.
     */
    suspend fun getWeatherData(cityName: String): Response<WeatherResponse> {
        // Call WeatherAPI's getWeatherData method with the provided city name and API key
        return weatherAPI.getWeatherData(cityName, API_KEY)
    }

    /**
     * Fetches rain data based on latitude and longitude.
     * @param latitude The latitude of the location for which rain data is requested.
     * @param longitude The longitude of the location for which rain data is requested.
     * @return A Response containing rain data.
     */
    suspend fun getRainData(latitude: String, longitude: String): Response<RainResponse> {
        // Call WeatherAPI's getRainData method with the provided latitude, longitude, and API key
        return weatherAPI.getRainData(latitude, longitude, API_KEY)
    }
}