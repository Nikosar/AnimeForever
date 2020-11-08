package com.nikosar.animeforever.shikimori

import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair

data class AnimeSearch(val search: String = "", val order: String = "ranked", val status: String = "",
                       val season: Season? = null, val year: Int? = null) {
    fun toNameValuePairs(): List<NameValuePair> {
        return listOf<NameValuePair>(
                BasicNameValuePair("search", search),
                BasicNameValuePair("order", order),
                BasicNameValuePair("status", status),
                BasicNameValuePair("season", makeSeason())
        )
    }

    private fun makeSeason(): String {
        val season = this.season?.shikiSeason ?: ""
        val year = this.year?.toString() ?: ""
        return if (season.isNotBlank() && year.isNotBlank()) {
            "${season}_$year"
        } else {
            year
        }
    }

}