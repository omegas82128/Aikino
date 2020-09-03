package com.omegas.util.functions

import java.math.BigDecimal
import java.math.RoundingMode

fun Int.toHoursAndMinutes():String{
    var hoursAndMinutes:String = if(this>60){
        "${this/60} hours and "
    }else{
        ""
    }
    hoursAndMinutes+="${this%60} minutes"

    return hoursAndMinutes
}

fun Double.round(decimals: Int): Double {
    return BigDecimal(this).setScale(decimals, RoundingMode.HALF_EVEN).toDouble()
}