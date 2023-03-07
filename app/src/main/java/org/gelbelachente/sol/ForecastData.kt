package org.gelbelachente.sol

import android.content.Context
import android.content.res.AssetFileDescriptor
import kotlin.math.sin

object ForecastData {

    val overview: Array<Overview?> = Array(3) { null }
    val values: Array<Array<Overview?>> = Array(3) { Array(24) { null } }

}