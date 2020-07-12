package com.omegas.controllers

import com.omegas.api.TheMovieDb
import com.omegas.main.SecondMain.Companion.args
import com.omegas.util.SeasonName
import java.io.File
import java.net.URL
import java.util.*


class TvSeriesController:MediaController() {
    private lateinit var showName: String

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        btnPrevious.isDisable = true
        file = File(args[0])
        
        showName = file.name.replace(Regex(" +\\((([DdSs]ub)|([Dd]ual-[Aa]udio))\\)"),"")
        showName = showName.replace("\\(Mini-Series\\)","")
        getPosters(showName){
            val seasonName = SeasonName(showName)
            println(seasonName.name)
            println(seasonName.number)
            TheMovieDb.getShowPosters(seasonName.name,seasonName.number)
        }
    }
    fun downloadPoster(){
        super.downloadPoster(showName)
    }
    override fun nameChanged(){
        /*
        if (txtName.text.isNotEmpty() && txtName.text.isNotBlank()){
            if(txtName.text.matches(Constants.ANIME_RE)){
                val text = txtName.text
                txtName.clear()
                showName = text
                getPosters(text)
            }else{
                showMessage(
                    "Show name entered is in invalid format.",
                    title = "Invalid Format"
                )
            }
        }*/
    }

    override fun linkChanged(){
        /*if (txtLink.text.isNotEmpty() && txtLink.text.isNotBlank()){
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
                        "Show name entered is in invalid format.",
                        title = "Invalid Format"
                    )
                }
            }
        }*/
    }
    private fun getPosters(id:Int){
        txtLink.clear()
        showName = TheMovieDb.getMovieName(id)!!
        getPosters(showName) {
            TheMovieDb.getMoviePosters(id)
        }
    }
}
