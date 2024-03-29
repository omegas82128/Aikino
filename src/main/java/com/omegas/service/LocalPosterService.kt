package com.omegas.service

import com.omegas.model.Poster
import com.omegas.util.Constants.POSTER_EXTENSIONS_LIST
import javafx.scene.image.Image
import java.io.File
import java.util.concurrent.ExecutorService
import kotlin.properties.Delegates

/**
 * @author Muhammad Haris
 * */
class LocalPosterService(val folder: File, private val executorService: ExecutorService) {
    private var isNotDirectory by Delegates.notNull<Boolean>()
    private val localPosters = mutableListOf<Image>()

    init {
        if (folder.isDirectory){
            isNotDirectory = false
            val imageFiles = folder.listFiles { _, name ->
                var flag = false
                for (extension in POSTER_EXTENSIONS_LIST){
                    flag = flag || name.endsWith(extension, true)
                }
                flag
            }
            if(imageFiles != null){
                for(imageFile in imageFiles){
                    val image =  Image(imageFile.toURI().toString())
                    if(image.height>image.width){
                        localPosters.add(image)
                    }
                }
            }
        }else{
            isNotDirectory = true
        }
    }

    fun getPosters(): List<Poster>{
        return if (localPosters.isEmpty() || isNotDirectory) {
            emptyList()
        } else {
            val futurePosters = mutableListOf<Poster>()

            for (localPoster in localPosters) {
                futurePosters.add(
                    Poster(
                        executorService.submit<Image> { // Executor Service only provides a wrapper
                            localPoster
                        }
                    )
                )
            }
            futurePosters
        }
    }
}