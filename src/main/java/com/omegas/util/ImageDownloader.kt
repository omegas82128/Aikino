package com.omegas.util

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


object ImageDownloader {
    private val PNG_POSTER_DIMENSION = Dimension(512,512)

    @Throws(java.lang.Exception::class)
    fun getFileName(url: String?): String? {
        return url?.let{url.substring(url.lastIndexOf("/"))}
    }
    fun getImage(imageURL: String):Image{
        return Image(getFullURL(imageURL))
    }
    fun getFullURL(imageURL: String, size:String = "w1280"):String{
        return "https://image.tmdb.org/t/p/$size$imageURL"
    }
    fun downloadPng(image:Image, file: File):File?{
        return try {
            var outputFile = File(file.absolutePath + "/" + file.name+".png")
            var number = 2
            while(outputFile.exists()){
                outputFile = File(file.absolutePath + "/" + file.name+"v$number .png")
                number++
            }
            saveImage(image, outputFile)
            val imageNew =Image(outputFile.toURI().toString(), 0.0, 512.0, true, true)

            outputFile.delete()

            var bufferedImage:BufferedImage = SwingFXUtils.fromFXImage(imageNew,null)

            bufferedImage = increaseSize(bufferedImage,
                PNG_POSTER_DIMENSION
            )

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