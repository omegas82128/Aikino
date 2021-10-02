package com.omegas.task

import com.omegas.controller.MediaController
import com.omegas.util.Constants.NOT_FOUND_IMAGE
import javafx.concurrent.Task
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.util.concurrent.Future

/**
 * @author Muhammad Haris
 * */
class DisplayImageTask(
    private val imageView: ImageView,
    private val futureImage: Future<Image>,
    private val controller: MediaController
) : Task<Image?>() {

    override fun call(): Image? {
        while (!futureImage.isDone) {
            if (Thread.interrupted()) {
                return null
            }
        }
        val image = futureImage.get()
        if (image.width == 0.0) {
            return NOT_FOUND_IMAGE
        }
        return image
    }

    override fun succeeded() {
        if (value == NOT_FOUND_IMAGE) {
            controller.btnCreate.isDisable = true
            controller.btnApply.isDisable = true
            controller.btnDownload.isDisable = true
        }
        value?.let {
            imageView.image = it
        }
    }
}