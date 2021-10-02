package com.omegas.util

import java.util.*

/**
 * @author Muhammad Haris
 * */
enum class AlertType {
    ERROR, INFO
}

enum class MediaType {
    MOVIE, TV, UNKNOWN
}

enum class CreateType {
    CREATE,CREATE_AND_APPLY
}

enum class IconType(val iconName: String) {
    SIMPLE("free"), DVD_FOLDER("folder"), DVD_BOX("box");

    override fun toString(): String {
        return name.replace('_', ' ').lowercase(Locale.getDefault())
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}

enum class NotFoundType{
    MEDIA_NOT_FOUND, POSTER_NOT_FOUND
}

enum class PosterType {
    LOCAL, TMDB
}

enum class WindowType {
    MOVIE, TV, SEARCH, START
}

enum class IconDialogType {
    HORIZONTAL, VERTICAL
}