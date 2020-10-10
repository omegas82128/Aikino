package com.omegas.util.functions

import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.file.Files

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

fun File.refresh() { // creates and deletes files in folder so Windows Explorer updates Icon
    if (this.isDirectory) {
        val times = listOf(1000L, 2000L)
        val range = (0..Int.MAX_VALUE)
        for (time in times) {
            val temp = File("${this.absolutePath}\\${range.random()}.temp")
            temp.createNewFile()
            Files.setAttribute(temp.toPath(), "dos:hidden", true)
            Thread.sleep(time)
            temp.delete()
        }
    }
}