package com.omegas.controllers

import com.omegas.api.moviedb.TheMovieDb
import com.omegas.api.moviedb.TmdbManager
import com.omegas.controllers.controls.OpenSettingsControl
import com.omegas.main.Main
import com.omegas.main.Main.Companion.stage
import com.omegas.model.Icon
import com.omegas.model.MediaInfo
import com.omegas.model.Poster
import com.omegas.services.*
import com.omegas.tasks.DisplayImageTask
import com.omegas.util.*
import com.omegas.util.Constants.LOCAL_POSTER_TOOL_TIP
import com.omegas.util.Constants.PLACEHOLDER_IMAGE_PATH
import com.omegas.util.functions.applyIcon
import com.omegas.util.functions.posterConditionsDialog
import com.omegas.util.functions.progressDialog
import com.omegas.util.functions.showMessage
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.StackPane
import java.io.File
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.system.exitProcess


abstract class MediaController:Initializable, OpenSettingsControl() {
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
    @FXML
    private lateinit var root:StackPane
    @FXML
    private lateinit var progressIndicator: ProgressIndicator
    @FXML
    private lateinit var stackPane:StackPane

    protected lateinit var folder:File
    protected lateinit var mediaInfo: MediaInfo
    private var currentPosition = -1
    private var posters: MutableList<Poster> = mutableListOf()
    private var executorService: ExecutorService? = null
    private var imageThread: Thread? = null
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        mediaInfo = Main.mediaInfo!!
        btnPrevious.isDisable = true
        folder = mediaInfo.file
        root.addEventHandler(KeyEvent.KEY_RELEASED){
            when(it.code){
                KeyCode.RIGHT -> nextPoster()
                KeyCode.LEFT -> previousPoster()
                else -> {}
            }
        }
        imageView.imageProperty().addListener { _, _, newValue ->
            if (newValue == null){
                if(!stackPane.children.contains(progressIndicator)){
                    stackPane.children.add(progressIndicator)
                }
            }else{
                stackPane.children.remove(progressIndicator)
            }
        }
        initButtonListeners()
        imageView.image = Image(PLACEHOLDER_IMAGE_PATH)
        if (TheMovieDb.isNotConnected()){
            btnDownload.isDisable = true
            btnSearch.isDisable = true
        }
        lblSearch.text = when(mediaInfo.mediaType){
            MediaType.MOVIE -> "Not the desired movie? Search."
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
    private fun nextPoster() {
        if(!btnNext.isDisabled){
            currentPosition++
            showPoster()
        }
    }
    private fun  previousPoster() {
        if(!btnPrevious.isDisabled){
            currentPosition--
            showPoster()
        }
    }
    private fun showPoster(){
        imageThread?.interrupt()

        when (posters[currentPosition].posterType){
            PosterType.LOCAL -> {
                Tooltip.install(imageView, LOCAL_POSTER_TOOL_TIP)
                btnDownload.isDisable = true
            }
            PosterType.TMDB -> {
                Tooltip.uninstall(imageView, LOCAL_POSTER_TOOL_TIP)
                btnDownload.isDisable = false
            }
        }

        imageView.image = null
        if(currentPosition in 0 until posters.size){
            imageThread = Thread(
                DisplayImageTask(
                    imageView,
                    posters[currentPosition].poster
                )
            )
            imageThread!!.start()
            btnNext.isDisable = currentPosition + 1 == Constants.MAX_POSTERS || currentPosition + 1 == posters.size
            btnPrevious.isDisable = currentPosition - 1 == -1
        }else{
            imageThread = null
            btnNext.isDisable = false
            btnPrevious.isDisable = false
        }
    }
    abstract fun downloadPoster()
    fun downloadPoster(name: String){
        if(imageView.image == null){
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
    abstract fun showSeasonInfo()
    protected fun getPosters(mediaInfo: MediaInfo, function: (mediaInfo: MediaInfo) -> MutableList<String>){
        btnNext.isDisable = true
        btnPrevious.isDisable = true
        imageView.image = null
        currentPosition = -1
        showSeasonInfo()
        val task = object: Task<List<String>>() {
            override fun call(): List<String> {
                return try {
                    function(mediaInfo)

                }catch (exception: RuntimeException){
                    println(exception)
                    emptyList<String>().toMutableList()
                }
            }
            override fun succeeded() {
                stage.title = mediaInfo.title
                if(mediaInfo.mediaType == MediaType.TV){
                    showSeasonInfo()
                }
                //Create new Executor Service and close an already existing one if there is one
                val thread = thread(true) { posters.clear() }
                executorService?.shutdownNow()
                executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2)
                thread.join()

                // check if local posters are allowed
                if(Preferences.localPostersAllowed){
                    // add local posters
                    val localPosterService = LocalPosterService(folder, executorService!!)
                    posters.addAll(localPosterService.getPosters())
                }

                for (posterUrl in value){
                    val futureImage = executorService!!.submit<Image> {
                        DownloadService.getImage(posterUrl)
                    }
                    posters.add(
                        Poster(futureImage, PosterType.TMDB, posterUrl)
                    )
                }
                if(posters.isNotEmpty()){
                    btnNext.isDisable = false
                    nextPoster()
                    if(TheMovieDb.isNotConnected()){
                        showMessage(
                            "TheMovieDB.org cannot be reached. Showing local posters.",
                            AlertType.ERROR,
                            "Connection Error"
                        )
                    }
                }else{
                    if (TheMovieDb.isNotConnected()){
                        showMessage("TheMovieDB.org cannot be reached.", AlertType.ERROR, "Connection Error")
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
                        val media = when (mediaInfo.mediaType) {
                            MediaType.MOVIE -> "Movie"
                            MediaType.TV -> "Tv Show"
                        }
                        showMessage(
                            title = "$media not Found",
                            text = "$name was not found"
                        )
                    }
                    null -> {
                    }
                }
                TmdbManager.notFoundType = null
            }
        }
        Thread(task).start()
    }

    private fun createIcon(){
        if(imageView.image == null){
            showMessage(
                "Cannot create icon, poster has not finished loading",
                AlertType.ERROR,
                "Poster Not Loaded"
            )
            return
        }
        when(Preferences.iconType){
            IconType.SIMPLE -> {
                val progressDialog = progressDialog(root, "created")
                progressDialog.show(root)
                thread(true) {
                    createIcon(true)
                    showMessage("Icon saved to folder ${folder.name}", AlertType.INFO, "Icon created successfully")
                    progressDialog.close()
                }
            }
            IconType.WITH_TEMPLATE -> {
                showIconDialog(CreateType.CREATE)
            }
        }

    }
    private fun createIcon(delete: Boolean): Icon?{
        val pngFile = ImageSaveService.saveTransparentPng(imageView.image, folder)
        return com.omegas.util.functions.createIcon(pngFile, delete)
    }
    private fun createAndApply(){
        if(imageView.image == null){
            showMessage(
                "Cannot create and apply icon, poster has not finished loading.",
                AlertType.ERROR,
                "Poster Not Loaded"
            )
            return
        }
        when(Preferences.iconType){
            IconType.SIMPLE -> {
                val progressDialog = progressDialog(root)
                progressDialog.show(root)
                thread(true) {
                    val icon = createIcon(false)
                    applyIcon(icon, this.folder)
                    progressDialog.close()
                }
            }
            IconType.WITH_TEMPLATE -> {
                showIconDialog(CreateType.CREATE_AND_APPLY)
            }
        }
    }
    private fun showIconDialog(createType: CreateType){
        val authService = TemplateAuthService(imageView.image)
        if(authService.isPosterValid){
            val templateService = TemplateService(imageView.image, folder)
            val iconChooserDialog = IconDialog(templateService, createType, folder, root)
            iconChooserDialog.show()
        }else{
            posterConditionsDialog(authService,root).show()
        }
    }
}
