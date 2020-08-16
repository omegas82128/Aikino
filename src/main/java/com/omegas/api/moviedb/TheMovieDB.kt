package com.omegas.api.moviedb

import com.omegas.util.Constants.API_KEY
import info.movito.themoviedbapi.TmdbApi
import info.movito.themoviedbapi.TmdbSearch


object TheMovieDb {
    val tmdbApi: TmdbApi = TmdbApi(API_KEY)
    val tmdbSearch: TmdbSearch = TmdbSearch(tmdbApi)
    const val TIMEOUT = 9000
}
