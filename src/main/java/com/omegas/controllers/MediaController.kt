package com.omegas.controllers

import com.omegas.api.moviedb.MovieDAL
import com.omegas.api.moviedb.TheMovieDb
import com.omegas.api.moviedb.TmdbManager
import com.omegas.main.Main
import com.omegas.main.Main.Companion.stage
import com.omegas.model.Icon
import com.omegas.model.MediaInfo
import com.omegas.model.Poster
import com.omegas.services.DownloadService
import com.omegas.services.ImageSaveService.saveTransparentPng
import com.omegas.services.LocalPosterService
import com.omegas.services.TemplateService
import com.omegas.tasks.DisplayImageTask
import com.omegas.util.*
import com.omegas.util.Constants.APP_NAME
import com.omegas.util.Constants.MAX_POSTERS
import com.omegas.util.Constants.PLACEHOLDER_IMAGE_PATH
import com.omegas.util.Preferences.iconType
import com.omegas.util.Preferences.localPostersAllowed
import com.omegas.util.functions.applyIcon
import com.omegas.util.functions.showMessage
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.File
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.system.exitProcess


abstract class MediaController:Initializable {
    @FXML
    protected lateinit var btnNext: Button
    @FXML
    protected lateinit var btnPrevious: Button
    @FXML
    protected lateinit var imageView:ImageView
    @FXML
    protected lateinit var btnSearch: Button
    @FXML
    protected lateinit var btnDownload: Button
    @FXML
    protected lateinit var btnCreate: Button
    @FXML
    protected lateinit var btnApply: Button
    @FXML
    protected lateinit var btnSettings: Button
    @FXML
    protected lateinit var lblSearch:Label

    protected lateinit var folder:File
    protected lateinit var mediaInfo: MediaInfo
    private var currentPosition = -1
    private var posters: MutableList<Poster> = mutableListOf()
    private var executorService: ExecutorService? = null
    private var imageThread: Thread? = null
    private val placeholderImage = Image(PLACEHOLDER_IMAGE_PATH)
    override fun initialize(location: URL?, resources: ResourceBundle?) {

        initButtonListeners()
        imageView.image = Image(PLACEHOLDER_IMAGE_PATH)
        if (TheMovieDb.isNotConnected()){
            btnDownload.isDisable = true
            btnSearch.isDisable = true
        }
        lblSearch.text = when(mediaInfo.mediaType){
            MediaType.MOVIE ->  "Not the desired movie? Search."
            MediaType.TV -> "Not the desired show? Search."
        }
    }

    private fun initButtonListeners() {
        btnSearch.setOnAction {
            search()
        }
        btnDownload.setOnAction {
            downloadPoster()
        }
        btnCreate.setOnAction{
            createIcon()
        }
        btnApply.setOnAction {
            createAndApply()
        }
        btnPrevious.setOnAction {
            previousPoster()
        }
        btnNext.setOnAction {
            nextPoster()
        }
        btnSettings.setOnAction{
            openSettings()
        }
    }

    fun search(){
        Main.mediaInfo = mediaInfo
        Main.setScene("Search Window", WindowType.SEARCH)
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

        when (posters[currentPosition].posterType){
            PosterType.LOCAL ->  btnDownload.isDisable = true
            PosterType.TMDB ->  btnDownload.isDisable = false
        }

        imageView.image = placeholderImage
        if(currentPosition in 0 until posters.size){
            imageThread = Thread(
                DisplayImageTask(
                    imageView,
                    posters[currentPosition].poster
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
    abstract fun downloadPoster()
    fun downloadPoster(name:String){
        if(imageView.image == placeholderImage){
            showMessage(
                "Cannot download, poster has not finished loading",
                AlertType.ERROR,
                "Poster Not Loaded"
            )
        }else if(imageView.image!=null){

            val folder:String = folder.absolutePath

            val filePath:String? = DownloadService.download(
                posters[currentPosition].posterURL,
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
                    "Due to some error, poster was not downloaded. ",
                    AlertType.ERROR,
                    "Poster Not Downloaded"
                )
            }
        }else{
            showMessage(
                "Due to some error, poster was not downloaded. ",
                AlertType.ERROR,
                "Poster Not Downloaded"
            )
        }
    }
    protected fun getPosters(mediaInfo: MediaInfo, function:(mediaInfo: MediaInfo)-> MutableList<String> = normalForPosters){
        stage.title = mediaInfo.title
        imageView.image = null
        currentPosition = -1
        var posterUrls: List<String>
        try {
            posterUrls = function(mediaInfo)

        }catch (exception: RuntimeException){
            posterUrls = emptyList<String>().toMutableList()
            println(exception)
        }

        //Create new Executor Service and close an already existing one if there is one
        val thread = thread(true) { posters.clear() }
        executorService?.shutdownNow()
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2)
        thread.join()

        // check if local posters are allowed
        if(localPostersAllowed){
            // add local posters
            val localPosterService = LocalPosterService(folder,executorService!!)
            posters.addAll(localPosterService.getPosters())
        }

        for (posterUrl in posterUrls){
            val futureImage = executorService!!.submit<Image> {
                DownloadService.getImage(posterUrl)
            }
            posters.add(
                Poster(futureImage, PosterType.TMDB, posterUrl)
            )
        }
        if(posters.isNotEmpty()){
            nextPoster()
            if(TheMovieDb.isNotConnected()){
                showMessage("TheMovieDB.org cannot be reached. Showing local posters.", AlertType.ERROR,"Connection Error")
            }
        }else{
            if (TheMovieDb.isNotConnected()){
                showMessage("TheMovieDB.org cannot be reached.", AlertType.ERROR,"Connection Error")
                exitProcess(1)
            }else{
                thread {
                    // waits till scene has been displayed before changing to search window
                    // if it does not wait through thread.sleep(), then the search window will be replaced by tv/movie window
                    Thread.sleep(300)
                    Platform.runLater {
                        search()
                    }
                }
            }
        }
        val name = when(mediaInfo.mediaType){
            MediaType.MOVIE -> mediaInfo.toMovieName()
            MediaType.TV -> mediaInfo.toTVSeriesName()
        }
        when(TmdbManager.notFoundType){
            NotFoundType.POSTER_NOT_FOUND -> {
                showMessage(
                    title = "No Posters Found",
                    text = "$name has no posters available"
                )
            }
            NotFoundType.MEDIA_NOT_FOUND -> {
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
        TmdbManager.notFoundType = null
    }
    fun createIcon(){
        if(imageView.image == placeholderImage){
            showMessage(
                "Cannot create icon, poster has not finished loading",
                AlertType.ERROR,
                "Poster Not Loaded"
            )
            return
        }
        when(iconType){
            IconType.SIMPLE -> {
                thread(true) {
                    createIcon(true)
                    showMessage("Icon saved to folder ${folder.name}", AlertType.INFO, "Icon created successfully")
                }
            }
            IconType.WITH_TEMPLATE -> {
                createIconChooserDialog(CreateType.CREATE)
            }
        }

    }
    private fun createIcon(delete:Boolean): Icon?{
        val pngFile = saveTransparentPng(imageView.image,folder)
        return com.omegas.util.functions.createIcon(pngFile, delete)
    }
    private fun createAndApply(){
        if(imageView.image == placeholderImage){
            showMessage(
                "Cannot create and apply icon, poster has not finished loading.",
                AlertType.ERROR,
                "Poster Not Loaded"
            )
            return
        }
        when(iconType){
            IconType.SIMPLE -> {
                thread(true) {
                    val icon = createIcon(false)
                    applyIcon(icon,this.folder)
                }
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
        stage.title = "$APP_NAME - Settings"
        stage.initModality(Modality.APPLICATION_MODAL)
        stage.icons.add(Image(javaClass.getResource("/icon.png").toString()))
        stage.scene = scene
        stage.isResizable = false
        stage.show()
    }

    private fun createIconChooserDialog(createType: CreateType){
        val templateService = TemplateService(imageView.image, folder)
        val iconChooserDialog = IconChooserDialog(templateService,createType, folder)
        iconChooserDialog.show()
    }
    private val normalForPosters : (mediaInfo: MediaInfo) -> MutableList<String> = { mediaInfo->
        MovieDAL.getMoviePosters(mediaInfo)
    }
}
