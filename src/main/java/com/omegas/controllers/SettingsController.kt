package com.omegas.controllers

import com.omegas.util.Constants.HIDE_ICONS_KEY
import com.omegas.util.Constants.ICON_TYPE_KEY
import com.omegas.util.Constants.POSTER_SIZES
import com.omegas.util.Constants.POSTER_SIZE_KEY
import com.omegas.util.IconType
import com.omegas.util.Preferences.hideIcon
import com.omegas.util.Preferences.iconType
import com.omegas.util.Preferences.posterSize
import com.omegas.util.Preferences.preferences
import com.omegas.util.displayAlert
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import java.net.URL
import java.util.*

class SettingsController:Initializable {
    @FXML
    lateinit var chkBoxHidden : CheckBox
    @FXML
    lateinit var dpdPosterQuality:ComboBox<String>
    @FXML
    lateinit var dpdIconType:ComboBox<IconType>
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        dpdIconType.items.addAll(IconType.values())
        dpdIconType.value = iconType

        dpdPosterQuality.items.addAll(POSTER_SIZES)
        dpdPosterQuality.value = posterSize

        chkBoxHidden.isSelected = hideIcon
    }

    fun iconTypeChanged(){
        iconType = dpdIconType.value
        preferences.put(ICON_TYPE_KEY, iconType.name)
    }
    fun posterQualityChanged(){
        posterSize = dpdPosterQuality.value
        preferences.put(POSTER_SIZE_KEY, posterSize)
        displayAlert("New Poster size will take effect after app restart","Poster Size Changed")
    }
    fun iconHiddenPropertyChanged(){
        hideIcon = chkBoxHidden.isSelected
        preferences.putBoolean(HIDE_ICONS_KEY, hideIcon)
    }
}