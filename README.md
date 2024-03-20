# Weather-App

# This is a simple weather app that allows users to check the current weather conditions for a specific location. The app retrieves weather data from the OpenWeatherMap API and displays it in a user-friendly interface.

# Design Decisions:

# MVVM Architecture: 
The app follows the Model-View-ViewModel (MVVM) architecture pattern to separate concerns and make the codebase more maintainable and testable.

# Hilt for Dependency Injection: 
Hilt is used for dependency injection to provide a clean and modular approach to managing dependencies.

# Coroutines: 
Coroutines are used for asynchronous programming to perform network requests and handle background tasks efficiently.

# Retrofit: 
Retrofit is used for making HTTP requests to the OpenWeatherMap API and handling JSON data conversion.

# LiveData: 
LiveData is used to observe data changes in the ViewModel and update the UI accordingly, ensuring a reactive UI experience.

# Android-SpinKit: 
Android-SpinKit library is used to provide loading indicators for better user experience while fetching data from the API.

# Libraries Used:

# Retrofit: 
For making HTTP requests and handling API responses.
# Hilt: 
For dependency injection.
# Coroutines: 
For asynchronous programming.
# LiveData: 
For observing data changes in the ViewModel.
# Android-SpinKit: 
For loading indicators.
