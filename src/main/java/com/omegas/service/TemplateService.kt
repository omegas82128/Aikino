package com.omegas.service

import com.omegas.main.Main
import com.omegas.model.Template
import com.omegas.util.Constants.ICON_IMAGE_DIMENSION
import com.omegas.util.IconDialogType
import com.omegas.util.Preferences
import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


/**
 * Renders posters into the given template
 * @author Muhammad Haris
 * */
class TemplateService(
    poster: Image,
    val iconDialogType: IconDialogType = IconDialogType.VERTICAL,
    private val template: Template = Preferences.templateProperty.get()
) {
    private val resizedPoster: BufferedImage

    /**
     * Dimension of the template
     */
    private val dimension = ICON_IMAGE_DIMENSION

    init {
        val outputFile = File.createTempFile("aikino_", ".png")
        ImageSaveService.saveImage(poster, outputFile)
        val imageNew = when (iconDialogType) {
            IconDialogType.HORIZONTAL -> Image(
                outputFile.toURI().toString(),
                0.0,
                template.posterDimension.height,
                true,
                true
            )
            IconDialogType.VERTICAL -> Image(
                outputFile.toURI().toString(),
                template.posterDimension.width,
                0.0,
                true,
                true
            )
        }
        outputFile.deleteOnExit()
        resizedPoster = SwingFXUtils.fromFXImage(imageNew, null)
    }

    /**
     * Places the poster in the template, after getting a sub image from the poster
     * @param x the starting x position of the poster
     * @param y the starting y position of the poster
     * @return image with poster in the template
     */
    fun getImageInTemplate(x: Int = 0, y: Int = 0): BufferedImage {
        val input = resizedPoster.getSubimage(
            x,
            y,
            template.posterDimension.width.toInt(),
            template.posterDimension.height.toInt()
        )

        val output = BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_4BYTE_ABGR)
        val outputGraphics = output.graphics

        val transparent = Color(0f, 0f, 0f, 0f)
        outputGraphics.color = transparent
        //Make the whole image transparent
        outputGraphics.fillRect(0, 0, dimension.width, dimension.height)

        //Draw poster
        outputGraphics.drawImage(input, template.posterStart.x, template.posterStart.y, null)

        //Draw the template overlays
        for (overlayName in template.overlays) {
            val templateFrame = ImageIO.read(Main::class.java.getResource("/templates/$overlayName.png"))
            outputGraphics.drawImage(templateFrame, 0, 0, null)
        }

        outputGraphics.dispose()
        return output
    }

    /**
     * The difference between resized poster height and required poster height
     */
    fun getHeightDifference(): Double {
        return (resizedPoster.height - template.posterDimension.height)
    }

    /**
     * The difference between resized poster width and required poster height
     */
    fun getWidthDifference(): Double {
        return (resizedPoster.width - template.posterDimension.width)
    }
}
