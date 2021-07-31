package com.omegas.util

import javafx.geometry.Dimension2D
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.util.Duration
import java.awt.Dimension


/**
 * @author Muhammad Haris
 * */
object Constants {

    const val APP_NAME = "Aikino"
    const val APP_VERSION = "2.7.0"

    val ICON = Image(javaClass.getResource("/icon.png")!!.toString())
    val NOT_FOUND_IMAGE = Image(javaClass.getResource("/images/image-not-found.png")!!.toString())

    //tmdb api key
    val API_KEY: String = System.getenv("TMDB_API_KEY")

    //dimensions
    val PNG_POSTER_DIMENSION = Dimension(512,512)
    val TEMPLATE_POSTER_DIMENSION = Dimension2D(338.0 ,460.0)
    const val TEMPLATE_POSTER_RATIO = 1.36094675

    //poster util
    val POSTER_SIZES = listOf("w500","w780","w1280","original")
    const val MAX_POSTERS = 80

    //preferences keys
    const val ICON_TYPE_KEY = "ICON_TYPE"
    const val HIDE_ICONS_KEY = "HIDE_ICONS"
    const val POSTER_SIZE_KEY = "POSTER_SIZE"
    const val LOCAL_POSTERS_ALLOWED_KEY = "LOCAL_POSTERS_ALLOWED"

    // supported local poster file types
    val POSTER_EXTENSIONS_LIST = listOf(".png", ".jpeg", ".jpg")

    // colors
    const val VALID_COLOR = "#8ccfb9"
    const val INVALID_COLOR = "#d67774"

    // UI
    val LOCAL_POSTER_TOOL_TIP = Tooltip("Local Poster")

    init {
        LOCAL_POSTER_TOOL_TIP.showDelay = Duration(10.0)
    }
}
