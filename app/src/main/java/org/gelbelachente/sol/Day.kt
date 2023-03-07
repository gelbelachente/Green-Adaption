package org.gelbelachente.sol

import java.time.LocalDate


data class Day(val id : Int, val dateTime : LocalDate, val values : Array<Hour>){
    override fun toString(): String {
        return "$id§${dateTime.year},${dateTime.monthValue},${dateTime.dayOfMonth}§" + values.joinToString(",")
    }
    companion object{
        fun fromString(str : String) : Day{
            val(id,time,hours) = str.split("§")
            val(year,month,day) = time.split(",").map { it.toInt() }
            val values = hours.split(",").map { Hour.fromString(it) }
            return Day(id.toInt(), LocalDate.of(year,month,day),values.toTypedArray())
        }
    }
}