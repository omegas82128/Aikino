package com.omegas.util

import java.io.File


object Constants{
    val ANIME_RE = Regex(".+ \\((([DdSs]ub)|([Dd]ual-[Aa]udio))\\)")
    val TV_SERIES_RE = Regex(".+ ((\\(Mini-Series\\))|([Ss]eason \\d+))")
    val ID_RE = Regex("\\d+")
    val LINK_RE = Regex("(https://?)?(www.)?themoviedb.org/movie/(\\d)+[a-zA-z-?=]+")
    val MOVIE_RE = Regex(".+ \\(\\d{4}\\)$")
    val LOCATION =  File("F:\\(Icons)\\(New Projects)\\")
    val PLACEHOLDER_IMAGE_PATH = javaClass.getResource("/placeholder.jpg").toString()
    const val MAX_POSTERS = 80
    val APP_TYPE:AppType = AppType.PUBLIC
}