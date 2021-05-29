package weather.app.live.update.forecast.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.gson.JsonElement
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import weather.app.live.update.forecast.database_connection.MyApi
import weather.app.live.update.forecast.models.LatAndLonModel
import weather.app.live.update.forecast.utils.Constants

class MyLocationService : Service() {

    private var fusedLocationProviderClient: FusedLocationProviderClient?=null
    private var mLocationCallback: LocationCallback?=null
    private val binder = MyLocationBinder()
    private var latAndLonMutableLiveData: MutableLiveData<LatAndLonModel> = MutableLiveData()
    private lateinit var locationManager: LocationManager
    private var latAndLonModel: LatAndLonModel= LatAndLonModel()



    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()

        locationManager=this.getSystemService(LOCATION_SERVICE) as LocationManager
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)


        startLocationUpdate()


    }


    override fun onDestroy() {
        stopLocationUpdate()
        super.onDestroy()
    }

    inner class MyLocationBinder: Binder() {
        fun getMyBinder(): MyLocationService =this@MyLocationService
    }

    fun getLatAndLong(): LiveData<LatAndLonModel> {
        return latAndLonMutableLiveData
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdate() {
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.interval = 60000
        mLocationRequest.fastestInterval = 2000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        latAndLonModel.status=true
                        latAndLonModel.lat=location.latitude
                        latAndLonModel.lon=location.longitude
                        latAndLonMutableLiveData.value=latAndLonModel
                    }
                }
            }

            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
                if (p0.isLocationAvailable) {
                    fusedLocationProviderClient?.lastLocation?.addOnSuccessListener { location: Location? ->
                        if (location!=null) {
                            latAndLonModel.status=true
                            latAndLonModel.lat=location.latitude
                            latAndLonModel.lon=location.longitude
                            latAndLonMutableLiveData.value=latAndLonModel
                        } else {
                            getLocationFromIp()
                        }
                    }
                } else {
                    getLocationFromIp()
                }
            }
        }
        try {
            LocationServices.getFusedLocationProviderClient(applicationContext).requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback!!,
                    Looper.getMainLooper()
            )
        } catch (e: Exception) {

        }
    }

    private fun stopLocationUpdate() {
        try {
            if (fusedLocationProviderClient != null && mLocationCallback!=null) {
                fusedLocationProviderClient?.removeLocationUpdates(mLocationCallback!!)
            }
        } catch (e: Exception) {

        }
    }

    private fun getLocationFromIp() {
        val call: Call<JsonElement> =MyApi.invoke(" "," ").getIpInfo()
        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.code()==200) {
                    if (response.body()!=null) {
                        try {
                            val rootObject: JSONObject= JSONObject(response.body().toString())
                            if (rootObject.getString("status").equals("success",true)) {
                                val lat: Double=rootObject.getString("lat").toDouble()
                                val lon: Double=rootObject.getString("lon").toDouble()
                                latAndLonModel.status=true
                                latAndLonModel.lat=lat
                                latAndLonModel.lon=lon
                                latAndLonMutableLiveData.value=latAndLonModel
                            } else {
                                openGoogleMap()
                            }
                        }catch (e: Exception) {
                            openGoogleMap()
                        }
                    } else {
                        openGoogleMap()
                    }
                } else {
                    openGoogleMap()
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                openGoogleMap()
            }

        })
    }

    private fun openGoogleMap() {
        val mapIntent = Intent(Intent.ACTION_VIEW)
        mapIntent.setPackage("com.google.android.apps.maps")
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        mapIntent.resolveActivity(packageManager)?.let {
            startActivity(mapIntent)
        }
    }



}