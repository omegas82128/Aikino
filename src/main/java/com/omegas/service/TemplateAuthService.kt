package com.omegas.service

import com.omegas.model.Template
import com.omegas.util.functions.round
import javafx.scene.image.Image

/**
 * @author Muhammad Haris
 * */
class TemplateAuthService(poster: Image, template: Template) {
    val width = poster.width
    val height = poster.height
    private val ratio = (poster.height / poster.width).round(2)
    val isHeightValid: Boolean = height >= template.posterDimension.height
    val isWidthValid: Boolean = width >= template.posterDimension.width
    val isRatioValid: Boolean = ratio >= template.posterRatio()
    val isPosterValid: Boolean = isHeightValid && isHeightValid
    val conditionsViolated: Int

    init {
        var counter = 0
        if (!isHeightValid) {
            counter++
        }
        if (!isWidthValid) {
            counter++
        }
        if (!isRatioValid) {
            counter++
        }
        conditionsViolated = counter
    }

}