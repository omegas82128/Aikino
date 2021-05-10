package com.omegas.api.moviedb

import com.omegas.model.Media
import com.omegas.util.MediaType
import com.omegas.util.NotFoundType
import info.movito.themoviedbapi.TmdbMovies
import info.movito.themoviedbapi.TmdbTV

/**
 * @author Muhammad Haris
 * */
object TmdbManager {
    var notFoundType:NotFoundType? = null
    fun searchSeries(showName: String, seasonNumber:Int):List<Media>{
        val mediaList:MutableList<Media> = mutableListOf()
        val tvSeriesList = TvDAL.searchSeries(showName)
        if(tvSeriesList.isNotEmpty()){
            for(tvSeries in tvSeriesList){
                // tvSeries is used to get refreshedTvSeries which has values of totalSeasons and episodes
                val refreshedTvSeries=TvDAL.tmdbTV!!.getSeries(tvSeries.id,null, TmdbTV.TvMethod.credits)
                if(refreshedTvSeries.numberOfSeasons>=seasonNumber){
                    val media = Media(refreshedTvSeries.id,refreshedTvSeries.name,
                        MediaType.TV,refreshedTvSeries.posterPath, refreshedTvSeries.overview)
                    media.totalSeasons = refreshedTvSeries.numberOfSeasons
                    media.currentSeason = seasonNumber
                    media.episodes = refreshedTvSeries.numberOfEpisodes
                    mediaList.add(media)
                }
            }
        }
        return mediaList
    }

    fun searchMovie(movieName: String, year: Int): List<Media> {
        val mediaList:MutableList<Media> = mutableListOf()
        val movieList = MovieDAL.search(movieName,year)
        if(movieList.isNotEmpty()){
            for(movieDB in movieList){
                val movie = MovieDAL.movies!!.getMovie(movieDB.id, null, TmdbMovies.MovieMethod.credits)
                val media = Media(movie.id,movie.title,
                    MediaType.MOVIE,movie.posterPath,movie.overview)
                media.runtime = movie.runtime
                MovieDAL.getDirector(movie)?.let{
                    media.director = it
                }
                media.yearOfRelease = movie.releaseDate.substring(0,4).toInt()
                mediaList.add(media)
            }
        }
        return mediaList
    }
}