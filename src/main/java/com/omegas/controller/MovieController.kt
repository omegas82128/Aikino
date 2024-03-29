package com.omegas.controller

import com.omegas.model.MediaInfo
import com.omegas.moviedb.MovieDAL
import javafx.fxml.FXML
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import java.net.URL
import java.util.*


/**
 * @author Muhammad Haris
 * */
class MovieController:MediaController() {
    @FXML
    private lateinit var seasonBorderPane: BorderPane

    @FXML
    private lateinit var actionsVBox: VBox
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        super.initialize(location, resources)
        actionsVBox.children.remove(seasonBorderPane)
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

    override fun showSeasonInfo() {}

    private val getMoviePosters : (mediaInfo: MediaInfo) -> MutableList<String> = { mediaInfo->
        MovieDAL.getMoviePosters(mediaInfo)
    }
}
