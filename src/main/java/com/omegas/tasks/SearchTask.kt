package com.omegas.tasks

import com.omegas.controllers.MediaItemController
import com.omegas.model.Media
import com.omegas.moviedb.TmdbManager
import com.omegas.util.MediaType
import com.omegas.util.functions.addComponent
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.concurrent.Task
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint


/**
 * @author Muhammad Haris
 * */
class SearchTask(
    private val vBox: VBox, private val mediaType: MediaType, private val name: String,
    private val number: Int
) : Task<List<Media>>() {

    override fun call(): List<Media> {
        try {
            val mediaList: MutableList<Media> = mutableListOf()
            when (mediaType) {
                MediaType.MOVIE -> {
                    mediaList.addAll(TmdbManager.searchMovie(name, number))
                }
                MediaType.TV -> {
                    mediaList.addAll(TmdbManager.searchSeries(name, number))
                }
                else -> {
                    throw IllegalStateException("MediaType is Unknown")
                }
            }

            return mediaList
        }catch (ex:Exception){
            ex.printStackTrace()
            return emptyList()
        }
    }

    override fun succeeded() {
        vBox.children.clear()
        if(value.isNotEmpty()){
            for(media in value){
                val fxmlLoader = FXMLLoader()
                val mediaItem =
                    fxmlLoader.load<BorderPane>(javaClass.getResource("/fxml/partials/MediaItem.fxml")!!.openStream())
                fxmlLoader.getController<MediaItemController>().media = media
                vBox.children.add(mediaItem)
            }
        }else {
            val mediaType: String = when (mediaType) {
                MediaType.MOVIE -> "MOVIE"
                MediaType.TV -> "TV SERIES"
                else -> ""
            }
            val label = Label("NO RESULTS FOUND FOR $mediaType '${name.toUpperCase()}'")
            label.style = "-fx-font-weight: bold;"
            label.textFill = Paint.valueOf("white")

            label.alignment = Pos.CENTER
            label.isWrapText = true
            val fontSize = SimpleDoubleProperty(10.0)
            val blues = SimpleIntegerProperty(50)
            fontSize.bind(vBox.widthProperty().add(vBox.heightProperty()).divide(50))
            label.styleProperty().bind(
                Bindings.concat(
                    "-fx-font-size: ", fontSize.asString(), ";", "-fx-base: rgb(100,100,", blues.asString(), ");"
                )
            )
            addComponent(vBox, label)
        }
    }
}