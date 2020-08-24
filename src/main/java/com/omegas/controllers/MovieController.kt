package com.omegas.controllers

import com.omegas.api.moviedb.MovieDAL
import com.omegas.main.Main
import java.net.URL
import java.util.*


class MovieController:MediaController() {

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        super.initialize(location, resources)
        btnPrevious.isDisable = true
        mediaInfo = Main.mediaInfo!!
        folder = mediaInfo.file
        if(mediaInfo.id<0){
            getPosters(mediaInfo)
        }else{
            getPosters(mediaInfo) {
                MovieDAL.getMoviePosters(mediaInfo.id)
            }
        }
    }
    fun downloadPoster(){
        super.downloadPoster(folder.name)
    }

}
