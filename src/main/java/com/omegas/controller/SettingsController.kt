package com.omegas.controller

import com.jfoenix.controls.JFXToggleButton
import com.omegas.util.Constants.POSTER_SIZES
import com.omegas.util.IconType
import com.omegas.util.Preferences.hideIconProperty
import com.omegas.util.Preferences.iconTypeProperty
import com.omegas.util.Preferences.localPostersAllowedProperty
import com.omegas.util.Preferences.posterSizeProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ComboBox
import java.net.URL
import java.util.*


/**
 * @author Muhammad Haris
 * */
class SettingsController : Initializable {
    @FXML
    lateinit var tglBtnHidden: JFXToggleButton

    @FXML
    lateinit var tglBtnPosters: JFXToggleButton

    @FXML
    lateinit var dpdPosterQuality: ComboBox<String>

    @FXML
    lateinit var dpdIconType: ComboBox<IconType>
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        dpdIconType.items.addAll(IconType.values())
        dpdIconType.value = iconTypeProperty.value!!

        dpdPosterQuality.items.addAll(POSTER_SIZES)
        dpdPosterQuality.value = posterSizeProperty.value

        tglBtnHidden.isSelected = hideIconProperty.value
        tglBtnPosters.isSelected = localPostersAllowedProperty.value
    }

    fun iconTypeChanged() {
        iconTypeProperty.value = dpdIconType.value
        // updated in preferences through changeListener
        // notification/message is generated in changeListener as well
    }

    fun posterQualityChanged() {
        posterSizeProperty.value = dpdPosterQuality.value
        // updated in preferences through changeListener
        // notification/message is generated in changeListener as well
    }

    fun iconHiddenPropertyChanged() {
        hideIconProperty.value = tglBtnHidden.isSelected

        // updated in preferences through changeListener
        // notification/message is generated in changeListener as well
    }

    fun localPostersAllowedPropertyChanged() {
        localPostersAllowedProperty.value = tglBtnPosters.isSelected
        // updated in preferences through changeListener
        // notification/message is generated in changeListener as well

    }
}