package com.omegas.util

import javafx.geometry.Dimension2D
import javafx.scene.image.Image
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.awt.Dimension
import java.io.File
import java.util.regex.Pattern


object Constants{

    //resources
    private val CONFIG_FILE = File(javaClass.getResource("/app.config.json").file)
    val PLACEHOLDER_IMAGE_PATH = javaClass.getResource("/placeholder.jpg").toString()
    val ICON = Image(javaClass.getResource("/icon.png").toString())

    //tmdb api key
    // you will have to get your own from themoviedb.org and add it in the resources/app.config.json file
    val API_KEY :String = (JSONParser().parse(CONFIG_FILE.reader()) as JSONObject)["tmdbApiKey"] as String

    //regular expressions
    val ANIME_RE = Regex("(.+) \\((([DdSs]ub)|([Dd]ual-[Aa]udio)|([aA][nN][iI][mM][eE]))\\)")
    val TV_SERIES_RE = Regex("(.+) ((\\([Mm]ini-[Ss]eries\\))|([Ss]eason \\d+))")
    val MOVIE_RE = Regex("(.+) \\((\\d{4})\\)$")

    //patterns
    val PREFERRED_MOVIE_PATTERN: Pattern = Pattern.compile(MOVIE_RE.pattern)
    val PREFERRED_ANIME_PATTERN:Pattern = Pattern.compile(ANIME_RE.pattern)
    val PREFERRED_TV_PATTERN:Pattern = Pattern.compile(TV_SERIES_RE.pattern)

    //dimensions
    val PNG_POSTER_DIMENSION = Dimension(512,512)
    val TEMPLATE_POSTER_DIMENSION = Dimension2D(338.0 ,460.0)
    val POSTER_SIZES = listOf("w500","w780","w1280","original")

    const val MAX_POSTERS = 80

    //preferences keys
    const val ICON_TYPE_KEY = "ICON_TYPE"
    const val HIDE_ICONS_KEY = "HIDE_ICONS"
    const val POSTER_SIZE_KEY = "POSTER_SIZE"

}
