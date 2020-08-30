package com.omegas.controllers

import com.omegas.api.moviedb.TvDAL.getShowPosters
import java.net.URL
import java.util.*


class TvSeriesController:MediaController() {
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        super.initialize(location, resources)
        if(mediaInfo.id<0){
            getPosters(mediaInfo){
                getShowPosters(mediaInfo.title,mediaInfo.seasonNumber)
            }
        }else{
            loadPostersById()
        }
    }
    override fun downloadPoster(){
        super.downloadPoster(folder.name)
    }
    private fun loadPostersById(){
        getPosters(mediaInfo){
            getShowPosters(mediaInfo.id,mediaInfo.seasonNumber)
        }
    }
}
