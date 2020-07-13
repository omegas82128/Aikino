package com.omegas.controllers

import com.omegas.api.moviedb.MovieDAL
import com.omegas.main.SecondMain
import java.net.URL
import java.util.*


class MovieController:MediaController() {

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        btnPrevious.isDisable = true
        mediaInfo = SecondMain.mediaInfo!!
        file = mediaInfo.file
        if(mediaInfo.id<0){
            getPosters(mediaInfo)
        }else{
            getPosters(mediaInfo) {
                MovieDAL.getMoviePosters(mediaInfo.id)
            }
        }
    }
    fun downloadPoster(){
        super.downloadPoster(file.name)
    }

}
