package com.omegas.controllers

import com.omegas.api.moviedb.TvDAL.getShowPosters
import com.omegas.main.SecondMain
import java.net.URL
import java.util.*


class TvSeriesController:MediaController() {
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        btnPrevious.isDisable = true
        mediaInfo = SecondMain.mediaInfo!!
        file = mediaInfo.file
        if(mediaInfo.id<0){
            getPosters(mediaInfo){
                getShowPosters(mediaInfo.title,mediaInfo.seasonNumber)
            }
        }else{
            loadPostersById()
        }
    }
    fun downloadPoster(){
        super.downloadPoster(file.name)
    }
    private fun loadPostersById(){
        getPosters(mediaInfo){
            getShowPosters(mediaInfo.id,mediaInfo.seasonNumber)
        }
    }
}
