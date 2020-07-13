package com.omegas.util

import com.omegas.model.Icon
import com.omegas.main.SecondMain
import com.omegas.util.Constants.ICON
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.*
import javafx.stage.Stage
import javafx.stage.StageStyle
import net.sf.image4j.codec.ico.ICOEncoder
import org.ini4j.Wini
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import javax.imageio.ImageIO


fun exceptionDialog(exception: Exception) {
    val alert = Alert(Alert.AlertType.ERROR)
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
    GridPane.setVgrow(textArea, Priority.ALWAYS)
    GridPane.setHgrow(textArea, Priority.ALWAYS)

    val expContent = GridPane()
    expContent.maxWidth = java.lang.Double.MAX_VALUE
    expContent.add(label, 0, 0)
    expContent.add(textArea, 0, 1)

    alert.dialogPane.expandableContent = expContent

    alert.showAndWait()
}


enum class Type {
    ERROR, INFO
}

@Throws(AWTException::class)
fun showMessage(text: String = "No Folder Selected", type: Type = Type.ERROR, title: String = "Error:", action: () -> Unit = {}) {
    println(title+text)
    if (SystemTray.isSupported()) {
        displayTray(title, text, type, action)
    } else {
        displayAlert(title, text)
    }
}

private fun displayAlert(contentText: String, title: String) {
    val alert = Alert(Alert.AlertType.NONE, contentText)
    alert.title = title
    alert.initStyle(StageStyle.UTILITY)
    alert.dialogPane.buttonTypes.add(ButtonType.OK)
    alert.dialogPane.minHeight = Region.USE_PREF_SIZE
    alert.showAndWait()
}



@Throws(AWTException::class)
private fun displayTray(title: String, text: String, type: Type, action:()->Unit ) {
    //Obtain only one instance of the SystemTray object
    val tray = SystemTray.getSystemTray()

    //If the icon is a file
    val image = Toolkit.getDefaultToolkit().createImage(SecondMain::class.java.getResource("/icon.gif"))
    //Alternative (if the icon is on the classpath):
    //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

    val trayIcon = TrayIcon(image, "Poster downloader")
    //Let the system resize the image if needed
    trayIcon.isImageAutoSize = true
    tray.add(trayIcon)
    val messageType: TrayIcon.MessageType = if (type == Type.ERROR) {
        TrayIcon.MessageType.ERROR
    } else {
        TrayIcon.MessageType.INFO
    }
    trayIcon.displayMessage(title, text, messageType)
    trayIcon.addActionListener{
        action()
    }
}
@Throws(Exception::class)
fun convertToIcon(file: File):String{
    val pngFileName = file.toString()
    var outputFile = File(pngFileName.replace(".png", " Icon.ico"))
    var version = 1
    while(outputFile.exists()){
        outputFile =  File(pngFileName.replace(".png","Icon v$version.ico"))
        version++
    }
    val bi = ImageIO.read(file)
    ICOEncoder.write(bi, outputFile)
    return outputFile.name
}

private fun setIcon(folderPath: String, iconName:String, hideFile: Boolean= true) {
    try {
        val diPath = File("$folderPath\\desktop.ini")
        if (!diPath.exists()) {
            val writer = FileWriter(diPath)
            writer.write("")
            writer.close()
            val desktopIni = Wini(diPath)
            desktopIni.put(".ShellClassInfo", "IconResource", "$iconName,0")
            desktopIni.put("ViewState", "Mode", null)
            desktopIni.put("ViewState", "Vid", null)
            desktopIni.put("ViewState", "FolderType", "Pictures")
            desktopIni.store()
            println("attrib +h +s \"$diPath\"")
            println("attrib +s \"$folderPath\"")
            Runtime.getRuntime().exec("attrib +h +s \"$diPath\"")
            Runtime.getRuntime().exec("attrib +s \"$folderPath\"")
            if (hideFile) {
                Runtime.getRuntime().exec("attrib -a -r +h -s \"$folderPath\\$iconName\"")
            } else {
                Runtime.getRuntime().exec("attrib -a -r -h -s \"$folderPath\\$iconName\"")
            }
            showMessage("Icon applied successfully", Type.INFO, "Icon applied to folder $folderPath")
        } else if (diPath.exists()) {
            val alert = showAlert("Folder already has an icon. Do you want to overwrite icon?","Overwrite Icon Confirmation",false,ButtonType.YES,ButtonType.NO)
            alert.showAndWait()
            if(alert.result == ButtonType.YES){
                diPath.delete()
                setIcon(folderPath, iconName, hideFile)
            }
            alert.close()
        }
    } catch (e: Exception) {
        exceptionDialog(e)
    }
}


fun applyIcon(icon: Icon){
    setIcon(icon.file.absolutePath, icon.iconName)
}

fun increaseSize(input: BufferedImage, dimension: Dimension): BufferedImage {
    //Create a new image of 600*600 pixels
    val output = BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_4BYTE_ABGR)
    //Get the graphics object to draw onto the image
    val g = output.graphics
    //This is a transparent color
    val transparent = Color(0f, 0f, 0f, 0f)
    //Set the transparent color as drawing color
    g.color = transparent
    //Make the whole image transparent
    g.fillRect(0, 0, dimension.width, dimension.height)
    //Draw the input image at P(100/0), so there are transparent margins
    g.drawImage(input, (dimension.width - input.width) / 2, 0, null)
    //Release the Graphics object
    g.dispose()
    //Return the 600*600 image
    return output
}
fun showAlert(contentText: String= "Internet disconnected. Reconnect and try again.", title: String="Connection Error",show:Boolean = true, vararg buttonTypes: ButtonType):Alert {
    val alert = Alert(Alert.AlertType.NONE, contentText)

    alert.title = title
    //alert.initStyle(StageStyle.UTILITY)

    if (buttonTypes.isEmpty()){
        alert.dialogPane.buttonTypes.add(ButtonType.OK)
    }else{
        alert.dialogPane.buttonTypes.addAll(buttonTypes)
    }

    (alert.dialogPane.scene.window as Stage).
    icons.add(ICON)

    alert.dialogPane.minHeight = Region.USE_PREF_SIZE
    if(show){
        alert.showAndWait()
    }
    return alert
}

fun addComponent(pane: Pane, component: Node){
    pane.children.clear()
    for(i in 1..7){
        pane.children.add(Label(""))
    }
    val border = BorderPane()
    border.center= component
    pane.children.add(border)
}