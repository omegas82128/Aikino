package com.omegas.util

import com.omegas.util.functions.getIconTypeImagePath
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView

class IconTypeButton(iconType: IconType) : Button() {
    val iconTypeProperty: SimpleObjectProperty<IconType> = SimpleObjectProperty<IconType>(iconType)
    private val imageView = ImageView(getIconTypeImagePath(iconType))

    init {
        this.graphic = imageView
        iconTypeProperty.addListener { _, _, newValue ->
            imageView.image = Image(getIconTypeImagePath(newValue))
        }
    }
}