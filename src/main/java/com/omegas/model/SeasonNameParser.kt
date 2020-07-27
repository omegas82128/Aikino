package com.omegas.model

class SeasonNameParser(seasonName:String) {

    companion object {
        val regex = Regex("[Ss]eason [0-9]+")
    }
    val name = seasonName.replace(Regex("\\((([DdSs]ub)|([Dd]ual-[Aa]udio))\\)"),"").replace(Regex("( [Ss]eason [0-9]+)"),"")
    var number:Int = if (seasonName.contains(regex)){
            regex.find(seasonName)!!.value.replace("season","",ignoreCase = true).trim().toInt()
        }else{
            1
        }
        private set
}