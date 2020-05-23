package com.omegas.main

import com.omegas.util.Constants
import com.omegas.util.showMessage
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import java.io.File
import kotlin.system.exitProcess

class SecondMain : Application() {
    override fun start(primaryStage: Stage?) {
        stage = primaryStage!!
        println(javaClass.getResource(""))
        val windowType = getTypeFromArgs()


        val root = FXMLLoader.load<Parent>(javaClass.getResource("/window/${windowType}Window.fxml"))
        stage.title = File(args[0]).name
        primaryStage.icons.add(Image(javaClass.getResource("/icon.png").toString()))
        stage.scene = Scene(root)
        stage.show()
        stage.isResizable = false
        stage.setOnCloseRequest {
            exitProcess(0)
        }
    }

    private fun getTypeFromArgs():String{
        if (args.isNotEmpty()){
            val file = File(args[0])
            val isMovie = file.name.matches(Constants.MOVIE_RE)
            val isAnime = file.name.matches(Constants.ANIME_RE)
            val isTvSeries = file.name.matches(Constants.TV_SERIES_RE)
            when {
                isAnime || isTvSeries -> {return "TvSeries"}
                isMovie -> {return "Movie"}
                !isMovie && !isAnime -> {
                    showMessage(
                        "Cannot distinguish type from Movie and Anime.",
                        title = "Invalid Folder Name"
                    )
                    Thread.sleep(10000)
                    exitProcess(0)
                }
            }
        }else{
            showMessage()
            Thread.sleep(10000)
            exitProcess(0)
        }
        return  ""
    }

    companion object{
        lateinit var args:Array<String>
        lateinit var stage: Stage
        @JvmStatic
        fun main(args:Array<String>){
            Companion.args = args
            //Companion.args = arrayOf("E:\\Carnival Row Season 1")
            launch(SecondMain::class.java)
        }
    }
}
