package weather.app.live.update.forecast.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import weather.app.live.update.forecast.R
import java.text.SimpleDateFormat
import java.util.*

object CommonMethod {

    fun convertKelvinToCelsius(kelvin: Double): Double{
        return kelvin-273.15
    }

    // (meter/sec) to (mile/hour)
    fun convertMpsToMph(mps: Double): Double{
        return mps*2.237
    }

    // (meter) to (mile)
    fun convertMeterToMile(meter: Double): Double{
        return meter/1609
    }

    fun utcToDate(millisecond: Long, timeZone: String) : String{
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone)
        return simpleDateFormat.format(millisecond * 1000)
    }

    fun utcToTime(millisecond: Long, timeZone: String) : String{
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone)
        return simpleDateFormat.format(millisecond * 1000)
    }

    fun utcToOnlyHour(millisecond: Long, timeZone: String): String{
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("hh a", Locale.ENGLISH)
        simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone)
        return simpleDateFormat.format(millisecond * 1000)
    }

    fun utcToOnlyHourAs24Format(millisecond: Long, timeZone: String): String{
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("HH", Locale.ENGLISH)
        simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone)
        return simpleDateFormat.format(millisecond * 1000)
    }

    fun utcToOnlyMinute(millisecond: Long, timeZone: String): String{
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("mm", Locale.ENGLISH)
        simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone)
        return simpleDateFormat.format(millisecond * 1000)
    }

    fun utcToOnlyDateAndMonth(millisecond: Long, timeZone: String): String{
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("MM/dd", Locale.ENGLISH)
        simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone)
        return simpleDateFormat.format(millisecond * 1000)
    }

    fun utcToOnlyDayName(millisecond: Long, timeZone: String): String{
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
        simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone)
        return simpleDateFormat.format(millisecond * 1000)
    }

    fun windDegToDir(directionInDegrees: Double): String {
        var cardinalDirection: String? = null
        cardinalDirection = if (directionInDegrees in 348.75..360.0 ||
            directionInDegrees in 0.0..11.25
        ) {
            "North"
        } else if (directionInDegrees in 11.25..33.75) {
            "North North East"
        } else if (directionInDegrees in 33.75..56.25) {
            "North East"
        } else if (directionInDegrees in 56.25..78.75) {
            "East North East"
        } else if (directionInDegrees in 78.75..101.25) {
            "East"
        } else if (directionInDegrees in 101.25..123.75) {
            "East South East"
        } else if (directionInDegrees in 123.75..146.25) {
            "South East"
        } else if (directionInDegrees in 146.25..168.75) {
            "South South East"
        } else if (directionInDegrees in 168.75..191.25) {
            "South"
        } else if (directionInDegrees in 191.25..213.75) {
            "South South West"
        } else if (directionInDegrees in 213.75..236.25) {
            "South West"
        } else if (directionInDegrees in 236.25..258.75) {
            "West South West"
        } else if (directionInDegrees in 258.75..281.25) {
            "West"
        } else if (directionInDegrees in 281.25..303.75) {
            "West North West"
        } else if (directionInDegrees in 303.75..326.25) {
            "North West"
        } else if (directionInDegrees in 326.25..348.75) {
            "North North West"
        } else {
            "?"
        }
        return " ($cardinalDirection)"
    }

    fun getWindyUrl(lat: Double, lon: Double): String {
        return "https://embed.windy.com/?$lat,$lon,5,wind"
    }

    fun getTargetGifIcon(value: String) : Int{
        var resourceId: Int
        when(value) {
            "01d" -> resourceId=R.drawable.icon_01d
            "01n" -> resourceId=R.drawable.icon_01n
            "02d" -> resourceId=R.drawable.icon_02d
            "02n" -> resourceId=R.drawable.icon_02n
            "03d" -> resourceId=R.drawable.icon_03d
            "03n" -> resourceId=R.drawable.icon_03n
            "04d" -> resourceId=R.drawable.icon_04d
            "04n" -> resourceId=R.drawable.icon_04n
            "09d" -> resourceId=R.drawable.icon_09d
            "09n" -> resourceId=R.drawable.icon_09n
            "10d" -> resourceId=R.drawable.icon_10d
            "10n" -> resourceId=R.drawable.icon_10n
            "11d" -> resourceId=R.drawable.icon_11d
            "11n" -> resourceId=R.drawable.icon_11n
            "13d" -> resourceId=R.drawable.icon_13d
            "13n" -> resourceId=R.drawable.icon_13n
            "50d" -> resourceId=R.drawable.icon_50d
            "50n" -> resourceId=R.drawable.icon_50n

            else -> {
                resourceId=R.drawable.icon_01d
            }
        }
        return resourceId
    }


    fun openAppLink(context: Context) {
        val appPackageName: String=context.applicationContext.packageName
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (e: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    fun isRightToShowAdsAppOpenAds(): Boolean {
        val dateDifference: Long = Date().time - Constants.lastAppOpenAdsShownTime
        val numMilliSecondsForMinutes: Long = (60000*Constants.adsIntervalInMinute).toLong()
        return dateDifference > numMilliSecondsForMinutes
    }









}