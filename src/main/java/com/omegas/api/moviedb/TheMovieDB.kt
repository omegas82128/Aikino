package com.omegas.api.moviedb

import info.movito.themoviedbapi.TmdbApi
import info.movito.themoviedbapi.TmdbSearch


object TheMovieDb {
    private val API_KEY = System.getenv("TMDB_API_KEY")
    val tmdbApi: TmdbApi = TmdbApi(API_KEY)
    val tmdbSearch: TmdbSearch = TmdbSearch(tmdbApi)
    const val TIMEOUT = 9000
}
