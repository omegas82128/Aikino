package com.omegas.controller.control

import com.omegas.util.Constants
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Modality
import javafx.stage.Stage


/**
 * @author Muhammad Haris
 * */
open class OpenSettingsControl {
    fun openSettings() {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/SettingsWindow.fxml"))
        val root: Parent = fxmlLoader.load()

        val scene = Scene(root)
        val stage = Stage()
        stage.title = "${Constants.APP_NAME} - Settings"
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.icons.add(Image(javaClass.getResource("/icon.png")!!.toString()))
        stage.scene = scene
        stage.isResizable = false
        stage.show()
    }
}