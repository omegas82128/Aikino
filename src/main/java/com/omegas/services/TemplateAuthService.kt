package com.omegas.services

import com.omegas.util.Constants.TEMPLATE_POSTER_DIMENSION
import com.omegas.util.Constants.TEMPLATE_POSTER_RATIO
import com.omegas.util.functions.round
import javafx.scene.image.Image

class TemplateAuthService (image: Image){
    val width = image.width
    val height = image.height
    val ratio = (image.height / image.width).round(2)
    val isHeightValid:Boolean = height >= TEMPLATE_POSTER_DIMENSION.height
    val isWidthValid:Boolean = width >= TEMPLATE_POSTER_DIMENSION.width
    val isRatioValid:Boolean = ratio >= TEMPLATE_POSTER_RATIO
    val isPosterValid:Boolean = isRatioValid && isHeightValid && isHeightValid
    val conditionsViolated:Int
    init {
        var counter = 0
        if(!isHeightValid){
            counter++
        }
        if(!isWidthValid){
            counter++
        }
        if(!isRatioValid){
            counter++
        }
        conditionsViolated = counter
    }

}