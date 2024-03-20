package com.appozee.technologies.supertalassignment.viewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.appozee.technologies.supertalassignment.model.rain.RainResponse
import com.appozee.technologies.supertalassignment.model.weather.WeatherResponse
import com.appozee.technologies.supertalassignment.repository.WeatherRepository
import com.appozee.technologies.supertalassignment.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    application: Application
) : AndroidViewModel(application) {

    /** RETROFIT */

    /**
     * MutableLiveData object to hold the network result of weather data fetching operation.
     * It emits instances of NetworkResult<WeatherResponse> to observe changes in the weather data.
     */
    var weatherResponse: MutableLiveData<NetworkResult<WeatherResponse>> = MutableLiveData()

    /**
     * LiveData object to observe changes in the weather data network result.
     * It exposes weatherResponse MutableLiveData object as LiveData to external classes.
     */
    val weatherResponseLiveData: LiveData<NetworkResult<WeatherResponse>> = weatherResponse

    /**
     * MutableLiveData object to hold the network result of rain data fetching operation.
     * It emits instances of NetworkResult<RainResponse> to observe changes in the rain data.
     */
    var rainResponse: MutableLiveData<NetworkResult<RainResponse>> = MutableLiveData()

    /**
     * LiveData object to observe changes in the rain data network result.
     * It exposes rainResponse MutableLiveData object as LiveData to external classes.
     */
    val rainResponseLiveData: LiveData<NetworkResult<RainResponse>> = rainResponse


    /**
     * Initiates the weather data fetching operation for the given city name.
     * It launches a coroutine in the viewModelScope to fetch weather data safely.
     * @param cityName The name of the city for which weather data is to be fetched.
     */

    fun getWeatherResponse(cityName: String) = viewModelScope.launch {
        getSafeWeather(cityName)
    }

    fun getRainResponse(latitude: String, longitude: String) = viewModelScope.launch {
        getSafeRain(latitude , longitude)
    }

    /**
     * Safely fetches weather data for the given city name.
     * It updates the weatherResponse LiveData with loading status and handles network errors gracefully.
     * @param cityName The name of the city for which weather data is to be fetched.
     */

    private suspend fun getSafeWeather(cityName: String) {
        weatherResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.getWeatherData(cityName)
                }
                weatherResponse.value = handleResponse(response)

            } catch (e: Exception) {
                val error = e.message.toString()
                weatherResponse.value = NetworkResult.Error("Data not found.")
            }
        } else {
            weatherResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }


    private suspend fun getSafeRain(latitude: String, longitude: String) {
        rainResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.getRainData(latitude , longitude )
                }
                rainResponse.value = handleResponseRain(response)

            } catch (e: Exception) {
                val error = e.message.toString()
                rainResponse.value = NetworkResult.Error("Data not found.")
            }
        } else {
            rainResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    /**
     * Handles the response received from the weather data API.
     * It parses the response and returns the appropriate NetworkResult.
     * @param response The response received from the weather data API.
     * @return The NetworkResult containing either the success data or error message.
     */

    private fun handleResponse(response: Response<WeatherResponse>): NetworkResult<WeatherResponse> {
        return try {
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("API_CHECK", "handleResponse: $data")
                    NetworkResult.Success(data)
                } else {
                    NetworkResult.Error("Empty response body")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody ?: response.message()
                val errorCode = response.code()

                when (response.code()) {
                    401 -> NetworkResult.Error("Unauthorized: $errorMessage")
                    403 -> NetworkResult.Error("Forbidden: $errorMessage")
                    404 -> NetworkResult.Error("Not found: $errorMessage")
                    408 -> NetworkResult.Error("Timeout: $errorMessage")
                    429 -> NetworkResult.Error("Too Many Requests: $errorMessage")
                    in 500..599 -> NetworkResult.Error("Server error: $errorMessage")
                    else -> NetworkResult.Error("Network error: $errorMessage")
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error("Exception: ${e.message}")
        }
    }


    private fun handleResponseRain(response: Response<RainResponse>): NetworkResult<RainResponse> {
        return try {
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Log.i("API_CHECK", "handleResponse: $data")
                    NetworkResult.Success(data)
                } else {
                    NetworkResult.Error("Empty response body")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = errorBody ?: response.message()
                val errorCode = response.code()

                when (response.code()) {
                    401 -> NetworkResult.Error("Unauthorized: $errorMessage")
                    403 -> NetworkResult.Error("Forbidden: $errorMessage")
                    404 -> NetworkResult.Error("Not found: $errorMessage")
                    408 -> NetworkResult.Error("Timeout: $errorMessage")
                    429 -> NetworkResult.Error("Too Many Requests: $errorMessage")
                    in 500..599 -> NetworkResult.Error("Server error: $errorMessage")
                    else -> NetworkResult.Error("Network error: $errorMessage")
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error("Exception: ${e.message}")
        }
    }


    /**
     * Checks if the device has an active internet connection.
     * @return True if the device has an active internet connection, otherwise False.
     */
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}