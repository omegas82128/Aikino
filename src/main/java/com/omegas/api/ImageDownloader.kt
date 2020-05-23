package com.omegas.api

import com.omegas.api.Posters.getPoster
import com.omegas.util.Constants.LOCATION
import com.omegas.util.Type
import com.omegas.util.exceptionDialog
import com.omegas.util.showMessage
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO

object ImageDownloader {

    fun savePosterMovie(file: File, location:File,
                        posterURL: String = getPoster(file) ) {
        if(download(
                posterURL,
                location.absolutePath + "\\" + file.name
            )
        ){
            showMessage(
                "Poster downloaded",
                Type.INFO
            )
        }else{
            showMessage(
                "Poster not found",
                Type.ERROR
            )
        }
    }

    @Throws(java.lang.Exception::class)
    fun getFileName(url: String?): String? {
        return url?.let{url.substring(url.lastIndexOf("/"))}
    }
    fun getImage(imageURL: String):Image{
        return Image(getFullURL(imageURL))
    }
    private fun getFullURL(imageURL: String):String{
        return "https://image.tmdb.org/t/p/w1280$imageURL"
    }
    private fun download(imageURL: String?, folder: String): Boolean {
        return try {
            val folderFile = File(folder)
            if(!folderFile.exists()){
                folderFile.mkdirs()
            }
            val path = folder + "/" + getFileName(imageURL)
            if (imageURL != null) {
                Files.copy(URL(getFullURL(imageURL)).openStream(), Paths.get(path))
            }
            true
        } catch (e: Exception) {
            exceptionDialog(e)
            false
        }
    }
    fun download(imageURL: String?, folder: String, image:Image): String? {
        return try {
            val folderFile = File(folder)
            if(!folderFile.exists()){
                folderFile.mkdirs()
            }
            print(folder)
            val outputFile = File(LOCATION.absolutePath + "/" + getFileName(imageURL))
            if (imageURL != null) {
                val bufferedImage:BufferedImage = SwingFXUtils.fromFXImage(image,null)
                ImageIO.write(bufferedImage,"jpg", outputFile)
            }
            outputFile.absolutePath
        } catch (e: Exception) {
            exceptionDialog(e)
            null
        }
    }
}