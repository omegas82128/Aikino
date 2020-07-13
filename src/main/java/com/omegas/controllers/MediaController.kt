package com.omegas.controllers

import com.omegas.api.moviedb.MovieDAL
import com.omegas.enums.AppType
import com.omegas.model.Icon
import com.omegas.main.SecondMain
import com.omegas.main.SecondMain.Companion.stage
import com.omegas.model.MediaInfo
import com.omegas.tasks.DisplayImageTask
import com.omegas.util.*
import com.omegas.util.Constants.APP_TYPE
import com.omegas.util.Constants.LOCATION
import com.omegas.util.Constants.MAX_POSTERS
import com.omegas.util.Constants.PLACEHOLDER_IMAGE_PATH
import com.omegas.util.ImageDownloader.downloadPng
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.concurrent.thread
import kotlin.system.exitProcess


abstract class MediaController:Initializable {
    @FXML
    protected lateinit var btnCreate: Button
    @FXML
    protected lateinit var btnCreateApply: Button
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
        SecondMain.changeScene("Search","Search Window")
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

            val filePath:String? = ImageDownloader.download(
                posterUrls[currentPosition],
                folder,
                imageView.image
            )
            if(filePath !=null){
                showMessage(
                    "$name poster downloaded",
                    Type.INFO,
                    "Download Complete"
                ) {
                    Runtime.getRuntime().exec("explorer.exe /select,$filePath")
                }
            }else{
                showMessage(
                    "No image found",
                    Type.ERROR,
                    "Error:"
                )
            }
        }else{
            showMessage(
                "No image found",
                Type.ERROR,
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
                    ImageDownloader.getImage(posterUrl)
                }
            )
        }
        if(posterUrls.isNotEmpty()){
            nextPoster()
        }else{
            showMessage(
                title = "No Posters Found",
                text = "${mediaInfo.toMovieName()} has no posters available"
            )
            exitProcess(1)
        }
    }
    fun createIcon(){
        createIcon(true)
    }
    private fun createIcon(delete:Boolean): Icon?{
        val pngFile = downloadPng(imageView.image,file)
        val name =
        pngFile?.let{
            val iconName = convertToIcon(it)
            if(delete){
                it.delete()
            }
            iconName
        }
        return if(pngFile == null){
            null
        }else{
            Icon(pngFile, name!!)
        }
    }
    fun createAndApply(){
        val icon = createIcon(false)
        val file = icon?.file
        icon?.file = this.file
        if(icon!=null){
            applyIcon(icon)
        }else{
            showMessage("Icon could not be created.",Type.ERROR,"Icon Creation Failed")
        }
        file?.delete()
    }
    private val normalForPosters : (mediaInfo: MediaInfo) -> MutableList<String> = { mediaInfo->
        MovieDAL.getMoviePosters(mediaInfo)
    }
}
