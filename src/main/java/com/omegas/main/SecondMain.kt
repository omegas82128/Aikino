package com.omegas.main

import com.omegas.api.PTN
import com.omegas.enums.MediaType
import com.omegas.model.MediaInfo
import com.omegas.util.Constants.ICON
import com.omegas.util.Type
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
                primaryStage.icons.add(ICON)

                stage.scene = Scene(root)
                stage.show()
                stage.isResizable = false
                stage.setOnCloseRequest {
                    exitProcess(0)
                }
            }
        }else{
            val directoryChooser = DirectoryChooser()
            directoryChooser.title = "Choose Movie or TV Series Folder"
            val file = directoryChooser.showDialog(primaryStage)
            if(file!=null){
                args = arrayOf(file.absolutePath)
                start(primaryStage)
                return
            }
            showMessage()
            Thread.sleep(10000)
            exitProcess(0)
        }

    }

    companion object{
        lateinit var args:Array<String>
        lateinit var stage: Stage
        const val TITLE = "PosterDownloader"
        var mediaInfo:MediaInfo? = null
        fun changeScene(fxmlPath:String,title:String){
            try {
                val root = FXMLLoader.load<Parent>(SecondMain::class.java.getResource("/fxml/${fxmlPath}Window.fxml"))
                stage.title = "$TITLE - $title"
                stage.scene = Scene(root)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        @JvmStatic
        fun main(args:Array<String>){
            Companion.args = args
            //Companion.args = arrayOf("E:\\"+"Sucker Punch (2011)")
            try{
                launch(SecondMain::class.java)
            }catch (exception:Exception){
                exception.printStackTrace()
                showMessage("TheMovieDB.org cannot be reached.",Type.ERROR,"Connection Error:")
                exitProcess(1)
            }
        }
    }
}
