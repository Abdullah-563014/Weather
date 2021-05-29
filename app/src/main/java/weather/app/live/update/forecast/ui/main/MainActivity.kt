package weather.app.live.update.forecast.ui.main


import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.MatrixCursor
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.BaseColumns
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.tianma8023.model.Time
import com.google.android.gms.ads.*
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import com.google.gson.Gson
import com.urapp.myappratinglibrary.AppRatingDialog
import com.urapp.myappratinglibrary.listener.RatingDialogListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import weather.app.live.update.forecast.BuildConfig
import weather.app.live.update.forecast.R
import weather.app.live.update.forecast.utils.*
import weather.app.live.update.forecast.utils.Constants.unitsName
import weather.app.live.update.forecast.utils.mpchart.MyValueFormatter
import weather.app.live.update.forecast.databinding.*
import weather.app.live.update.forecast.models.AppSettings
import weather.app.live.update.forecast.models.CitySuggestionModel
import weather.app.live.update.forecast.models.CurrentAndForecastWeatherInfoModel
import weather.app.live.update.forecast.services.MyLocationService
import weather.app.live.update.forecast.ui.daily_forecast.DailyForecastActivity
import weather.app.live.update.forecast.ui.hourly_forecast.HourlyForecastDetailsActivity
import java.util.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    View.OnClickListener, RatingDialogListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var sectionHourlyForecastConditionDividerViewBinding: SectionForecastConditionDividerViewBinding
    private lateinit var sectionDailyForecastConditionDividerViewBinding: SectionForecastConditionDividerViewBinding
    private lateinit var sectionHourlyForecastConditionImageViewBinding: SectionForecastConditionImageViewBinding
    private lateinit var sectionDailyForecastConditionImageViewBinding: SectionForecastConditionImageViewBinding
    private lateinit var sectionHourlyForecastConditionNameTextViewBinding: SectionForecastConditionNameTextViewBinding
    private lateinit var sectionDailyForecastConditionNameTextViewBinding: SectionForecastConditionNameTextViewBinding
    private lateinit var sectionHourlyForecastConditionTimeTextViewBinding: SectionForecastConditionTimeTextViewBinding
    private lateinit var sectionDailyForecastConditionTimeTextViewBinding: SectionForecastConditionTimeTextViewBinding
    private lateinit var sectionDailyForecastConditionDayNameTextViewBinding: SectionForecastConditionTimeTextViewBinding
    private var locationManager: LocationManager?=null
    private var fusedLocationProviderClient: FusedLocationProviderClient?=null
    private var myLocationService: MyLocationService?=null
    private var serviceConnection: ServiceConnection?=null
    private var boundStatus: Boolean = false
    private lateinit var searchView: SearchView
    private var latitude: Double=.0
    private var longitude: Double=.0
    private var cityName: String?=null
    private var cities: Array<Array<String>> = arrayOf(
        arrayOf(
            "CityName",
            "CountryName",
            "LatAndLon"
        )
    )
    private lateinit var cursorAdapter: SimpleCursorAdapter
    private val columnCityName: String="city_name"
    private val columnCountryName: String="country_name"
    private val columnLatAndLon: String="lat_and_lon"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        sectionHourlyForecastConditionDividerViewBinding=binding.hourlyForecastDividerSection
        sectionHourlyForecastConditionImageViewBinding=binding.hourlyForecastImageSection
        sectionHourlyForecastConditionNameTextViewBinding=binding.hourlyForecastNameSection
        sectionHourlyForecastConditionTimeTextViewBinding=binding.hourlyForecastTimeSection
        sectionDailyForecastConditionDividerViewBinding=binding.dailyForecastDividerSection
        sectionDailyForecastConditionImageViewBinding=binding.dailyForecastImageSection
        sectionDailyForecastConditionNameTextViewBinding=binding.dailyForecastNameSection
        sectionDailyForecastConditionTimeTextViewBinding=binding.dailyForecastTimeSection
        sectionDailyForecastConditionDayNameTextViewBinding=binding.dailyForecastDayNameSection
        viewModel=ViewModelProvider(this).get(MainActivityViewModel::class.java)
        setContentView(binding.root)




        initAll()

        setUpToolbar()

        loadPreviousData()

        if (haveInternet()) {
            checkLocationStatus()
        } else{
            longToast(resources.getString(R.string.no_internet_message))
        }

        if (savedInstanceState==null) {
            appFeedbackDialog().monitor()
            appFeedbackDialog().showRateDialogIfMeetsConditions()
            initServiceConnection()
            loadAppSettingFromDatabase()
            loadSettingFromStorage()
        }

        loadHints()





    }


    private fun initAll() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(
            applicationContext
        )
        binding.hourlyDetailsTextView.setOnClickListener(this)
        binding.dailyDetailsTextView.setOnClickListener(this)
    }

    private fun initServiceConnection() {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
                val binder = service as MyLocationService.MyLocationBinder
                myLocationService = binder.getMyBinder()
                boundStatus = true

                myLocationService!!.getLatAndLong().observe(this@MainActivity,
                    { t ->
                        t?.let {
                            if (it.status) {
                                latitude = it.lat
                                longitude = it.lon
                                getAddressInfo(it.lat, it.lon)
                                getCurrentAndForecastWeather()
                            }
                        }
                    })
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                boundStatus = false
            }
        }
    }

    private fun checkLocationStatus() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.MY_PERMISSIONS_ACCESS_FINE_LOCATION
            )
        } else if (locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager!!.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )) {
            if (boundStatus && serviceConnection!=null) {
                unbindService(serviceConnection!!)
                boundStatus = false
                serviceConnection=null
                checkLocationStatus()
            } else {
                initServiceConnection()
                try {
                    Intent(this, MyLocationService::class.java).also { intent ->
                        bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)
                    }
                } catch (e: Exception) {}
            }
        } else {
            showLocationSettingsDialog()
        }
    }

    private fun showLocationSettingsDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle(R.string.location_settings)
        alertDialog.setCancelable(false)
        alertDialog.setMessage(R.string.location_settings_message)
        alertDialog.setPositiveButton(R.string.location_settings_button) { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        alertDialog.setNegativeButton(R.string.dialog_cancel) { dialog, which ->
            longToast(resources.getString(R.string.location_cancel_message))
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun getAddressInfo(lat: Double, lan: Double) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(lat, lan, 3)
            val city: String? = addresses[0].getAddressLine(0)
            city?.let {
                cityName=it
                binding.currentCityNameTextView.text=cityName
                Coroutines.io {
                    SharedPreUtils.setStringToStorage(applicationContext, Constants.cityNameKey, it)
                }
            }
        } catch (e: Exception) {

        }
    }

    private fun getFeatureNameOrAdminAreaInfo(lat: Double, lan: Double) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(lat, lan, 3)
            var city: String?=null
            for (i in addresses.indices) {
                if (!addresses[i].locality.isNullOrEmpty()) {
                    city = addresses[i].locality
                    break
                }
                if (!addresses[i].subAdminArea.isNullOrEmpty()) {
                    city = addresses[i].subAdminArea
                    break
                }
                if (!addresses[i].adminArea.isNullOrEmpty()) {
                    city = addresses[i].adminArea
                    break
                }
                if (!addresses[i].countryName.isNullOrEmpty()) {
                    city = addresses[i].countryName
                    break
                }
            }
            city?.let {
                cityName=it
                binding.currentCityNameTextView.text=cityName
                Coroutines.io {
                    SharedPreUtils.setStringToStorage(applicationContext, Constants.cityNameKey, it)
                }
            }
        } catch (e: Exception) {

        }
    }

    private fun getCurrentAndForecastWeather() {
        binding.spinKitId.visibility=View.VISIBLE
        val response: Call<CurrentAndForecastWeatherInfoModel> =viewModel.getCurrentAndForecastWeather(
            latitude.toString(),
            longitude.toString(),
            unitsName,
            BuildConfig.API_KEY
        )
        response.enqueue(object : Callback<CurrentAndForecastWeatherInfoModel> {
            override fun onResponse(
                call: Call<CurrentAndForecastWeatherInfoModel>,
                currentAndForecastResponse: Response<CurrentAndForecastWeatherInfoModel>
            ) {
                if (currentAndForecastResponse.isSuccessful && currentAndForecastResponse.code() == 200) {
                    val currentAndForecast: CurrentAndForecastWeatherInfoModel? =
                        currentAndForecastResponse.body()
                    currentAndForecast?.let {
                        updateUi(it)
                        saveCurrentWeatherDataToStorage(it)
                        shortToast(resources.getString(R.string.data_updated_successfully))
                    }
                } else {
                    shortToast(resources.getString(R.string.weather_info_retrieve_filed))
                }
                binding.spinKitId.visibility = View.GONE
            }

            override fun onFailure(
                call: Call<CurrentAndForecastWeatherInfoModel>,
                t: Throwable
            ) {
                shortToast(resources.getString(R.string.failed_for) + t.message)
                binding.spinKitId.visibility = View.VISIBLE
            }

        })
    }

    private fun getLanLonUsingPlaceName(place: String) {
        val gc: Geocoder = Geocoder(this)
        try{
            val ads: List<Address> = gc.getFromLocationName(place, 3)
            if (ads.isNotEmpty()) {
                latitude=ads[0].latitude
                longitude=ads[0].longitude
                getFeatureNameOrAdminAreaInfo(ads[0].latitude, ads[0].longitude)
                getCurrentAndForecastWeather()
            }
        }catch (e: Exception) {

        }
    }

    private fun getCitySuggestion(q: String) {
        val call: Call<List<CitySuggestionModel>> =viewModel.getCitySuggestion(
            Constants.weatherApi,
            q
        )
        call.enqueue(object : Callback<List<CitySuggestionModel>> {
            override fun onResponse(
                call: Call<List<CitySuggestionModel>>,
                response: Response<List<CitySuggestionModel>>
            ) {
                if (response.isSuccessful && response.code() == 200) {
                    val resultList: List<CitySuggestionModel>? = response.body()
                    resultList?.let {
                        val cursor = MatrixCursor(
                            arrayOf(
                                BaseColumns._ID,
                                columnCityName,
                                columnCountryName,
                                columnLatAndLon
                            )
                        )
                        for (i in it.indices) {
                            cursor.addRow(
                                arrayOf(
                                    i,
                                    it[i].name,
                                    if (it[i].country.isEmpty()) it[i].region else it[i].country,
                                    "${it[i].lat},${it[i].lon}"
                                )
                            )
                        }
                        cursorAdapter.changeCursor(cursor)
                    }
                }
            }

            override fun onFailure(call: Call<List<CitySuggestionModel>>, t: Throwable) {

            }

        })

    }

    private fun updateUi(currentAndForecastWeatherInfoModel: CurrentAndForecastWeatherInfoModel) {
        binding.currentTemperatureTextView.text=currentAndForecastWeatherInfoModel.current.temp.roundToInt().toString()+" \u2103"
        binding.currentWindSpeedTextView.text="wind speed:- "+String.format(
            "%.2f", CommonMethod.convertMpsToMph(
                currentAndForecastWeatherInfoModel.current.wind_speed
            )
        )+" mile/hour"
        binding.currentDescriptionTextView.text=currentAndForecastWeatherInfoModel.current.weather[0].description
        binding.currentCityNameTextView.text=cityName

        binding.currentMaxAndMinTempTextView.text=currentAndForecastWeatherInfoModel.daily[0].temp.max.roundToInt().toString()+"\u00B0 /"+ currentAndForecastWeatherInfoModel.daily[0].temp.min.roundToInt().toString()+"\u00B0"
        try {
            Glide.with(this).asGif().load(
                CommonMethod.getTargetGifIcon(
                    currentAndForecastWeatherInfoModel.current.weather[0].icon
                )
            ).into(binding.currentWeatherConditionImageView)
        } catch (e: Exception) {}
        binding.currentSunriseTimeTextView.text=resources.getString(R.string.sunrise)+":- "+CommonMethod.utcToTime(
            currentAndForecastWeatherInfoModel.current.sunrise,
            currentAndForecastWeatherInfoModel.timezone
        )
        binding.currentSunsetTimeTextView.text=resources.getString(R.string.sunset)+":- "+CommonMethod.utcToTime(
            currentAndForecastWeatherInfoModel.current.sunset,
            currentAndForecastWeatherInfoModel.timezone
        )
        binding.currentDateTimeTextView.text=CommonMethod.utcToDate(
            currentAndForecastWeatherInfoModel.current.dt,
            currentAndForecastWeatherInfoModel.timezone
        )

        updateHourlyForecastSection(currentAndForecastWeatherInfoModel)
        updateDailyForecastSection(currentAndForecastWeatherInfoModel)
        updateCurrentWindSection(currentAndForecastWeatherInfoModel)
        updateCurrentSunsection(currentAndForecastWeatherInfoModel)
        updateCurrentWeatherTempDetailsSection(currentAndForecastWeatherInfoModel)
        updateCurrentWeatherFeelsLikeTempDetailsSection(currentAndForecastWeatherInfoModel)
        updateCurrentWeatherOthersDetailsSection(currentAndForecastWeatherInfoModel)
        updateCurrentWeatherRadarSection(currentAndForecastWeatherInfoModel)


    }


    private fun updateHourlyForecastSection(currentAndForecast: CurrentAndForecastWeatherInfoModel) {
        updateHourlyForecastLineChart(currentAndForecast)
        try {
            Glide.with(this).asGif().load(CommonMethod.getTargetGifIcon(currentAndForecast.hourly[0].weather[0].icon)).into(
                sectionHourlyForecastConditionImageViewBinding.forecastConditionImageView1
            )
            Glide.with(this).asGif().load(CommonMethod.getTargetGifIcon(currentAndForecast.hourly[1].weather[0].icon)).into(
                sectionHourlyForecastConditionImageViewBinding.forecastConditionImageView2
            )
            Glide.with(this).asGif().load(CommonMethod.getTargetGifIcon(currentAndForecast.hourly[2].weather[0].icon)).into(
                sectionHourlyForecastConditionImageViewBinding.forecastConditionImageView3
            )
            Glide.with(this).asGif().load(CommonMethod.getTargetGifIcon(currentAndForecast.hourly[3].weather[0].icon)).into(
                sectionHourlyForecastConditionImageViewBinding.forecastConditionImageView4
            )
            Glide.with(this).asGif().load(CommonMethod.getTargetGifIcon(currentAndForecast.hourly[4].weather[0].icon)).into(
                sectionHourlyForecastConditionImageViewBinding.forecastConditionImageView5
            )
            Glide.with(this).asGif().load(CommonMethod.getTargetGifIcon(currentAndForecast.hourly[5].weather[0].icon)).into(
                sectionHourlyForecastConditionImageViewBinding.forecastConditionImageView6
            )
            Glide.with(this).asGif().load(CommonMethod.getTargetGifIcon(currentAndForecast.hourly[6].weather[0].icon)).into(
                sectionHourlyForecastConditionImageViewBinding.forecastConditionImageView7
            )
        } catch (e: Exception) {}

        sectionHourlyForecastConditionNameTextViewBinding.forecastConditionNameOneTextView.text=currentAndForecast.hourly[0].weather[0].main
        sectionHourlyForecastConditionNameTextViewBinding.forecastConditionNameTwoTextView.text=currentAndForecast.hourly[1].weather[0].main
        sectionHourlyForecastConditionNameTextViewBinding.forecastConditionNameThreeTextView.text=currentAndForecast.hourly[2].weather[0].main
        sectionHourlyForecastConditionNameTextViewBinding.forecastConditionNameFourTextView.text=currentAndForecast.hourly[3].weather[0].main
        sectionHourlyForecastConditionNameTextViewBinding.forecastConditionNameFiveTextView.text=currentAndForecast.hourly[4].weather[0].main
        sectionHourlyForecastConditionNameTextViewBinding.forecastConditionNameSixTextView.text=currentAndForecast.hourly[5].weather[0].main
        sectionHourlyForecastConditionNameTextViewBinding.forecastConditionNameSevenTextView.text=currentAndForecast.hourly[6].weather[0].main

        sectionHourlyForecastConditionTimeTextViewBinding.forecastConditionTimeOneTextView.text=CommonMethod.utcToOnlyHour(
            currentAndForecast.hourly[0].dt,
            currentAndForecast.timezone
        )
        sectionHourlyForecastConditionTimeTextViewBinding.forecastConditionTimeTwoTextView.text=CommonMethod.utcToOnlyHour(
            currentAndForecast.hourly[1].dt,
            currentAndForecast.timezone
        )
        sectionHourlyForecastConditionTimeTextViewBinding.forecastConditionTimeThreeTextView.text=CommonMethod.utcToOnlyHour(
            currentAndForecast.hourly[2].dt,
            currentAndForecast.timezone
        )
        sectionHourlyForecastConditionTimeTextViewBinding.forecastConditionTimeFourTextView.text=CommonMethod.utcToOnlyHour(
            currentAndForecast.hourly[3].dt,
            currentAndForecast.timezone
        )
        sectionHourlyForecastConditionTimeTextViewBinding.forecastConditionTimeFiveTextView.text=CommonMethod.utcToOnlyHour(
            currentAndForecast.hourly[4].dt,
            currentAndForecast.timezone
        )
        sectionHourlyForecastConditionTimeTextViewBinding.forecastConditionTimeSixTextView.text=CommonMethod.utcToOnlyHour(
            currentAndForecast.hourly[5].dt,
            currentAndForecast.timezone
        )
        sectionHourlyForecastConditionTimeTextViewBinding.forecastConditionTimeSevenTextView.text=CommonMethod.utcToOnlyHour(
            currentAndForecast.hourly[6].dt,
            currentAndForecast.timezone
        )




    }

    private fun updateHourlyForecastLineChart(currentAndForecastModel: CurrentAndForecastWeatherInfoModel) {

        val values: ArrayList<Entry> = ArrayList()
        try{
            for (i in 1..7) {
                values.add(Entry(i.toFloat(), currentAndForecastModel.hourly[i].temp.toFloat()))
            }
        } catch (e: Exception) {

        }

        val set1: LineDataSet
        if (binding.hourlyForecastLineChart.data != null && binding.hourlyForecastLineChart.data.dataSetCount > 0) {
            set1 = binding.hourlyForecastLineChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
        } else {
            set1 = LineDataSet(values, "Hourly Forecast Data")
            set1.setDrawIcons(true)
            set1.color = Color.GREEN
            set1.setCircleColor(Color.RED)
            set1.valueTextColor=Color.WHITE
//            set1.fillColor=Color.GRAY
            set1.lineWidth = 3f
            set1.circleRadius = 5f
            set1.setDrawCircleHole(false)
            set1.valueTextSize = 13f
            set1.setDrawFilled(false)
            set1.disableDashedLine()
            set1.isHighlightEnabled=false
            set1.disableDashedHighlightLine()
            set1.setDrawHighlightIndicators(false)
            set1.valueFormatter=MyValueFormatter()
//            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
//            set1.formSize = 15f
//            val drawable = ContextCompat.getDrawable(this, R.drawable.weather_bg)
//            set1.fillDrawable = drawable
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set1)
            val data = LineData(dataSets)
            binding.hourlyForecastLineChart.data = data
        }
        binding.hourlyForecastLineChart.setTouchEnabled(false)
        binding.hourlyForecastLineChart.setPinchZoom(false)
        binding.hourlyForecastLineChart.setDrawGridBackground(false)
//        binding.hourlyForecastLineChart.setBackgroundColor(Color.YELLOW)
        val legend: Legend=binding.hourlyForecastLineChart.legend
        legend.isEnabled=false
//        binding.hourlyForecastLineChart.xAxis.setDrawGridLines(false)
        binding.hourlyForecastLineChart.xAxis.isEnabled=false
//        binding.hourlyForecastLineChart.axisLeft.setDrawGridLines(false)
        binding.hourlyForecastLineChart.axisLeft.isEnabled=false
//        binding.hourlyForecastLineChart.axisRight.setDrawGridLines(false)
        binding.hourlyForecastLineChart.axisRight.isEnabled=false
        binding.hourlyForecastLineChart.setDrawBorders(false)
        binding.hourlyForecastLineChart.description.isEnabled=false
        binding.hourlyForecastLineChart.data.notifyDataChanged()
        binding.hourlyForecastLineChart.notifyDataSetChanged()
        binding.hourlyForecastLineChart.invalidate()
    }

    private fun updateDailyForecastSection(currentAndForecast: CurrentAndForecastWeatherInfoModel) {
        updateDailyForecastMinTempLineChart(currentAndForecast)
        updateDailyForecastMaxTempLineChart(currentAndForecast)
        try {
            Glide.with(this).asGif().load(CommonMethod.getTargetGifIcon(currentAndForecast.daily[0].weather[0].icon)).into(
                sectionDailyForecastConditionImageViewBinding.forecastConditionImageView1
            )
            Glide.with(this).asGif().load(CommonMethod.getTargetGifIcon(currentAndForecast.daily[1].weather[0].icon)).into(
                sectionDailyForecastConditionImageViewBinding.forecastConditionImageView2
            )
            Glide.with(this).asGif().load(CommonMethod.getTargetGifIcon(currentAndForecast.daily[2].weather[0].icon)).into(
                sectionDailyForecastConditionImageViewBinding.forecastConditionImageView3
            )
            Glide.with(this).asGif().load(CommonMethod.getTargetGifIcon(currentAndForecast.daily[3].weather[0].icon)).into(
                sectionDailyForecastConditionImageViewBinding.forecastConditionImageView4
            )
            Glide.with(this).asGif().load(CommonMethod.getTargetGifIcon(currentAndForecast.daily[4].weather[0].icon)).into(
                sectionDailyForecastConditionImageViewBinding.forecastConditionImageView5
            )
            Glide.with(this).asGif().load(CommonMethod.getTargetGifIcon(currentAndForecast.daily[5].weather[0].icon)).into(
                sectionDailyForecastConditionImageViewBinding.forecastConditionImageView6
            )
            Glide.with(this).asGif().load(CommonMethod.getTargetGifIcon(currentAndForecast.daily[6].weather[0].icon)).into(
                sectionDailyForecastConditionImageViewBinding.forecastConditionImageView7
            )
        } catch (e: Exception) {}

        sectionDailyForecastConditionNameTextViewBinding.forecastConditionNameOneTextView.text=currentAndForecast.daily[0].weather[0].main
        sectionDailyForecastConditionNameTextViewBinding.forecastConditionNameTwoTextView.text=currentAndForecast.daily[1].weather[0].main
        sectionDailyForecastConditionNameTextViewBinding.forecastConditionNameThreeTextView.text=currentAndForecast.daily[2].weather[0].main
        sectionDailyForecastConditionNameTextViewBinding.forecastConditionNameFourTextView.text=currentAndForecast.daily[3].weather[0].main
        sectionDailyForecastConditionNameTextViewBinding.forecastConditionNameFiveTextView.text=currentAndForecast.daily[4].weather[0].main
        sectionDailyForecastConditionNameTextViewBinding.forecastConditionNameSixTextView.text=currentAndForecast.daily[5].weather[0].main
        sectionDailyForecastConditionNameTextViewBinding.forecastConditionNameSevenTextView.text=currentAndForecast.daily[6].weather[0].main

        sectionDailyForecastConditionDayNameTextViewBinding.forecastConditionTimeOneTextView.text=CommonMethod.utcToOnlyDayName(
            currentAndForecast.daily[0].dt,
            currentAndForecast.timezone
        )
        sectionDailyForecastConditionDayNameTextViewBinding.forecastConditionTimeTwoTextView.text=CommonMethod.utcToOnlyDayName(
            currentAndForecast.daily[1].dt,
            currentAndForecast.timezone
        )
        sectionDailyForecastConditionDayNameTextViewBinding.forecastConditionTimeThreeTextView.text=CommonMethod.utcToOnlyDayName(
            currentAndForecast.daily[2].dt,
            currentAndForecast.timezone
        )
        sectionDailyForecastConditionDayNameTextViewBinding.forecastConditionTimeFourTextView.text=CommonMethod.utcToOnlyDayName(
            currentAndForecast.daily[3].dt,
            currentAndForecast.timezone
        )
        sectionDailyForecastConditionDayNameTextViewBinding.forecastConditionTimeFiveTextView.text=CommonMethod.utcToOnlyDayName(
            currentAndForecast.daily[4].dt,
            currentAndForecast.timezone
        )
        sectionDailyForecastConditionDayNameTextViewBinding.forecastConditionTimeSixTextView.text=CommonMethod.utcToOnlyDayName(
            currentAndForecast.daily[5].dt,
            currentAndForecast.timezone
        )
        sectionDailyForecastConditionDayNameTextViewBinding.forecastConditionTimeSevenTextView.text=CommonMethod.utcToOnlyDayName(
            currentAndForecast.daily[6].dt,
            currentAndForecast.timezone
        )

        sectionDailyForecastConditionTimeTextViewBinding.forecastConditionTimeOneTextView.text=CommonMethod.utcToOnlyDateAndMonth(
            currentAndForecast.daily[0].dt,
            currentAndForecast.timezone
        )
        sectionDailyForecastConditionTimeTextViewBinding.forecastConditionTimeTwoTextView.text=CommonMethod.utcToOnlyDateAndMonth(
            currentAndForecast.daily[1].dt,
            currentAndForecast.timezone
        )
        sectionDailyForecastConditionTimeTextViewBinding.forecastConditionTimeThreeTextView.text=CommonMethod.utcToOnlyDateAndMonth(
            currentAndForecast.daily[2].dt,
            currentAndForecast.timezone
        )
        sectionDailyForecastConditionTimeTextViewBinding.forecastConditionTimeFourTextView.text=CommonMethod.utcToOnlyDateAndMonth(
            currentAndForecast.daily[3].dt,
            currentAndForecast.timezone
        )
        sectionDailyForecastConditionTimeTextViewBinding.forecastConditionTimeFiveTextView.text=CommonMethod.utcToOnlyDateAndMonth(
            currentAndForecast.daily[4].dt,
            currentAndForecast.timezone
        )
        sectionDailyForecastConditionTimeTextViewBinding.forecastConditionTimeSixTextView.text=CommonMethod.utcToOnlyDateAndMonth(
            currentAndForecast.daily[5].dt,
            currentAndForecast.timezone
        )
        sectionDailyForecastConditionTimeTextViewBinding.forecastConditionTimeSevenTextView.text=CommonMethod.utcToOnlyDateAndMonth(
            currentAndForecast.daily[6].dt,
            currentAndForecast.timezone
        )


    }

    private fun updateDailyForecastMinTempLineChart(currentAndForecastModel: CurrentAndForecastWeatherInfoModel) {

        val values: ArrayList<Entry> = ArrayList()
        try{
            for (i in 1..7) {
                values.add(Entry(i.toFloat(), currentAndForecastModel.daily[i].temp.min.toFloat()))
            }
        } catch (e: Exception) {

        }

        val set1: LineDataSet
        if (binding.dailyForecastMinTempLineChart.data != null && binding.dailyForecastMinTempLineChart.data.dataSetCount > 0) {
            set1 = binding.dailyForecastMinTempLineChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
        } else {
            set1 = LineDataSet(values, "Daily Forecast Min Temp Data")
            set1.setDrawIcons(true)
            set1.color = Color.YELLOW
            set1.setCircleColor(Color.BLUE)
            set1.valueTextColor=Color.WHITE
//            set1.fillColor=Color.RED
            set1.lineWidth = 3f
            set1.circleRadius = 5f
            set1.setDrawCircleHole(false)
            set1.valueTextSize = 13f
            set1.setDrawFilled(false)
            set1.disableDashedLine()
            set1.isHighlightEnabled=false
            set1.disableDashedHighlightLine()
            set1.setDrawHighlightIndicators(false)
            set1.valueFormatter=MyValueFormatter()
//            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
//            set1.formSize = 15f
//            val drawable = ContextCompat.getDrawable(this, R.drawable.weather_bg)
//            set1.fillDrawable = drawable
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set1)
            val data = LineData(dataSets)
            binding.dailyForecastMinTempLineChart.data = data
        }
        binding.dailyForecastMinTempLineChart.setTouchEnabled(false)
        binding.dailyForecastMinTempLineChart.setPinchZoom(false)
        binding.dailyForecastMinTempLineChart.setDrawGridBackground(false)
//        binding.dailyForecastMinTempLineChart.setBackgroundColor(Color.YELLOW)
        val legend: Legend=binding.dailyForecastMinTempLineChart.legend
        legend.isEnabled=false
//        binding.dailyForecastMinTempLineChart.xAxis.setDrawGridLines(false)
        binding.dailyForecastMinTempLineChart.xAxis.isEnabled=false
//        binding.dailyForecastMinTempLineChart.axisLeft.setDrawGridLines(false)
        binding.dailyForecastMinTempLineChart.axisLeft.isEnabled=false
//        binding.dailyForecastMinTempLineChart.axisRight.setDrawGridLines(false)
        binding.dailyForecastMinTempLineChart.axisRight.isEnabled=false
        binding.dailyForecastMinTempLineChart.setDrawBorders(false)
        binding.dailyForecastMinTempLineChart.description.isEnabled=false
        binding.dailyForecastMinTempLineChart.data.notifyDataChanged()
        binding.dailyForecastMinTempLineChart.notifyDataSetChanged()
        binding.dailyForecastMinTempLineChart.invalidate()
    }

    private fun updateDailyForecastMaxTempLineChart(currentAndForecastModel: CurrentAndForecastWeatherInfoModel) {

        val values: ArrayList<Entry> = ArrayList()
        try{
            for (i in 1..7) {
                values.add(Entry(i.toFloat(), currentAndForecastModel.daily[i].temp.max.toFloat()))
            }
        } catch (e: Exception) {

        }

        val set1: LineDataSet
        if (binding.dailyForecastMaxTempLineChart.data != null && binding.dailyForecastMaxTempLineChart.data.dataSetCount > 0) {
            set1 = binding.dailyForecastMaxTempLineChart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
        } else {
            set1 = LineDataSet(values, "Daily Forecast Max Temp Data")
            set1.setDrawIcons(true)
            set1.color = Color.RED
            set1.setCircleColor(Color.GREEN)
            set1.valueTextColor=Color.WHITE
//            set1.fillColor=Color.RED
            set1.lineWidth = 3f
            set1.circleRadius = 5f
            set1.setDrawCircleHole(false)
            set1.valueTextSize = 13f
            set1.setDrawFilled(false)
            set1.disableDashedLine()
            set1.isHighlightEnabled=false
            set1.disableDashedHighlightLine()
            set1.setDrawHighlightIndicators(false)
            set1.valueFormatter=MyValueFormatter()
//            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
//            set1.formSize = 15f
//            val drawable = ContextCompat.getDrawable(this, R.drawable.weather_bg)
//            set1.fillDrawable = drawable
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set1)
            val data = LineData(dataSets)
            binding.dailyForecastMaxTempLineChart.data = data
        }
        binding.dailyForecastMaxTempLineChart.setTouchEnabled(false)
        binding.dailyForecastMaxTempLineChart.setPinchZoom(false)
        binding.dailyForecastMaxTempLineChart.setDrawGridBackground(false)
//        binding.dailyForecastMaxTempLineChart.setBackgroundColor(Color.YELLOW)
        val legend: Legend=binding.dailyForecastMaxTempLineChart.legend
        legend.isEnabled=false
//        binding.dailyForecastMaxTempLineChart.xAxis.setDrawGridLines(false)
        binding.dailyForecastMaxTempLineChart.xAxis.isEnabled=false
//        binding.dailyForecastMaxTempLineChart.axisLeft.setDrawGridLines(false)
        binding.dailyForecastMaxTempLineChart.axisLeft.isEnabled=false
//        binding.dailyForecastMaxTempLineChart.axisRight.setDrawGridLines(false)
        binding.dailyForecastMaxTempLineChart.axisRight.isEnabled=false
        binding.dailyForecastMaxTempLineChart.setDrawBorders(false)
        binding.dailyForecastMaxTempLineChart.description.isEnabled=false
        binding.dailyForecastMaxTempLineChart.data.notifyDataChanged()
        binding.dailyForecastMaxTempLineChart.notifyDataSetChanged()
        binding.dailyForecastMaxTempLineChart.invalidate()
    }

    private fun updateCurrentWindSection(model: CurrentAndForecastWeatherInfoModel) {
        binding.currentWeatherDetailsWindSpeedTextView.text=resources.getString(R.string.wind_speed_with_clone)+CommonMethod.convertMpsToMph(
            model.current.wind_speed
        ).roundToInt().toString()+resources.getString(R.string.mile_per_hour)
        binding.currentWeatherDetailsWindDegTextView.text=resources.getString(R.string.wind_deg_with_clone)+model.current.wind_deg.roundToInt().toString()
        binding.currentWeatherDetailsWindDirectionTextView.text=resources.getString(R.string.wind_direction_with_clone)+CommonMethod.windDegToDir(
            model.current.wind_deg
        )
        try {
            Glide.with(this).asGif().load(R.drawable.wind_rotate_wheel).into(binding.windRotatingImageView)
        } catch (e: Exception) {}
    }

    private fun updateCurrentSunsection(model: CurrentAndForecastWeatherInfoModel) {
        binding.currentWeatherSunriseSunsetView.sunriseTime=Time(
            CommonMethod.utcToOnlyHourAs24Format(
                model.current.sunrise,
                model.timezone
            ).toInt(), CommonMethod.utcToOnlyMinute(model.current.sunrise, model.timezone).toInt()
        )
        binding.currentWeatherSunriseSunsetView.sunsetTime=Time(
            CommonMethod.utcToOnlyHourAs24Format(
                model.current.sunset,
                model.timezone
            ).toInt(), CommonMethod.utcToOnlyMinute(model.current.sunset, model.timezone).toInt()
        )
        binding.currentWeatherSunriseSunsetView.startAnimate()
    }

    private fun updateCurrentWeatherTempDetailsSection(model: CurrentAndForecastWeatherInfoModel) {
        binding.currentWeatherDetailsTempTextView.text=resources.getString(R.string.current_temp)+model.current.temp.roundToInt().toString()+resources.getString(
            R.string.degree_celsius
        )
        binding.currentWeatherDetailsMaxTempTextView.text=resources.getString(R.string.max_temp_with_clone)+model.daily[0].temp.max.roundToInt().toString()+resources.getString(
            R.string.degree_celsius
        )
        binding.currentWeatherDetailsMinTempTextView.text=resources.getString(R.string.min_temp_with_clone)+model.daily[0].temp.min.roundToInt().toString()+resources.getString(
            R.string.degree_celsius
        )
        binding.currentWeatherDetailsMorningTempTextView.text=resources.getString(R.string.morning_temp_with_clone)+model.daily[0].temp.morn.roundToInt().toString()+resources.getString(
            R.string.degree_celsius
        )
        binding.currentWeatherDetailsDayTempTextView.text=resources.getString(R.string.day_temp_with_clone)+model.daily[0].temp.day.roundToInt().toString()+resources.getString(
            R.string.degree_celsius
        )
        binding.currentWeatherDetailsEveTempTextView.text=resources.getString(R.string.eve_temp_with_clone)+model.daily[0].temp.eve.roundToInt().toString()+resources.getString(
            R.string.degree_celsius
        )
        binding.currentWeatherDetailsNightTempTextView.text=resources.getString(R.string.night_temp_with_clone)+model.daily[0].temp.night.roundToInt().toString()+resources.getString(
            R.string.degree_celsius
        )
    }

    private fun updateCurrentWeatherFeelsLikeTempDetailsSection(model: CurrentAndForecastWeatherInfoModel) {
        binding.currentWeatherDetailsFeelsLikeTextView.text=resources.getString(R.string.current_feels_like)+model.current.feels_like.roundToInt().toString()+resources.getString(
            R.string.degree_celsius
        )
        binding.currentWeatherDetailsMorningFeelsLikeTempTextView.text=resources.getString(R.string.morning_temp_with_clone)+model.daily[0].feels_like.morn.roundToInt().toString()+resources.getString(
            R.string.degree_celsius
        )
        binding.currentWeatherDetailsDayFeelsLikeTempTextView.text=resources.getString(R.string.day_temp_with_clone)+model.daily[0].feels_like.day.roundToInt().toString()+resources.getString(
            R.string.degree_celsius
        )
        binding.currentWeatherDetailsEveFeelsLikeTempTextView.text=resources.getString(R.string.eve_temp_with_clone)+model.daily[0].feels_like.eve.roundToInt().toString()+resources.getString(
            R.string.degree_celsius
        )
        binding.currentWeatherDetailsNightFeelsLikeTempTextView.text=resources.getString(R.string.night_temp_with_clone)+model.daily[0].feels_like.night.roundToInt().toString()+resources.getString(
            R.string.degree_celsius
        )
    }

    private fun updateCurrentWeatherOthersDetailsSection(model: CurrentAndForecastWeatherInfoModel) {
        binding.currentWeatherDetailsSunriseTextView.text=resources.getString(R.string.sunrise_with_clone)+CommonMethod.utcToTime(
            model.current.sunrise,
            model.timezone
        )
        binding.currentWeatherDetailsSunsetTextView.text=resources.getString(R.string.sunset_with_clone)+CommonMethod.utcToTime(
            model.current.sunset,
            model.timezone
        )
        binding.currentWeatherDetailsPressureTextView.text=resources.getString(R.string.pressure_with_clone)+model.current.pressure.toString()+resources.getString(
            R.string.pressure_unit
        )
        binding.currentWeatherDetailsHumidityTextView.text=resources.getString(R.string.humidity_with_clone)+model.current.humidity.toString()+resources.getString(
            R.string.percent_icon
        )
        binding.currentWeatherDetailsDewPointTextView.text=resources.getString(R.string.dew_point_with_clone)+model.current.dew_point.roundToInt().toString()+resources.getString(
            R.string.degree_celsius
        )
        binding.currentWeatherDetailsUviTextView.text=resources.getString(R.string.uvi_index_with_clone)+model.current.uvi.roundToInt().toString()
        binding.currentWeatherDetailsCloudTextView.text=resources.getString(R.string.cloud_with_clone)+model.current.clouds.toString()
        binding.currentWeatherDetailsVisibilityTextView.text=resources.getString(R.string.visibility_with_clone)+CommonMethod.convertMeterToMile(
            model.current.visibility
        ).roundToInt().toString()+resources.getString(R.string.mile)
        binding.currentWeatherDetailsDescriptionTextView.text=resources.getString(R.string.description_with_clone)+model.current.weather[0].description

    }

    private fun updateCurrentWeatherRadarSection(model: CurrentAndForecastWeatherInfoModel) {
        binding.currentWeatherRadarMapWebView.settings.javaScriptEnabled=true
        binding.currentWeatherRadarMapWebView.setBackgroundColor(Color.TRANSPARENT)
        if (haveInternet()) {
            binding.currentWeatherRadarMapWebView.loadUrl(
                CommonMethod.getWindyUrl(
                    model.lat,
                    model.lon
                )
            )
        } else {
            val textTitleStyling = "<head><style>* {margin:0;padding:0;font-size:20; text-align:justify; color:#FFFFFF; background-color:#000000;}</style><body><h1>Check Internet Connection</h1></body></head>"
            binding.currentWeatherRadarMapWebView.loadData(textTitleStyling, "text/html", "utf-8")
        }
    }

    private fun openCloseDrawerLayout() {
        if (binding.mainActivityDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.mainActivityDrawerLayout.closeDrawer(GravityCompat.START)
        } else{
            binding.mainActivityDrawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.myToolbar)
        val drawerToggle: ActionBarDrawerToggle= ActionBarDrawerToggle(
            this,
            binding.mainActivityDrawerLayout,
            binding.myToolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerToggle.syncState()
        binding.mainActivityDrawerLayout.addDrawerListener(drawerToggle)
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
        }
        binding.mainActivityDrawerNavView.setNavigationItemSelectedListener(this)
    }

    private fun loadPreviousData() {
        val gson: Gson= Gson()
        var dataString: String?=null
        Coroutines.main {
            dataString=SharedPreUtils.getStringFromStorage(
                applicationContext,
                Constants.weatherDataKey,
                null
            )
            cityName=SharedPreUtils.getStringFromStorage(
                applicationContext,
                Constants.cityNameKey,
                null
            )
            dataString?.let {
                val model: CurrentAndForecastWeatherInfoModel=gson.fromJson(
                    it,
                    CurrentAndForecastWeatherInfoModel::class.java
                )
                try {
                    updateUi(model)
                } catch (e: Exception) {}
            }
            cityName?.let {
                binding.currentCityNameTextView.text=cityName
            }
        }
    }

    private fun saveCurrentWeatherDataToStorage(model: CurrentAndForecastWeatherInfoModel) {
        val gson: Gson= Gson()
        val stringData: String=gson.toJson(model)
        Coroutines.io {
            SharedPreUtils.setStringToStorage(
                applicationContext,
                Constants.weatherDataKey,
                stringData
            )
        }
    }

    private fun appFeedbackDialog(): AppRatingDialog {
        return AppRatingDialog.Builder()
                .setCancelable(false)
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Never")
                .setNeutralButtonText("Later")
                .setTitle(resources.getString(R.string.app_feedback_title))
                .setDescription(resources.getString(R.string.app_feedback_message))
                .setStarColor(R.color.lightSeaGreen)
                .setTitleTextColor(R.color.teal)
                .setDescriptionTextColor(R.color.teal)
                .setDialogBackgroundColor(R.color.lightRed)
                .setAfterInstallDay(1)
                .setDefaultRating(3)
                .setNumberOfLaunches(3)
                .setRemindIntervalDay(1)
                .setCanceledOnTouchOutside(false)
                .create(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.MY_PERMISSIONS_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationStatus()
            } else{
                shortToast(resources.getString(R.string.location_cancel_message))
            }
        }
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.hourlyDetailsTextView -> startActivity(
                Intent(
                    this@MainActivity,
                    HourlyForecastDetailsActivity::class.java
                )
            )
            R.id.dailyDetailsTextView -> startActivity(
                Intent(
                    this@MainActivity,
                    DailyForecastActivity::class.java
                )
            )
        }
    }

    private fun loadHints() {
        val from = arrayOf(columnCityName, columnCountryName)
        val to = intArrayOf(R.id.city_name_text_view, R.id.country_name_text_view)
        cursorAdapter = SimpleCursorAdapter(
            this,
            R.layout.search_item,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
    }

    private fun loadAppSettingFromDatabase() {
        FirebaseDatabase.getInstance().reference.child("AppSettings").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.getValue(AppSettings::class.java)!=null) {
                    val appSettings: AppSettings=snapshot.getValue(AppSettings::class.java)!!
                    Coroutines.io {
                        SharedPreUtils.setStringToStorage(applicationContext,Constants.versionNameKey,appSettings.versionName)
                        SharedPreUtils.setStringToStorage(applicationContext,Constants.versionMessageKey,appSettings.versionMessage)
                        SharedPreUtils.setStringToStorage(applicationContext,Constants.weatherApiKey,appSettings.weatherApiKey)
                        SharedPreUtils.setBooleanToStorage(applicationContext,Constants.adsFlagKey,appSettings.adsFlag.toBoolean())
                        SharedPreUtils.setIntToStorage(applicationContext,Constants.adsIntervalInMinuteKey,appSettings.adsIntervalInMinute.toInt())
                        SharedPreUtils.setStringToStorage(applicationContext,Constants.appOpenAdsCodeKey,appSettings.appOpenAdsCode)

                        loadSettingFromStorage()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun loadSettingFromStorage() {
        Coroutines.io {
            Constants.appVersionName=SharedPreUtils.getStringFromStorage(applicationContext,Constants.versionNameKey,Constants.appVersionName)!!
            Constants.appVersionMessage=SharedPreUtils.getStringFromStorage(applicationContext,Constants.versionMessageKey,Constants.appVersionMessage)!!
            Constants.weatherApi=SharedPreUtils.getStringFromStorage(applicationContext,Constants.weatherApiKey,Constants.weatherApi)!!
            Constants.adsFlag=SharedPreUtils.getBooleanFromStorage(applicationContext,Constants.adsFlagKey,Constants.adsFlag)
            Constants.adsIntervalInMinute=SharedPreUtils.getIntFromStorage(applicationContext,Constants.adsIntervalInMinuteKey,Constants.adsIntervalInMinute)
            Constants.appOpenAdsCode=SharedPreUtils.getStringFromStorage(applicationContext,Constants.appOpenAdsCodeKey,Constants.appOpenAdsCode)!!
            Constants.lastAppOpenAdsShownTime=SharedPreUtils.getLongFromStorage(applicationContext, Constants.lastAppOpenAdsShownTimeKey, Constants.lastAppOpenAdsShownTime)

            checkMandatoryUpdate()
        }
    }

    private fun checkMandatoryUpdate() {
        Coroutines.main {
            val appVersionNameInString: String=applicationContext.packageManager.getPackageInfo(applicationContext.packageName,0).versionName
            val appVersionName: Double=appVersionNameInString.toDouble()
            if (appVersionName<Constants.appVersionName.toDouble()) {
                val builder: AlertDialog.Builder=AlertDialog.Builder(this,R.style.MyDialogTheme)
                        .setCancelable(false)
                        .setTitle(resources.getString(R.string.found_new_update))
                        .setMessage(Constants.appVersionMessage)
                        .setPositiveButton(resources.getString(R.string.update_now)) {
                            p0, p1 -> CommonMethod.openAppLink(this@MainActivity)
                        }
                val alertDialog: AlertDialog=builder.create()
                if (!isFinishing) {
                    alertDialog.show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        menu?.let {
            val search=it.findItem(R.id.toolbarMenuSearch)
            searchView=search.actionView as SearchView

            searchView.findViewById<AutoCompleteTextView>(R.id.search_src_text).threshold=1
            searchView.suggestionsAdapter=cursorAdapter
            searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
                override fun onSuggestionSelect(position: Int): Boolean {
                    return true
                }

                override fun onSuggestionClick(position: Int): Boolean {
                    hideKeyboard(searchView)
                    val cursor = searchView.suggestionsAdapter.getItem(position) as Cursor
                    val selectedLatAndLon = cursor.getString(cursor.getColumnIndex(columnLatAndLon))
                    val selectedCityName = cursor.getString(cursor.getColumnIndex(columnCityName))
                    if (!searchView.isIconified) {
                        searchView.clearFocus()
                        searchView.isIconified = true
                        searchView.onActionViewCollapsed()
                    }
                    latitude = selectedLatAndLon.split(",".toRegex(), 0)[0].toDouble()
                    longitude = selectedLatAndLon.split(",".toRegex(), 0)[1].toDouble()
                    cityName = selectedCityName
                    binding.currentCityNameTextView.text = cityName
                    Coroutines.io {
                        SharedPreUtils.setStringToStorage(
                            applicationContext,
                            Constants.cityNameKey,
                            selectedCityName
                        )
                    }
                    getCurrentAndForecastWeather()
                    return true
                }

            })

            searchView.queryHint=resources.getString(R.string.search_city_name)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { q ->
                        getLanLonUsingPlaceName(q)
                    }
                    if (!searchView.isIconified) {
                        searchView.clearFocus()
                        searchView.isIconified = true
                        searchView.onActionViewCollapsed()
                    }
                    return false
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    query?.let {
                        getCitySuggestion(it)
                    }
                    return true
                }

            })
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.toolbarMenuMyLocation -> {
                checkLocationStatus()
                return true
            }
            else ->super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_hourly_forecast_details -> startActivity(
                Intent(
                    this@MainActivity,
                    HourlyForecastDetailsActivity::class.java
                )
            )
            R.id.nav_daily_forecast_details -> startActivity(
                Intent(
                    this@MainActivity,
                    DailyForecastActivity::class.java
                )
            )
            R.id.nav_privacy_policy -> startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(resources.getString(R.string.privacy_policy_link))
                )
            )
        }
        openCloseDrawerLayout()
        return true
    }

    override fun onBackPressed() {
        if (binding.mainActivityDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.mainActivityDrawerLayout.closeDrawer(GravityCompat.START)
        }
        if (!searchView.isIconified) {
            searchView.isIconified=true
        } else{
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        checkMandatoryUpdate()
        if (haveInternet()) {
            checkLocationStatus()
        } else{
            longToast(resources.getString(R.string.no_internet_message))
        }
    }

    override fun onPause() {
        super.onPause()
        if (boundStatus && serviceConnection!=null) {
            unbindService(serviceConnection!!)
            boundStatus = false
            serviceConnection=null
        }
    }

    override fun onNegativeButtonClicked() {
        shortToast(resources.getString(R.string.thank_you))
        onBackPressed()
    }

    override fun onNeutralButtonClicked() {
        shortToast(resources.getString(R.string.thank_you))
        onBackPressed()
    }

    override fun onPositiveButtonClicked(rate: Int) {
        if (rate==5) {
            val appPackageName = applicationContext.packageName
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        } else{
            shortToast(resources.getString(R.string.thank_you))
            onBackPressed()
        }
    }


}