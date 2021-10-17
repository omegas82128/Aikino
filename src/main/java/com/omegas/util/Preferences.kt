package com.omegas.util

import com.omegas.model.Template
import com.omegas.util.functions.showMessage
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import java.util.*
import java.util.prefs.Preferences

/**
 * @author Muhammad Haris
 * */
object Preferences {
    private val preferences: Preferences = Preferences.userRoot().node(javaClass.name)

    var iconTypeProperty = SimpleObjectProperty(
        IconType.getValue(preferences[Constants.ICON_TYPE_KEY, IconType.DVD_FOLDER.name])
    )
    var hideIconProperty = SimpleBooleanProperty(preferences.getBoolean(Constants.HIDE_ICONS_KEY, true))
    var posterSizeProperty = SimpleStringProperty(preferences[Constants.POSTER_SIZE_KEY, "w1280"])
    var localPostersAllowedProperty =
        SimpleBooleanProperty(preferences.getBoolean(Constants.LOCAL_POSTERS_ALLOWED_KEY, true))

    // TODO add in settings
    var removeNotification: Boolean = false
    var removeSeconds: Long = 3


    var templateProperty = SimpleObjectProperty(
        when (iconTypeProperty.get()) {
            IconType.DVD_FOLDER -> Template.DVD_FOLDER_TEMPLATE
            IconType.DVD_BOX -> Template.DVD_BOX_TEMPLATE
            else -> Template.DVD_FOLDER_TEMPLATE
        }
    )

    init {
        iconTypeProperty.addListener { _, _, newValue ->
            preferences.put(Constants.ICON_TYPE_KEY, newValue.name)
            templateProperty.value = when (newValue) {
                IconType.DVD_FOLDER -> Template.DVD_FOLDER_TEMPLATE
                IconType.DVD_BOX -> Template.DVD_BOX_TEMPLATE
                else -> Template.DVD_FOLDER_TEMPLATE
            }
//            showMessage(
//                text = "Icon type changed to $newValue",
//                type = AlertType.INFO,
//                title = "Icon Type Changed"
//            )
        }
        posterSizeProperty.addListener { _, _, newValue ->
            preferences.put(Constants.POSTER_SIZE_KEY, newValue)
            showMessage("New Poster size will take effect after app restart", AlertType.INFO, "Poster Size Changed")
        }

        localPostersAllowedProperty.addListener { _, _, newValue ->
            preferences.putBoolean(Constants.LOCAL_POSTERS_ALLOWED_KEY, newValue)
            val showOrHidden = if (newValue) {
                "shown"
            } else {
                "hidden"
            }
            showMessage(
                "Local posters will be $showOrHidden after app restart", AlertType.INFO, "Local Posters ${
                    showOrHidden.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                }"
            )
        }
        hideIconProperty.addListener { _, _, newValue ->
            preferences.putBoolean(Constants.HIDE_ICONS_KEY, newValue)
            var not = ""
            val hideOrShown: String = if (newValue) {
                "Hidden"
            } else {
                not = "not "
                "Shown"
            }
            showMessage(
                text = "Icons will ${not}be hidden, after they have been applied.",
                type = AlertType.INFO,
                title = "Icons $hideOrShown"
            )
        }
    }
}