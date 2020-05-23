package com.omegas.main

import com.omegas.util.Constants
import com.omegas.api.ImageDownloader.savePosterMovie
import com.omegas.util.showMessage
import java.io.File
import javafx.application.Application
import javafx.stage.Stage
import kotlin.system.exitProcess

class Main: Application() {
    override fun start(primaryStage: Stage?) {
        var file: File
        if (args.isNotEmpty()){
            for(argument in args){
                file = File(argument)
                if(file.exists() && file.isDirectory && file.name.matches(Constants.MOVIE_RE)){
                    savePosterMovie(file, file.parentFile)
                }
            }
        }else{
            showMessage()
        }
        Thread.sleep(10000)
        exitProcess(0)
    }
    companion object{
        lateinit var args:Array<String>
        @JvmStatic
        fun main(args:Array<String>){
            Companion.args = args
            launch(Main::class.java)
        }
    }
}