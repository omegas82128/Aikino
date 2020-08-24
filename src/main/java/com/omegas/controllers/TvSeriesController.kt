package com.omegas.controllers

import com.omegas.api.moviedb.TvDAL.getShowPosters
import com.omegas.main.Main
import java.net.URL
import java.util.*


class TvSeriesController:MediaController() {
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        super.initialize(location, resources)
        btnPrevious.isDisable = true
        mediaInfo = Main.mediaInfo!!
        folder = mediaInfo.file
        if(mediaInfo.id<0){
            getPosters(mediaInfo){
                getShowPosters(mediaInfo.title,mediaInfo.seasonNumber)
            }
        }else{
            loadPostersById()
        }
    }
    fun downloadPoster(){
        super.downloadPoster(folder.name)
    }
    private fun loadPostersById(){
        getPosters(mediaInfo){
            getShowPosters(mediaInfo.id,mediaInfo.seasonNumber)
        }
    }
}
