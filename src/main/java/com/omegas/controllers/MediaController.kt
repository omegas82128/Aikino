package com.omegas.controllers

import com.omegas.api.moviedb.MovieDAL
import com.omegas.api.moviedb.TmdbManager.notFoundType
import com.omegas.image.Downloader
import com.omegas.image.ImageSaver.saveTransparentPng
import com.omegas.image.TemplateImage
import com.omegas.main.SecondMain
import com.omegas.main.SecondMain.Companion.stage
import com.omegas.model.Icon
import com.omegas.model.MediaInfo
import com.omegas.tasks.DisplayImageTask
import com.omegas.util.*
import com.omegas.util.Constants.APP_TYPE
import com.omegas.util.Constants.LOCATION
import com.omegas.util.Constants.MAX_POSTERS
import com.omegas.util.Constants.PLACEHOLDER_IMAGE_PATH
import com.omegas.util.Preferences.iconType
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.concurrent.thread


abstract class MediaController:Initializable {
    @FXML
    protected lateinit var btnNext: Button
    @FXML
    protected lateinit var btnPrevious: Button
    @FXML
    protected lateinit var imageView:ImageView

    protected lateinit var file:File
    protected lateinit var mediaInfo: MediaInfo
    private var currentPosition = -1
    private lateinit var posterUrls:MutableList<String>
    private var posters: MutableList<Future<Image>> = mutableListOf()
    private var executorService: ExecutorService? = null
    private var imageThread: Thread? = null
    fun search(){
        SecondMain.mediaInfo = mediaInfo
        SecondMain.changeScene("Search","Search Window", true)
    }

    fun nextPoster() {
        currentPosition++
        applyPoster()
    }
    private fun checkConditions(){
        btnNext.isDisable = currentPosition + 1 == MAX_POSTERS || currentPosition + 1 == posters.size
        btnPrevious.isDisable = currentPosition - 1 == -1
    }
    private fun applyPoster(){
        imageThread?.interrupt()
        //imageView.image = posters[currentPosition].get()
        imageView.image = Image(PLACEHOLDER_IMAGE_PATH)
        if(currentPosition in 0 until posters.size){
            imageThread = Thread(
                DisplayImageTask(
                    imageView,
                    posters[currentPosition]
                )
            )
            imageThread!!.start()
            checkConditions()
        }else{
            imageThread = null
            btnNext.isDisable = false
            btnPrevious.isDisable = false
        }
    }
    fun  previousPoster() {
        currentPosition--
        applyPoster()
    }
    fun downloadPoster(name:String){
        if(imageView.image!=null){

            val folder:String = when(APP_TYPE){
                AppType.PERSONAL->LOCATION.absolutePath + "\\" + name
                AppType.PUBLIC -> file.absolutePath
            }

            val filePath:String? = Downloader.download(
                posterUrls[currentPosition],
                folder,
                imageView.image
            )
            if(filePath !=null){
                showMessage(
                    "$name poster downloaded",
                    AlertType.INFO,
                    "Download Complete"
                ) {
                    Runtime.getRuntime().exec("explorer.exe /select,$filePath")
                }
            }else{
                showMessage(
                    "No image found",
                    AlertType.ERROR,
                    "Error:"
                )
            }
        }else{
            showMessage(
                "No image found",
                AlertType.ERROR,
                "Error:"
            )
        }
    }
    protected fun getPosters(mediaInfo: MediaInfo, function:(mediaInfo: MediaInfo)-> MutableList<String> = normalForPosters){
        stage.title = mediaInfo.title
        imageView.image = null
        currentPosition = -1
        posterUrls = function(mediaInfo)
        val thread = thread(true) { posters.clear() }
        executorService?.shutdownNow()
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2)
        thread.join()
        for (posterUrl in posterUrls){
            posters.add(
                executorService!!.submit<Image> {
                    Downloader.getImage(posterUrl)
                }
            )
        }
        if(posterUrls.isNotEmpty()){
            nextPoster()
        }else{
            val name = when(mediaInfo.mediaType){
                MediaType.MOVIE -> mediaInfo.toMovieName()
                MediaType.TV -> mediaInfo.toTVSeriesName()
            }
            when(notFoundType){
                NotFoundType.MEDIA_NOT_FOUND -> {
                    showMessage(
                        title = "No Posters Found",
                        text = "$name has no posters available"
                    )
                }
                NotFoundType.POSTER_NOT_FOUND -> {
                    val media = when(mediaInfo.mediaType){
                        MediaType.MOVIE -> "Movie"
                        MediaType.TV -> "Tv Show"
                    }
                    showMessage(
                        title = "$media not Found",
                        text = "$name was not found"
                    )
                }
                null -> {}
            }
            thread {
                Thread.sleep(600)
                Platform.runLater {
                    search()
                }
            }
        }
    }
    fun createIcon(){
        when(iconType){
            IconType.SIMPLE -> createIcon(true)
            IconType.WITH_TEMPLATE -> {
                createIconChooserDialog(CreateType.CREATE)
            }
        }

    }
    private fun createIcon(delete:Boolean): Icon?{
        val pngFile = saveTransparentPng(imageView.image,file)
        return createIcon(pngFile, delete)
    }
    fun createAndApply(){
        when(iconType){
            IconType.SIMPLE -> {
                val icon = createIcon(false)
                applyIcon(icon,this.file)
            }
            IconType.WITH_TEMPLATE -> {
                createIconChooserDialog(CreateType.CREATE_AND_APPLY)
            }
        }

    }

    fun openSettings(){
        val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/SettingsWindow.fxml"))
        val root : Parent = fxmlLoader.load()

        val scene = Scene(root)
        val stage = Stage()
        stage.title = SecondMain.TITLE+" - Settings"
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.icons.add(Image(javaClass.getResource("/icon.png").toString()))
        stage.scene = scene
        stage.isResizable = false
        stage.show()
    }

    private fun createIconChooserDialog(createType: CreateType){
        val list = TemplateImage.getShortenedImages(imageView.image,file)
        val iconChooserDialog = IconChooserDialog(list,createType, file)
        iconChooserDialog.show()
    }
    private val normalForPosters : (mediaInfo: MediaInfo) -> MutableList<String> = { mediaInfo->
        MovieDAL.getMoviePosters(mediaInfo)
    }
}
