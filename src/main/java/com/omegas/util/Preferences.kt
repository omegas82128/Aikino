package com.omegas.util

import java.util.prefs.Preferences

object Preferences {
    val preferences: Preferences = Preferences.userRoot().node(javaClass.name)
    var iconType: IconType = IconType.valueOf(preferences[Constants.ICON_TYPE_KEY,IconType.WITH_TEMPLATE.name])
    var hideIcon = preferences.getBoolean(Constants.HIDE_ICONS_KEY,true)
    var posterSize: String =  preferences[Constants.POSTER_SIZE_KEY, "w1280"]
    var localPostersAllowed: Boolean = preferences.getBoolean(Constants.LOCAL_POSTERS_ALLOWED_KEY,true)
}