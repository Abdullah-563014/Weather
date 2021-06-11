package weather.app.live.update.forecast.utils

object Constants {

     val TAG: String="Abdullah"
     val MY_PERMISSIONS_ACCESS_FINE_LOCATION: Int=1
     val unitsName: String="metric"
     val sharedPreferenceName: String="MySharedPreference"
     val weatherDataKey: String="WeatherDataKey"
     val cityNameKey: String="CityNameKey"
     val unitSettingKey: String="UnitSettingKey"


//     =============================== for ads ===============================
     val lastAppOpenAdsShownTimeKey: String="LastAppOpenAdsShownTimeKey"
     var lastAppOpenAdsShownTime: Long=0




//     =============================== for database app setting values ===============================
     val versionNameKey: String="VersionNameKey"
     val versionMessageKey: String="VersionMessageKey"
     val weatherApiKey: String="WeatherApiKey"
     val adsFlagKey: String="AdsFlagKey"
     val adsIntervalInMinuteKey: String="AdsIntervalInMinuteKey"
     val appOpenAdsCodeKey: String="AppOpenAdsCodeKey"
     var appVersionName: String="1.0"
     var appVersionMessage: String="Found new Update. Your app is not updated. Please update to latest version."
     var weatherApi: String="9252f218dd5845adb8a112515212505"
     var adsFlag: Boolean=false
     var adsIntervalInMinute: Int=2880
     var appOpenAdsCode: String="ca-app-pub-3940256099942544/3419835294"



//     =============================== for analytics event ===============================
     val userAppRatingEventKey: String="user_app_rating"
     val appRatingNegativeBundleKey: String="rate_dialog_negative"
     val appRatingFiveStarBundleKey: String="rate_dialog_five_star"
     val appRatingLessThanFiveStarBundleKey: String="rate_dialog_less_than_five_star"
     val appRatingNeutralBundleKey: String="rate_dialog_neutral"
     val appOpenAdsEventKey: String="app_open_ads"
     val appOpenAdsLoadedBundleKey: String="app_open_ads_loaded"
     val appOpenAdsShownBundleKey: String="app_open_ads_shown"
     val appOpenAdsFailedBundleKey: String="app_open_ads_failed"



//     ============================== for unit setting key==============================
     val dateFormatUnitKey: String="DateUnitKey"
     val temperatureUnitKey: String="TemperatureUnitKey"
     val timeFormatUnitKey: String="TimeFormatUnitKey"
     val precipitationUnitKey: String="PrecipitationUnitKey"
     val windSpeedUnitKey: String="WindSpeedUnitKey"
     val pressureUnitKey: String="PressureUnitKey"
     var dateFormatUnit: String="dd MMM yyyy"
     var temperatureUnit: String="C"
     var timeFormatUnit: String="12 hour"
     var precipitationUnit: String="mm"
     var windSpeedUnit: String="km/h"
     var pressureUnit: String="hPa"




}