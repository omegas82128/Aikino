package com.omegas.util.functions

fun Int.toHoursAndMinutes():String{
    var hoursAndMinutes:String = if(this>60){
        "${this/60} hours and "
    }else{
        ""
    }
    hoursAndMinutes+="${this%60} minutes"

    return hoursAndMinutes
}