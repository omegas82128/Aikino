package com.omegas.controllers

import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import com.jfoenix.controls.JFXSlider
import com.omegas.main.Main.Companion.stage
import com.omegas.model.Icon
import com.omegas.services.ImageSaveService.saveTemplatePng
import com.omegas.services.TemplateService
import com.omegas.util.AlertType
import com.omegas.util.CreateType
import com.omegas.util.IconDialogType
import com.omegas.util.functions.applyIcon
import com.omegas.util.functions.progressDialog
import com.omegas.util.functions.showMessage
import javafx.embed.swing.SwingFXUtils
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.absoluteValue

class IconDialog(
    private val templateService: TemplateService,
    private val createType: CreateType,
    private val file: File,
    private val root: StackPane,
    private val btnSource: Button
) : Initializable {

    private val btnSelect: Button

    @FXML
    lateinit var imageView: ImageView

    @FXML
    lateinit var slider: JFXSlider

    private lateinit var image: BufferedImage
    private val iconDialog = JFXDialog()

    private val iconDialogType = templateService.iconDialogType

    init {
        val backgroundColor = "#2C3440"
        val textColor = "white"
        val prefWidth = 300.0

        val fxmlLoader = when (iconDialogType) {
            IconDialogType.HORIZONTAL -> FXMLLoader(javaClass.getResource("/fxml/partials/IconDialogHorizontal.fxml"))
            IconDialogType.VERTICAL -> FXMLLoader(javaClass.getResource("/fxml/partials/IconDialogVertical.fxml"))
        }
        fxmlLoader.setController(this)

        btnSelect = Button("Select")
        btnSelect.style = "-fx-text-fill: $textColor"
        btnSelect.prefWidth = 214.0
        val btnBorderPane = BorderPane(btnSelect)
        btnBorderPane.padding = Insets(0.0, 30.0, 10.0, 0.0)
        btnBorderPane.prefWidth = prefWidth

        val content: Parent = fxmlLoader.load()
        val layout = JFXDialogLayout()
        layout.prefWidth = prefWidth
        layout.prefHeight = 300.0
        layout.style = "-fx-background-color: $backgroundColor;"

        // on hold
        /*
        val lblClose = Label("X")
        lblClose.style = "-fx-text-fill: $textColor"
        lblClose.font = Font("Arial", lblClose.font.size)
        lblClose.setOnMouseClicked {
            iconDialog.close()
        }
        val headingPane = BorderPane()
        headingPane.right = lblClose
        layout.setHeading(headingPane)*/

        layout.setBody(content)
        layout.setActions(btnBorderPane)

        iconDialog.content = layout
        iconDialog.dialogContainer = root
        iconDialog.setOnDialogClosed {
            btnSource.isDisable = false
        }

    }
    fun show(){
        iconDialog.show()
    }
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        slider.addEventFilter(KeyEvent.KEY_PRESSED) { event ->
            event.consume()
            onKeyPressed(event)
        }
        iconDialog.addEventFilter(ScrollEvent.SCROLL) { event ->
            event.consume()
            val incrementOrDecrement = (event.deltaY / 26.666666666666664).absoluteValue.toInt()
            when { // values are inverted because slider is inverted
                event.deltaY > 0 -> slider.value = slider.value - incrementOrDecrement
                event.deltaY < 0 -> slider.value = slider.value + incrementOrDecrement
            }
        }
        btnSelect.setOnAction {
            select()
        }
        when (createType) {
            CreateType.CREATE -> btnSelect.text = "Create"
            CreateType.CREATE_AND_APPLY -> btnSelect.text = "Apply"
        }
        slider.max = when (iconDialogType) {
            IconDialogType.HORIZONTAL -> templateService.getWidthDifference()
            IconDialogType.VERTICAL -> templateService.getHeightDifference()
        }
        slider.value = slider.max / 2
        slider.majorTickUnit = slider.value
        slider.valueProperty().addListener { _, _, newValue ->
            slider.value = newValue.toDouble()
            displayImage(newValue.toInt())
        }
        displayImage(slider.value.toInt())
    }

    private fun displayImage(displacement: Int) {
        image = when (iconDialogType) {
            IconDialogType.HORIZONTAL -> templateService.getImageInTemplate(displacement)
            IconDialogType.VERTICAL -> templateService.getImageInTemplate(y = displacement)
        }
        imageView.image = SwingFXUtils.toFXImage(image, null)
    }

    fun select() {
        val onCloseRequest = stage.onCloseRequest
        stage.setOnCloseRequest {
            it.consume()
        }
        val progressDialog = when (createType) {
            CreateType.CREATE -> progressDialog(root, "created")
            CreateType.CREATE_AND_APPLY -> progressDialog(root)
        }
        progressDialog.show(root)
        thread {
            when (createType) {
                CreateType.CREATE -> {
                    createIcon(image, true)
                    showMessage("Icon created successfully", AlertType.INFO, "Icon saved to folder ${file.name}")
                }
                CreateType.CREATE_AND_APPLY -> {
                    val icon = createIcon(image, false)
                    applyIcon(icon, this.file)
                }
            }
            progressDialog.close()
            stage.onCloseRequest = onCloseRequest
        }
        iconDialog.close()
    }
    private fun createIcon(bufferedImage: BufferedImage, delete: Boolean):Icon?{
        val pngFile = saveTemplatePng(bufferedImage, file)
        return com.omegas.util.functions.createIcon(pngFile, delete)
    }

    private fun onKeyPressed(event: KeyEvent) {
        when (iconDialogType) {
            IconDialogType.HORIZONTAL -> {
                when (event.code) { // values are inverted because slider is inverted
                    KeyCode.LEFT -> slider.value = slider.value - 1
                    KeyCode.RIGHT -> slider.value = slider.value + 1
                    else -> {
                    }
                }
            }
            IconDialogType.VERTICAL -> {
                when (event.code) { // values are inverted because slider is inverted
                    KeyCode.UP -> slider.value = slider.value - 1
                    KeyCode.DOWN -> slider.value = slider.value + 1
                    else -> {
                    }
                }
            }
        }
    }
}