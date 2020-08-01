package com.omegas.util

import javafx.geometry.Dimension2D
import javafx.scene.image.Image
import java.awt.Dimension
import java.util.regex.Pattern


object Constants{
    val ANIME_RE = Regex("(.+) \\((([DdSs]ub)|([Dd]ual-[Aa]udio)|([aA][nN][iI][mM][eE]))\\)")
    val TV_SERIES_RE = Regex("(.+) ((\\([Mm]ini-[Ss]eries\\))|([Ss]eason \\d+))")
    val MOVIE_RE = Regex("(.+) \\((\\d{4})\\)$")
    val PREFERRED_MOVIE_PATTERN: Pattern = Pattern.compile(MOVIE_RE.pattern)
    val PREFERRED_ANIME_PATTERN:Pattern = Pattern.compile(ANIME_RE.pattern)
    val PREFERRED_TV_PATTERN:Pattern = Pattern.compile(TV_SERIES_RE.pattern)
    val PLACEHOLDER_IMAGE_PATH = javaClass.getResource("/placeholder.jpg").toString()
    const val MAX_POSTERS = 80
    val PNG_POSTER_DIMENSION = Dimension(512,512)
    val TEMPLATE_POSTER_DIMENSION = Dimension2D(338.0 ,460.0)
    val ICON = Image(javaClass.getResource("/icon.png").toString())
    val POSTER_SIZES = listOf("w500","w780","w1280","original")

    //preferences keys
    const val ICON_TYPE_KEY = "ICON_TYPE"
    const val HIDE_ICONS_KEY = "HIDE_ICONS"
    const val POSTER_SIZE_KEY = "POSTER_SIZE"

}
