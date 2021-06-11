package weather.app.live.update.forecast.models

import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("1h") val firstHour : Double
)
