package weather.app.live.update.forecast.ui.main

import androidx.lifecycle.ViewModel
import weather.app.live.update.forecast.database_connection.MyApi

class MainActivityViewModel : ViewModel() {

    fun getCurrentAndForecastWeather(lat: String, lon: String, units: String, appId: String) =MyApi.invoke().getCurrentAndForecastWeather(lat,lon,units,appId)

    fun getCitySuggestion(key: String, q: String) =MyApi.invoke(" ").getCitySuggestion(key,q)



}