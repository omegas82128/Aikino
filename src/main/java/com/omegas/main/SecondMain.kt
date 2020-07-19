package com.omegas.main

import com.omegas.api.PTN
import com.omegas.controllers.SearchController
import com.omegas.model.MediaInfo
import com.omegas.util.AlertType
import com.omegas.util.Constants
import com.omegas.util.MediaType
import com.omegas.util.showMessage
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File
import kotlin.system.exitProcess

class SecondMain : Application() {
    override fun start(primaryStage: Stage?) {
        primaryStage?.icons?.add(Constants.ICON)
        if (args.isNotEmpty()){
            stage = primaryStage!!
            mediaInfo = PTN.getMediaInfo(File(args[0]))
            if(mediaInfo==null){
                showMessage(
                    "Incomplete information in folder name.",
                    title = "Invalid Folder Name"
                )
                Thread.sleep(10000)
                exitProcess(0)
            }else{
                val windowType:String? = when(mediaInfo!!.mediaType) {
                    MediaType.TV-> "TvSeries"
                    MediaType.MOVIE-> "Movie"
                }

                val root = FXMLLoader.load<Parent>(javaClass.getResource("/fxml/${windowType}Window.fxml"))
                stage.title = TITLE+" - "+mediaInfo!!.title
                stage.scene = Scene(root)
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
            Thread.sleep(7000)
            exitProcess(0)
        }

    }

    companion object{
        lateinit var args:Array<String>
        lateinit var stage: Stage
        const val TITLE = "Aikino"
        var mediaInfo:MediaInfo? = null
        fun changeScene(fxmlPath:String,title:String, search:Boolean = false){
            try {
                val fxmlLoader =FXMLLoader(SecondMain::class.java.getResource("/fxml/${fxmlPath}Window.fxml"))
                val root = fxmlLoader.load<Parent>()
                stage.title = "$TITLE - $title"
                stage.scene = Scene(root)
                if (search){
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
                launch(SecondMain::class.java)
            }catch (exception:Exception){
                exception.printStackTrace()
                showMessage("TheMovieDB.org cannot be reached.", AlertType.ERROR,"Connection Error:")
                exitProcess(1)
            }
        }
    }
}
