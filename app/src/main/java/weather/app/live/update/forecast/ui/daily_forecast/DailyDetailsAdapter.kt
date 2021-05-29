package weather.app.live.update.forecast.ui.daily_forecast

import android.content.Context
import android.view.LayoutInflater
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
        holder.binding.dailyDetailsPressureTextView.text="${context.resources.getString(R.string.pressure_with_clone)}${list[position].pressure}${context.resources.getString(R.string.pressure_unit)}"
        holder.binding.dailyDetailsHumidityTextView.text="${context.resources.getString(R.string.humidity_with_clone)}${list[position].humidity}${context.resources.getString(R.string.percent_icon)}"
        holder.binding.dailyDetailsDewPointTextView.text="${context.resources.getString(R.string.dew_point_with_clone)}${list[position].dew_point.roundToInt()}${context.resources.getString(R.string.degree_celsius)}"
        holder.binding.dailyDetailsUviTextView.text="${context.resources.getString(R.string.uvi_index_with_clone)}${list[position].uvi.roundToInt()}"
        holder.binding.dailyDetailsPopTextView.text="${context.resources.getString(R.string.pop_with_clone)}${list[position].pop}${context.resources.getString(R.string.percent_icon)}"
        holder.binding.dailyDetailsCloudTextView.text="${context.resources.getString(R.string.cloud_with_clone)}${list[position].clouds}"
        holder.binding.dailyDetailsWindSpeedTextView.text="${context.resources.getString(R.string.wind_speed_with_clone)}${CommonMethod.convertMpsToMph(list[position].wind_speed).roundToInt()}${context.resources.getString(R.string.mile_per_hour)}"
        holder.binding.dailyDetailsWindDirectionTextView.text="${context.resources.getString(R.string.wind_direction_with_clone)}${CommonMethod.windDegToDir(list[position].wind_deg)}"
        holder.binding.dailyDetailsDescriptionTextView.text="${context.resources.getString(R.string.description_with_clone)}${list[position].weather[0].description}"
        holder.binding.dailyDetailsMorningTempTextView.text="${context.resources.getString(R.string.morning_temp_with_clone)}${list[position].temp.morn.roundToInt()}${context.resources.getString(R.string.degree_celsius)}"
        holder.binding.dailyDetailsDayTempTextView.text="${context.resources.getString(R.string.day_temp_with_clone)}${list[position].temp.day.roundToInt()}${context.resources.getString(R.string.degree_celsius)}"
        holder.binding.dailyDetailsEveTempTextView.text="${context.resources.getString(R.string.eve_temp_with_clone)}${list[position].temp.eve.roundToInt()}${context.resources.getString(R.string.degree_celsius)}"
        holder.binding.dailyDetailsNightTempTextView.text="${context.resources.getString(R.string.night_temp_with_clone)}${list[position].temp.night.roundToInt()}${context.resources.getString(R.string.degree_celsius)}"
        holder.binding.dailyDetailsMaxTempTextView.text="${context.resources.getString(R.string.max_temp_with_clone)}${list[position].temp.max.roundToInt()}${context.resources.getString(R.string.degree_celsius)}"
        holder.binding.dailyDetailsMinTempTextView.text="${context.resources.getString(R.string.min_temp_with_clone)}${list[position].temp.min.roundToInt()}${context.resources.getString(R.string.degree_celsius)}"
        holder.binding.dailyDetailsMorningFeelsLikeTextView.text="${context.resources.getString(R.string.morning_temp_with_clone)}${list[position].feels_like.morn.roundToInt()}${context.resources.getString(R.string.degree_celsius)}"
        holder.binding.dailyDetailsDayFeelsLikeTextView.text="${context.resources.getString(R.string.day_temp_with_clone)}${list[position].feels_like.day.roundToInt()}${context.resources.getString(R.string.degree_celsius)}"
        holder.binding.dailyDetailsEveFeelsLikeTextView.text="${context.resources.getString(R.string.eve_temp_with_clone)}${list[position].feels_like.eve.roundToInt()}${context.resources.getString(R.string.degree_celsius)}"
        holder.binding.dailyDetailsNightFeelsLikeTextView.text="${context.resources.getString(R.string.night_temp_with_clone)}${list[position].feels_like.night.roundToInt()}${context.resources.getString(R.string.degree_celsius)}"
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class DailyViewHolder(binding: DailyForecastDetailsRecyclerViewModelBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: DailyForecastDetailsRecyclerViewModelBinding=binding
    }


}