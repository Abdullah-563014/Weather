package weather.app.live.update.forecast.models

import com.google.gson.annotations.SerializedName

data class Hourly (

    @SerializedName("dt") val dt : Long,
    @SerializedName("temp") val temp : Double,
    @SerializedName("feels_like") val feels_like : Double,
    @SerializedName("pressure") val pressure : Int,
    @SerializedName("humidity") val humidity : Int,
    @SerializedName("dew_point") val dew_point : Double,
    @SerializedName("uvi") val uvi : Double,
    @SerializedName("clouds") val clouds : Int,
    @SerializedName("visibility") val visibility : Double,
    @SerializedName("wind_speed") val wind_speed : Double,
    @SerializedName("wind_deg") val wind_deg : Double,
    @SerializedName("wind_gust") val wind_gust : Double,
    @SerializedName("weather") val weather : List<Weather>,
    @SerializedName("pop") val pop : Double
)
