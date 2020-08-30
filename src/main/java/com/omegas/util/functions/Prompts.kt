package com.omegas.util.functions

import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import com.omegas.main.Main
import com.omegas.util.AlertType
import com.omegas.util.Constants.ICON
import com.omegas.util.Preferences.removeNotification
import com.omegas.util.Preferences.removeSeconds
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.*
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
fun displayTray(title: String, text: String, type: AlertType, action: () -> Unit) {
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