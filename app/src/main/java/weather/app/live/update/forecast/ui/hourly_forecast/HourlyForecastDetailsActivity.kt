package weather.app.live.update.forecast.ui.hourly_forecast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import weather.app.live.update.forecast.R
import weather.app.live.update.forecast.utils.Constants
import weather.app.live.update.forecast.utils.Coroutines
import weather.app.live.update.forecast.utils.SharedPreUtils
import weather.app.live.update.forecast.databinding.ActivityHourlyForecastDetailsBinding
import weather.app.live.update.forecast.models.CurrentAndForecastWeatherInfoModel
import weather.app.live.update.forecast.models.Hourly

class HourlyForecastDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHourlyForecastDetailsBinding
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: HourlyDetailsAdapter
    private lateinit var model: CurrentAndForecastWeatherInfoModel
    private lateinit var list: MutableList<Hourly>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHourlyForecastDetailsBinding.inflate(layoutInflater)
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
                binding.hourlyDetailsItemCountTextView.text="${resources.getString(R.string.item_count_with_clone)}${model.hourly.size}"
                generateHourlyForecastList()
            }
        }

    }

    private fun generateHourlyForecastList() {
        list=ArrayList<Hourly>()
        for (i in model.hourly.indices) {
            list.add(model.hourly[i])
        }
        initRecyclerView()
    }

    private fun initRecyclerView() {
        layoutManager= LinearLayoutManager(this)
        adapter= HourlyDetailsAdapter(this,list,model.timezone)
        binding.hourlyDetailsRecyclerView.layoutManager=layoutManager
        binding.hourlyDetailsRecyclerView.adapter=adapter
    }


}