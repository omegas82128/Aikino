package com.omegas.services

import com.omegas.main.Main
import com.omegas.util.Constants.PNG_POSTER_DIMENSION
import com.omegas.util.Constants.TEMPLATE_POSTER_DIMENSION
import com.omegas.util.IconDialogType
import com.omegas.util.functions.getOutputFile
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class TemplateService(image: Image, val file: File, val iconDialogType: IconDialogType = IconDialogType.VERTICAL) {
    private val shortenedImage: BufferedImage

    init {
        val outputFile = getOutputFile(file)
        ImageSaveService.saveImage(image, outputFile)
        val imageNew = when (iconDialogType) {
            IconDialogType.HORIZONTAL -> Image(
                outputFile.toURI().toString(),
                0.0,
                TEMPLATE_POSTER_DIMENSION.height,
                true,
                true
            )
            IconDialogType.VERTICAL -> Image(
                outputFile.toURI().toString(),
                TEMPLATE_POSTER_DIMENSION.width,
                0.0,
                true,
                true
            )
        }
        outputFile.delete()
        shortenedImage = SwingFXUtils.fromFXImage(imageNew, null)
    }

    fun getImageInTemplate(x: Int = 0, y: Int = 0): BufferedImage {
        val input = shortenedImage.getSubimage(
            x,
            y,
            TEMPLATE_POSTER_DIMENSION.width.toInt(),
            TEMPLATE_POSTER_DIMENSION.height.toInt()
        )

        val dimension = PNG_POSTER_DIMENSION
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

    fun getHeightDifference(): Double {
        return (shortenedImage.height - TEMPLATE_POSTER_DIMENSION.height)
    }

    fun getWidthDifference(): Double {
        return (shortenedImage.width - TEMPLATE_POSTER_DIMENSION.width)
    }
}
