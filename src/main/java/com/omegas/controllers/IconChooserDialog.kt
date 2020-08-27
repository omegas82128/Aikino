package com.omegas.controllers

import com.jfoenix.controls.JFXSlider
import com.omegas.model.Icon
import com.omegas.services.ImageSaveService.saveTemplatePng
import com.omegas.services.TemplateService
import com.omegas.util.AlertType
import com.omegas.util.Constants.APP_NAME
import com.omegas.util.CreateType
import com.omegas.util.functions.applyIcon
import com.omegas.util.functions.showMessage
import javafx.embed.swing.SwingFXUtils
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Modality
import javafx.stage.Stage
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

class IconChooserDialog(
    private val templateService: TemplateService,
    private val createType: CreateType,
    private val file: File
) : Initializable{

    @FXML
    lateinit var btnSelect:Button
    @FXML
    lateinit var imageView: ImageView
    @FXML
    lateinit var slider:JFXSlider

    private var stage: Stage
    private lateinit var image:BufferedImage
    init {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/IconChooserDialog.fxml"))
        fxmlLoader.setController(this)
        val root : Parent = fxmlLoader.load()

        val scene = Scene(root)
        stage = Stage()
        stage.title = "$APP_NAME - Icon Selection"
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.icons.add(Image(javaClass.getResource("/icon.png").toString()))
        stage.scene = scene
        stage.isResizable = false
    }
    fun show(){
        stage.show()
    }
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        slider.addEventFilter(KeyEvent.KEY_PRESSED) { event ->
            event.consume()
            onKeyPressed(event)
        }
        btnSelect.setOnAction {
            select()
        }
        when(createType){
            CreateType.CREATE -> btnSelect.text = "Create"
            CreateType.CREATE_AND_APPLY -> btnSelect.text = "Apply"
        }
        slider.max = templateService.getHeightDifference()
        slider.value = slider.max/2
        slider.majorTickUnit = slider.value
        slider.valueProperty().addListener { _, _, newValue ->
            slider.value = newValue.toDouble()
            displayImage(newValue.toInt())
        }
        displayImage(slider.value.toInt())
    }

    fun displayImage(y: Int){
        image = templateService.getImageInTemplate(y)
        imageView.image = SwingFXUtils.toFXImage(image, null)
    }

    fun select(){
        when(createType){
            CreateType.CREATE -> {
                thread(true) {
                    createIcon(image, true)
                    showMessage("Icon created successfully", AlertType.INFO, "Icon saved to folder ${file.name}")
                }
            }
            CreateType.CREATE_AND_APPLY -> createAndApply(image)
        }
        stage.hide()
    }
    private fun createIcon(bufferedImage: BufferedImage, delete: Boolean):Icon?{
        val pngFile = saveTemplatePng(bufferedImage, file)
        return com.omegas.util.functions.createIcon(pngFile, delete)
    }
    private fun createAndApply(bufferedImage: BufferedImage){
        thread(true) {
            val icon = createIcon(bufferedImage, false)
            applyIcon(icon, this.file)
        }
    }

    private fun onKeyPressed(event: KeyEvent) {
        when(event.code) {
            KeyCode.UP -> slider.value = slider.value-1
            KeyCode.DOWN -> slider.value = slider.value+1
            else -> {}
        }
    }
}