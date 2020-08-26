package com.omegas.main

import com.omegas.api.NameParser
import com.omegas.controllers.MovieController
import com.omegas.controllers.SearchController
import com.omegas.controllers.TvSeriesController
import com.omegas.model.MediaInfo
import com.omegas.util.AlertType
import com.omegas.util.Constants
import com.omegas.util.Constants.APP_NAME
import com.omegas.util.MediaType
import com.omegas.util.WindowType
import com.omegas.util.functions.showMessage
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File
import kotlin.system.exitProcess

class Main : Application() {
    override fun start(primaryStage: Stage?) {
        primaryStage?.icons?.add(Constants.ICON)
        if (args.isNotEmpty()){
            stage = primaryStage!!
            mediaInfo = NameParser.getMediaInfo(File(args[0]))
            if(mediaInfo==null){
                showMessage(
                    "Incomplete information in folder name.",
                    title = "Invalid Folder Name"
                )
                Thread.sleep(10000)
                exitProcess(0)
            }else{
                val windowType:WindowType = when(mediaInfo!!.mediaType) {
                    MediaType.TV-> WindowType.TV
                    MediaType.MOVIE-> WindowType.MOVIE
                }

                setScene(mediaInfo!!.title, windowType)

                stage.sizeToScene()
                stage.show()
                stage.isResizable = false
                stage.setOnCloseRequest {
                    exitProcess(0)
                }
            }
        }else{

            primaryStage?.let {
                it.height = 0.0
                it.width = 0.0
                it.show()
            }

            val directoryChooser = DirectoryChooser()
            directoryChooser.title = "Choose a Movie or TV series folder"
            val file = directoryChooser.showDialog(primaryStage)

            if(file!=null){
                args = arrayOf(file.absolutePath)
                primaryStage?.hide()
                start(primaryStage)
                return
            }
            primaryStage?.hide()
            showMessage()
            Thread.sleep(3000)
            exitProcess(0)
        }

    }

    companion object{
        lateinit var args:Array<String>
        lateinit var stage: Stage
        var mediaInfo:MediaInfo? = null
        fun setScene(title: String, windowType: WindowType){
            try {
                val fxmlPath:String = when(windowType){
                    WindowType.MOVIE,WindowType.TV -> "Media"
                    WindowType.SEARCH ->"Search"
                }
                val fxmlLoader =FXMLLoader(Main::class.java.getResource("/fxml/${fxmlPath}Window.fxml"))

                when(windowType){
                    WindowType.MOVIE -> fxmlLoader.setController(MovieController())
                    WindowType.TV -> fxmlLoader.setController(TvSeriesController())

                    WindowType.SEARCH -> {
                    }
                }

                val root = fxmlLoader.load<Parent>()
                stage.title = "$APP_NAME - $title"
                stage.scene = Scene(root)
                if (windowType == WindowType.SEARCH){
                    fxmlLoader.getController<SearchController>().search()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        @JvmStatic
        fun main(args:Array<String>){
            Companion.args = args
            try{
                launch(Main::class.java)
            }catch (exception:Exception){
                exception.printStackTrace()
                showMessage("TheMovieDB.org cannot be reached.", AlertType.ERROR,"Connection Error:")
                exitProcess(1)
            }
        }
    }
}
