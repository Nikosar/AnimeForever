package com.nikosar.animeforever.shikimori

import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair

data class Page(val page: Int = 1, val limit: Int = 1) {
    fun toNameValuePairs(): List<NameValuePair> {
        return listOf(
                BasicNameValuePair("page", page.toString()),
                BasicNameValuePair("limit", limit.toString())
        )
    }
}

