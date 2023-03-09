package org.gelbelachente.sol

import android.content.SharedPreferences
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import org.json.JSONObject
import java.io.InputStream
import java.time.LocalDate


class WeatherApi(private val scope: CoroutineScope, private val queue: RequestQueue, private val preferences: SharedPreferences) {


    fun updateData(error: (Boolean) -> Unit) {
        if (WeatherData.count.get() == 5) {
            return
        }
        for (place in WeatherData.places) {
            scope.launch {
                retrieveData(place.first to place.second) { error(loadOfflineData()) }
            }
        }
    }

    fun loadStaticAssets(azimuthAsset : InputStream, elevationAsset : InputStream){
        val azimuthReader = azimuthAsset.bufferedReader()
        for((idx,line) in azimuthReader.readLines().withIndex()){
            if(line.isBlank())break
            WeatherData.azimuthAngles[idx] = line.split(" ").take(24).map { it.toFloat() }.toFloatArray()
        }

        val elevationReader = elevationAsset.bufferedReader()
        for((idx,line) in elevationReader.readLines().withIndex()){
            if(line.isBlank())break
            WeatherData.elevationAngles[idx] = line.split(" ").take(24).map { it.toFloat() }.toFloatArray()
        }
    }

    private fun loadOfflineData() : Boolean{
        if(WeatherData.results.isNotEmpty()){
            return true
        }
        val res = preferences.getString("weather_data",null)
        repeat(WeatherData.places.size){
            WeatherData.count.incrementAndGet()
        }
        return if(res == null){
            false
        }else{
            WeatherData.deserializeWeatherData(res)
            true
        }
    }

    fun saveOfflineData(){
        preferences.edit().apply{
            putString("weather_data",WeatherData.serializeWeatherData())
            apply()
        }
    }

    private suspend fun retrieveData(place: Pair<String, Pair<Double, Double>>, error: () -> Unit) {
        val current = LocalDate.now()
        val date = "${current.year}-${current.monthValue.toTime()}-${current.dayOfMonth.toTime()}"
        val future = LocalDate.now().plusDays(2)
        val dateFuture =
            "${future.year}-${future.monthValue.toTime()}-${future.dayOfMonth.toTime()}"
        val url =
            WeatherData.url + "&latitude=${place.second.first}&longitude=${place.second.second}&start_date=$date&end_date=$dateFuture"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                respond(response, place.first,current)
            },
            { e ->
                error()
            }
        )
        queue.add(jsonObjectRequest)
    }

    private fun respond(response: JSONObject, place: String, now : LocalDate) {
        val weather = response.getJSONObject("hourly")
        val temperatureJson = weather.getJSONArray("temperature_2m")
        val windSpeed80Json = weather.getJSONArray("windspeed_80m")
        val windSpeed100Json = weather.getJSONArray("windspeed_120m")
        val directNormalIrradianceJson = weather.getJSONArray("direct_normal_irradiance")
        val hours = mutableListOf<Hour>()
        val weekOfYear = now.dayOfYear % 7
        for(day in 0..2){
            for(hour in 0..23){
                val idx = day*24+hour
                val temp = temperatureJson.getDouble(idx).toFloat()
                val windSpeed = (windSpeed80Json.getDouble(idx) + windSpeed100Json.getDouble(idx)).toFloat() / 2F
                val dni = directNormalIrradianceJson.getDouble(idx).toFloat()
                hours.add(Hour(hour, temp, windSpeed, dni,
                    WeatherData.azimuthAngles[weekOfYear][hour], WeatherData.elevationAngles[weekOfYear][hour]))
            }
        }

        val schedule = arrayOf(
            Day(0,now,hours.subList(0,24).toTypedArray()),
            Day(1,now.plusDays(1),hours.subList(24,48).toTypedArray()),
            Day(2,now.plusDays(2),hours.subList(48,72).toTypedArray()))
        WeatherData.results[place] = schedule
        WeatherData.count.incrementAndGet()
    }


    private fun Int.toTime() : String{
        return if(this < 10){
            "0$this"
        }else{
            this.toString()
        }
    }


}