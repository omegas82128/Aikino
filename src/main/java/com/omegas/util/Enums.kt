package com.omegas.util

/**
 * @author Muhammad Haris
 * */
enum class AlertType {
    ERROR, INFO
}

enum class MediaType {
    MOVIE,TV
}

enum class CreateType {
    CREATE,CREATE_AND_APPLY
}

enum class IconType{
    SIMPLE,WITH_TEMPLATE
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