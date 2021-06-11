package weather.app.live.update.forecast.models

import com.google.gson.annotations.SerializedName

data class Daily (

    @SerializedName("dt") val dt : Long,
    @SerializedName("sunrise") val sunrise : Long,
    @SerializedName("sunset") val sunset : Long,
    @SerializedName("moonrise") val moonrise : Long,
    @SerializedName("moonset") val moonset : Long,
    @SerializedName("moon_phase") val moon_phase : Double,
    @SerializedName("temp") val temp : Temp,
    @SerializedName("feels_like") val feels_like : Feels_like,
    @SerializedName("pressure") val pressure : Int,
    @SerializedName("humidity") val humidity : Int,
    @SerializedName("dew_point") val dew_point : Double,
    @SerializedName("wind_speed") val wind_speed : Double,
    @SerializedName("wind_deg") val wind_deg : Double,
    @SerializedName("wind_gust") val wind_gust : Double,
    @SerializedName("weather") val weather : List<Weather>,
    @SerializedName("clouds") val clouds : Int,
    @SerializedName("pop") val pop : Double,
    @SerializedName("rain") val rain : Double?,
    @SerializedName("uvi") val uvi : Double
)
