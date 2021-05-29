package weather.app.live.update.forecast.models

data class AppSettings(
        val versionName: String="",
        val versionMessage: String="",
        val weatherApiKey: String="",
        val adsFlag: String="",
        val adsIntervalInMinute: String="",
        val appOpenAdsCode: String=""
)
