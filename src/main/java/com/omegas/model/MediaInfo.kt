package com.omegas.model

import com.omegas.util.MediaType
import java.io.File

/**
 * @author Muhammad Haris
 * */
data class MediaInfo(var title:String, val mediaType: MediaType, var file:File){
    var year:Int = 0
    var seasonNumber = 0
    var id = -1
    var totalSeasons = -1

    constructor(title:String, mediaType: MediaType, file: File, year: Int):this(title,mediaType,file){
        this.year = year
    }

    constructor(title:String, mediaType: MediaType, seasonNumber:Int, file: File):this(title,mediaType,file){
        this.seasonNumber = seasonNumber
    }

    fun toMovieName():String{
        return "$title ($year)"
    }

    fun toTVSeriesName():String{
        return "$title season $seasonNumber"
    }
}