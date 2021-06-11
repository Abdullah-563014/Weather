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
     val appRatingUserPropertyKey: String="app_rating_user_property"
     val appRatingBundleKey: String="app_rating"
     val userAppRatingEventKey: String="user_app_rating"



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