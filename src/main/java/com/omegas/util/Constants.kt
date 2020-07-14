package com.omegas.util

import com.omegas.enums.AppType
import javafx.scene.image.Image
import java.io.File
import java.util.regex.Pattern


object Constants{
    val ANIME_RE = Regex("(.+) \\((([DdSs]ub)|([Dd]ual-[Aa]udio))\\)")
    val TV_SERIES_RE = Regex(".+ ((\\(Mini-Series\\))|([Ss]eason \\d+))")
    val MOVIE_RE = Regex("(.+) \\((\\d{4})\\)$")
    val PREFERRED_MOVIE_PATTERN: Pattern = Pattern.compile(MOVIE_RE.pattern)
    val PREFERRED_ANIME_PATTERN:Pattern = Pattern.compile(ANIME_RE.pattern)
    val LOCATION =  File("F:\\(Icons)\\(New Projects)\\")
    val PLACEHOLDER_IMAGE_PATH = javaClass.getResource("/placeholder.jpg").toString()
    const val MAX_POSTERS = 80
    val ICON = Image(javaClass.getResource("/icon.png").toString())
    val APP_TYPE: AppType = AppType.PUBLIC
}