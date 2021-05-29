package weather.app.live.update.forecast.models

import com.google.gson.annotations.SerializedName

data class CurrentAndForecastWeatherInfoModel(
    @SerializedName("lat") val lat : Double,
    @SerializedName("lon") val lon : Double,
    @SerializedName("timezone") val timezone : String,
    @SerializedName("timezone_offset") val timezone_offset : Int,
    @SerializedName("current") val current : Current,
    @SerializedName("hourly") val hourly : List<Hourly>,
    @SerializedName("daily") val daily : List<Daily>,
    @SerializedName("alerts") val alerts : List<Alerts>
)