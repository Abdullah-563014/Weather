package weather.app.live.update.forecast.ui.daily_forecast

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import weather.app.live.update.forecast.R
import weather.app.live.update.forecast.utils.CommonMethod
import weather.app.live.update.forecast.databinding.DailyForecastDetailsRecyclerViewModelBinding
import weather.app.live.update.forecast.models.Daily
import kotlin.math.roundToInt

class DailyDetailsAdapter(context: Context, list: List<Daily>, timeZone: String): RecyclerView.Adapter<DailyDetailsAdapter.DailyViewHolder>() {

    private val context: Context = context
    private val list: List<Daily> = list
    private val timeZone: String = timeZone


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        return DailyViewHolder(DailyForecastDetailsRecyclerViewModelBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        try {
            Glide.with(context).asGif().load(CommonMethod.getTargetGifIcon(list[position].weather[0].icon)).into(holder.binding.conditionImageView)
        } catch (e: Exception) {}
        holder.binding.timeTextView.text="${CommonMethod.utcToOnlyDayName(list[position].dt,timeZone)}, ${CommonMethod.utcToTime(list[position].dt,timeZone)}"
        holder.binding.dailyDetailsSunriseTextView.text="${context.resources.getString(R.string.sunrise_with_clone)}${CommonMethod.utcToTime(list[position].sunrise,timeZone)}"
        holder.binding.dailyDetailsSunsetTextView.text="${context.resources.getString(R.string.sunset_with_clone)}${CommonMethod.utcToTime(list[position].sunset,timeZone)}"
        holder.binding.dailyDetailsPressureTextView.text="${context.resources.getString(R.string.pressure_with_clone)}${CommonMethod.getPressureValue(list[position].pressure)}"
        holder.binding.dailyDetailsHumidityTextView.text="${context.resources.getString(R.string.humidity_with_clone)}${list[position].humidity}${context.resources.getString(R.string.percent_icon)}"
        holder.binding.dailyDetailsDewPointTextView.text="${context.resources.getString(R.string.dew_point_with_clone)}${CommonMethod.getTempValue(list[position].dew_point)}"
        holder.binding.dailyDetailsUviTextView.text="${context.resources.getString(R.string.uvi_index_with_clone)}${list[position].uvi.roundToInt()}"
        holder.binding.dailyDetailsPopTextView.text="${context.resources.getString(R.string.pop_with_clone)}${(list[position].pop*100).roundToInt()}${context.resources.getString(R.string.percent_icon)}"
        holder.binding.dailyDetailsCloudTextView.text="${context.resources.getString(R.string.cloud_with_clone)}${list[position].clouds}"
        if (list[position].rain!=null && !list[position].rain!!.isNaN()) {
            holder.binding.dailyDetailsRainTextView.visibility=View.VISIBLE
            holder.binding.dailyDetailsRainTextView.text="${context.resources.getString(R.string.rain_for_first_hour_with_clone)} ${CommonMethod.getPrecipitationValue(list[position].rain!!)}"
        } else {
            holder.binding.dailyDetailsRainTextView.visibility=View.GONE
        }
        holder.binding.dailyDetailsWindSpeedTextView.text="${context.resources.getString(R.string.wind_speed_with_clone)}${CommonMethod.getSpeedValue(list[position].wind_speed)}"
        holder.binding.dailyDetailsWindDirectionTextView.text="${context.resources.getString(R.string.wind_direction_with_clone)}${CommonMethod.windDegToDir(list[position].wind_deg)}"
        holder.binding.dailyDetailsDescriptionTextView.text="${context.resources.getString(R.string.description_with_clone)}${list[position].weather[0].description}"
        holder.binding.dailyDetailsMorningTempTextView.text="${context.resources.getString(R.string.morning_temp_with_clone)}${CommonMethod.getTempValue(list[position].temp.morn)}"
        holder.binding.dailyDetailsDayTempTextView.text="${context.resources.getString(R.string.day_temp_with_clone)}${CommonMethod.getTempValue(list[position].temp.day)}"
        holder.binding.dailyDetailsEveTempTextView.text="${context.resources.getString(R.string.eve_temp_with_clone)}${CommonMethod.getTempValue(list[position].temp.eve)}"
        holder.binding.dailyDetailsNightTempTextView.text="${context.resources.getString(R.string.night_temp_with_clone)}${CommonMethod.getTempValue(list[position].temp.night)}"
        holder.binding.dailyDetailsMaxTempTextView.text="${context.resources.getString(R.string.max_temp_with_clone)}${CommonMethod.getTempValue(list[position].temp.max)}"
        holder.binding.dailyDetailsMinTempTextView.text="${context.resources.getString(R.string.min_temp_with_clone)}${CommonMethod.getTempValue(list[position].temp.min)}"
        holder.binding.dailyDetailsMorningFeelsLikeTextView.text="${context.resources.getString(R.string.morning_temp_with_clone)}${CommonMethod.getTempValue(list[position].feels_like.morn)}"
        holder.binding.dailyDetailsDayFeelsLikeTextView.text="${context.resources.getString(R.string.day_temp_with_clone)}${CommonMethod.getTempValue(list[position].feels_like.day)}"
        holder.binding.dailyDetailsEveFeelsLikeTextView.text="${context.resources.getString(R.string.eve_temp_with_clone)}${CommonMethod.getTempValue(list[position].feels_like.eve)}"
        holder.binding.dailyDetailsNightFeelsLikeTextView.text="${context.resources.getString(R.string.night_temp_with_clone)}${CommonMethod.getTempValue(list[position].feels_like.night)}"
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class DailyViewHolder(binding: DailyForecastDetailsRecyclerViewModelBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: DailyForecastDetailsRecyclerViewModelBinding=binding
    }


}