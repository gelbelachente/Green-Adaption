package org.gelbelachente.sol

import java.util.concurrent.atomic.AtomicInteger

object WeatherData{
    const val url = "https://api.open-meteo.com/v1/dwd-icon?hourly=temperature_2m,windspeed_80m,windspeed_120m,direct_normal_irradiance"

    val places = arrayOf("Hamburg" to (53.6 to 10.0), "Köln" to (50.9 to 7.0), "Kassel" to (51.3 to 9.5),
        "Leipzig" to (51.1 to 13.7), "Augsburg" to (48.4 to 10.9), "Rügen" to (54.4 to 13.4), "Sylt" to (54.9 to 8.3))

    val results = mutableMapOf<String,Array<Day>>()
    val azimuthAngles = Array<FloatArray>(53){ floatArrayOf() }
    val elevationAngles = Array<FloatArray>(53){ floatArrayOf() }
    val count = AtomicInteger(0)

    fun getSolarFeatures(day : Int, hour : Int) : FloatArray{
        val features = FloatArray(12)
        var pos = 0
        for((place,_) in places.take(5)){
            val h = results[place]!![day].values[hour]
            features[0] = h.azimuthAngle
            features[1] = h.elevationAngle
            features[pos*2+2] = h.temperature
            features[pos*2+2+1] = h.dni
            pos++
        }
        return features
    }

    fun getOnshoreFeatures(day : Int, hour : Int) : FloatArray{
        val features = FloatArray(10)
        var pos = 0
        for((place,_) in places.take(5)){
            val h = results[place]!![day].values[hour]
            features[pos*2] = h.temperature
            features[pos*2+1] = h.windSpeed
            pos++
        }
        return features
    }

    fun getOffshoreFeatures(day : Int, hour : Int) : FloatArray{
        val features = FloatArray(6)
        var pos = 0
        for((place,_) in places.takeLast(3)){
            val h = results[place]!![day].values[hour]
            features[pos*2] = h.temperature
            features[pos*2+1] = h.windSpeed
            pos++
        }
        return features
    }

    fun serializeWeatherData() : String{
        var str = StringBuilder()
        for((place,_) in places){
            val days = results[place]!!
            str.append("$place=${days[0].toString()}%${days[1].toString()}%${days[2].toString()}" + if(place == places.last().first) "" else "?")
        }
        return str.toString()
    }

    fun deserializeWeatherData(string: String){
        val places = string.split("?")
        for(i in places){
            val (place,data) = i.split("=")
            val days = data.split("%").map { Day.fromString(it) }
            results[place] = days.toTypedArray()
        }
    }

}