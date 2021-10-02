package com.omegas.model

import javafx.geometry.Dimension2D
import java.awt.Point

/**
 * @author Muhammad Haris
 */
enum class Template(
    /**
     * The point from where the poster should be rendered into the template
     */
    val posterStart: Point,
    /**
     * Dimension of the poster
     */
    val posterDimension: Dimension2D,
    /**
     * The overlays needed to be added on top of the poster for the template
     */
    val overlays: List<String>
) {
    DVD_FOLDER_TEMPLATE(
        Point(29, 23), Dimension2D(338.0, 460.0), listOf("folder/frame", "folder/lightEffect")
    ),
    DVD_BOX_TEMPLATE(
        Point(115, 33), Dimension2D(311.0, 443.0),
        listOf("box/main", "box/inner", "box/things", "box/lights")
    );

    fun posterRatio(): Double {
        return posterDimension.height / posterDimension.width
    }
}
