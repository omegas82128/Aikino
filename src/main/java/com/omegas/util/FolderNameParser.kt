package com.omegas.util

import com.omegas.model.MediaInfo
import com.omegas.model.SeasonNameParser
import java.io.File
import java.util.regex.Pattern

/**
 * @author Muhammad Haris
 * */
class FolderNameParser(val folder: File) {
    companion object {
        private val ANIME_RE = Regex("(.+)\\s*\\((([DdSs]ub)|([Dd]ual-[Aa]udio)|([aA][nN][iI][mM][eE]))\\)")
        private val MOVIE_RE = Regex("(.+)((18|19|20)\\d{2}).*")
        private val TV_RE = Regex("(.*((([Ss]eason)|[Ss])\\s*\\d+)?.*)\\s*((\\([Mm]ini-[Ss]eries\\))|([tT][vV]))?.*")

        private val MOVIE_PATTERN = Pattern.compile(MOVIE_RE.pattern)
        private val ANIME_PATTERN = Pattern.compile(ANIME_RE.pattern)
        private val TV_PATTERN = Pattern.compile(TV_RE.pattern)
    }

    val mediaInfo: MediaInfo

    init {
        if (!folder.isDirectory && folder.exists()) {
            throw IllegalStateException("File sent to FolderNameParser")
        }
        mediaInfo = parse()
    }

    private fun parse(): MediaInfo {
        val name = folder.name
        return when {
            ANIME_RE.matches(name) -> {
                val matcher = ANIME_PATTERN.matcher(name)
                matcher.find()
                MediaInfo(
                    SeasonNameParser(matcher.group(1)), folder
                )
            }
            MOVIE_RE.matches(name) -> {
                val matcher = MOVIE_PATTERN.matcher(name)
                matcher.find()
                MediaInfo(matcher.group(1), MediaType.MOVIE, folder, matcher.group(2).toInt())
            }
            TV_RE.matches(name) -> {
                val matcher = TV_PATTERN.matcher(name)
                matcher.find()
                MediaInfo(SeasonNameParser(matcher.group(1)), folder)
            }
            else -> {
                MediaInfo(name, MediaType.UNKNOWN, folder)
            }
        }
    }
}