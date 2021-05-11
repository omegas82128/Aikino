package com.omegas.test.util

import com.omegas.util.FolderNameParser
import com.omegas.util.MediaType
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Muhammad Haris
 * */
class FolderNameParserTest {
    private fun shouldBeAMovieOrTV(
        folderName: String,
        expectedMediaType: MediaType,
        expectedTitle: String,
        expectedNumber: Int
    ) {
        val mediaInfo = FolderNameParser(File(folderName)).mediaInfo
        assertEquals(expectedMediaType, mediaInfo.mediaType)
        assertEquals(expectedTitle, mediaInfo.title)
        when (expectedMediaType) {
            MediaType.MOVIE -> assertEquals(expectedNumber, mediaInfo.year)
            MediaType.TV -> assertEquals(expectedNumber, mediaInfo.seasonNumber)
            else -> {
            }
        }

    }

    @Test
    fun `should be a movie of year 2005 with title A Bittersweet Life`() {
        shouldBeAMovieOrTV("A Bittersweet Life (2005)", MediaType.MOVIE, "A Bittersweet Life", 2005)
    }

    @Test
    fun `should be a movie of year 2019 with title Doctor Sleep`() {
        shouldBeAMovieOrTV("Doctor.Sleep.2019.DC.1080p.BluRay.x265-RARBG", MediaType.MOVIE, "Doctor Sleep", 2019)
    }

    @Test
    fun `should be a movie of year 1989 with title Road House`() {
        shouldBeAMovieOrTV(
            "Road House (1989) (1080p BluRay x265 HEVC 10bit AAC 5.1 Tigole)",
            MediaType.MOVIE,
            "Road House",
            1989
        )
    }

    @Test
    fun `should be a movie of year 2020 with title Soul`() {
        shouldBeAMovieOrTV(
            "Soul (2020) 1080p 10bit Bluray x265 HEVC English DDP 5.1 ESubs",
            MediaType.MOVIE,
            "Soul",
            2020
        )
    }

    @Test
    fun `should be a movie of year 2010 with title 13 Assassins`() {
        shouldBeAMovieOrTV(
            "13.Assassins.2010.Extended.BluRay.1080p.DTS-HD.MA.5.1.x265.10bit-BeiTai",
            MediaType.MOVIE,
            "13 Assassins",
            2010
        )
    }

    @Test
    fun `should be a movie of year 2014 with title When Marnie Was There`() {
        shouldBeAMovieOrTV(
            "[USS] When Marnie Was There ⁄ Omoide no Marnie (2014) (Dual Audio) [BD 720p H264 AAC]",
            MediaType.MOVIE,
            "When Marnie Was There ⁄ Omoide no Marnie",
            2014
        )
    }

    @Test
    fun `should be a tv show of season 2 with title 9-1-1 Lone Star`() {
        shouldBeAMovieOrTV("9-1-1.Lone.Star.S02E12.WEB.x264-PHOENiX[TGx]", MediaType.TV, "9-1-1 Lone Star", 2)
    }

    @Test
    fun `should be a tv show of season 3 with title Ben 10 Alien Force`() {
        shouldBeAMovieOrTV("Ben 10 Alien Force Season 3", MediaType.TV, "Ben 10 Alien Force", 3)
    }

    @Test
    fun `should be a tv show of season 1 with title Dark`() {
        shouldBeAMovieOrTV("Dark", MediaType.TV, "Dark", 1)
    }

    @Test
    fun `should be a tv show of season 1 with title Death Note`() {
        shouldBeAMovieOrTV("Death Note (Dub)", MediaType.TV, "Death Note", 1)
    }
}