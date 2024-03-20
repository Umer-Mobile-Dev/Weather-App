package com.appozee.technologies.supertalassignment


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.appozee.technologies.supertalassignment.databinding.ActivityMainBinding
import com.appozee.technologies.supertalassignment.utils.NetworkResult
import com.appozee.technologies.supertalassignment.utils.getCurrentTimeHHmm
import com.appozee.technologies.supertalassignment.utils.parseToHHmm
import com.appozee.technologies.supertalassignment.utils.showToast
import com.appozee.technologies.supertalassignment.utils.toFormattedCelsiusString
import com.appozee.technologies.supertalassignment.utils.toFormattedDateString
import com.appozee.technologies.supertalassignment.viewModel.MainViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MyBottomSheetFragment.BottomSheetListener {

    companion object {
        val TAG = MainActivity::class.simpleName
    }

    private lateinit var mainViewModel: MainViewModel
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lat: Double? = null
    private var lng: Double? = null
    private var city: String? = null
    private var country: String? = null
    private var searchData: Boolean = false
    private val iconDrawableMap = mapOf(
        "50d" to R.drawable.fifteen_d,
        "50n" to R.drawable.fifteen_n,
        "13d" to R.drawable.thirteen_d,
        "13n" to R.drawable.thirteen_n,
        "11d" to R.drawable.eleven_d,
        "11n" to R.drawable.eleven_n,
        "10d" to R.drawable.ten_d,
        "10n" to R.drawable.ten_n,
        "09d" to R.drawable.zero_nine_d,
        "09n" to R.drawable.zero_nine_n,
        "04d" to R.drawable.zero_four_d,
        "04n" to R.drawable.zero_four_n,
        "03d" to R.drawable.zero_three_d,
        "03n" to R.drawable.zero_three_n,
        "02d" to R.drawable.zero_two_d,
        "02n" to R.drawable.zero_two_n,
        "01d" to R.drawable.zero_one_d,
        "01n" to R.drawable.zero_one_n
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setupUI()
        getCurrentLocation()
    }



    /**
     * Sets up the UI components and defines their behavior.
     * This function initializes various UI elements and defines click listeners.
     */
    @SuppressLint("SetTextI18n")
    private fun setupUI() {

        // Format and set the current date in the TextView
        val format1 = SimpleDateFormat("EEEE, MMMM dd  ")
        val date = format1.format(Date())
        binding.tvDate.text = date

        // Set onClickListener for refresh icon to fetch weather data again
        binding.ivRefresh.setOnClickListener {
            getWeatherFromApi()
        }

        // Set onClickListener for search icon to show the bottom sheet for city search
        binding.ivSearch.setOnClickListener {
            showBottomSheet()
        }

        // Observe the LiveData for weather response
        mainViewModel.weatherResponseLiveData.observe(this) { response ->
            when (response) {
                    // Handle successful response
                is NetworkResult.Success -> {
                    binding.progressBar.isVisible = false
                    Log.i(TAG, "requestApiData called Success!")
                    response.data?.let { data ->
                        Log.i(TAG, "Show Data $data")
                        data.let { result->
                            if (searchData){
                                binding.tvLocation.text = "${result.name}, ${result.sys?.country}"
                                searchData = false
                            }
                            binding.tvTemperature.text = "${result.main?.temp.toFormattedCelsiusString()} °c"
                            binding.tvMostSunny.text = result.weather?.get(0)?.main ?: ""
                            binding.tvFeelLike.text = "${result.main?.feels_like.toFormattedCelsiusString()} °c"
                            binding.tvTempUp.text = "${result.main?.temp_max.toFormattedCelsiusString()} °c"
                            binding.tvTempDown.text = "${result.main?.temp_min.toFormattedCelsiusString()} °c"
                            binding.tvTempHumidity.text = "${result.main?.humidity.toString()} %"
                            binding.tvTempPressure.text = "${result.main?.pressure.toString()} mb"
                            binding.tvTempWind.text = "${result.wind?.speed.toString()} Km/h"
                            binding.tvTempUvIndex.text = "${result.wind?.speed.toString()} Km/h"
                            binding.tvTempVisibility.text = "${result.visibility} m"
                            val icon = result.weather?.get(0)?.icon
                            val drawableResId = iconDrawableMap[icon] ?: R.drawable.zero_one_n
                            binding.ivTempIcon.setImageResource(drawableResId)

                            val valSunRise: Long = result.sys?.sunrise ?: 0
                            binding.tvBottomDateAm.text = valSunRise.toFormattedDateString("hh:mm aa")
                            val valSunSet: Long = result.sys?.sunset ?: 0
                            binding.tvBottomDatePm.text = valSunSet.toFormattedDateString("hh:mm aa")

                            val formattedDate = valSunRise.toFormattedDateString("hh:mm aa")
                            val formattedDate2 = valSunSet.toFormattedDateString("hh:mm aa")
                            val time = formattedDate.parseToHHmm()
                            val valSunRiseUp = time.toInt()
                            binding.seekBar.progress = 0
                            val timeMax = formattedDate2.parseToHHmm()
                            val valSunSetDown = timeMax.toInt()
                            val subBothValue = valSunSetDown - valSunRiseUp
                            binding.seekBar.maxProgress = subBothValue
                            val now = Calendar.getInstance()
                            val currentTime = now.getCurrentTimeHHmm().toInt()
                            var currentTimeShow = currentTime
                            currentTimeShow -= valSunRiseUp


                            /**
                             * Launches a coroutine in the main thread.
                             * Delays execution for a specified time before updating the seek bar progress.
                             *
                             * @param currentTimeShow The current time value to set as the seek bar progress.
                             */
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(10) // Delay for 10 milliseconds
                                binding.seekBar.progress = currentTimeShow
                            }
                            // Retrieve latitude and longitude coordinates from the result object and trigger a request for rain response.
                            result.coord?.let {
                                // Convert latitude and longitude to strings and pass them to the view model function to fetch rain response data.
                                mainViewModel.getRainResponse(it.lat!!.toString(), it.lon!!.toString()) }
                        }
                    }
                }
                is NetworkResult.Error -> {
                    // Handle error response
                    binding.progressBar.isVisible = false
                    Log.i(TAG, "requestApiData called Error!")
                    onSnack(binding.root,response.message.toString())
                }
                is NetworkResult.Loading -> {
                    // Handle loading state
                    binding.progressBar.isVisible = true
                    Log.i(TAG, "requestApiData called Loading!")
                }
            }
        }

        // Observe the LiveData for rain response
        mainViewModel.rainResponseLiveData.observe(this){response->
            when (response) {
                is NetworkResult.Success -> {
                    binding.progressBar.isVisible = false
                    Log.i(TAG, "requestApiData called Success!")
                    response.data?.let { data ->
                        Log.i(TAG, "Show Data $data")
                        data.let { result->
                            val popValue = result.daily?.get(0)?.pop ?: 0
                            val rainPercentage = (popValue.toString().toDouble() * 100).toInt()
                            binding.tvTempPrecipitation.text = "$rainPercentage %"
                        }
                    }
                }
                is NetworkResult.Error -> {
                    binding.progressBar.isVisible = false
                    Log.i(TAG, "requestApiData called Error!")
                    onSnack(binding.root,response.message.toString())
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.isVisible = true
                    Log.i(TAG, "requestApiData called Loading!")
                }
            }

        }
    }

    /**
     * Displays the bottom sheet for city search.
     * This function creates an instance of [MyBottomSheetFragment], shows it, and sets a listener.
     */

    private fun showBottomSheet() {
        val bottomSheetFragment = MyBottomSheetFragment()
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        bottomSheetFragment.setBottomSheetListener(this)
    }

    /**
     * Callback method triggered when the button in the bottom sheet is clicked.
     * It fetches weather data for the entered city name if the text is not empty.
     * @param text The city name entered in the bottom sheet.
     */
    override fun onButtonClicked(text: String) {
        if (text.isNotEmpty()){
            searchData = true
            city = text
            mainViewModel.getWeatherResponse(cityName = text)
        }

    }


    /**
     * Fetches weather data from the API based on the current city.
     * If the city is available, it triggers the retrieval of weather data for that city using [mainViewModel].
     * If the city is null, it displays a toast message indicating to check the location.
     */

    private fun getWeatherFromApi() {
        if (city !=null){
            mainViewModel.getWeatherResponse(city!!)
        }else{
            showToast("Please check your location")
        }
    }

    /**
     * Displays a Snackbar message with the provided [message] on the given [view].
     *
     * @param view The view on which the Snackbar should be displayed.
     * @param message The message to be shown in the Snackbar.
     */

    fun onSnack(view: View, message:String){
        val snack = Snackbar.make(view,""+message, Snackbar.LENGTH_LONG)
        snack.show()
    }

    /**
     * Requests and retrieves the current device location.
     * Handles location permission requests and updates UI accordingly.
     */
    private fun getCurrentLocation(){

        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this)

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions->
            when {
                permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                        permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    if (isLocationEnabled(this)){
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                        }
                        val result =fusedLocationClient.getCurrentLocation(
                            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            CancellationTokenSource().token
                        )
                        result.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val location = task.result
                                if (location != null) {
                                    // Location fetched successfully
                                    lat = location.latitude
                                    lng = location.longitude
                                    Log.i("LOCATION_RESULT", "Latitude: ${location.latitude}, Longitude: ${location.longitude}")

                                    // Perform reverse geocoding
                                    val geocoder = Geocoder(this, Locale.getDefault())
                                    try {
                                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                        if (addresses!!.isNotEmpty()) {
                                            val address = addresses[0]
                                            city = address.locality
                                            country = address.countryName
                                            binding.tvLocation.text = "$city, $country"
                                            getWeatherFromApi()
                                            Log.i("LOCATION_RESULT", "City: $city, Country: $country")
                                            // Now you have city and country name
                                        }
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                        // Handle geocoding error
                                    }
                                } else {
                                    showToast("Location not available")
                                }
                            } else {
                                showToast("Failed to fetch location")
                            }
                        }
                    } else{
                        showToast("Please turn ON location")
                        createLocationRequest()
                    }
                }
                else -> {
                    showToast("no Location access")
                }
            }
        }

        // Launch location permission request
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        binding.ivLocationMarker.setOnClickListener {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

    }


    /**
     * Creates a location request and checks the location settings.
     * Prompts the user to enable location services if necessary.
     */
    private fun createLocationRequest(){
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).setMinUpdateIntervalMillis(5000).build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client  = LocationServices.getSettingsClient(this)
        val task  = client.checkLocationSettings(builder.build())

        task.addOnCanceledListener {

        }

        task.addOnFailureListener {
            if (it is ResolvableApiException){
                try {
                    it.startResolutionForResult(
                        this,
                        100
                    )
                } catch (sendEx: java.lang.Exception){

                }
            }
        }
    }

    /**
     * Checks if location services are enabled on the device.
     *
     * @return True if location services are enabled, false otherwise.
     */
    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}