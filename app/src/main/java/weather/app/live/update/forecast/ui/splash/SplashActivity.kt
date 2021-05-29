package weather.app.live.update.forecast.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import weather.app.live.update.forecast.BuildConfig
import weather.app.live.update.forecast.R
import weather.app.live.update.forecast.databinding.ActivitySplashBinding
import weather.app.live.update.forecast.ui.main.MainActivity
import weather.app.live.update.forecast.utils.CommonMethod
import weather.app.live.update.forecast.utils.Constants
import weather.app.live.update.forecast.utils.Coroutines
import weather.app.live.update.forecast.utils.SharedPreUtils
import java.util.*

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var timer: CountDownTimer?=null
    private var appOpenAd: AppOpenAd?=null
    private lateinit var loadCallback: AppOpenAd.AppOpenAdLoadCallback
    private var isShowingAds=false
    private var loadTime: Long=0
    private var isCountDownTimerEnd: Boolean=false
    private var appOpenCounter: Long=1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initAll()

        loadSettingFromStorage()

        startCountDownTimer()

        if (savedInstanceState==null) {
            fetchAd()
        }



    }


    private fun initAll() {
        binding.splashVersionNameTextView.text="${resources.getString(R.string.version_name)} ${BuildConfig.VERSION_NAME}"
    }

    private fun loadSettingFromStorage() {
        Coroutines.io {
            Constants.appVersionName=SharedPreUtils.getStringFromStorage(applicationContext, Constants.versionNameKey, Constants.appVersionName)!!
            Constants.appVersionMessage=SharedPreUtils.getStringFromStorage(applicationContext, Constants.versionMessageKey, Constants.appVersionMessage)!!
            Constants.weatherApi=SharedPreUtils.getStringFromStorage(applicationContext, Constants.weatherApiKey, Constants.weatherApi)!!
            Constants.adsFlag=SharedPreUtils.getBooleanFromStorage(applicationContext, Constants.adsFlagKey, Constants.adsFlag)
            Constants.adsIntervalInMinute=SharedPreUtils.getIntFromStorage(applicationContext, Constants.adsIntervalInMinuteKey, Constants.adsIntervalInMinute)
            Constants.appOpenAdsCode=SharedPreUtils.getStringFromStorage(applicationContext, Constants.appOpenAdsCodeKey, Constants.appOpenAdsCode)!!
            Constants.lastAppOpenAdsShownTime=SharedPreUtils.getLongFromStorage(applicationContext, Constants.lastAppOpenAdsShownTimeKey, Constants.lastAppOpenAdsShownTime)
        }
    }

    private fun startCountDownTimer() {
        timer?.cancel()
        timer=null
        timer= object : CountDownTimer(if (Constants.adsFlag && CommonMethod.isRightToShowAdsAppOpenAds()) 4000 else 2000,1000){
            override fun onTick(p0: Long) {

            }

            override fun onFinish() {
                showAdIfAvailable()
            }

        }.start()
    }

    private fun fetchAd() {
        if (isAdAvailable()) {
            return
        }
        loadCallback = object :AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(p0: AppOpenAd) {
                appOpenAd = p0
                loadTime = (Date()).time
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
            }
        }
        val request: AdRequest =getAdRequest()
        AppOpenAd.load(this, Constants.appOpenAdsCode, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback)
    }

    private fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4) && CommonMethod.isRightToShowAdsAppOpenAds()
    }

    private fun showAdIfAvailable() {
        if (!isShowingAds && isAdAvailable()) {
            val fullScreenContentCallback: FullScreenContentCallback =object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    Coroutines.io {
                        SharedPreUtils.setLongToStorage(applicationContext,Constants.lastAppOpenAdsShownTimeKey,Date().time)
                    }
                    gotoNextActivity()
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAds = true
                }
            }
            appOpenAd?.fullScreenContentCallback=fullScreenContentCallback
            appOpenAd?.show(this)
        } else {
            gotoNextActivity()
        }
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - this.loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    private fun gotoNextActivity() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        timer?.cancel()
        timer=null
        super.onDestroy()
    }




}