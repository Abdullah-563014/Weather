package weather.app.live.update.forecast.ui.daily_forecast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import weather.app.live.update.forecast.R
import weather.app.live.update.forecast.utils.Constants
import weather.app.live.update.forecast.utils.Coroutines
import weather.app.live.update.forecast.utils.SharedPreUtils
import weather.app.live.update.forecast.databinding.ActivityDailyForecastBinding
import weather.app.live.update.forecast.models.CurrentAndForecastWeatherInfoModel
import weather.app.live.update.forecast.models.Daily

class DailyForecastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDailyForecastBinding
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: DailyDetailsAdapter
    private lateinit var model: CurrentAndForecastWeatherInfoModel
    private lateinit var list: MutableList<Daily>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityDailyForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)


        loadLocalPreviousDataFromStorage()

    }


    private fun loadLocalPreviousDataFromStorage() {
        val gson: Gson = Gson()
        var dataString: String?=null
        Coroutines.main {
            dataString= SharedPreUtils.getStringFromStorage(
                applicationContext,
                Constants.weatherDataKey,
                null
            )
            dataString?.let {
                val model: CurrentAndForecastWeatherInfoModel=gson.fromJson(
                    it,
                    CurrentAndForecastWeatherInfoModel::class.java
                )
                this.model=model
                binding.dailyDetailsItemCountTextView.text="${resources.getString(R.string.item_count_with_clone)}${model.daily.size}"
                generateHourlyForecastList()
            }
        }

    }

    private fun generateHourlyForecastList() {
        list=ArrayList<Daily>()
        for (i in model.daily.indices) {
            list.add(model.daily[i])
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        layoutManager= LinearLayoutManager(this)
        adapter= DailyDetailsAdapter(this,list,model.timezone)
        binding.dailyDetailsRecyclerView.layoutManager=layoutManager
        binding.dailyDetailsRecyclerView.adapter=adapter
    }



}