package com.omegas.api.moviedb

import com.omegas.util.Constants.API_KEY
import info.movito.themoviedbapi.TmdbApi
import info.movito.themoviedbapi.TmdbSearch


object TheMovieDb {
    val tmdbApi: TmdbApi? = try {
        TmdbApi(API_KEY)
    }catch (exception:Exception){
        exception.printStackTrace()
        null
    }
    val tmdbSearch: TmdbSearch = TmdbSearch(tmdbApi)
    const val TIMEOUT = 9000
    fun isConnected():Boolean{
        return tmdbApi!=null
    }
    fun isNotConnected():Boolean{
        return !isConnected()
    }
}
