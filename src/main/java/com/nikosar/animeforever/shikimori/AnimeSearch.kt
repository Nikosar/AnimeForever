package com.nikosar.animeforever.shikimori

import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import java.time.Year

data class AnimeSearch(val search: String = "", val order: String = "ranked", val status: String = "",
                       val season: Season? = null, val year: Int = Year.now().value) {
    fun toNameValuePairs(): List<NameValuePair> {
        return listOf<NameValuePair>(
                BasicNameValuePair("search", search),
                BasicNameValuePair("order", order),
                BasicNameValuePair("status", status),
                BasicNameValuePair("season", makeSeason())
        )
    }

    private fun makeSeason(): String =
            season?.shikiSeason?.let { "${season.shikiSeason}_$year" } ?: ""

}