package com.omegas.util.functions

import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

fun Int.toHoursAndMinutes():String{
    var hoursAndMinutes:String = if(this>60){
        "${this/60} hours and "
    }else{
        ""
    }
    hoursAndMinutes += "${this % 60} minutes"

    return hoursAndMinutes
}

fun Double.round(decimals: Int): Double {
    return BigDecimal(this).setScale(decimals, RoundingMode.HALF_EVEN).toDouble()
}

fun File.refresh(iterations: Int = 2) { // creates and deletes files in folder so Windows Explorer updates Icon
    if (this.isDirectory) {
        val times: MutableList<Long> = mutableListOf()
        for (iteration in 1..iterations) {
            times.add(
                iteration * 1000L
            )
        }
        val range = (0..Int.MAX_VALUE)
        for (time in times) {
            Thread.sleep(time)
            val temp = File("${this.absolutePath}\\${range.random()}.temp")
            temp.createNewFile()
            temp.delete()
        }
    }
}