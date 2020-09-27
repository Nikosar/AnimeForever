package com.nikosar.animeforever.shikimori

import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair

data class AnimeSearch(val search: String = "", val order: String = "ranked", val status: String = "", val season: String = "") {
    fun toNameValuePairs(): List<NameValuePair> {
        val searchUri = search.replace(" ", "+")
        return listOf<NameValuePair>(
                BasicNameValuePair("search", searchUri),
                BasicNameValuePair("order", order),
                BasicNameValuePair("status", status),
                BasicNameValuePair("season", season)
        )
    }
}