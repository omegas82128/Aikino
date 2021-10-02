package com.omegas.controller

import com.omegas.controller.control.OpenSettingsControl
import com.omegas.main.Main.Companion.stage
import com.omegas.main.Main.Companion.startAikino
import javafx.fxml.Initializable
import javafx.stage.DirectoryChooser
import java.awt.Desktop
import java.net.URI
import java.net.URL
import java.util.*
import kotlin.system.exitProcess


/**
 * @author Muhammad Haris
 * */
class StartController : Initializable, OpenSettingsControl() {
    override fun initialize(location: URL?, resources: ResourceBundle?) {
    }

    fun start() {
        val directoryChooser = DirectoryChooser()
        directoryChooser.title = "Choose a Movie or TV series folder"
        val file = directoryChooser.showDialog(stage)
        file?.let {
            startAikino(
                mutableListOf(it.absolutePath)
            )
        }
    }

    fun faq() {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI("https://github.com/omegas82128/aikino#faq"))
        }
    }

    fun support() {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI("https://discord.gg/rKAWJhGjxm"))
        }
    }

    fun exit() {
        exitProcess(0)
    }
}