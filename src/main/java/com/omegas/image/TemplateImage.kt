package com.omegas.image

import com.omegas.main.Main
import com.omegas.util.Constants.PNG_POSTER_DIMENSION
import com.omegas.util.Constants.TEMPLATE_POSTER_DIMENSION
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.awt.Color
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object TemplateImage {
    fun getShortenedImages(image:Image, file: File):List<BufferedImage>{
        val outputFile = getOutputFile(file)
        ImageSaver.saveImage(image, outputFile)
        val imageNew =Image(outputFile.toURI().toString(), TEMPLATE_POSTER_DIMENSION.width, 0.0, true, true)
        outputFile.delete()
        val bufferedImage:BufferedImage = SwingFXUtils.fromFXImage(imageNew,null)
        val images = getShortenedImages(bufferedImage)
        for(i in 0 until images.size){
            images[i] = getImageInTemplate(images[i], PNG_POSTER_DIMENSION)
        }
        return images
    }
    fun getImageInTemplate(input: BufferedImage, dimension: Dimension): BufferedImage {
        val output = BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_4BYTE_ABGR)
        val g = output.graphics
        val transparent = Color(0f, 0f, 0f, 0f)
        g.color = transparent
        //Make the whole image transparent
        g.fillRect(0, 0, dimension.width, dimension.height)

        // draw poster
        g.drawImage(input, 29, 23, null)

        val templateFrame = ImageIO.read(Main::class.java.getResource("/template/frame.png"))
        //Draw the template frame
        g.drawImage(templateFrame, 0, 0, null)

        val templateLightEffect = ImageIO.read(Main::class.java.getResource("/template/lightEffect.png"))
        //Draw the template Light Effect
        g.drawImage(templateLightEffect, 0, 0, null)

        //Release the Graphics object
        g.dispose()
        return output
    }

    fun getShortenedImages(image: BufferedImage): MutableList<BufferedImage> {
        println("image height = "+image.height)
        println("dimension height = "+ TEMPLATE_POSTER_DIMENSION.height)
        val heightDifference = (image.height- TEMPLATE_POSTER_DIMENSION.height).toInt()
        println(heightDifference)

        val shortOne = image.getSubimage(0,0, TEMPLATE_POSTER_DIMENSION.width.toInt(), TEMPLATE_POSTER_DIMENSION.height.toInt())
        val shortTwo = image.getSubimage(0,heightDifference/2, TEMPLATE_POSTER_DIMENSION.width.toInt(), TEMPLATE_POSTER_DIMENSION.height.toInt())
        val shortThree = image.getSubimage(0,heightDifference, TEMPLATE_POSTER_DIMENSION.width.toInt(), TEMPLATE_POSTER_DIMENSION.height.toInt())

        return mutableListOf(
            copyImage(shortOne),
            copyImage(shortTwo),
            copyImage(shortThree)
        )
    }

}
