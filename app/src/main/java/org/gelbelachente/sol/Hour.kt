package org.gelbelachente.sol

data class Hour(val id : Int, val temperature : Float, val windSpeed : Float, val dni : Float, val azimuthAngle : Float, val elevationAngle : Float){

    override fun toString(): String {
        return "$id!$temperature!$windSpeed!$dni!$azimuthAngle!$elevationAngle"
    }
    companion object{
        fun fromString(str : String) : Hour{
            val (id,temp,ws,dni,aa,ea) = str.split("!").map { it.toFloat() }
            return Hour(id.toInt(),temp,ws,dni,aa,ea)
        }
    }
}

private operator fun <E> List<E>.component6(): E {
    return this[5]
}
