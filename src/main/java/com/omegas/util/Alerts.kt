package com.omegas.util

import com.omegas.main.SecondMain
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
    //alert.initStyle(StageStyle.UTILITY)

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
    //Obtain only one instance of the SystemTray object
    val tray = SystemTray.getSystemTray()

    //If the icon is a file
    val image = Toolkit.getDefaultToolkit()
        .createImage(SecondMain::class.java.getResource("/icon.gif"))
    //Alternative (if the icon is on the classpath):
    //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

    val trayIcon = TrayIcon(image, "Poster downloader")
    //Let the system resize the image if needed
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