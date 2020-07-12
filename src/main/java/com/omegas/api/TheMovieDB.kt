package com.omegas.api

import info.movito.themoviedbapi.TmdbApi
import info.movito.themoviedbapi.TmdbMovies
import info.movito.themoviedbapi.TmdbSearch
import info.movito.themoviedbapi.TmdbTvSeasons
import info.movito.themoviedbapi.model.ArtworkType
import info.movito.themoviedbapi.model.MovieDb
import info.movito.themoviedbapi.model.core.MovieResultsPage
import info.movito.themoviedbapi.model.tv.TvSeries
import javafx.scene.control.Alert
import java.io.File


object TheMovieDb {
    private val API_KEY = System.getenv("TMDB_API_KEY")
    private val tmdbApi: TmdbApi = TmdbApi(API_KEY)
    val tmdbSearch: TmdbSearch = TmdbSearch(tmdbApi)
    private val movies: TmdbMovies = tmdbApi.movies
    val tvSeasons: TmdbTvSeasons = tmdbApi.tvSeasons
    fun getMoviePoster(file: File): String{
        val movieName = file.name.substring(0, file.name.lastIndexOf(" ("))
        val year =
            file.name.substring(file.name.lastIndexOf("(") + 1, file.name.lastIndexOf(")")).toInt()
        return getMoviePoster(movieName, year)
    }

    private fun search(movieName: String, year: Int): List<MovieDb> {
        val page: MovieResultsPage = tmdbSearch.searchMovie(movieName, year, null, false, 0)
        return page.results
    }
    private fun getMovie(movieName: String, year: Int):MovieDb?{
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
    private fun getMoviePoster(movieName: String, year: Int): String {
        var movieDb = getMovie(movieName, year)
        if (movieDb != null) {
            movieDb = movies.getMovie(movieDb.id, null)
            return movieDb.posterPath
        }
        return ""
    }

    private fun movieFromList(movieDbList: List<MovieDb>,movieName: String,year: Int): MovieDb? {
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

    fun getMoviePosters(fileName:String):MutableList<String>{
        val movieName = fileName.substring(0, fileName.lastIndexOf(" ("))
        val year =
            fileName.substring(fileName.lastIndexOf("(") + 1, fileName.lastIndexOf(")")).toInt()
        return getMoviePosters(movieName, year)
    }
    private fun getMoviePosters(movieName: String, year: Int):MutableList<String> {
         return getMoviePosters(
             getMovie(
                 movieName,
                 year
             )?.id
         )
    }
    fun getMovieName(movieId: Int?):String?{
        return movieId?.let { movies.getMovie(it, null).title }
    }

    fun getShowName(showId: Int?, seasNumber:Int=1):String?{
        return showId?.let { tvSeasons.getSeason(showId,seasNumber,null).name }
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

    fun getShowPosters(showName:String,seasonNumber: Int):MutableList<String>{
        val page = tmdbSearch.searchTv(showName,null,0)
        val results = page.results
        val posterList = mutableListOf<String>()
        if(results.isNotEmpty()){
            val series: TvSeries = findSeries(showName,results)
            val season = tvSeasons.getSeason(series.id,seasonNumber,null,TmdbTvSeasons.SeasonMethod.images)
            val posters = season.images
            series.posterPath?.let{
                posterList.add(it)
            }
            for(poster in posters.posters){
                posterList.add(poster.filePath)
            }
        }
        return posterList
    }

    private fun findSeries(showName: String, results: List<TvSeries>): TvSeries {
        var series:TvSeries? = null
        for(result in results){
            val name = result.name
            if(name == showName){
                series = result
            }
        }
        if(series == null || results[0].name == showName){
            series = results[0]
        }
        return series
    }
}


fun main(){
    val showName = "Space Dandy"
    val seasonNumber = 1
    val page = TheMovieDb.tmdbSearch.searchTv(showName,null,0)
    val results = page.results
    val posterList = mutableListOf<String>()
    if(results.isNotEmpty()){
        val season = TheMovieDb.tvSeasons.getSeason(results[0].id,seasonNumber,null,TmdbTvSeasons.SeasonMethod.images)
        val posters = season.images
        posterList.add(results[0].posterPath)
        for(poster in posters.posters){
            posterList.add(poster.filePath)
            println(poster.filePath)
        }
    }else{
        println("failed")
    }
    //for(season in results){
        //ImageDownloader.download(posterPath,Constants.LOCATION.absolutePath+"\\$showName\\")
       // */
}
