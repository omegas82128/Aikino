package com.omegas.services

import com.omegas.util.Constants.PNG_POSTER_DIMENSION
import com.omegas.util.functions.exceptionDialog
import com.omegas.util.functions.getOutputFile
import com.omegas.util.functions.increaseSize
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


/**
 * @author Muhammad Haris
 * */
object ImageSaveService {
    fun saveTransparentPng(image:Image, file: File):File?{
        return try {
            val outputFile = getOutputFile(file)
            saveImage(image, outputFile)
            val imageNew =Image(outputFile.toURI().toString(), 0.0, 512.0, true, true)

            outputFile.delete()

            var bufferedImage:BufferedImage = SwingFXUtils.fromFXImage(imageNew,null)

            bufferedImage = increaseSize(
                bufferedImage,
                PNG_POSTER_DIMENSION
            )

            saveImage(bufferedImage, outputFile)

            outputFile
        } catch (e: Exception) {
            exceptionDialog(e)
            null
        }
    }

    fun saveImage(image:Image, outputFile: File){
        saveImage(SwingFXUtils.fromFXImage(image, null), outputFile)
    }

    private fun saveImage(bufferedImage:BufferedImage, outputFile: File, format:String = "png"){
        ImageIO.write(bufferedImage,format, outputFile)
    }

    fun saveTemplatePng(bufferedImage: BufferedImage, file:File):File?{
        return try {
            val outputFile = getOutputFile(file)
            saveImage(bufferedImage, outputFile)
            outputFile
        } catch (e: Exception) {
            exceptionDialog(e)
            null
        }
    }
}