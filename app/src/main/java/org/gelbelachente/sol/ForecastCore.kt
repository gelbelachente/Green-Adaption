package org.gelbelachente.sol

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Color
import kotlin.math.pow
import kotlin.math.sin

class ForecastCore(private val ctx: Context) {


    fun predict() {
        val solarPredictor = SolarPredictor(ctx)
        val onshorePredictor = OnshorePredictor(ctx)
        val offshorePredictor = OffshorePredictor(ctx)

        for (day in 0..2) {
            var solSum = 0F
            var onshoreSum = 0F
            var offshoreSum = 0F
            for (hour in 0..23) {
                val solar = predictSolarPower(day, hour, solarPredictor)
                val onshore = predictOnshorePower(day, hour, onshorePredictor)
                val offshore = predictOffshorePower(day, hour, offshorePredictor)
                val green = getGreenAverage(onshore, offshore, solar)
                val consumption = getAverageConsumption(hour)
                val fossil = maxOf(0F, consumption - green)
                ForecastData.values[day][hour] = Overview(hour, green, fossil, assess(fossil), solar, onshore, offshore)
                solSum += solar
                onshoreSum += onshore
                offshoreSum += offshore
            }
            val onshore = onshoreSum / 24F
            val offshore = offshoreSum / 24F
            val solar = solSum / 24F
            val avg = 55F
            val green = getGreenAverage(onshore, offshore, solar)
            val cons = maxOf(0F, avg - green)
            ForecastData.overview[day] = Overview(day, green, cons, assess(cons), solar, onshore, offshore)
        }
        solarPredictor.close()
        onshorePredictor.close()
        offshorePredictor.close()
    }


    private fun assess(conservativeUse: Float): Assessment {
        return if(conservativeUse < 13F){
            Assessment.VeryHigh
        }else if(conservativeUse < 26F){
            Assessment.High
        }else if(conservativeUse < 39F){
            Assessment.Average
        }else if(conservativeUse < 52F){
            Assessment.Low
        }else{
            Assessment.VeryLow
        }
    }

    private fun getGreenAverage(onshore: Float, offshore : Float, sol: Float): Float {
        return onshore + offshore + sol + 6F
    }

    //sinus-graph
    private fun getAverageConsumption(hour: Int): Float {
        return (-1 * sin((hour + 3F) * (Math.PI.toFloat() / 12F)) + 5.5F) * 10F
    }

    private fun predictOnshorePower(day: Int, hour: Int, predictor: Predictor): Float {
        val features = WeatherData.getOnshoreFeatures(day, hour)
        return predictor.predict(features)
    }

    private fun predictOffshorePower(day: Int, hour: Int, predictor: Predictor): Float {
        val features = WeatherData.getOffshoreFeatures(day, hour)
        return predictor.predict(features)
    }

    private fun predictSolarPower(day: Int, hour: Int, predictor: Predictor): Float {
        val features = WeatherData.getSolarFeatures(day, hour)
        return predictor.predict(features)
    }

}