package com.nikosar.animeforever.shikimori

import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair

data class AnimeSearch(val search: String = "", val order: String = "ranked", val status: String = "", val season: String = "") {
    fun toNameValuePairs(): List<NameValuePair> {
        return listOf<NameValuePair>(
                BasicNameValuePair("search", search),
                BasicNameValuePair("order", order),
                BasicNameValuePair("status", status),
                BasicNameValuePair("season", season)
        )
    }
}