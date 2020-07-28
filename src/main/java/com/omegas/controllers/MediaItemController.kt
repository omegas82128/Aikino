package com.omegas.controllers

import com.omegas.api.moviedb.TheMovieDb.TIMEOUT
import com.omegas.main.Main
import com.omegas.main.Main.Companion.changeScene
import com.omegas.model.Media
import com.omegas.util.Constants
import com.omegas.util.MediaType
import com.omegas.util.toHoursAndMinutes
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.net.URL
import java.util.*

class MediaItemController:Initializable {
    @FXML
    lateinit var lblTitle: Label
    @FXML
    lateinit var lblYear: Label
    @FXML
    lateinit var lblDirectorOrSeasons: Label
    @FXML
    lateinit var lblRuntimeOrEpisodes:Label
    @FXML
    lateinit var imageView:ImageView
    @FXML
    lateinit var txtOverview:TextArea
    var media: Media? = null
    set(media){
        field = media
        field?.let{
            if(it.posterPath!=null){
                val url = URL(it.posterPath)
                val con = url.openConnection()
                con.connectTimeout = TIMEOUT
                con.readTimeout = TIMEOUT

                imageView.image = Image(url.openStream(),126.6667,190.0,true,true)
            }else{
                imageView.image = Image(Constants.PLACEHOLDER_IMAGE_PATH)
            }

            lblTitle.text = it.title

            txtOverview.text = it.overView

            when(it.mediaType){
                MediaType.MOVIE -> {
                    lblYear.text= "(${it.yearOfRelease})"
                    lblDirectorOrSeasons.text = it.director
                    lblRuntimeOrEpisodes.text = it.runtime.toHoursAndMinutes()
                }
                MediaType.TV -> {
                    lblTitle.prefWidth = 260.0
                    lblYear.text=""
                    lblDirectorOrSeasons.text = "${it.totalSeasons} seasons"
                    lblRuntimeOrEpisodes.text = "${it.episodes} total episodes"
                }
            }
        }
    }
    override fun initialize(location: URL?, resources: ResourceBundle?) {
    }
    fun select(){
        Main.mediaInfo?.let {
            val media = this.media!!
            it.id = media.id
            it.title = media.title
            when(media.mediaType){
                MediaType.MOVIE ->{
                    it.year = media.yearOfRelease
                    changeScene("Movie",it.title)
                }
                MediaType.TV ->{
                    it.seasonNumber = media.currentSeason
                    changeScene("TvSeries",it.title)
                }
            }
        }
    }

}