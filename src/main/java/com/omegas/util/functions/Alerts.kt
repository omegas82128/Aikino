package com.omegas.util.functions

import com.omegas.main.Main
import com.omegas.util.AlertType
import com.omegas.util.Constants
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.stage.Stage
import java.awt.AWTException
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.io.PrintWriter
import java.io.StringWriter

fun displayAlert(contentText: String= "Internet disconnected. Reconnect and try again.", title: String="Connection Error", show:Boolean = true, vararg buttonTypes: ButtonType): Alert {
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
    alert.dialogPane.stylesheets.addAll(object{}.javaClass.classLoader!!.getResource("css/alert.css")!!.toExternalForm())
    (alert.dialogPane.scene.window as Stage).
    icons.add(Constants.ICON)

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
fun showMessage(text: String = "No Folder Selected", type: AlertType = AlertType.ERROR, title: String = "Error:", action: () -> Unit = {}) {
    println(title+text)
    if (SystemTray.isSupported()) {
        displayTray(title, text, type, action)
    } else {
        displayAlert(title, text)
    }
}

@Throws(AWTException::class)
fun displayTray(title: String, text: String, type: AlertType, action:()->Unit ) {
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
}