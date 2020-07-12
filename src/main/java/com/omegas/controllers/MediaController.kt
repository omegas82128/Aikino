package com.omegas.controllers

import com.omegas.api.DisplayImageTask
import com.omegas.api.ImageDownloader
import com.omegas.api.ImageDownloader.downloadPng
import com.omegas.api.Posters
import com.omegas.main.SecondMain.Companion.stage
import com.omegas.util.*
import com.omegas.util.Constants.APP_TYPE
import com.omegas.util.Constants.LOCATION
import com.omegas.util.Constants.MAX_POSTERS
import com.omegas.util.Constants.PLACEHOLDER_IMAGE_PATH
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.concurrent.thread


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
    @FXML
    protected lateinit var txtName:TextField
    @FXML
    protected lateinit var txtLink:TextField
    protected lateinit var file:File
    private var currentPosition = -1
    private lateinit var posterUrls:MutableList<String>
    private var posters: MutableList<Future<Image>> = mutableListOf()
    private var executorService: ExecutorService? = null
    private var imageThread: Thread? = null
    abstract fun nameChanged()
    abstract fun linkChanged()
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
    protected fun getPosters(name:String, function:(name: String)-> MutableList<String> = normalForPosters){
        stage.title = name
        imageView.image = null
        currentPosition = -1
        posterUrls = function(name)
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
                text = "$name has no posters available"
            )
            btnNext.isDisable = true
        }
    }
    fun createIcon(){
        createIcon(true)
    }
    private fun createIcon(delete:Boolean):File?{
        val pngFile = downloadPng(imageView.image,file)
        pngFile?.let{
            convertToIcon(it)
            if(delete){
                it.delete()
            }
        }
        return pngFile
    }
    fun createAndApply(){
        //TODO return icon
        //TODO add ability to apply icon even if one already exists
        val pngFile = createIcon(false)
        applyIcon(file)
        pngFile?.delete()
    }
    private val normalForPosters : (movieName:String) -> MutableList<String> = { name->
        Posters.getPosters(name)
    }
}
