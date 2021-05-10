package com.omegas.model

import com.omegas.util.PosterType
import javafx.scene.image.Image
import java.util.concurrent.Future

/**
 * @author Muhammad Haris
 * */
data class Poster(
    var poster: Future<Image>,
    var posterType: PosterType = PosterType.LOCAL,
    var posterURL: String? = null
)