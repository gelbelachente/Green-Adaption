package org.gelbelachente.sol

import android.graphics.Color


data class Overview(val id : Int,
                    val greenAverage : Float,
                    val conservativeAverage : Float,
                    val assessment : Assessment,
                    val solarPower : Float,
                    val onshorePower : Float,
                    val offshorePower : Float){

    fun isWind() = this.onshorePower + this.offshorePower > 14.0F
    fun isSun() = this.solarPower > 5.6F


}

val months = arrayOf("Januar","Februar","März","April","Mai","Juni","Juli","August","September","Oktober","November","Dezember")
