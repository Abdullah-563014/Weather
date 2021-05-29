package weather.app.live.update.forecast.database_connection

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import weather.app.live.update.forecast.BuildConfig
import weather.app.live.update.forecast.models.CitySuggestionModel
import weather.app.live.update.forecast.models.CurrentAndForecastWeatherInfoModel

interface MyApi {

    @GET("onecall?")
    fun getCurrentAndForecastWeather(@Query("lat") lat: String, @Query("lon") lon: String, @Query("units") units: String, @Query("appid") appid: String): Call<CurrentAndForecastWeatherInfoModel>

    @GET("search.json?")
    fun getCitySuggestion(@Query("key") key: String, @Query("q") q: String): Call<List<CitySuggestionModel>>

    @GET("json")
    fun getIpInfo(): Call<JsonElement>




    companion object{
        operator fun invoke() : MyApi{
            return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }

        operator fun invoke(baseUrl: String) : MyApi{
            return Retrofit.Builder()
                .baseUrl(BuildConfig.WEATHER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }

        operator fun invoke(baseUrl: String,parameter: String) : MyApi{
            val baseUrl: String="http://ip-api.com/"
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }

}