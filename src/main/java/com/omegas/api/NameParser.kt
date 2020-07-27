package com.omegas.api

import com.omegas.model.MediaInfo
import com.omegas.model.SeasonName
import com.omegas.util.Constants.ANIME_RE
import com.omegas.util.Constants.MOVIE_RE
import com.omegas.util.Constants.PREFERRED_ANIME_PATTERN
import com.omegas.util.Constants.PREFERRED_MOVIE_PATTERN
import com.omegas.util.Constants.PREFERRED_TV_PATTERN
import com.omegas.util.Constants.TV_SERIES_RE
import com.omegas.util.MediaType
import org.python.core.PyString
import org.python.util.PythonInterpreter
import java.io.File


object NameParser {
    fun getMediaInfo(folder:File):MediaInfo?{
        return when {
            MOVIE_RE.matches(folder.name) -> {
                parseMovieFormat(folder)
            }
            ANIME_RE.matches(folder.name)->{
                parseAnimeFormat(folder)
            }
            TV_SERIES_RE.matches(folder.name)->{
                parseTVFormat(folder)
            }
            else -> {
                return parse(folder)
            }
        }
    }
    private fun parse(folder: File):MediaInfo?{
        val pi = PythonInterpreter()
        pi.exec("from python import parse")
        pi["name"] = PyString(folder.name)
        pi.exec("result = parse(name)")
        pi.exec("title = result['title']")
        return try{
            pi.exec("year = result['year']")
            MediaInfo(pi["title"].asString(), MediaType.MOVIE,folder,pi["year"].asInt())
        } catch (e: Exception) {
            try{
                pi.exec("season = result['season']")
                MediaInfo(pi["title"].asString(),
                    MediaType.TV,pi["season"].asInt(),folder)
            }catch (e:Exception){
                null
            }

        }
    }
    private fun parseAnimeFormat(file: File):MediaInfo{
        val matcher = PREFERRED_ANIME_PATTERN.matcher(file.name)
        matcher.find()
        val seasonName = SeasonName(matcher.group(1))
        val animeName = seasonName.name
        val seasonNumber = seasonName.number
        return MediaInfo(animeName, MediaType.TV,seasonNumber,file)
    }

    private fun parseTVFormat(file: File):MediaInfo{

        val name:String = if(file.name.contains("(mini-series)",true)){
            val matcher = PREFERRED_TV_PATTERN.matcher(file.name)
            matcher.find()
            matcher.group(1)
        }else{
            file.name
        }
        val seasonName = SeasonName(name)

        val animeName = seasonName.name
        val seasonNumber = seasonName.number

        return MediaInfo(animeName, MediaType.TV,seasonNumber,file)
    }

    private fun parseMovieFormat(file: File): MediaInfo {
        val matcher = PREFERRED_MOVIE_PATTERN.matcher(file.name)
        matcher.find()
        val movieName = matcher.group(1)
        val year = matcher.group(2).toInt()
        return MediaInfo(movieName, MediaType.MOVIE,file,year)
    }
}