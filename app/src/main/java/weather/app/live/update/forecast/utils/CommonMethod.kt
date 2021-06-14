package weather.app.live.update.forecast.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import weather.app.live.update.forecast.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object CommonMethod {


    fun getTempValue(originalValue: Double): String {
        if (Constants.temperatureUnit.equals("F",true)) {
            return "${((originalValue*1.8)+32).roundToInt()} \u2109"
        } else if (Constants.temperatureUnit.equals("K",true)) {
            return "${(originalValue+273.15).roundToInt()} \u212A"
        } else {
            return "${originalValue.roundToInt()} \u2103"
        }
    }

    fun getPressureValue(originalValue: Int): String {
        if (Constants.pressureUnit.equals("mmHg",true)) {
            return "${(originalValue*0.75).roundToInt()} mmHg"
        } else if (Constants.pressureUnit.equals("atm",true)) {
            return "${(originalValue*0.0009869232667160128).roundToInt()} atm"
        } else if (Constants.pressureUnit.equals("mbar",true)) {
            return "$originalValue mbar"
        } else {
            return "$originalValue hPa"
        }
    }

    fun getSpeedValue(originalValue: Double): String {
        if (Constants.windSpeedUnit.equals("km/h",true)) {
            return "${(originalValue*3.6).roundToInt()} km/h"
        } else if (Constants.windSpeedUnit.equals("mi/h",true)) {
            return "${(originalValue*2.23694).roundToInt()} mi/h"
        } else {
            return "$originalValue m/s"
        }
    }

    fun getDistanceValue(originalValue: Double): String {
        try {
            val refinedValue: String?= getRefiningStringForParsing(originalValue.toString())
            if (refinedValue!=null) {
                val refinedDoubleValue: Double=refinedValue.toDouble()
                if (Constants.windSpeedUnit.equals("km/h",true)) {
                    val value: String=String.format("%.2f",(refinedDoubleValue/1000))
                    val convertedValue: String?= getRefiningStringForParsing(value)
                    if (convertedValue!=null) {
                        return "${convertedValue.toDouble()} kilo meter"
                    } else {
                        return "$value kilo meter"
                    }
                } else if (Constants.windSpeedUnit.equals("mi/h",true)) {
                    val value: String=String.format("%.2f",(refinedDoubleValue/1609))
                    val convertedValue: String?= getRefiningStringForParsing(value)
                    if (convertedValue!=null) {
                        return "${convertedValue.toDouble()} mile"
                    } else {
                        return "$value mile"
                    }
                } else {
                    val value: String=String.format("%.2f",refinedDoubleValue)
                    val convertedValue: String?= getRefiningStringForParsing(value)
                    if (convertedValue!=null) {
                        return "${convertedValue.toDouble()} meter"
                    } else {
                        return "$value meter"
                    }
                }
            } else {
                return "$originalValue meter"
            }
        } catch (e: Exception) {
            return "$originalValue meter"
        }
    }

    fun getPrecipitationValue(originalValue: Double): String {
        try {
            val refinedValue: String?= getRefiningStringForParsing(originalValue.toString())
            if (refinedValue!=null) {
                val refinedDoubleValue: Double=refinedValue.toDouble()
                if (Constants.precipitationUnit.equals("in",true)) {
                    val value: String=String.format("%.2f",(refinedDoubleValue/25.4))
                    val convertedValue: String?= getRefiningStringForParsing(value)
                    if (convertedValue!=null) {
                        return "${convertedValue.toDouble()} in"
                    } else {
                        return "$value in"
                    }
                } else {
                    val value: String=String.format("%.2f",refinedDoubleValue)
                    val convertedValue: String?= getRefiningStringForParsing(value)
                    if (convertedValue!=null) {
                        return "${convertedValue.toDouble()} mm"
                    } else {
                        return "$value mm"
                    }
                }
            } else {
                return "$originalValue mm"
            }
        } catch (e: Exception) {
            return "$originalValue mm"
        }
    }

    fun getRefiningStringForParsing(originalValue: String): String? {
        try {
            var unitValue=originalValue
            if (unitValue.contains(",")) {
                unitValue=unitValue.replace(",","",true)
            }
            if (unitValue.contains(" ")) {
                unitValue=unitValue.replace(" ","",true)
            }
            return unitValue
        } catch (e: Exception) {
            return null
        }
    }

    fun utcToDate(millisecond: Long, timeZone: String) : String{
        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat(Constants.dateFormatUnit, Locale.ENGLISH)
        simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone)
        return simpleDateFormat.format(millisecond * 1000)
    }

    fun utcToTime(millisecond: Long, timeZone: String) : String{
        var simpleDateFormat: SimpleDateFormat
        if (Constants.timeFormatUnit.equals("12 Hour",true)) {
            simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        } else {
            simpleDateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        }
        simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone)
        return simpleDateFormat.format(millisecond * 1000)
    }

    fun utcToHour(millisecond: Long, timeZone: String): String{
        var simpleDateFormat: SimpleDateFormat
        if (Constants.timeFormatUnit.equals("12 Hour",true)) {
            simpleDateFormat = SimpleDateFormat("hh a", Locale.ENGLISH)
        } else {
            simpleDateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        }
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

    fun getWaqiToMessage(context: Context, value: Int): String {
        if (value in 0..50) {
            return "${context.resources.getString(R.string.waqi_first_message)}"
        } else if (value in 51..100) {
            return "${context.resources.getString(R.string.waqi_second_message)}"
        } else if (value in 101..150) {
            return "${context.resources.getString(R.string.waqi_third_message)}"
        } else if (value in 151..200) {
            return "${context.resources.getString(R.string.waqi_fourth_message)}"
        } else if (value in 201..300) {
            return "${context.resources.getString(R.string.waqi_fifth_message)}"
        } else {
            return "${context.resources.getString(R.string.waqi_sixth_message)}"
        }
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
            val appIntent: Intent= Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=$appPackageName"))
            appIntent.setPackage("com.android.vending")
            context.startActivity(appIntent)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(Intent.createChooser(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")),context.resources.getString(R.string.choose_one)))
        }
    }

    fun isRightToShowAdsAppOpenAds(): Boolean {
        val dateDifference: Long = Date().time - Constants.lastAppOpenAdsShownTime
        val numMilliSecondsForMinutes: Long = (60000*Constants.adsIntervalInMinute).toLong()
        return dateDifference > numMilliSecondsForMinutes
    }











}