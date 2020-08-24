package com.omegas.util.functions

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import java.awt.Color
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File

fun increaseSize(input: BufferedImage, dimension: Dimension): BufferedImage {
    val output = BufferedImage(
        dimension.width,
        dimension.height,
        BufferedImage.TYPE_4BYTE_ABGR
    )
    val g = output.graphics
    val transparent = Color(0f, 0f, 0f, 0f)
    g.color = transparent

    //Make the whole image transparent
    g.fillRect(0, 0, dimension.width, dimension.height)

    //Draw the input image so there are transparent margins
    g.drawImage(input, (dimension.width - input.width) / 2, 0, null)
    g.dispose()

    return output
}

fun copyImage(img: BufferedImage): BufferedImage {
    val copy = BufferedImage(
        img.width,
        img.height,
        BufferedImage.TYPE_4BYTE_ABGR
    )
    val g = copy.graphics
    g.drawImage(img,0,0,null)
    return copy
}

fun getOutputFile(folder: File, extension: String = "png"):File{
    var outputFile = File(folder.absolutePath + "/${folder.name}.$extension")
    var number = 2
    while(outputFile.exists()){
        outputFile = File(folder.absolutePath + "/" + folder.name+"v$number.$extension")
        number++
    }
    return outputFile
}

fun getImage(bufferedImage: BufferedImage):Image{
    return SwingFXUtils.toFXImage(bufferedImage,null)
}