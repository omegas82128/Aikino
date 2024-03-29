package com.omegas.controller

import com.omegas.main.Main
import com.omegas.main.Main.Companion.setScene
import com.omegas.model.Media
import com.omegas.moviedb.TheMovieDb.TIMEOUT
import com.omegas.util.Constants
import com.omegas.util.MediaType
import com.omegas.util.WindowType
import com.omegas.util.functions.toHoursAndMinutes
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.net.URL
import java.util.*


/**
 * @author Muhammad Haris
 * */
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
    lateinit var txtOverview: TextArea
    var media: Media? = null
        set(media) {
            field = media
            field?.let {
                if (it.posterPath != null) {
                    val url = URL(it.posterPath)
                    val con = url.openConnection()
                    con.connectTimeout = TIMEOUT
                    con.readTimeout = TIMEOUT

                    imageView.image = Image(url.openStream(), 126.6667, 190.0, true, true)
                } else {
                    imageView.image = Constants.NOT_FOUND_IMAGE
                }

                lblTitle.text = it.title

                txtOverview.text = if (it.overView.isNotEmpty()) {
                    it.overView
                } else {
                    "Synopsis not available".uppercase(Locale.getDefault())
                }

                when (it.mediaType) {
                    MediaType.MOVIE -> {
                        lblYear.text = "(${it.yearOfRelease})"
                        lblDirectorOrSeasons.text = it.director
                        lblRuntimeOrEpisodes.text = it.runtime.toHoursAndMinutes()
                    }
                    MediaType.TV -> {
                        lblTitle.prefWidth = 260.0
                        lblYear.text = ""
                        lblDirectorOrSeasons.text = "${it.totalSeasons} seasons"
                        lblRuntimeOrEpisodes.text = "${it.episodes} total episodes"
                    }
                    else -> {
                        throw IllegalStateException("MediaType is Unknown")
                    }
                }
            }
        }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
    }

    fun select() {
        Main.mediaInfo?.let {
            val media = this.media!!
            it.id = media.id
            it.title = media.title
            when (media.mediaType) {
                MediaType.MOVIE -> {
                    it.year = media.yearOfRelease
                    setScene(it.title, WindowType.MOVIE)
                }
                MediaType.TV -> {
                    it.seasonNumber = media.currentSeason
                    it.totalSeasons = media.totalSeasons
                    setScene(it.title, WindowType.TV)
                }
                else -> {
                    throw IllegalStateException("MediaType is Unknown")
                }
            }
        }
    }

}