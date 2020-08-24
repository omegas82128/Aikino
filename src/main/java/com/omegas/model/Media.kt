package com.omegas.model

import com.omegas.util.MediaType
import com.omegas.services.DownloadService.getFullURL
import java.time.LocalDate

class Media (var id:Int, var title:String, var mediaType: MediaType, posterP:String?, var overView:String){
    var yearOfRelease:Int = LocalDate.now().year
    var totalSeasons:Int = 1
    var currentSeason:Int = 0
    var director:String = ""
    var runtime:Int = -1
    var episodes:Int = 0
    var posterPath:String? = posterP
    get() = field?.let{getFullURL(it,"w154")}
}