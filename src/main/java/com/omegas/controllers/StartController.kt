package com.omegas.controllers

import com.omegas.controllers.controls.OpenSettingsControl
import com.omegas.main.Main
import com.omegas.main.Main.Companion.stage
import com.omegas.main.Main.Companion.startAikino
import javafx.fxml.Initializable
import javafx.stage.DirectoryChooser
import java.awt.Desktop
import java.net.URI
import java.net.URL
import java.util.*
import kotlin.system.exitProcess

class StartController : Initializable, OpenSettingsControl(){
    override fun initialize(location: URL?, resources: ResourceBundle?) {
    }
    fun start(){
        val directoryChooser = DirectoryChooser()
        directoryChooser.title = "Choose a Movie or TV series folder"
        val file = directoryChooser.showDialog(stage)
        if(file!=null){
            Main.args = arrayOf(file.absolutePath)
            startAikino()
        }
    }
    fun help(){
        if (Desktop.isDesktopSupported() ) {
            Desktop.getDesktop().browse(URI("https://github.com/omegas82128/aikino#faq"))
        }
    }
    fun exit(){
        exitProcess(0)
    }
}