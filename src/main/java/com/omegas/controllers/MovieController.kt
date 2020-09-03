package com.omegas.controllers

import com.omegas.api.moviedb.MovieDAL
import com.omegas.model.MediaInfo
import java.net.URL
import java.util.*


class MovieController:MediaController() {

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        super.initialize(location, resources)
        if(mediaInfo.id<0){
            getPosters(mediaInfo, function = getMoviePosters)
        }else{
            getPosters(mediaInfo) {
                MovieDAL.getMoviePosters(mediaInfo.id)
            }
        }
    }
    override fun downloadPoster(){
        super.downloadPoster(folder.name)
    }

    private val getMoviePosters : (mediaInfo: MediaInfo) -> MutableList<String> = { mediaInfo->
        MovieDAL.getMoviePosters(mediaInfo)
    }
}
