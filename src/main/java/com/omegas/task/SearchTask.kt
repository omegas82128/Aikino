package com.omegas.task

import com.omegas.controller.MediaItemController
import com.omegas.model.Media
import com.omegas.moviedb.TmdbManager
import com.omegas.util.MediaType
import com.omegas.util.functions.addComponent
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.concurrent.Task
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread


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
        } catch (ex: Exception) {
            ex.printStackTrace()
            return emptyList()
        }
    }

    override fun succeeded() {
        if (value.isNotEmpty()) {
            val executor: ExecutorService = Executors.newFixedThreadPool(
                7.coerceAtLeast(
                    Runtime.getRuntime().availableProcessors()
                )
            )
            var isCleared = false
            for (media in value) {
                executor.submit(thread(false) {
                    val fxmlLoader = FXMLLoader()
                    val mediaNode =
                        fxmlLoader.load<BorderPane>(
                            javaClass.getResource("/fxml/partials/MediaItem.fxml")!!.openStream()
                        )
                    fxmlLoader.getController<MediaItemController>().media = media
                    Platform.runLater {
                        if (!isCleared) {
                            vBox.children.clear()
                            isCleared = true
                        }
                        vBox.children.add(mediaNode)
                    }
                })
            }
        } else {
            vBox.children.clear()
            val mediaType: String = when (mediaType) {
                MediaType.MOVIE -> "MOVIE"
                MediaType.TV -> "TV SERIES"
                else -> ""
            }
            val label = Label("NO RESULTS FOUND FOR $mediaType '${name.uppercase(Locale.getDefault())}'")
            label.style = "-fx-font-weight: bold;"
            label.textFill = Paint.valueOf("white")

            label.alignment = Pos.CENTER
            label.isWrapText = true
            val fontSize = SimpleDoubleProperty(10.0)
            fontSize.bind(vBox.widthProperty().add(vBox.heightProperty()).divide(50))
            label.styleProperty().bind(
                Bindings.concat(
                    "-fx-font-size: ", fontSize.asString(), ";", "-fx-base: rgb(100,100,50);"
                )
            )
            addComponent(vBox, label)
        }
    }
}