package weather.app.live.update.forecast.models

import com.google.gson.annotations.SerializedName

data class CitySuggestionModel(
    @SerializedName("id") val id : Int,
    @SerializedName("name") val name : String,
    @SerializedName("region") val region : String,
    @SerializedName("country") val country : String,
    @SerializedName("lat") val lat : Double,
    @SerializedName("lon") val lon : Double,
    @SerializedName("url") val url : String
)
