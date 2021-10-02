package com.omegas.main

import com.omegas.controller.MovieController
import com.omegas.controller.SearchController
import com.omegas.controller.TvSeriesController
import com.omegas.model.MediaInfo
import com.omegas.service.UpdateService
import com.omegas.util.*
import com.omegas.util.Constants.APP_NAME
import com.omegas.util.functions.refresh
import com.omegas.util.functions.showMessage
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.io.File
import kotlin.system.exitProcess

/**
 * @author Muhammad Haris
 * */
class Main : Application() {
    override fun start(primaryStage: Stage?) {
        stage = primaryStage!!
        primaryStage.icons?.add(Constants.ICON)
        stage.isResizable = false
        stage.setOnCloseRequest {
            stage.hide()
            exitProcess(0)
        }
        if (this.parameters.raw.isNotEmpty()) {
            startAikino(this.parameters.raw)
        } else {
            setScene("Start Menu", WindowType.START)
        }
        stage.show()
    }

    companion object {
        lateinit var stage: Stage
        var mediaInfo: MediaInfo? = null
        fun startAikino(parameters: MutableList<String>) {
            mediaInfo = FolderNameParser(File(parameters.first())).mediaInfo
            if (mediaInfo == null) {
                showMessage(
                    "Incomplete information in folder name.",
                    title = "Invalid Folder Name"
                )
                Thread.sleep(10000)
                exitProcess(1)
            } else {
                val windowType: WindowType = when (mediaInfo!!.mediaType) {
                    MediaType.TV -> WindowType.TV
                    MediaType.MOVIE -> WindowType.MOVIE
                    else -> {
                        throw IllegalStateException("MediaType is Unknown")
                    }
                }
                setScene(mediaInfo!!.title, windowType)
                stage.sizeToScene()
            }
        }

        fun setScene(title: String, windowType: WindowType){
            try {
                val fxmlPath:String = when (windowType) {
                    WindowType.MOVIE, WindowType.TV -> "Media"
                    WindowType.SEARCH -> "Search"
                    WindowType.START -> "Start"
                }
                val fxmlLoader =FXMLLoader(Main::class.java.getResource("/fxml/${fxmlPath}Window.fxml"))

                when(windowType){
                    WindowType.MOVIE -> fxmlLoader.setController(MovieController())
                    WindowType.TV -> fxmlLoader.setController(TvSeriesController())
                    else -> {}
                }

                val root = fxmlLoader.load<Parent>()
                stage.title = "$APP_NAME - $title"
                stage.scene = Scene(root)
                if (windowType == WindowType.SEARCH) {
                    fxmlLoader.getController<SearchController>().search()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size == 2 && args[0].equals("-r", true)) {
                File(args[1]).refresh(2)
                exitProcess(1)
            }
            try {
                UpdateService.automaticStart()
                launch(Main::class.java, *args)
            } catch (exception: Exception) {
                exception.printStackTrace()
                showMessage("TheMovieDB.org cannot be reached.", AlertType.ERROR, "Connection Error:")
                exitProcess(1)
            }
        }
    }
}
