package com.omegas.util

import com.omegas.util.functions.showMessage
import javafx.beans.property.SimpleObjectProperty
import java.util.prefs.Preferences

/**
 * @author Muhammad Haris
 * */
object Preferences {
    // TODO turn all preferences to properties and change value through changeListeners
    val preferences: Preferences = Preferences.userRoot().node(javaClass.name)
    var iconTypeProperty =
        SimpleObjectProperty(IconType.valueOf(preferences[Constants.ICON_TYPE_KEY, IconType.WITH_TEMPLATE.name]))
    var hideIcon = preferences.getBoolean(Constants.HIDE_ICONS_KEY, true)
    var posterSize: String = preferences[Constants.POSTER_SIZE_KEY, "w1280"]
    var localPostersAllowed: Boolean = preferences.getBoolean(Constants.LOCAL_POSTERS_ALLOWED_KEY, true)

    // TODO add in settings
    var removeNotification: Boolean = false
    var removeSeconds: Long = 3

    init {
        iconTypeProperty.addListener { _, _, newValue ->
            preferences.put(Constants.ICON_TYPE_KEY, newValue.name)
            showMessage(
                text = "Icon type changed to ${newValue.name.replace('_', ' ').toLowerCase().capitalize()}",
                type = AlertType.INFO,
                title = "Icon Type Changed"
            )
        }
    }
}