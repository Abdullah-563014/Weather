package weather.app.live.update.forecast.utils.mpchart

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat
import kotlin.math.roundToInt

class MyValueFormatter: ValueFormatter() {

    private val format = DecimalFormat("###.#")
    // override this for e.g. LineChart or ScatterChart
    override fun getPointLabel(entry: Entry?): String {
        var result: String
        try {
            result="${entry?.y?.roundToInt()}\u2103"
        } catch (e: Exception) {
            result=format.format(entry?.y)+"\u2103"
        }
        return result
    }
    // override this for BarChart
    override fun getBarLabel(barEntry: BarEntry?): String {
        return format.format(barEntry?.y)+"\u2103"
    }
    // override this for custom formatting of XAxis or YAxis labels
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return format.format(value)+"\u2103"
    }
    // ... override other methods for the other chart types

}