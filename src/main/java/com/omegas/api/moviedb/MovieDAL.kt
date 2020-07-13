package com.omegas.api.moviedb

import com.omegas.model.MediaInfo
import info.movito.themoviedbapi.TmdbMovies
import info.movito.themoviedbapi.model.ArtworkType
import info.movito.themoviedbapi.model.MovieDb
import info.movito.themoviedbapi.model.core.MovieResultsPage
import javafx.scene.control.Alert

object MovieDAL {
    val movies: TmdbMovies = TheMovieDb.tmdbApi.movies

    fun search(movieName: String, year: Int): List<MovieDb> {
        val page: MovieResultsPage = TheMovieDb.tmdbSearch.searchMovie(movieName, year, null, false, 0)
        return page.results
    }
    fun getDirector(movieDb: MovieDb):String? {
        for (crew in movieDb.crew) {
            if (crew.job.equals("Director", ignoreCase = true)) {
                return crew.name
            }
        }
        return null
    }
    private fun getMovie(movieName: String, year: Int): MovieDb?{
        var movieDbList: List<MovieDb> = search(movieName, year)
        if (movieDbList.isEmpty()){
            movieDbList = search(movieName, year + 1)
            if (movieDbList.isEmpty()) {
                movieDbList = search(movieName, year - 1)
                if (movieDbList.isEmpty()) {
                    val alert = Alert(Alert.AlertType.INFORMATION, "$movieName ($year) not found")
                    alert.showAndWait()
                    return null
                }
            }
        }
        return movieFromList(movieDbList, movieName, year)
    }

    private fun movieFromList(movieDbList: List<MovieDb>, movieName: String, year: Int): MovieDb? {
        for (movie in movieDbList) {
            if (isMovie(movie, movieName, year)) {
                return movie
            }
        }
        for (movie in movieDbList) {
            if (isPossiblyMovie(movie, movieName, year)) {
                return movie
            }
        }
        for (movie in movieDbList) {
            if (canBeMovie(movie, movieName, year)) {
                return movie
            }
        }
        return null
    }
    private fun isMovie(movieDb: MovieDb, movieName: String, year: Int): Boolean {
        return (movieDb.title.replace(":", "").replace("?", "").trim { it <= ' ' }.equals(
            movieName,
            ignoreCase = true
        )
                && movieDb.releaseDate.startsWith(year.toString()))
    }
    private fun isPossiblyMovie(movieDb: MovieDb, movieName: String, year: Int): Boolean {
        return (movieDb.title.replace(":", "").replace("?", "").trim { it <= ' ' }.equals(
            movieName,
            ignoreCase = true
        )
                && (movieDb.releaseDate.startsWith(year.toString())
                || movieDb.releaseDate.startsWith((year - 1).toString())
                || movieDb.releaseDate.startsWith((year + 1).toString())))
    }
    private fun canBeMovie(movieDb: MovieDb, movieName: String, year: Int): Boolean {
        return (movieDb.title.replace(":", "").replace("?", "").trim { it <= ' ' }.equals(
            movieName,
            ignoreCase = true
        )
                || movieDb.releaseDate.startsWith(year.toString())
                || movieDb.releaseDate.startsWith((year - 1).toString())
                || movieDb.releaseDate.startsWith((year + 1).toString()))
    }

    fun getMoviePosters(mediaInfo: MediaInfo):MutableList<String>{
        return getMoviePosters(mediaInfo.title, mediaInfo.year)
    }
    private fun getMoviePosters(movieName: String, year: Int):MutableList<String> {
        return getMoviePosters(
            getMovie(
                movieName,
                year
            )?.id
        )
    }

    fun getMoviePosters(movieId: Int?):MutableList<String> {
        val posterList:MutableList<String> = mutableListOf()
        if (movieId != null) {
            val movieDb = movies.getMovie(movieId, null, TmdbMovies.MovieMethod.images)
            for (poster in movieDb.getImages(ArtworkType.POSTER))
                posterList.add(poster.filePath)
        }
        return posterList
    }
}