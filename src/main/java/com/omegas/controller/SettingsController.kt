package com.omegas.controller

import com.jfoenix.controls.JFXToggleButton
import com.omegas.util.AlertType
import com.omegas.util.Constants.HIDE_ICONS_KEY
import com.omegas.util.Constants.LOCAL_POSTERS_ALLOWED_KEY
import com.omegas.util.Constants.POSTER_SIZES
import com.omegas.util.Constants.POSTER_SIZE_KEY
import com.omegas.util.IconType
import com.omegas.util.Preferences.hideIcon
import com.omegas.util.Preferences.iconTypeProperty
import com.omegas.util.Preferences.localPostersAllowed
import com.omegas.util.Preferences.posterSize
import com.omegas.util.Preferences.preferences
import com.omegas.util.functions.showMessage
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
        dpdPosterQuality.value = posterSize

        tglBtnHidden.isSelected = hideIcon
        tglBtnPosters.isSelected = localPostersAllowed
    }

    fun iconTypeChanged() {
        iconTypeProperty.value = dpdIconType.value
        // updated in preferences through changeListener
        // notification/message is generated in changeListener as well
    }

    fun posterQualityChanged(){
        posterSize = dpdPosterQuality.value
        preferences.put(POSTER_SIZE_KEY, posterSize)
        showMessage("New Poster size will take effect after app restart",AlertType.INFO,"Poster Size Changed")
    }

    fun iconHiddenPropertyChanged(){
        hideIcon = tglBtnHidden.isSelected
        preferences.putBoolean(HIDE_ICONS_KEY, hideIcon)
        var not = ""
        val hideOrShown:String = if(hideIcon){
            "Hidden"
        }else{
            not =  "not "
            "Shown"
        }
        showMessage(
            text = "Icons will ${not}be hidden, after they have been applied.",
            type = AlertType.INFO,
            title = "Icons $hideOrShown"
        )
    }

    fun localPostersAllowedPropertyChanged() {
        localPostersAllowed = tglBtnPosters.isSelected
        preferences.putBoolean(LOCAL_POSTERS_ALLOWED_KEY, localPostersAllowed)
        val showOrHidden = if (localPostersAllowed) {
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
}