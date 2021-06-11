package weather.app.live.update.forecast.ui.hourly_forecast

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import weather.app.live.update.forecast.R
import weather.app.live.update.forecast.utils.CommonMethod
import weather.app.live.update.forecast.databinding.HourlyForecastDetailsRecyclerViewModelBinding
import weather.app.live.update.forecast.models.Hourly
import kotlin.math.roundToInt

class HourlyDetailsAdapter(context: Context, list: List<Hourly>, timeZone: String): RecyclerView.Adapter<HourlyDetailsAdapter.HourlyDetailsViewHolder>() {

    private val context: Context = context
    private val list: List<Hourly> = list
    private val timeZone: String = timeZone


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyDetailsViewHolder {
        return HourlyDetailsViewHolder(HourlyForecastDetailsRecyclerViewModelBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: HourlyDetailsViewHolder, position: Int) {
        try {
            Glide.with(context).asGif().load(CommonMethod.getTargetGifIcon(list[position].weather[0].icon)).into(holder.binding.conditionImageView)
        } catch (e: Exception) {}
        holder.binding.timeTextView.text="${CommonMethod.utcToOnlyDayName(list[position].dt,timeZone)}, ${CommonMethod.utcToTime(list[position].dt,timeZone)}"
        holder.binding.tempTextView.text="${context.resources.getString(R.string.temp_with_clone)}${CommonMethod.getTempValue(list[position].temp)}"
        holder.binding.feelsLikeTextView.text="${context.resources.getString(R.string.feels_like_with_clone)}${CommonMethod.getTempValue(list[position].feels_like)}"
        holder.binding.pressureTextView.text="${context.resources.getString(R.string.pressure_with_clone)}${CommonMethod.getPressureValue(list[position].pressure)}"
        holder.binding.humidityTextView.text="${context.resources.getString(R.string.humidity_with_clone)}${list[position].humidity}${context.resources.getString(R.string.percent_icon)}"
        holder.binding.dewPointTextView.text="${context.resources.getString(R.string.dew_point_with_clone)}${CommonMethod.getTempValue(list[position].dew_point)}"
        holder.binding.uviTextView.text="${context.resources.getString(R.string.uvi_index_with_clone)}${list[position].uvi.roundToInt()}"
        holder.binding.popTextView.text="${context.resources.getString(R.string.pop_with_clone)}${(list[position].pop*100).roundToInt()}${context.resources.getString(R.string.percent_icon)}"
        holder.binding.cloudTextView.text="${context.resources.getString(R.string.cloud_with_clone)}${list[position].clouds}"
        holder.binding.visibilityTextView.text="${context.resources.getString(R.string.visibility_with_clone)}${CommonMethod.getDistanceValue(list[position].visibility)}"
        holder.binding.windSpeedTextView.text="${context.resources.getString(R.string.wind_speed_with_clone)}${CommonMethod.getSpeedValue(list[position].wind_speed)}"
        holder.binding.windDirectionTextView.text="${context.resources.getString(R.string.wind_direction_with_clone)}${CommonMethod.windDegToDir(list[position].wind_deg)}"
        holder.binding.descriptionTextView.text="${context.resources.getString(R.string.description_with_clone)}${list[position].weather[0].description}"
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class HourlyDetailsViewHolder(binding: HourlyForecastDetailsRecyclerViewModelBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding: HourlyForecastDetailsRecyclerViewModelBinding=binding
    }

}