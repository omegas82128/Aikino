package com.omegas.controllers

import com.omegas.main.Main
import com.omegas.model.MediaInfo
import com.omegas.tasks.SearchTask
import com.omegas.util.MediaType
import com.omegas.util.functions.addComponent
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.layout.VBox
import javafx.util.converter.IntegerStringConverter
import java.net.URL
import java.util.*
import java.util.function.UnaryOperator


class SearchController:Initializable {
    private lateinit var mediaInfo: MediaInfo
    private lateinit var mediaType: MediaType
    @FXML
    lateinit var txtName:TextField
    @FXML
    lateinit var txtYearOrSeason:TextField
    @FXML
    lateinit var vbox:VBox
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        mediaInfo = Main.mediaInfo!!
        mediaType = mediaInfo.mediaType
        vbox.isFocusTraversable = false
        val integerFilter: UnaryOperator<TextFormatter.Change?>? = UnaryOperator { change ->
            val newText: String = change!!.controlNewText
            if (newText.matches(Regex("([1-9][0-9]*)?"))) {
                change
            }else{
                null
            }
        }
        txtName.text = mediaInfo.title
        val defaultValue:Int = when(mediaType){
            MediaType.TV -> mediaInfo.seasonNumber
            MediaType.MOVIE -> {
                mediaInfo.year
            }
        }
        txtYearOrSeason.textFormatter = TextFormatter<Int>(IntegerStringConverter(), defaultValue,integerFilter)
    }
    fun search(){
        if(txtName.text.isNotEmpty()){
            val name = txtName.text
            val number = if(txtYearOrSeason.text.isEmpty()){
                1
            }else{
                txtYearOrSeason.text.toInt()
            }
            val task: Task<*> = SearchTask(vbox,mediaType,name,number)
            Thread(task).start()
            simulateLoad()
        }
    }
    private fun simulateLoad(){
        addComponent(vbox, ProgressIndicator(-1.0))
    }
}