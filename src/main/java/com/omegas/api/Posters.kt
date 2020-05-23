package com.omegas.api

import com.omegas.api.TheMovieDb
import java.io.File

object Posters {
    fun getPosters(movieName:String):MutableList<String>{
        return TheMovieDb.getMoviePosters(movieName)
    }
    fun getPoster(file: File):String{
        return TheMovieDb.getMoviePoster(file)
    }
}