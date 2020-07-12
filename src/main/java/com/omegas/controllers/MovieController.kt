package com.omegas.controllers

import com.omegas.api.TheMovieDb
import com.omegas.main.SecondMain.Companion.args
import com.omegas.util.Constants
import com.omegas.util.showMessage
import java.io.File
import java.net.URL
import java.util.*


class MovieController:MediaController() {
    private lateinit var movieName: String
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        btnPrevious.isDisable = true
        file = File(args[0])
        movieName = file.name
        getPosters(movieName)
    }
    fun downloadPoster(){
        super.downloadPoster(movieName)
    }
    override fun nameChanged(){
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

    override fun linkChanged(){
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
}
