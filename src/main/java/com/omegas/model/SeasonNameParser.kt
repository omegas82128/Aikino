package com.omegas.model

import java.util.regex.Pattern

/**
 * @author Muhammad Haris
 * */
class SeasonNameParser(seasonName: String) {

    companion object {
        val SEASON_NUM_REGEX = Regex("(([Ss]eason)|[Ss])\\s*([0-9]+).*")
    }

    val name =
        seasonName.replace(SEASON_NUM_REGEX, "")
    var number: Int = if (seasonName.contains(SEASON_NUM_REGEX)) {
        val matcher = Pattern.compile(SEASON_NUM_REGEX.pattern).matcher(seasonName)
        matcher.find()
        matcher.group(3).toInt()
    } else {
        1
    }
        private set
}