package old

import com.omegas.api.ImageDownloader
import com.omegas.api.Posters
import com.omegas.api.TheMovieDb
import com.omegas.main.SecondMain.Companion.args
import com.omegas.main.SecondMain.Companion.stage
import com.omegas.util.Constants
import com.omegas.util.showMessage
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import java.io.File
import java.net.URL
import java.util.*
import kotlin.system.exitProcess

class Controller:Initializable {
    companion object{
        val location =  File("F:\\(Icons)\\(New Projects)\\")
    }
    private lateinit var posterList:MutableList<String>
    @FXML
    private lateinit var btnCreate: Button
    @FXML
    private lateinit var btnCreateApply: Button
    @FXML
    private lateinit var btnNext: Button
    @FXML
    private lateinit var btnPrevious: Button
    @FXML
    private lateinit var imageView:ImageView
    @FXML
    private lateinit var txtName:TextField
    @FXML
    private lateinit var txtLink:TextField
    private lateinit var file:File
    private lateinit var movieName: String
    private var currentPosition = -1
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        btnCreate.isDisable = true
        btnCreateApply.isDisable = true
        btnPrevious.isDisable = true
        if (args.isNotEmpty()){
            file = File(args[0])
            if(!file.name.matches(Constants.MOVIE_RE)){
                showMessage(
                    "Invalid name format or selected folder is not of a movie",
                    title = "Invalid Folder"
                )
                Thread.sleep(10000)
                exitProcess(0)
            }
        }else{
            showMessage()
            Thread.sleep(10000)
            exitProcess(0)
        }
        movieName = file.name
        getPosters(movieName)
    }
    fun nextPoster() {
        currentPosition++
        imageView.image = ImageDownloader.getImage(posterList[currentPosition])
        checkConditions()
    }
    private fun checkConditions(){
        btnNext.isDisable = currentPosition + 1 == posterList.size
        btnPrevious.isDisable = currentPosition - 1 == -1
    }
    fun  previousPoster() {
        currentPosition--
        imageView.image = ImageDownloader.getImage(posterList[currentPosition])
        checkConditions()
    }
    fun downloadPoster(){
        //download() was boolean
        /*if(imageView.image!=null && ImageDownloader.download(posterList[currentPosition], location.absolutePath + "\\" + movieName, imageView.image)){
            showMessage("$movieName poster downloaded",Type.INFO,"Download Complete")
        }else{
            showMessage("No image found", Type.ERROR,"Error:")
        }*/
    }
    fun changeMovieName(){
        if (txtName.text.isNotEmpty() && txtName.text.isNotBlank()){
            if(txtName.text.matches(Constants.MOVIE_RE)){
                val text = txtName.text
                txtName.clear()
                movieName = text
                getPosters(text)
            }else{
                showMessage(
                    "Movie name and year entered in invalid format.",
                    title = "Invalid Format"
                )
            }
        }
    }

    fun changeMovieLink(){
        if (txtLink.text.isNotEmpty() && txtLink.text.isNotBlank()){
            when {
                txtLink.text.matches(Constants.LINK_RE) -> {
                    val id = Regex("\\d+").find(txtLink.text)!!.value.toInt()
                    getPosters(id)
                }
                txtLink.text.matches(Constants.ID_RE) -> {
                    val id = txtLink.text.toInt()
                    getPosters(id)
                }
                else -> {
                    showMessage(
                        "Movie name and year entered in invalid format.",
                        title = "Invalid Format"
                    )
                }
            }
        }
    }
    private fun getPosters(id:Int){
        txtLink.clear()
        movieName = TheMovieDb.getMovieName(id)!!
        getPosters(movieName) {
            TheMovieDb.getMoviePosters(id)
        }
    }
    private fun getPosters(movieName:String, function:(movieName: String)-> MutableList<String> = normalForPosters){
        stage.title = movieName
        imageView.image = null
        currentPosition = -1
        posterList = function(movieName)
        println(posterList.size)
        if(posterList.isNotEmpty()){
            nextPoster()
        }else{
            showMessage(
                title = "No Posters Found",
                text = "$movieName has no posters available"
            )
            btnNext.isDisable = true
        }
    }
    private val normalForPosters : (movieName:String) -> MutableList<String> = { m-> Posters.getPosters(m)}
}
