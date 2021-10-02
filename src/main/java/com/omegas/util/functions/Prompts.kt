package com.omegas.util.functions

import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import com.omegas.main.Main
import com.omegas.service.TemplateAuthService
import com.omegas.util.AlertType
import com.omegas.util.Constants
import com.omegas.util.Constants.ICON
import com.omegas.util.Constants.INVALID_COLOR
import com.omegas.util.Constants.VALID_COLOR
import com.omegas.util.Preferences
import com.omegas.util.Preferences.removeNotification
import com.omegas.util.Preferences.removeSeconds
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.stage.Stage
import java.awt.AWTException
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.concurrent.thread

fun displayAlert(
    contentText: String = "Internet disconnected. Reconnect and try again.",
    title: String = "Connection Error",
    show: Boolean = true,
    vararg buttonTypes: ButtonType
): Alert {
    val alert = Alert(
        Alert.AlertType.NONE,
        contentText
    )

    alert.title = title

    if (buttonTypes.isEmpty()){
        alert.dialogPane.buttonTypes.add(ButtonType.OK)
    }else{
        alert.dialogPane.buttonTypes.addAll(buttonTypes)
    }
    alert.dialogPane.stylesheets.addAll(
        object {}.javaClass.classLoader!!.getResource("css/alert.css")!!.toExternalForm())
    (alert.dialogPane.scene.window as Stage).
    icons.add(ICON)

    alert.dialogPane.minHeight = Region.USE_PREF_SIZE
    if(show){
        alert.showAndWait()
    }
    return alert
}

fun exceptionDialog(exception: Exception) {
    val alert =
        Alert(Alert.AlertType.ERROR)
    alert.title = "Exception Dialog"
    alert.headerText = "Exception found"
    alert.contentText = exception.message


    val sw = StringWriter()
    val pw = PrintWriter(sw)
    exception.printStackTrace(pw)
    val exceptionText = sw.toString()

    val label = Label("The exception stacktrace was:")

    val textArea = TextArea(exceptionText)
    textArea.isEditable = false
    textArea.isWrapText = true

    textArea.maxWidth = java.lang.Double.MAX_VALUE
    textArea.maxHeight = java.lang.Double.MAX_VALUE
    GridPane.setVgrow(
        textArea,
        Priority.ALWAYS
    )
    GridPane.setHgrow(
        textArea,
        Priority.ALWAYS
    )

    val expContent = GridPane()
    expContent.maxWidth = java.lang.Double.MAX_VALUE
    expContent.add(label, 0, 0)
    expContent.add(textArea, 0, 1)

    alert.dialogPane.expandableContent = expContent

    alert.showAndWait()
}

@Throws(AWTException::class)
fun showMessage(
    text: String = "No Folder Selected",
    type: AlertType = AlertType.ERROR,
    title: String = "Error:",
    action: () -> Unit = {}
) {
    println(title + text)
    if (SystemTray.isSupported()) {
        displayTray(title, text, type, action)
    } else {
        displayAlert(title, text)
    }
}

@Throws(AWTException::class)
private fun displayTray(title: String, text: String, type: AlertType, action: () -> Unit) {
    val tray = SystemTray.getSystemTray()

    val image = Toolkit.getDefaultToolkit()
        .createImage(Main::class.java.getResource("/icon.gif"))

    val trayIcon = TrayIcon(image, "Poster downloader")

    trayIcon.isImageAutoSize = true
    tray.add(trayIcon)

    val messageType: TrayIcon.MessageType = if (type == AlertType.ERROR) {
        TrayIcon.MessageType.ERROR
    } else {
        TrayIcon.MessageType.INFO
    }
    trayIcon.displayMessage(title, text, messageType)
    trayIcon.addActionListener{
        action()
    }
    if(removeNotification){
        thread {
            Thread.sleep(removeSeconds * 1000)
            tray.remove(trayIcon)
        }
    }
}

fun progressDialog(root:StackPane, text: String = "created and applied"): JFXDialog{
    val dialog = JFXDialog()
    val layout = JFXDialogLayout()
    val backgroundColor = "#2C3440"
    val textColor = "white"
    layout.prefWidth = 350.0
    layout.style = "-fx-background-color: $backgroundColor;"
    val label = Label("Please wait, while Icon is being $text")
    label.alignment = Pos.CENTER
    label.prefWidth = layout.prefWidth
    label.style = "-fx-text-fill: $textColor"
    layout.setHeading(label)
    val progressIndicator = ProgressIndicator(-1.0)
    progressIndicator.style = "-fx-accent: $textColor;"
    progressIndicator.minWidth = 75.0
    progressIndicator.minHeight = progressIndicator.minWidth
    val borderPane = BorderPane(progressIndicator)
    borderPane.prefWidth = 350.0
    layout.setBody(borderPane)
    dialog.content = layout
    dialog.dialogContainer = root
    dialog.isOverlayClose = false
    return dialog
}

fun posterConditionsDialog(templateAuthService: TemplateAuthService, root: StackPane): JFXDialog{
    val dialog = JFXDialog()
    val layout = JFXDialogLayout()
    val backgroundColor = "#2C3440"
    val textColor = "white"
    layout.prefWidth = 350.0
    layout.prefHeight = 200.0
    layout.style = "-fx-background-color: $backgroundColor;"
    val arrayOfViolations = arrayOf("one","two","all")
    val titleContent = Label("Icon Cannot be Created")
    titleContent.alignment = Pos.CENTER
    titleContent.prefWidth = layout.prefWidth
    titleContent.font = Font.font("System",FontWeight.BOLD, 16.0)
    titleContent.style = "-fx-text-fill: $textColor"
    layout.setHeading(titleContent)

    val spacing = 20.0
    val vBox = VBox(spacing)
    vBox.prefHeight = layout.prefWidth

    for (i in 1..2) {
        val hBox = HBox(20.0)
        val imageView = ImageView()
        imageView.fitHeight = 50.0
        imageView.fitWidth = 50.0
        val label = Label("TEST LABEL")
        label.prefHeight = 50.0
        label.font = Font.font("System Bold")
        hBox.children.addAll(imageView, label)
        vBox.children.addAll(hBox)
    }
    val arrayLabel = arrayOf(
        "Image height should be ${Preferences.templateProperty.get().posterDimension.height} or higher (height = ${templateAuthService.height})",
        "Image width should be ${Preferences.templateProperty.get().posterDimension.width} or higher (width = ${templateAuthService.width})"
    )

    for ((index, node) in vBox.children.withIndex()){
        val hBox = node as HBox
        for (child in hBox.children){
            when (child) {
                is Label -> {
                    child.text = arrayLabel[index]
                    child.style = "-fx-text-fill: ${
                        when (index) {
                            0 -> {
                                if (templateAuthService.isHeightValid) {
                                    VALID_COLOR
                                } else {
                                    INVALID_COLOR
                                }
                            }
                            1 -> {
                                if (templateAuthService.isWidthValid) {
                                    VALID_COLOR
                                } else {
                                    INVALID_COLOR
                                }
                            }
                            else -> ""
                        }
                    }"
                }
                is ImageView -> {
                    val imageLocation = when (index) {
                        0 -> {
                            if (templateAuthService.isHeightValid) {
                                "/images/ok.png"
                            } else {
                                "/images/cancel.png"
                            }
                        }
                        1 -> {
                            if (templateAuthService.isWidthValid) {
                                "/images/ok.png"
                            } else {
                                "/images/cancel.png"
                            }
                        }
                        else -> ""
                    }
                    val image = Image(Constants.javaClass.getResource(imageLocation)!!.toString())
                    child.image = image
                }
                else -> {}
            }
        }
    }
    val label = Label(" Poster violates ${arrayOfViolations[templateAuthService.conditionsViolated-1]} of the following Conditions")
    label.style = "-fx-text-fill: $textColor"
    label.prefHeight = 50.0
    label.font = Font.font("System",FontWeight.BOLD,11.5 )
    label.prefWidth = layout.prefWidth
    label.alignment = Pos.CENTER
    vBox.children.add(0, label)

    layout.setBody(vBox)
    val button = Button("Okay")
    button.setOnAction { dialog.close() }
    button.style = "-fx-text-fill: $textColor"
    button.prefWidth = 214.0
    val borderPane = BorderPane(button)
    borderPane.padding = Insets(0.0, 0.0, 10.0, 0.0)
    borderPane.prefWidth = layout.prefWidth
    layout.actions.add(borderPane)
    dialog.content = layout
    dialog.dialogContainer = root
    return dialog
}