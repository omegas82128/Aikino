package com.omegas.controllers

import com.omegas.main.Main
import com.omegas.model.MediaInfo
import com.omegas.tasks.SearchTask
import com.omegas.util.MediaType
import com.omegas.util.functions.addComponent
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ComboBox
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.layout.VBox
import javafx.util.converter.IntegerStringConverter
import java.net.URL
import java.util.*
import java.util.function.UnaryOperator


/**
 * @author Muhammad Haris
 * */
class SearchController:Initializable {
    private lateinit var mediaInfo: MediaInfo

    @FXML
    lateinit var txtName: TextField

    @FXML
    lateinit var txtYearOrSeason: TextField

    @FXML
    lateinit var dpdMediaType: ComboBox<MediaType>

    @FXML
    lateinit var vbox: VBox
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        mediaInfo = Main.mediaInfo!!
        dpdMediaType.items.addAll(MediaType.MOVIE, MediaType.TV)
        dpdMediaType.value = mediaInfo.mediaType
        vbox.isFocusTraversable = false
        val integerFilter: UnaryOperator<TextFormatter.Change?> = UnaryOperator { change ->
            val newText: String = change!!.controlNewText
            if (newText.matches(Regex("([1-9][0-9]*)?"))) {
                change
            } else {
                null
            }
        }
        txtName.text = mediaInfo.title
        val defaultValue: Int = when (dpdMediaType.value) {
            MediaType.TV -> mediaInfo.seasonNumber
            MediaType.MOVIE -> {
                mediaInfo.year
            }
            else -> 0
        }
        txtYearOrSeason.textFormatter = TextFormatter(IntegerStringConverter(), defaultValue, integerFilter)
    }

    fun search(){
        if(txtName.text.isNotEmpty()){
            val name = txtName.text
            val number = if (txtYearOrSeason.text.isEmpty()) {
                1
            } else {
                txtYearOrSeason.text.toInt()
            }
            val task: Task<*> = SearchTask(vbox, dpdMediaType.value, name, number)
            Thread(task).start()
            simulateLoad()
        }
    }

    private fun simulateLoad() {
        val progressIndicator = ProgressIndicator(-1.0)
        progressIndicator.style = "-fx-accent: white"
        addComponent(vbox, progressIndicator)
    }

    fun mediaTypeChanged() {
        Main.mediaInfo?.let {
            it.mediaType = dpdMediaType.value
        }
        txtYearOrSeason.promptText = when (dpdMediaType.value) {
            MediaType.MOVIE -> "Enter year of release"
            MediaType.TV -> "Enter season"
            else -> {
                throw IllegalStateException("MediaType is unknown")
            }
        }
    }
}