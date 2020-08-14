package com.omegas.util

import com.omegas.model.Icon
import com.omegas.util.Preferences.hideIcon
import javafx.scene.control.ButtonType
import net.sf.image4j.codec.ico.ICOEncoder
import org.ini4j.Wini
import java.io.File
import java.io.FileWriter
import javax.imageio.ImageIO


@Throws(Exception::class)
fun convertToIcon(file: File):String{
    val pngFileName = file.toString()
    var outputFile = File(pngFileName.replace(".png", " Icon.ico"))
    var version = 1
    while(outputFile.exists()){
        outputFile =  File(pngFileName.replace(".png","  Icon v$version.ico"))
        version++
    }
    val bi = ImageIO.read(file)
    ICOEncoder.write(bi, outputFile)
    return outputFile.name
}

private fun setIcon(folderPath: String, iconName:String, hideFile: Boolean) {
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
            showMessage("Icon applied to folder $folderPath", AlertType.INFO, "Icon applied successfully")
        } else if (diPath.exists()) {
            val alert = displayAlert("Folder already has an icon. Do you want to overwrite icon?","Overwrite Icon Confirmation",false,ButtonType.YES,ButtonType.NO)
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
    setIcon(icon.file.absolutePath, icon.iconName, hideIcon)
}

fun createIcon(pngFile:File?, delete:Boolean):Icon?{
    val name =
        pngFile?.let{
            val iconName = convertToIcon(it)
            if(delete){
                it.delete()
            }
            iconName
        }
    return if(pngFile == null){
        null
    }else{
        Icon(pngFile, name!!)
    }
}

fun applyIcon(icon: Icon?, file: File){
    val fileToDelete = icon?.file
    icon?.file = file
    if(icon!=null){
        applyIcon(icon)
    }else{
        showMessage("Icon could not be created.", AlertType.ERROR,"Icon Creation Failed")
    }
    fileToDelete?.delete()
}