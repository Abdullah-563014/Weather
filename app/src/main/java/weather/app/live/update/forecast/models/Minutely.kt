package weather.app.live.update.forecast.models

import com.google.gson.annotations.SerializedName

data class Minutely(
    @SerializedName("dt") val dt : Long,
    @SerializedName("precipitation") val precipitation : Double
)
