package com.omegas.moviedb

import com.omegas.main.Main
import com.omegas.moviedb.TmdbManager.notFoundType
import com.omegas.util.NotFoundType
import info.movito.themoviedbapi.TmdbTV
import info.movito.themoviedbapi.TmdbTvSeasons
import info.movito.themoviedbapi.model.tv.TvSeries

/**
 * @author Muhammad Haris
 * */
object TvDAL {
    private val tvSeasons: TmdbTvSeasons? = TheMovieDb.tmdbApi?.tvSeasons
    val tmdbTV: TmdbTV? = TheMovieDb.tmdbApi?.tvSeries
    fun getShowPosters(showName:String,seasonNumber: Int):MutableList<String>{
        val results = searchSeries(showName)
        val posterList = mutableListOf<String>()
        if(results.isNotEmpty()){
            val series: TvSeries = findSeries(showName, results)
            val season = tvSeasons?.getSeason(series.id,seasonNumber,null, TmdbTvSeasons.SeasonMethod.images) ?: return posterList
            val posters = season.images
            for(poster in posters.posters){
                posterList.add(poster.filePath)
            }
            series.posterPath?.let{
                posterList.add(it)
            }
            if(posterList.isEmpty()){
                notFoundType = NotFoundType.POSTER_NOT_FOUND
            }
            Main.mediaInfo!!.totalSeasons = tmdbTV!!.getSeries(series.id,null).numberOfSeasons
            Main.mediaInfo!!.id = series.id
        }else{
            notFoundType = NotFoundType.MEDIA_NOT_FOUND
        }
        return posterList
    }

    fun getShowPosters(showId:Int,seasonNumber: Int):MutableList<String> {
        val posterList = mutableListOf<String>()
        val series: TvSeries = tmdbTV?.getSeries(showId, null) ?: return posterList
        val season = tvSeasons!!.getSeason(series.id, seasonNumber, null, TmdbTvSeasons.SeasonMethod.images)
        val posters = season.images
        for (poster in posters.posters) {
            posterList.add(poster.filePath)
        }
        series.posterPath?.let {
            posterList.add(it)
        }
        return posterList
    }

    fun searchSeries(showName: String):List<TvSeries>{
        val page = TheMovieDb.tmdbSearch.searchTv(showName,null,0)
        return page.results
    }

    private fun findSeries(showName: String, results: List<TvSeries>): TvSeries {
        var series: TvSeries? = null
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