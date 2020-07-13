package com.omegas.tasks

import javafx.concurrent.Task
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.util.concurrent.Future

class DisplayImageTask(private val imageView: ImageView, private val futureImage: Future<Image>) : Task<Image?>() {

    override fun call(): Image? {
        while (!futureImage.isDone){
            if (Thread.interrupted()){
                println("interrupted")
                return null
            }
        }
        println("done")
        return futureImage.get()
    }

    override fun succeeded() {
        value?.let {
            imageView.image = it
        }
    }
}