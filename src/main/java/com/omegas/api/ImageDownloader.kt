package com.omegas.api

import com.omegas.api.Posters.getPoster
import com.omegas.util.Type
import com.omegas.util.exceptionDialog
import com.omegas.util.increaseSize
import com.omegas.util.showMessage
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import javax.imageio.ImageIO


object ImageDownloader {
    private val PNG_POSTER_DIMENSION = Dimension(512,512)
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
    private fun getFullURL(imageURL: String, size:String = "w1280"):String{
        return "https://image.tmdb.org/t/p/$size$imageURL"
    }
    fun downloadPng(imageURL:String, file: File):File?{
        return try {
            var outputFile = File(file.absolutePath + "/" + file.name+".png")
            var number = 2
            while(outputFile.exists()){
                outputFile = File(file.absolutePath + "/" + file.name+"v$number .png")
                number++
            }
            download(imageURL, file.toString(), outputFile.toString(), "w500")
            outputFile
        } catch (e: Exception) {
            exceptionDialog(e)
            null
        }
    }
    fun downloadPng(image:Image, file: File):File?{
        return try {
            var outputFile = File(file.absolutePath + "/" + file.name+".png")
            var number = 2
            while(outputFile.exists()){
                outputFile = File(file.absolutePath + "/" + file.name+"v$number .png")
                number++
            }
            saveImage(image,outputFile)
            val imageNew =Image(outputFile.toURI().toString(), 0.0, 512.0, true, true)

            outputFile.delete()

            var bufferedImage:BufferedImage = SwingFXUtils.fromFXImage(imageNew,null)

            bufferedImage = increaseSize(bufferedImage, PNG_POSTER_DIMENSION)

            ImageIO.write(bufferedImage,"png", outputFile)

            outputFile
        } catch (e: Exception) {
            exceptionDialog(e)
            null
        }
    }
    private fun saveImage(image:Image, outputFile: File){
        val bufferedImage:BufferedImage = SwingFXUtils.fromFXImage(image,null)
        ImageIO.write(bufferedImage,"png", outputFile)

    }
    private fun download(imageURL: String?, folder: String, filePath:String = "$folder/"+getFileName(imageURL), posterSize:String = "w1280"): Boolean {
        return try {
            val folderFile = File(folder)
            if(!folderFile.exists()){
                folderFile.mkdirs()
            }
            if (imageURL != null) {
                Files.copy(URL(getFullURL(imageURL, posterSize)).openStream(), Paths.get(filePath))
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
            val outputFile = File(folder + "/" + getFileName(imageURL))
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