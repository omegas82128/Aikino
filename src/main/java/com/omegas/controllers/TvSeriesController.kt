package com.omegas.controllers

import com.omegas.api.moviedb.TvDAL.getShowPosters
import com.omegas.main.Main
import com.omegas.util.WindowType
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import java.net.URL
import java.util.*


/**
 * @author Muhammad Haris
 * */
class TvSeriesController:MediaController() {
    @FXML
    private lateinit var btnNextSeason:Button

    @FXML
    private lateinit var btnPreviousSeason:Button

    @FXML
    private lateinit var lblSeasonInfo:Label
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        super.initialize(location, resources)
        btnNextSeason.setOnAction {
            showNextSeasonPosters()
        }
        btnPreviousSeason.setOnAction {
            showPreviousSeasonPosters()
        }
        if(mediaInfo.id<0){
            getPosters(mediaInfo){
                getShowPosters(mediaInfo.title, mediaInfo.seasonNumber)
            }
        }else{
            loadPostersById()
        }
    }

    override fun downloadPoster(){
        super.downloadPoster(folder.name)
    }

    override fun showSeasonInfo() {
        btnNextSeason.isDisable = Main.mediaInfo!!.totalSeasons == Main.mediaInfo!!.seasonNumber
        btnPreviousSeason.isDisable = Main.mediaInfo!!.seasonNumber <= 1
        lblSeasonInfo.text = "${Main.mediaInfo!!.seasonNumber}/${
            if (Main.mediaInfo!!.totalSeasons < 0) {
                "?"
            } else {
                Main.mediaInfo!!.totalSeasons
            }
        }"
    }

    private fun loadPostersById(){
        getPosters(Main.mediaInfo!!){
            getShowPosters(Main.mediaInfo!!.id,Main.mediaInfo!!.seasonNumber)
        }
    }

    private fun showNextSeasonPosters(){
        if (Main.mediaInfo!!.totalSeasons != Main.mediaInfo!!.seasonNumber){
            Main.mediaInfo!!.seasonNumber++
            reload()
        }
    }

    private fun showPreviousSeasonPosters(){
        if (Main.mediaInfo!!.seasonNumber != 1){
            Main.mediaInfo!!.seasonNumber--
            reload()
        }
    }

    private fun reload(){
        Main.setScene(Main.mediaInfo!!.title, WindowType.TV)
    }
}
